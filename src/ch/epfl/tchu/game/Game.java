package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import ch.epfl.tchu.game.Constants;

import java.util.*;

import static ch.epfl.tchu.game.Constants.*;

/**
 * La classe Game publique, finale et non instanciable, représente une partie de tCHu.
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class Game {
    /**
     * Fait jouer une partie de tCHu aux joueurs donnés, dont les noms figurent dans la table playerNames,
     * les billets disponibles pour cette partie sont ceux de tickets, et le générateur aléatoire rng est utilisé pour créer l'état initial du jeu et pour mélanger les cartes de la défausse pour en faire une nouvelle pioche quand cela est nécessaire
     * @param players map des 2 players
     * @param playernames map des 2 noms des joueurs
     * @param tickets tas de billets
     * @param rng une instance de Random
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
            SortedBag<Ticket> ticketSortedBag = SortedBag.of(gameState.topTickets(INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(INITIAL_TICKETS_COUNT);
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
        while(!gameHasEnded){
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
                    receiveInfoAll(players, currentInfo.drewTickets(IN_GAME_TICKETS_COUNT));
                    SortedBag<Ticket> chosen = currentPlayer.chooseTickets(gameState.topTickets(IN_GAME_TICKETS_COUNT));
                    receiveInfoAll(players, currentInfo.keptTickets(chosen.size()));
                    gameState = gameState.withChosenAdditionalTickets(gameState.topTickets(IN_GAME_TICKETS_COUNT), chosen);
                    break;

                case DRAW_CARDS:
                    for(int i = 0; i < 2; ++i){
                        if(i == 1) {
                            updateStateForPlayers(players,gameState);
                        }
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        int slot = currentPlayer.drawSlot();
                        if(slot == Constants.DECK_SLOT){
                            receiveInfoAll(players, currentInfo.drewBlindCard());
                            gameState = gameState.withBlindlyDrawnCard();

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
                        //cas si le joueur ne peut pas claim la route
                        receiveInfoAll(players,currentInfo.didNotClaimRoute(claimedRoute));
                    }else{
                        if(claimedRoute.level().equals(Route.Level.OVERGROUND)){
                            // cas si la route est une route normal 
                            gameState = gameState.withClaimedRoute(claimedRoute, initialCards);
                            receiveInfoAll(players, currentInfo.claimedRoute(claimedRoute, initialCards));

                        }else{ 
                            // cas si la route est un tunnel
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

                            if(additionalCardsCount == 0) {
                                // cas ou il y a aucune cartes additionnels
                                gameState = gameState.withClaimedRoute(claimedRoute, initialCards);
                                receiveInfoAll(players, currentInfo.claimedRoute(claimedRoute, initialCards));
                            }else{
                                // cas ou il y a des cartes additionnels
                                List<SortedBag<Card>> list = currentPlayerState.possibleAdditionalCards(additionalCardsCount, initialCards,cards);
                                if (!list.isEmpty()){
                                    // cas ou il possede les cartes additionnels necessaires
                                    SortedBag<Card> suite = currentPlayer.chooseAdditionalCards(list);
                                    if(!suite.isEmpty()){
                                        // cas ou il decide de jouer ses cartes additionnels
                                        SortedBag<Card> union = suite.union(initialCards);
                                        gameState = gameState.withClaimedRoute(claimedRoute,union);
                                        receiveInfoAll(players,currentInfo.claimedRoute(claimedRoute, union));
                                    }else{
                                        // cas ou il decide de ne pas jouer ses cartes additionnels
                                        receiveInfoAll(players, currentInfo.didNotClaimRoute(claimedRoute));
                                    }
                                }else{
                                    // cas ou il ne possede pas les cartes additionnels necessaires
                                    receiveInfoAll(players, currentInfo.didNotClaimRoute(claimedRoute));
                                }
                            }
                            gameState = gameState.withMoreDiscardedCards(cards);
                        }
                    }
                    break;
            }

            if(gameState.lastTurnBegins()){
                receiveInfoAll(players, currentInfo.lastTurnBegins(currentPlayerState.carCount()));
            }

            if(!(gameState.lastPlayer() == null) && (gameState.lastPlayer().equals(gameState.currentPlayerId()))){
                updateStateForPlayers(players,gameState);
                int currentPlayerPoints = currentPlayerState.finalPoints();
                int nextPlayerPoints = nextPlayerState.finalPoints();
                Trail currentPlayerTrail = Trail.longest(currentPlayerState.routes());
                Trail nextPlayerTrail = Trail.longest(nextPlayerState.routes());
                String currentPlayerBonus = currentInfo.getsLongestTrailBonus(currentPlayerTrail);
                String nextPlayerBonus = nextInfo.getsLongestTrailBonus(nextPlayerTrail);

                //Calcul du trail le plus long
                if(currentPlayerTrail.length() > nextPlayerTrail.length()){
                    currentPlayerPoints += LONGEST_TRAIL_BONUS_POINTS;
                    receiveInfoAll(players, currentPlayerBonus);
                }else if(currentPlayerTrail.length() < nextPlayerTrail.length()){
                    nextPlayerPoints += LONGEST_TRAIL_BONUS_POINTS;
                    receiveInfoAll(players, nextPlayerBonus);
                }else{
                    currentPlayerPoints += LONGEST_TRAIL_BONUS_POINTS;
                    nextPlayerPoints += LONGEST_TRAIL_BONUS_POINTS;
                    receiveInfoAll(players, currentPlayerBonus);
                    receiveInfoAll(players, nextPlayerBonus);
                }

                //Calcul du gagnant
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
     * @param players map des 2 players
     * @param info String à communiquer
     */
    private static void receiveInfoAll(Map<PlayerId, Player> players, String info){
        PlayerId.ALL.forEach(id -> players.get(id).receiveInfo(info));
    }

    /**
     * Mets à jour l'état de tout les joueurs
     * @param players map des 2 players
     * @param gameState l'etat du jeu
     */
    private static void updateStateForPlayers(Map<PlayerId, Player> players,GameState gameState){
        PlayerId.ALL.forEach(id -> players.get(id).updateState(gameState, gameState.playerState(id)));
    }

}
