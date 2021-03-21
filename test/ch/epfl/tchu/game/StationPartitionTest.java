package ch.epfl.tchu.game;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class StationPartitionTest {
    @Test
    /*
    CA Marche!
     */
    void testExample(){
        StationPartition.Builder builder = new StationPartition.Builder(33+1);
        builder.connect(ChMap.routes().get(44).station1(), ChMap.routes().get(44).station2());
        builder.connect(ChMap.routes().get(13).station1(), ChMap.routes().get(13).station2());
        builder.connect(ChMap.routes().get(65).station1(), ChMap.routes().get(65).station2());
        builder.connect(ChMap.routes().get(15).station1(), ChMap.routes().get(15).station2());
        builder.connect(ChMap.routes().get(67).station1(), ChMap.routes().get(67).station2());
        builder.connect(ChMap.routes().get(77).station1(), ChMap.routes().get(77).station2());
        builder.connect(ChMap.routes().get(61).station1(), ChMap.routes().get(61).station2());
        builder.connect(ChMap.routes().get(79).station1(), ChMap.routes().get(79).station2());
        builder.connect(ChMap.routes().get(62).station1(), ChMap.routes().get(62).station2());
        StationPartition p = builder.build();
        /*
        for(int i = 0; i< p.gares.length; ++i){
            System.out.println(i + "  " + p.gares[i]);
        }

         */
        assertTrue(p.connected(ChMap.routes().get(62).station2(),ChMap.routes().get(62).station1()));
        assertFalse(p.connected(ChMap.routes().get(44).station1(), ChMap.routes().get(79).station2()));
    }

    @Test
    void testConnectedOutofBounds(){
        StationPartition.Builder a = new StationPartition.Builder(10);
        StationPartition b = a.build();
        assertTrue(b.connected(ChMap.routes().get(62).station1(), ChMap.routes().get(62).station1()));
        assertFalse(b.connected(ChMap.routes().get(62).station2(),ChMap.routes().get(62).station1()));
    }
    @Test
    void connected() {
        StationPartition.Builder builder = new StationPartition.Builder(ChMap.stations().size());
        builder.connect(ChMap.stations().get(7), ChMap.stations().get(15));
        builder.connect(ChMap.stations().get(9), ChMap.stations().get(12));
        StationPartition p1 = builder.build();
        assertTrue(p1.connected(ChMap.stations().get(7), ChMap.stations().get(15)));
        assertTrue(p1.connected(ChMap.stations().get(9), ChMap.stations().get(12)));
        assertFalse(p1.connected(ChMap.stations().get(8), ChMap.stations().get(4)));
        assertFalse(p1.connected(ChMap.stations().get(8), ChMap.stations().get(7)));
    }
}
