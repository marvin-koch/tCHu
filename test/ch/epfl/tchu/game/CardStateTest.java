package ch.epfl.tchu.game;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardStateTest {
    /**
    SortedBag<Card> bag = SortedBag.of(1, Card.BLACK, 4,Card.RED);
    List<Card> face = List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.RED, Card.WHITE);
    Deck<Card> deck = Deck.of(bag, new Random());
    CardState state = new CardState(face, deck, SortedBag.of());

    @Test
    void OfTest(){
        SortedBag.Builder<Card> builder = new SortedBag.Builder();
        builder.add(Card.BLACK);
        builder.add(Card.BLUE);
        builder.add(Card.YELLOW);
        builder.add(Card.LOCOMOTIVE);
        SortedBag<Card> bag = builder.build();
        assertThrows(IllegalArgumentException.class, () -> {
            CardState cardState = CardState.of(Deck.of(bag,new Random()));
        });

    }

    // Lors des prochains test nous avons commenté shuffle dans le constructeur et mis le constructeur de Cardstate en public
    @Test
    void withDrawnTest(){
        Card topCard = state.topDeckCard();
        CardState newState = state.withDrawnFaceUpCard(1);
        assertEquals(topCard,newState.faceUpCard(1));
    }


    @Test
    void withoutTopDeckCardTest(){
        CardState newState = state.withoutTopDeckCard();
        assertEquals(Card.RED, newState.topDeckCard());
    }
    @Test
    void withDeckRecreatedFromDiscardsThrows(){
        CardState newState = state.withMoreDiscardedCards(bag);
        assertThrows(IllegalArgumentException.class, () -> {
            CardState newState2 = newState.withDeckRecreatedFromDiscards(new Random());
        });

    }
    @Test
    void withDeckRecrreatedFromDiscardsTest(){
        CardState state2 = new CardState(face,Deck.of(SortedBag.of(Card.BLACK), new Random()), bag);
        state2 = state2.withoutTopDeckCard();
        state2 = state2.withDeckRecreatedFromDiscards(new Random());
        assertEquals(5, state2.deckSize());
    }

    @Test
    void withMoreDiscardedCardsTest(){
        CardState newState = state.withMoreDiscardedCards(bag);
        assertEquals(5, newState.discardsSize());
    }
    */

}
