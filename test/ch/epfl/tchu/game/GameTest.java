package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class GameTest {

    @Test
    void testGame(){
        Map<PlayerId,Player> players = new EnumMap<PlayerId, Player>(PlayerId.class);
        players.put(PlayerId.PLAYER_1, new TestPlayer(1, ChMap.routes()));
        players.put(PlayerId.PLAYER_2, new TestPlayer(2, ChMap.routes()));
        Map<PlayerId,String> playernames = new EnumMap<PlayerId, String>(PlayerId.class);
        playernames.put(PlayerId.PLAYER_1, "Marvin");
        playernames.put(PlayerId.PLAYER_2, "Shangeeth");

        Game.play(players, playernames, SortedBag.of(ChMap.tickets()), new Random());
    }


}
