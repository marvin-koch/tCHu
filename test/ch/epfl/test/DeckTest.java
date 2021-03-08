package ch.epfl.test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Deck;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {
    SortedBag bag = SortedBag.of(3, Card.of(Color.BLACK), 2,Card.of(Color.WHITE));
    @Test
    void testWithoutTopCard(){


    }
    //On peut rien faire?
}
