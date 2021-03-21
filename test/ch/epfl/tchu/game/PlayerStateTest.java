package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerStateTest {
    static SortedBag<Ticket> t = SortedBag.of(3, ChMap.tickets().get(0), 4, ChMap.tickets().get(2));
    static SortedBag<Card> s4 = SortedBag.of(2, Card.VIOLET, 3, Card.LOCOMOTIVE);
    static PlayerState p4 = new PlayerState(t, s4, List.of());
    @Test
    void initialTest(){
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerState playerState = PlayerState.initial(SortedBag.of(3,Card.BLACK));
        });
    }

    @Test
    void withAddedCardsTest(){
        PlayerState playerState = PlayerState.initial(SortedBag.of(4,Card.BLACK));
        playerState = playerState.withAddedCard(Card.BLUE);
        assertEquals(5,playerState.cards().size());
        playerState = playerState.withAddedCards(SortedBag.of(3,Card.ORANGE));
        assertEquals(8,playerState.cards().size());
    }
    @Test
    void canClaimTest(){
        PlayerState playerState = PlayerState.initial(SortedBag.of(4,Card.GREEN));
        assertEquals(true, playerState.canClaimRoute(ChMap.routes().get(0)));
        playerState = PlayerState.initial(SortedBag.of(2,Card.BLACK,2,Card.ORANGE));
        assertEquals(false, playerState.canClaimRoute(ChMap.routes().get(1)));
        assertEquals(false, playerState.canClaimRoute(ChMap.routes().get(0)));


    }

    @Test
    void ticketAndFinalPointsTest(){
        PlayerState playerstate = new PlayerState(SortedBag.of(ChMap.tickets().get(14)), SortedBag.of(), List.of(ChMap.routes().get(44), ChMap.routes().get(13), ChMap.routes().get(65)
        , ChMap.routes().get(15), ChMap.routes().get(15), ChMap.routes().get(67), ChMap.routes().get(77), ChMap.routes().get(61), ChMap.routes().get(79), ChMap.routes().get(62)));

        assertEquals(7, playerstate.ticketPoints());
        int expectPoint = 0;
        for (Route route : playerstate.routes()) {
            expectPoint += route.claimPoints();
        }
        assertEquals(7+ expectPoint, playerstate.finalPoints());
    }

    @Test
    void possibleClaimCardsTest(){
        PlayerState playerState = PlayerState.initial(SortedBag.of(2, Card.GREEN, 2, Card.LOCOMOTIVE));
        List<SortedBag<Card>> expected = List.of(SortedBag.of(2, Card.GREEN), SortedBag.of(1, Card.GREEN, 1, Card.LOCOMOTIVE), SortedBag.of(2, Card.LOCOMOTIVE));

        assertEquals(expected, playerState.possibleClaimCards(ChMap.routes().get(41)));
    }
    @Test
    void possibleAdditionalCards() {
        assertEquals(List.of(SortedBag.of(Card.LOCOMOTIVE)), p4.possibleAdditionalCards(1, SortedBag.of(Card.LOCOMOTIVE), SortedBag.of(1, Card.LOCOMOTIVE, 2, Card.BLACK)));
        assertEquals(List.of(SortedBag.of(Card.VIOLET), SortedBag.of(Card.LOCOMOTIVE)), p4.possibleAdditionalCards(1, SortedBag.of(Card.VIOLET), SortedBag.of(1, Card.LOCOMOTIVE, 2, Card.BLACK)));
        assertEquals(List.of(SortedBag.of(Card.VIOLET), SortedBag.of(Card.LOCOMOTIVE)), p4.possibleAdditionalCards(1, SortedBag.of(Card.VIOLET), SortedBag.of(1, Card.VIOLET, 2, Card.BLACK)));

        assertEquals(List.of(SortedBag.of(2, Card.LOCOMOTIVE)), p4.possibleAdditionalCards(2, SortedBag.of(Card.LOCOMOTIVE), SortedBag.of(2, Card.LOCOMOTIVE, 1, Card.BLACK)));
        assertEquals(List.of(SortedBag.of(1, Card.VIOLET, 1, Card.LOCOMOTIVE), SortedBag.of(2, Card.LOCOMOTIVE)), p4.possibleAdditionalCards(2, SortedBag.of(Card.VIOLET), SortedBag.of(2, Card.LOCOMOTIVE, 1, Card.BLACK)));
        assertEquals(List.of(SortedBag.of(2, Card.LOCOMOTIVE)), p4.possibleAdditionalCards(2, SortedBag.of(2, Card.VIOLET), SortedBag.of(2, Card.VIOLET, 1, Card.BLACK)));

        assertEquals(List.of(), p4.possibleAdditionalCards(3, SortedBag.of(2, Card.LOCOMOTIVE), SortedBag.of(3, Card.LOCOMOTIVE)));
    }

}
