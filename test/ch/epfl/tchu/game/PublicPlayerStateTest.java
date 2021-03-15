package ch.epfl.tchu.game;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class PublicPlayerStateTest {
    @Test
    void ConstructorthrowIA(){
        assertThrows(IllegalArgumentException.class, () ->{
            new PublicPlayerState(-1, -2, new ArrayList<>());
        });

        assertThrows(IllegalArgumentException.class, () ->{
            new PublicPlayerState(-1, 2, new ArrayList<>());
        });
        assertThrows(IllegalArgumentException.class, () ->{
            new PublicPlayerState(5, -2, new ArrayList<>());
        });
    }
}
