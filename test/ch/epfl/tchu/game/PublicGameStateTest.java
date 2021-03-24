package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PublicGameStateTest {
    SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0,15));
    Map<PlayerId, PlayerState> map = new EnumMap<>(PlayerId.class);
    GameState gameState = GameState.initial(tickets, new Random());
    PublicGameState publicGameState;


    @Test
    void ticketsCount() {
        assertEquals(15, ((PublicGameState) gameState).ticketsCount());
    }

    @Test
    void canDrawTickets() {
        publicGameState = gameState.withoutTopTickets(12);
        assertTrue(publicGameState.canDrawTickets());
        publicGameState = gameState.withoutTopTickets(15);
        assertTrue(!publicGameState.canDrawTickets());

    }


    @Test
    void canDrawCards() {
        int tkt = gameState.cardState().deckSize()-2;
        for(int i = 0; i< tkt;i++){
            gameState = gameState.withoutTopCard();
        }
        //System.out.println(gameState.cardState().deckSize());
        //System.out.println(gameState.cardState().discardsSize());
        gameState = gameState.withMoreDiscardedCards(SortedBag.of(2,Card.RED));
        publicGameState = gameState;
        assertTrue(!publicGameState.canDrawCards());
        gameState = gameState.withMoreDiscardedCards(SortedBag.of(1,Card.RED));
        publicGameState = gameState;
        assertTrue(publicGameState.canDrawCards());
        gameState = gameState.withMoreDiscardedCards(SortedBag.of(5,Card.RED));;
        publicGameState = gameState;
        assertTrue(publicGameState.canDrawCards());

    }

    @Test
    void claimedRoutes() {
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0,15));
        GameState state = GameState.initial(tickets, new Random());
        var newState = state.withClaimedRoute(ChMap.routes().get(0), SortedBag.of(1, Card.BLUE));
        var newState2 = newState.withClaimedRoute(ChMap.routes().get(1), SortedBag.of());
        assertEquals(2, newState2.claimedRoutes().size());
    }

}