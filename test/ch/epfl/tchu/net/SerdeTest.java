package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Card;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerdeTest {

    @Test
    void IntegerTest(){
        assertEquals("2021", Serdes.INTEGER_SERDE.serialize(2021));
        assertEquals(2021, Serdes.INTEGER_SERDE.deserialize("2021"));
    }
    @Test
    void CardTest(){
        assertEquals("1", Serdes.CARD_SERDE.serialize(Card.VIOLET));
    }
    @Test
    void StringTest(){
        assertEquals("Q2hhcmxlcw==", Serdes.STRING_SERDE.serialize("Charles"));
        assertEquals("Charles", Serdes.STRING_SERDE.deserialize("Q2hhcmxlcw=="));
    }
}
