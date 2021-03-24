package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {
    SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(0,15));
    GameState state = GameState.initial(tickets, new Random());
    @Test
    void initialTest(){
        assertEquals(4, state.playerState(PlayerId.PLAYER_1).cardCount());
        assertEquals(4, state.playerState(PlayerId.PLAYER_2).cardCount());
        assertEquals(Constants.TOTAL_CARDS_COUNT - 13, state.cardState().deckSize());
    }

    @Test
    void testTopTickets(){
        assertEquals(15- 5, state.withoutTopTickets(5).ticketsCount());

    }

    @Test
    void testWithoutTopCard(){
        assertEquals(Constants.TOTAL_CARDS_COUNT - 14, state.withoutTopCard().cardState().deckSize());
    }
    @Test
    void testWithMoreDiscardedCards(){
        assertEquals(5, state.withMoreDiscardedCards(SortedBag.of(3, Card.BLUE, 2, Card.RED)).cardState().discardsSize());
    }
    @Test
    void testwithInitiallyChosenTickets(){
       var newState = state.withInitiallyChosenTickets(PlayerId.PLAYER_1, SortedBag.of(ChMap.tickets().subList(0,3)));
       assertEquals(3, newState.playerState(PlayerId.PLAYER_1).ticketCount());
    }

    @Test
    void testwithChosenAdditionalTickets(){
        var newState = state.withChosenAdditionalTickets(SortedBag.of(ChMap.tickets().subList(0,5)), SortedBag.of(ChMap.tickets().subList(0,2)));
        assertEquals(2, newState.currentPlayerState().ticketCount());
        assertEquals(10, newState.ticketsCount());

    }
    @Test
    void testWithDrawnFaceUpCard(){
        var card = state.topCard();
        var newState = state.withDrawnFaceUpCard(4);
        assertEquals(card, newState.cardState().faceUpCard(4));
        assertEquals(5, newState.currentPlayerState().cardCount());
        assertEquals(Constants.TOTAL_CARDS_COUNT - 13 - 1, newState.cardState().deckSize());
    }

    @Test
    void testBlindlyDrawnCards(){
        var newState = state.withBlindlyDrawnCard();
        assertEquals(Constants.TOTAL_CARDS_COUNT - 13 -1, newState.cardState().deckSize());
        assertEquals(5, newState.currentPlayerState().cardCount());
    }
/*
    @Test
    void testWithClaimedRoute(){
        var newState = state.withClaimedRoute(ChMap.routes().get(0), SortedBag.of(1, Card.BLUE));
        assertEquals(3, newState.currentPlayerState().cardCount());
        assertEquals(List.of(ChMap.routes().get(0)), newState.currentPlayerState().routes());
    }

    @Test
    void testlastTurnBegins(){
        System.out.println(state.currentPlayerState().cards());
        assertFalse(state.lastTurnBegins());
        var newState = state.withClaimedRoute(ChMap.routes().get(0),SortedBag.of( 2, Card.YELLOW));
        assertTrue(newState.lastTurnBegins());
    }*/





}
