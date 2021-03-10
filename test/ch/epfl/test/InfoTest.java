package ch.epfl.test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.gui.Info;
import ch.epfl.tchu.gui.StringsFr;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InfoTest {
    @Test
    void claimedRouteTest(){
        SortedBag.Builder<Card> build = new SortedBag.Builder<>();
        build.add(2, Card.of(Color.GREEN));
        build.add(1, Card.LOCOMOTIVE);
        SortedBag<Card> bag = build.build();
        Info info = new Info("Marvin");
        assertEquals(String.format(StringsFr.CLAIMED_ROUTE,"Marvin","Martigny – Sion","2 vertes et 1 locomotive"), info.claimedRoute(ChMap.routes().get(64),bag));
    }


}
