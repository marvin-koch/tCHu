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
    Info info = new Info("Marvin");
    @Test
    void claimedRouteTest(){
        SortedBag.Builder<Card> build = new SortedBag.Builder<>();
        build.add(2, Card.of(Color.GREEN));
        build.add(1, Card.LOCOMOTIVE);
        SortedBag<Card> bag = build.build();

        assertEquals(String.format(StringsFr.CLAIMED_ROUTE,"Marvin","Martigny – Sion","2 vertes et 1 locomotive"), info.claimedRoute(ChMap.routes().get(64),bag));
    }


    @Test
    void drewAdditionalCardsTest(){
        SortedBag.Builder<Card> build = new SortedBag.Builder<>();
        build.add(1, Card.of(Color.RED));
        build.add(2, Card.LOCOMOTIVE);
        build.add(3, Card.WHITE);
        SortedBag<Card> bag = build.build();
        assertEquals(String.format(StringsFr.ADDITIONAL_CARDS_ARE, "1 rouge, 3 blanches et 2 locomotives") + String.format(StringsFr.SOME_ADDITIONAL_COST, 2, StringsFr.plural(2)), info.drewAdditionalCards(bag, 2));
    }

    @Test
    void drewAdditionalCarddsTest2(){
        SortedBag.Builder<Card> build = new SortedBag.Builder<>();
        build.add(1, Card.of(Color.RED));
        SortedBag<Card> bag = build.build();
        assertEquals(String.format(StringsFr.ADDITIONAL_CARDS_ARE, "1 rouge") + String.format(StringsFr.SOME_ADDITIONAL_COST, 2, StringsFr.plural(2)), info.drewAdditionalCards(bag, 2));
    }

    @Test
    void didNotClaimRouteTest(){
        assertEquals(String.format(StringsFr.DID_NOT_CLAIM_ROUTE, "Marvin", "Martigny – Sion"), info.didNotClaimRoute(ChMap.routes().get(64)));
    }


}
