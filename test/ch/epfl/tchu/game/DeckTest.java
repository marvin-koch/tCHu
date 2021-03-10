package ch.epfl.test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Deck;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Random;

public class DeckTest {
    SortedBag bag = SortedBag.of(3, Card.of(Color.BLACK), 2,Card.of(Color.WHITE));
    Random random = new Random();
    Deck<Card> deck = Deck.of(bag, random);
    @Test
    void testtopCard(){
        assertEquals(Card.BLACK, deck.topCard());
    }

    @Test
    void testtopCards(){
        SortedBag<Card> cards = SortedBag.of(2,Card.BLACK);
        assertEquals(cards, deck.topCards(2));
    }

    @Test
    void testwithoutTopCard(){
        SortedBag<Card> cards = SortedBag.of(2, Card.BLACK, 2, Card.WHITE);
        assertEquals(cards, deck.withoutTopCard().topCards(4));
    }

    @Test
    void testwithoutTopCards(){
        SortedBag<Card> cards = SortedBag.of(2, Card.WHITE);
        assertEquals(cards, deck.withoutTopCards(3).topCards(2));
    }

    @Test
    void withoutTopCardsthrowsIA(){
       assertThrows(IllegalArgumentException.class, () ->{
           deck.withoutTopCards(7);
       });
        assertThrows(IllegalArgumentException.class, () ->{
            deck.withoutTopCards(-1);
        });
    }


}
