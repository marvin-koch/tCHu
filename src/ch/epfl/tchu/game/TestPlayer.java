package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
final class TestPlayer implements Player {
    private static final int TURN_LIMIT = 1000;

    private final Random rng;
    // Toutes les routes de la carte
    private final List<Route> allRoutes;

    private int turnCounter;
    private PlayerState ownState;
    private GameState gameState;

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
        this.gameState = gameState;
        this.ownState = ownState;
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        System.out.println(tickets.size()+" billet distribué" );
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
            if(ownState.canClaimRoute(route) ){
               claimableRoutes.add(route);
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
        return options;
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