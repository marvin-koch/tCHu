package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

/**
 * Class Game
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class Game {

    /**
     * Fait jouer une partie de tCHu aux joueurs donnés, dont les noms figurent dans la table playerNames ; les billets disponibles pour cette partie sont ceux de tickets, et le générateur aléatoire rng est utilisé pour créer l'état initial du jeu et pour mélanger les cartes de la défausse pour en faire une nouvelle pioche quand cela est nécessaire 
     * @param players
     * @param playernames
     * @param tickets
     * @param rng
     * @throws IllegalArgumentException si l'une des deux tables associatives a une taille différente de 2
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playernames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(playernames.size() == 2 && players.size() == 2);
        GameState gameState = GameState.initial(tickets, rng);
        Map<PlayerId, Info> infos = new EnumMap<>(PlayerId.class);
        PlayerId.ALL.forEach(id -> infos.put(id,new Info(playernames.get(id))));

        players.forEach((id, player) -> player.initPlayers(id, playernames));
        receiveInfoAll(players, infos.get(gameState.currentPlayerId()).willPlayFirst());

        for (PlayerId id: PlayerId.values() ) {
            SortedBag<Ticket> ticketSortedBag = SortedBag.of(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
            players.get(id).setInitialTicketChoice(ticketSortedBag);
        }

        updateStateForPlayers(players,gameState);
        for (PlayerId id: PlayerId.values() ) {
            gameState = gameState.withInitiallyChosenTickets(id,players.get(id).chooseInitialTickets());
        }

        for (PlayerId id: PlayerId.values() ) {
            players.get(id).receiveInfo(infos.get(id).keptTickets(gameState.playerState(id).ticketCount()));
            players.get(id).receiveInfo(infos.get(id.next()).keptTickets(gameState.playerState(id.next()).ticketCount()));
        }

        boolean gameHasEnded = false;
        int tour = 1;//todo effacer ca
        while(!gameHasEnded){
            System.out.println("Tour "+ tour);tour++;
            System.out.println("main de marvin : "+(gameState.playerState(PlayerId.PLAYER_1).cards().size()));
            System.out.println("main de Shangeeth :"+gameState.playerState(PlayerId.PLAYER_2).cards().size());
            System.out.println("deck : "+gameState.cardState().deckSize());
            System.out.println("defausse : "+gameState.cardState().discardsSize());
            System.out.println("Total : "+(gameState.playerState(PlayerId.PLAYER_1).cards().size() + gameState.playerState(PlayerId.PLAYER_2).cards().size()+gameState.cardState().deckSize()+gameState.cardState().discardsSize()));
            System.out.println("Point claim Marvin : "+ gameState.playerState(PlayerId.PLAYER_1).claimPoints());
            System.out.println("Point claim Shangeeth : "+ gameState.playerState(PlayerId.PLAYER_2).claimPoints());
            System.out.println("Point ticket Marvin : "+ gameState.playerState(PlayerId.PLAYER_1).ticketPoints());
            System.out.println("Point ticket Shangeeth : "+ gameState.playerState(PlayerId.PLAYER_2).ticketPoints());
            System.out.println("Point Marvin : "+ gameState.playerState(PlayerId.PLAYER_1).finalPoints());
            System.out.println("Point Shangeeth : "+ gameState.playerState(PlayerId.PLAYER_2).finalPoints());
            Player currentPlayer = players.get(gameState.currentPlayerId());
            Info currentInfo = infos.get(gameState.currentPlayerId());
            Info nextInfo = infos.get(gameState.currentPlayerId().next());
            PlayerState currentPlayerState = gameState.currentPlayerState();
            PlayerState nextPlayerState = gameState.playerState(gameState.currentPlayerId().next());
            updateStateForPlayers(players,gameState);
            Player.TurnKind turn = currentPlayer.nextTurn();
            receiveInfoAll(players, currentInfo.canPlay());

            switch(turn){
                case DRAW_TICKETS:
                    receiveInfoAll(players, currentInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT));
                    SortedBag<Ticket> chosen = currentPlayer.chooseTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT));
                    receiveInfoAll(players, currentInfo.keptTickets(chosen.size()));
                    gameState = gameState.withChosenAdditionalTickets(chosen, gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT));
                    break;

                case DRAW_CARDS:
                    for(int i = 0; i < 2; ++i){
                        if(i == 1) {
                            updateStateForPlayers(players,gameState);
                        }
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        int slot = currentPlayer.drawSlot();
                        if(slot == Constants.DECK_SLOT){
                           gameState = gameState.withBlindlyDrawnCard();
                           receiveInfoAll(players, currentInfo.drewBlindCard());
                        }else{
                            receiveInfoAll(players, currentInfo.drewVisibleCard(gameState.cardState().faceUpCard(slot)));
                            gameState = gameState.withDrawnFaceUpCard(slot);
                        }

                    }
                    break;

                case CLAIM_ROUTE:
                    Route claimedRoute = currentPlayer.claimedRoute();
                    SortedBag<Card> initialCards= currentPlayer.initialClaimCards();
                    if(!currentPlayerState.canClaimRoute(claimedRoute)) {
                        receiveInfoAll(players,currentInfo.didNotClaimRoute(claimedRoute));
                        break;
                    }
                    if(claimedRoute.level().equals(Route.Level.OVERGROUND)){
                        gameState = gameState.withClaimedRoute(claimedRoute, initialCards);
                        receiveInfoAll(players, currentInfo.claimedRoute(claimedRoute, initialCards));

                    }else{
                        receiveInfoAll(players,currentInfo.attemptsTunnelClaim(claimedRoute,initialCards));
                        SortedBag.Builder<Card> cardsBuilder = new SortedBag.Builder<>();

                        for(int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++){
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            cardsBuilder.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }

                        SortedBag<Card> cards = cardsBuilder.build();
                        int additionalCardsCount = claimedRoute.additionalClaimCardsCount(initialCards, cards);
                        receiveInfoAll(players,currentInfo.drewAdditionalCards(cards, additionalCardsCount));

                        if(additionalCardsCount== 0) {
                            gameState = gameState.withClaimedRoute(claimedRoute, initialCards);
                            receiveInfoAll(players, currentInfo.claimedRoute(claimedRoute, initialCards));
                        }else{
                            List<SortedBag<Card>> list = currentPlayerState.possibleAdditionalCards(additionalCardsCount,initialCards,cards);
                            if (list.size() > 0){
                                SortedBag<Card> suite = currentPlayer.chooseAdditionalCards(list);
                                if(!suite.isEmpty()){
                                    SortedBag<Card> union = suite.union(initialCards);
                                    gameState = gameState.withClaimedRoute(claimedRoute,union);
                                    receiveInfoAll(players,currentInfo.claimedRoute(claimedRoute,union));
                                }else{
                                    receiveInfoAll(players,currentInfo.didNotClaimRoute(claimedRoute));
                                }
                            }else{
                                receiveInfoAll(players,currentInfo.didNotClaimRoute(claimedRoute));
                            }
                        }
                        gameState = gameState.withMoreDiscardedCards(cards);
                    }
                    break;
            }

            if(gameState.lastTurnBegins()){
                receiveInfoAll(players, currentInfo.lastTurnBegins(currentPlayerState.carCount()));
            }
            if(!(gameState.lastPlayer() == null) && (gameState.lastPlayer().equals(gameState.currentPlayerId()))){
                updateStateForPlayers(players,gameState);
                int bonus = Constants.LONGEST_TRAIL_BONUS_POINTS;
                int currentPlayerPoints = currentPlayerState.finalPoints();
                int nextPlayerPoints = nextPlayerState.finalPoints();
                Trail currentPlayerTrail = Trail.longest(currentPlayerState.routes());
                Trail nextPlayerTrail = Trail.longest(nextPlayerState.routes());
                String currentPlayerBonus = currentInfo.getsLongestTrailBonus(currentPlayerTrail);
                String nextPlayerBonus = nextInfo.getsLongestTrailBonus(nextPlayerTrail);

                if(currentPlayerTrail.length() > nextPlayerTrail.length()){
                    currentPlayerPoints += bonus;
                    receiveInfoAll(players, currentPlayerBonus);
                }else if(currentPlayerTrail.length() < nextPlayerTrail.length()){
                    nextPlayerPoints += bonus;
                    receiveInfoAll(players, nextPlayerBonus);
                }else{
                    currentPlayerPoints += bonus;
                    nextPlayerPoints += bonus;
                    receiveInfoAll(players, currentPlayerBonus);
                    receiveInfoAll(players, nextPlayerBonus);
                }

                if(currentPlayerPoints > nextPlayerPoints){
                    receiveInfoAll(players, currentInfo.won(currentPlayerPoints, nextPlayerPoints));
                }else if (currentPlayerPoints < nextPlayerPoints){
                    receiveInfoAll(players, currentInfo.won(nextPlayerPoints, currentPlayerPoints));
                }else{
                    receiveInfoAll(players, Info.draw(new ArrayList<>(playernames.values()), currentPlayerPoints));

                }
                gameHasEnded = true;
            }
            gameState = gameState.forNextTurn();
        }
    }

    /**
     * Communique une information a tout les joueurs
     * @param players
     * @param info
     */
    private static void receiveInfoAll(Map<PlayerId, Player> players, String info){
        PlayerId.ALL.forEach(id -> players.get(id).receiveInfo(info));

    }

    /**
     * Mets à jour l'état de tout les joueurs
     * @param players
     * @param gameState
     */
    private static void updateStateForPlayers(Map<PlayerId, Player> players,GameState gameState){
        PlayerId.ALL.forEach(id -> players.get(id).updateState(gameState, gameState.playerState(id)));
    }

}
