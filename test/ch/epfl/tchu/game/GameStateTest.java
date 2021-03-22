package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

}
