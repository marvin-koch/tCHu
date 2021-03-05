package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrailTest {

    private static List<Route> playerRoutes = new ArrayList<>(List.of(ChMap.routes().get(66), ChMap.routes().get(65),
            ChMap.routes().get(18), ChMap.routes().get(19), ChMap.routes().get(16), ChMap.routes().get(14)));
    @Test
    void longestWorks(){
        assertEquals(13, Trail.longest(playerRoutes).length());
    }

    @Test
    void longestVide(){
       List<Route> emptyList = new ArrayList<>();
       assertEquals(0, Trail.longest(emptyList).length());
    }

    @Test
    void toStringworks(){
        String expected = "Lucerne - Berne - Neuchâtel - Soleure - Berne - Fribourg (13)";
        assertEquals(expected, Trail.longest(playerRoutes).toString());

    }

    @Test
    void toStringVide(){
        assertEquals("Empty Trail (0)", Trail.longest(new ArrayList<Route>()).toString());
    }
}
