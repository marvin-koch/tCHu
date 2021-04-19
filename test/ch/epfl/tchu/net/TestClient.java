package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import javax.print.DocFlavor;
import java.util.*;

public final class TestClient {
    public static void main(String[] args) {
        System.out.println("Starting client!");
        RemotePlayerClient playerClient =
                new RemotePlayerClient(new TestPlayer2(21, ChMap.routes()),
                        "localhost",
                        5108);
        playerClient.run();
        System.out.println("Client done!");
    }

}

