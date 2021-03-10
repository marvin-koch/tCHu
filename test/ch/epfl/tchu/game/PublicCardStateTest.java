package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PublicCardStateTest {
    @Test
    void testConstructeur(){
        List<Card> cards5= new ArrayList<>();
        cards5.add(Card.BLACK);
        cards5.add(Card.BLACK);
        cards5.add(Card.LOCOMOTIVE);
        cards5.add(Card.BLACK);
        cards5.add(Card.BLUE);


    }


}
