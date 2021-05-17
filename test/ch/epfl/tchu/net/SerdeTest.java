package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.net.Serdes.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        assertEquals("", STRING_SERDE.serialize(""));
        assertEquals("", STRING_SERDE.deserialize(""));
    }

    @Test
    void PlayerIdTest(){
        assertEquals("0", PLAYER_ID_SERDE.serialize(PlayerId.PLAYER_1));
        assertEquals(PLAYER_ID_SERDE.deserialize("0"), PlayerId.PLAYER_1);
        assertEquals("", PLAYER_ID_SERDE.serialize(null));
        assertNull(PLAYER_ID_SERDE.deserialize(""));
    }

    @Test
    void RouteTest(){
        assertEquals("0", ROUTE_SERDE.serialize(ChMap.routes().get(0)));
        assertEquals(ChMap.routes().get(1), ROUTE_SERDE.deserialize("1"));
    }

    @Test
    void TicketTest(){
        /**
        assertEquals("39", TICKET_SERDE.serialize(ChMap.tickets().get(39)));
        assertEquals(ChMap.tickets().get(39), TICKET_SERDE.deserialize("39"));
        assertEquals("38", TICKET_SERDE.serialize(ChMap.tickets().get(38)));
        assertEquals(ChMap.tickets().get(38), TICKET_SERDE.deserialize("38"));
         **/
    }

    @Test
    void testListString(){
        assertEquals("Q2hhcmxlcw==,Q2hhcmxlcw==", LIST_STRING_SERDE.serialize(List.of("Charles", "Charles")));
        assertEquals(List.of("Charles", "Charles"), LIST_STRING_SERDE.deserialize("Q2hhcmxlcw==,Q2hhcmxlcw=="));
        assertEquals("", LIST_STRING_SERDE.serialize(List.of()));
        assertEquals(List.of(), LIST_STRING_SERDE.deserialize(""));
    }

    @Test
    void testListCard(){
        assertEquals("1,2,3", LIST_CARD_SERDE.serialize(List.of(Card.VIOLET, Card.BLUE, Card.GREEN)));
        assertEquals((List.of(Card.VIOLET, Card.BLUE, Card.GREEN)), LIST_CARD_SERDE.deserialize("1,2,3"));
        assertEquals("", LIST_CARD_SERDE.serialize(List.of()));
        assertEquals(List.of(), LIST_CARD_SERDE.deserialize(""));
    }

    @Test
    void testSortedBagTicket(){
        assertEquals("0,0,1,1", SORTEDBAG_TICKET_SERDE.serialize(SortedBag.of(2, ChMap.tickets().get(0), 2, ChMap.tickets().get(1))));
        assertEquals(SortedBag.of(2, ChMap.tickets().get(0), 2, ChMap.tickets().get(1)), SORTEDBAG_TICKET_SERDE.deserialize("0,0,1,1"));
        assertEquals("34,34,0,0", SORTEDBAG_TICKET_SERDE.serialize(SortedBag.of(2, ChMap.tickets().get(0), 2, ChMap.tickets().get(34))));
        assertEquals(SortedBag.of(2, ChMap.tickets().get(0), 2, ChMap.tickets().get(34)), SORTEDBAG_TICKET_SERDE.deserialize("34,34,0,0"));
        assertEquals("", SORTEDBAG_TICKET_SERDE.serialize(SortedBag.of()));
        assertEquals(SORTEDBAG_TICKET_SERDE.deserialize(""), SortedBag.of());
    }

    @Test
    void testPublicCardState(){
        var state = new PublicCardState(List.of(Card.BLACK, Card.BLACK,Card.BLUE, Card.BLACK, Card.BLACK), 50, 2);
        assertEquals("0,0,2,0,0;50;2", PUBLIC_CARD_STATE_SERDE.serialize(state));
        assertEquals(state.faceUpCards(),PUBLIC_CARD_STATE_SERDE.deserialize("0,0,2,0,0;50;2").faceUpCards());
    }

    @Test
    void testPublicPlayerState(){
        var state = new PublicPlayerState(10, 30, ChMap.routes().subList(0,2));
        assertEquals("10;30;0,1", PUBLIC_PLAYER_STATE_SERDE.serialize(state));
        assertEquals(state.routes(), PUBLIC_PLAYER_STATE_SERDE.deserialize("10;30;0,1").routes());
    }

    @Test
    void testPlayerState(){
        var state = new PlayerState(SortedBag.of(2, ChMap.tickets().get(1)), SortedBag.of(1, Card.BLACK, 1, Card.VIOLET), ChMap.routes().subList(0,2));
        assertEquals("1,1;0,1;0,1", PLAYER_STATE_SERDE.serialize(state));
        assertEquals(state.tickets(), PLAYER_STATE_SERDE.deserialize("1,1;0,1;0,1").tickets());
    }

    @Test
    void testPublicGameState(){
        List<Card> fu = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED);
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PlayerId.PLAYER_1, new PublicPlayerState(10, 11, rs1),
                PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs =
                new PublicGameState(40, cs, PlayerId.PLAYER_2, ps, null);
        assertEquals("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:", PUBLIC_GAME_STATE_SERDE.serialize(gs));
        assertEquals(gs.lastPlayer(), PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").lastPlayer());
    }

    @Test
    void LISTBAGCARD_Test() {
        List<SortedBag<Card>> lsbc = List.of(SortedBag.of( 1, Card.GREEN, 3, Card.ORANGE), SortedBag.of(5, Card.WHITE), SortedBag.of(2, Card.RED));
        String s = "3,5,5,5;7,7,7,7,7;6,6";

        assertEquals(s, LIST_SORTEDBAG_CARD_SERDE.serialize(lsbc));
        assertEquals(lsbc, LIST_SORTEDBAG_CARD_SERDE.deserialize(s));

        assertEquals("", LIST_SORTEDBAG_CARD_SERDE.serialize(List.of(SortedBag.of())));
        assertEquals(List.of(), LIST_SORTEDBAG_CARD_SERDE.deserialize(""));

        assertEquals("3,5;", LIST_SORTEDBAG_CARD_SERDE.serialize(List.of(SortedBag.of(1, Card.GREEN, 1, Card.ORANGE), SortedBag.of())));
        assertEquals(List.of(SortedBag.of(1, Card.GREEN, 1, Card.ORANGE), SortedBag.of()), LIST_SORTEDBAG_CARD_SERDE.deserialize("3,5;"));
    }




}
