package ch.epfl.tchu.game;


import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import ch.epfl.tchu.gui.StringsFr;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static ch.epfl.tchu.game.Constants.*;

/**
 * La classe Game publique, finale et non instanciable, représente une partie de tCHu.
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class Game {

    /**
     * Fait jouer une partie de tCHu aux joueurs donnés, dont les noms figurent dans la table playerNames,
     * les billets disponibles pour cette partie sont ceux de tickets,
     * et le générateur aléatoire rng est utilisé pour créer l'état initial du jeu
     * et pour mélanger les cartes de la défausse pour en faire une nouvelle pioche quand cela est nécessaire
     * @param players map des 2 players
     * @param playerNames map des 2 noms des joueurs
     * @param tickets tas de billets
     * @param rng une instance de Random
     * @throws IllegalArgumentException si l'une des deux tables associatives a une taille différente de 2
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng, boolean is3Players) {
        players.forEach((id, p) -> p.initNbrOfPlayer(is3Players));
        boolean play = false;
        do {
            Preconditions.checkArgument(playerNames.size() == PlayerId.COUNT() && players.size() == PlayerId.COUNT());
            GameState gameState = GameState.initial(tickets, rng);
            Map<PlayerId, Info> infos = new HashMap<>();
            PlayerId.ALL().forEach(id -> infos.put(id, new Info(playerNames.get(id))));

            players.forEach((id, player) -> player.initPlayers(id, playerNames));
            receiveInfoAll(players, infos.get(gameState.currentPlayerId()).willPlayFirst());

            for (PlayerId id : PlayerId.ALL()) {
                SortedBag<Ticket> ticketSortedBag = gameState.topTickets(INITIAL_TICKETS_COUNT);
                gameState = gameState.withoutTopTickets(INITIAL_TICKETS_COUNT);
                players.get(id).setInitialTicketChoice(ticketSortedBag);
            }

            if(is3Players) gameState =gameState.withoutTopTickets(1);// car 25 billets n'est pas un multiple de 3

            updateStateForPlayers(players, gameState);
            for (PlayerId id : PlayerId.ALL()) {
                gameState = gameState.withInitiallyChosenTickets(id, players.get(id).chooseInitialTickets());
            }

            GameState finalGameState = gameState;
            players.forEach((id, player) -> receiveInfoAll(players, infos.get(id).keptTickets(finalGameState.playerState(id).ticketCount())));
            boolean gameHasEnded = false;
            while (!gameHasEnded) {
                Player currentPlayer = players.get(gameState.currentPlayerId());
                Info currentInfo = infos.get(gameState.currentPlayerId());
                Info nextInfo = infos.get(gameState.currentPlayerId().next());

                PlayerState currentPlayerState = gameState.currentPlayerState();
                PlayerState nextPlayerState = gameState.playerState(gameState.currentPlayerId().next());


                updateStateForPlayers(players, gameState);
                receiveInfoAll(players, currentInfo.canPlay());
                Player.TurnKind turn = currentPlayer.nextTurn();
                switch (turn) {
                    case DRAW_TICKETS:
                        SortedBag<Ticket> topTickets = gameState.topTickets(IN_GAME_TICKETS_COUNT);
                        receiveInfoAll(players, currentInfo.drewTickets(IN_GAME_TICKETS_COUNT));
                        SortedBag<Ticket> chosen = currentPlayer.chooseTickets(topTickets);
                        receiveInfoAll(players, currentInfo.keptTickets(chosen.size()));
                        gameState = gameState.withChosenAdditionalTickets(topTickets, chosen);
                        break;

                    case DRAW_CARDS:
                        for (int i = 0; i < 2; ++i) {
                            if (i == 1) {
                                updateStateForPlayers(players, gameState);
                            }
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            int slot = currentPlayer.drawSlot();
                            if (slot == Constants.DECK_SLOT) {
                                receiveInfoAll(players, currentInfo.drewBlindCard());
                                gameState = gameState.withBlindlyDrawnCard();

                            } else {
                                receiveInfoAll(players, currentInfo.drewVisibleCard(gameState.cardState().faceUpCard(slot)));
                                gameState = gameState.withDrawnFaceUpCard(slot);
                            }
                        }
                        break;

                    case CLAIM_ROUTE:
                        Route claimedRoute = currentPlayer.claimedRoute();
                        SortedBag<Card> initialCards = currentPlayer.initialClaimCards();

                        if (!currentPlayerState.canClaimRoute(claimedRoute)) {
                            //cas si le joueur ne peut pas claim la route
                            receiveInfoAll(players, currentInfo.didNotClaimRoute(claimedRoute));
                        } else {
                            if (claimedRoute.level().equals(Route.Level.OVERGROUND)) {
                                // cas si la route est une route normal
                                gameState = gameState.withClaimedRoute(claimedRoute, initialCards);
                                receiveInfoAll(players, currentInfo.claimedRoute(claimedRoute, initialCards));

                            } else {
                                // cas si la route est un tunnel
                                receiveInfoAll(players, currentInfo.attemptsTunnelClaim(claimedRoute, initialCards));
                                SortedBag.Builder<Card> cardsBuilder = new SortedBag.Builder<>();
                                for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                                    cardsBuilder.add(gameState.topCard());
                                    gameState = gameState.withoutTopCard();
                                }

                                SortedBag<Card> cards = cardsBuilder.build();
                                int additionalCardsCount = claimedRoute.additionalClaimCardsCount(initialCards, cards);
                                receiveInfoAll(players, currentInfo.drewAdditionalCards(cards, additionalCardsCount));

                                if (additionalCardsCount == 0) {
                                    // cas ou il y a aucune cartes additionnels
                                    gameState = gameState.withClaimedRoute(claimedRoute, initialCards);
                                    receiveInfoAll(players, currentInfo.claimedRoute(claimedRoute, initialCards));
                                } else {
                                    // cas ou il y a des cartes additionnels
                                    List<SortedBag<Card>> list = currentPlayerState.possibleAdditionalCards(additionalCardsCount, initialCards);
                                    if (!list.isEmpty()) {
                                        // cas ou il possede les cartes additionnels necessaires
                                        SortedBag<Card> suite = currentPlayer.chooseAdditionalCards(list);
                                        if (!suite.isEmpty()) {
                                            // cas ou il decide de jouer ses cartes additionnels
                                            SortedBag<Card> union = suite.union(initialCards);
                                            gameState = gameState.withClaimedRoute(claimedRoute, union);
                                            receiveInfoAll(players, currentInfo.claimedRoute(claimedRoute, union));
                                        } else {
                                            // cas ou il decide de ne pas jouer ses cartes additionnels
                                            receiveInfoAll(players, currentInfo.didNotClaimRoute(claimedRoute));
                                        }
                                    } else {
                                        // cas ou il ne possede pas les cartes additionnels necessaires
                                        receiveInfoAll(players, currentInfo.didNotClaimRoute(claimedRoute));
                                    }
                                }
                                gameState = gameState.withMoreDiscardedCards(cards);
                            }
                        }
                        break;
                }

                if (gameState.lastTurnBegins()) {
                    receiveInfoAll(players, currentInfo.lastTurnBegins(currentPlayerState.carCount()));
                }

                if(!(gameState.lastPlayer() == null) && (gameState.lastPlayer().equals(gameState.currentPlayerId()))){
                    updateStateForPlayers(players, gameState);
                    int currentPlayerPoints = currentPlayerState.finalPoints();
                    int nextPlayerPoints = nextPlayerState.finalPoints();
                    String winnerText;
                    Map<PlayerId, Integer> pointsMap = new HashMap<>();
                    Map<PlayerId, Integer> longestTrailMap = new HashMap<>();
                    List<String> winners = new ArrayList<>();

                    for (PlayerId id: PlayerId.ALL()) {
                        pointsMap.put(id,gameState.playerState(id).finalPoints());
                        longestTrailMap.put(id,Trail.longest(gameState.playerState(id).routes()).length());
                    }
                    longestTrailMap.values().stream()
                            .max(Integer::compare)
                            .ifPresent(max -> pointsMap.forEach((id, playerPoints) -> {
                                if(longestTrailMap.get(id).equals(max)) {
                                    pointsMap.replace(id, playerPoints + LONGEST_TRAIL_BONUS_POINTS);
                                    receiveInfoAll(players, String.format(StringsFr.GETS_BONUS, playerNames.get(id), max));
                                }
                            }));

                    for(PlayerId id : PlayerId.ALL()){
                        if(pointsMap.get(id).equals(Collections.max(pointsMap.values()))){
                            winners.add(playerNames.get(id));
                        }
                    }

                    String winner = String.format("%s;%s", String.join(";", winners), Collections.max(pointsMap.values()));
                    winnerText = String.format(StringsFr.THREE_PLAYERS_WINNER, String.join(", ", winners), Collections.max(pointsMap.values()));


                    String info;
                    if (currentPlayerPoints > nextPlayerPoints) {
                        info = currentInfo.won(currentPlayerPoints, nextPlayerPoints);
                    } else if (currentPlayerPoints < nextPlayerPoints) {
                        info = nextInfo.won(nextPlayerPoints, currentPlayerPoints);
                    } else {
                        info = Info.draw(new ArrayList<>(playerNames.values()), currentPlayerPoints);
                    }

                    //Calcul du trail le plus long

                    PlayerId.ALL().forEach(id -> receiveInfoAll(players, String.format("\n%s a eu %s points\n", playerNames.get(id), pointsMap.get(id))));
                    receiveInfoAll(players, PlayerId.is3Players() ? winnerText : info);


                    ArrayList<BlockingQueue<Boolean>> Qlist = new ArrayList<>();
                    
                    for (PlayerId id: PlayerId.ALL()) {
                        BlockingQueue<Boolean> Q = new ArrayBlockingQueue<>(1);
                        Qlist.add(Q);
                        new Thread(() -> {
                            try {
                                Q.put(players.get(id).showEndMenu(winner) == 1);
                            } catch (InterruptedException e) {
                                throw new Error();
                            }
                        }).start();
                        
                    }

                    try {
                        play = true;
                        for (BlockingQueue<Boolean> Q :Qlist) {
                            play = play && Q.take();
                        }
                    } catch (InterruptedException e) {
                        throw new Error();
                    }

                    gameHasEnded = true;
                }
                gameState = gameState.forNextTurn();
            }
        }while(play);
    }

    /**
     * Constructuer privée
     */
    private Game(){}
    /**
     * Communique une information a tout les joueurs
     * @param players map des 2 players
     * @param info String à communiquer
     */
    private static void receiveInfoAll(Map<PlayerId, Player> players, String info){
        PlayerId.ALL().forEach(id -> players.get(id).receiveInfo(info));
    }

    /**
     * Mets à jour l'état de tout les joueurs
     * @param players map des 2 players
     * @param gameState l'etat du jeu
     */
    private static void updateStateForPlayers(Map<PlayerId, Player> players,GameState gameState){
        PlayerId.ALL().forEach(id -> players.get(id).updateState(gameState, gameState.playerState(id)));
    }

    /**
     * Lance une game à 2 joueurs (pour que les tests marchent encore)
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng){
        play(players,playerNames,tickets,rng,false);
    }

}
