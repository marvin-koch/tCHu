package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class GameTest {

    @Test
    void testGame(){
        Map<PlayerId,Player> players = new EnumMap<>(PlayerId.class);
        players.put(PlayerId.PLAYER_1, new TestPlayer(21, ChMap.routes()));
        players.put(PlayerId.PLAYER_2, new TestPlayer(21, ChMap.routes()));
        Map<PlayerId,String> playernames = new EnumMap<>(PlayerId.class);
        playernames.put(PlayerId.PLAYER_1, "Marvin");
        playernames.put(PlayerId.PLAYER_2, "Shangeeth");

        Game.play(players, playernames, SortedBag.of(ChMap.tickets()), new Random());
    }

    private static final class TestPlayer implements Player {
        private static final int TURN_LIMIT = 1000;

        private final Random rng;
        // Toutes les routes de la carte
        private final List<Route> allRoutes;

        private int turnCounter;
        private PlayerState ownState;
        private PublicGameState gameState;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;
        private SortedBag<Ticket> initialTickets;//home

        public TestPlayer(long randomSeed, List<Route> allRoutes) {
            this.rng = new Random(randomSeed);
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;
        }


        @Override
        public void initNbrOfPlayer(boolean is3Player) {

        }

        @Override
        public int endMenu(String name) {
            return 0;
        }

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
            System.out.println(ownId.toString()+" = "+ playerNames.get(ownId));
            System.out.println(ownId.next().toString()+" = "+ playerNames.get(ownId.next()));
        }

        @Override
        public void receiveInfo(String info) {
            System.out.println(info);

        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            System.out.println(tickets.size()+" billet distribué(s)" );
            initialTickets = tickets;
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            return initialTickets;
        }

        @Override
        public TurnKind nextTurn() {
            turnCounter += 1;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");

            // Détermine les routes dont ce joueur peut s'emparer
            /**
             List<Route> claimableRoutes = allRoutes.stream()
             .filter(r -> ownState.canClaimRoute(r))
             .collect(Collectors.toList());
             */
            List<Route> claimableRoutes = new ArrayList<>();
            for(Route route : allRoutes){
                if(ownState.canClaimRoute(route)){
                    if(!gameState.claimedRoutes().contains(route)){
                        claimableRoutes.add(route);
                    }
                }
            }

            if (claimableRoutes.isEmpty()) {
                return TurnKind.DRAW_CARDS;
            } else {
                int routeIndex = rng.nextInt(claimableRoutes.size());
                Route route = claimableRoutes.get(routeIndex);
                List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                routeToClaim = route;
                initialClaimCards = cards.get(0);
                return TurnKind.CLAIM_ROUTE;
            }
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            int r = rng.nextInt(options.size());
            List <Ticket> tickets = options.toList();
            Collections.shuffle(tickets);
            return SortedBag.of(tickets.subList(0,r));
        }

        @Override
        public int drawSlot() {
            return rng.nextInt(4);
        }

        @Override
        public Route claimedRoute() {
            return routeToClaim;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return initialClaimCards;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return options.get(0);
        }
    }


}
