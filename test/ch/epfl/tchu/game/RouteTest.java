package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import javax.print.DocFlavor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RouteTest {
    Station gare1 = ChMap.routes().get(0).station1();
    Station gare2 = ChMap.routes().get(0).station2();
    Route route = ChMap.routes().get(0);
    Station gare = ChMap.routes().get(1).station1();


    @Test
    void testConstructeurRoute(){
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("test",gare1,gare1,3, Route.Level.OVERGROUND,Color.YELLOW);
        });
        assertThrows(NullPointerException.class, () -> {
            new Route("test",null,gare1,3, Route.Level.OVERGROUND,Color.BLACK);
        });
        assertThrows(NullPointerException.class, () -> {
            new Route("test",gare1,gare2,3, null,Color.BLACK);
        });
        assertThrows(NullPointerException.class, () -> {
            new Route(null,gare1,gare2,3, Route.Level.OVERGROUND,Color.BLACK);
        });
    }

    @Test
    void testConstructeurLength(){
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("test",ChMap.routes().get(0).station1(),ChMap.routes().get(0).station2(),ChMap.routes().get(0).length()+10, Route.Level.OVERGROUND,Color.BLACK);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("test",gare1,gare1,Constants.MAX_ROUTE_LENGTH+1, Route.Level.OVERGROUND,Color.YELLOW);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("test",gare1,gare1, Constants.MIN_ROUTE_LENGTH-1, Route.Level.OVERGROUND,Color.YELLOW);
        });
    }

    @Test
    void testColorNull(){
        Route route = new Route("test",ChMap.routes().get(0).station1(),ChMap.routes().get(0).station2(),ChMap.routes().get(0).length(), Route.Level.OVERGROUND,null);
        assertNull(route.color());
    }

    @Test
    void testStationOpposite(){
        assertThrows(IllegalArgumentException.class,() -> {
            route.stationOpposite(gare);
        });
        assertEquals(gare1,route.stationOpposite(gare2));
    }

    @Test
    void testPossibleClaimCardTUNNELNEUTRE2(){
        List<SortedBag<Card>> expected = new ArrayList<>();
        for(Card card : Card.CARS){
            expected.add(SortedBag.of(2, card));
        }
        for(Card card : Card.ALL){
            SortedBag.Builder<Card> builder2 = new SortedBag.Builder<>();
            builder2.add(card);
            builder2.add(Card.LOCOMOTIVE);
            expected.add(builder2.build());
        }
        assertEquals(expected, ChMap.routes().get(41).possibleClaimCards());
    }

    @Test
    void testPossibleClaimCardTUNNELBLACK2(){
        List<SortedBag<Card>> expected = new ArrayList<>();
        expected.add(SortedBag.of(2, Card.BLACK));
        expected.add(SortedBag.of(1, Card.BLACK, 1, Card.LOCOMOTIVE));
        expected.add(SortedBag.of(2, Card.LOCOMOTIVE));
        assertEquals(expected, ChMap.routes().get(38).possibleClaimCards());
    }

    @Test
    void testPossibleClaimCardOvergroundGREEN4(){
        List<SortedBag<Card>> expected = new ArrayList<>();
        expected.add(SortedBag.of(4, Card.GREEN));
        assertEquals(expected, ChMap.routes().get(65).possibleClaimCards());
    }



    @Test
    void testAdditionalClaimCardsCount(){
        SortedBag.Builder<Card> claimBuilder = new SortedBag.Builder<>();
        claimBuilder.add(Card.ORANGE);
        SortedBag.Builder<Card> drawnBuilder = new SortedBag.Builder<>();
        drawnBuilder.add(Card.GREEN);
        drawnBuilder.add(Card.LOCOMOTIVE);
        drawnBuilder.add(Card.ORANGE);
        SortedBag<Card> claimCards = claimBuilder.build();
        SortedBag<Card> drawnCards = drawnBuilder.build();
        assertEquals(2,route.additionalClaimCardsCount(claimCards,drawnCards));

    }
    @Test
    void testClaimpoint(){
     Route route1 = new Route("test",gare1,gare2,5, Route.Level.OVERGROUND,Color.YELLOW);
        assertEquals(10,route1.claimPoints());
    }


}
