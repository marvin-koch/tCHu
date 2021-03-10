package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class PublicCardStateTest {
    @Test
    void testConstructeurAndOther(){
        List<Card> cards5= new ArrayList<>();
        cards5.add(Card.BLACK);
        cards5.add(Card.BLACK);
        cards5.add(Card.LOCOMOTIVE);
        cards5.add(Card.BLACK);
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(cards5,5,6);
        });
        cards5.add(Card.BLUE);
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(cards5,-5,6);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(cards5,5,-6);
        });
        PublicCardState cardState =  new PublicCardState(cards5,0,6);
        assertEquals(11,cardState.totalSize());
        assertEquals(Card.LOCOMOTIVE,cardState.faceUpCard(2));
        assertThrows(IndexOutOfBoundsException.class, () -> {
            cardState.faceUpCard(5);
        });
        assertEquals(true,cardState.isDeckEmpty());

    }


}
