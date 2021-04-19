package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public final class TestServer {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            Player playerProxy = new RemotePlayerProxy(socket);
            Player LocalPlayer = new TestPlayer2(21,ChMap.routes());
            var playerNames = Map.of(PlayerId.PLAYER_1, "Ada",
                    PlayerId.PLAYER_2, "Charles");
            /*
            playerProxy.initPlayers(PlayerId.PLAYER_1, playerNames);
            playerProxy.receiveInfo("salut");
            playerProxy.setInitialTicketChoice(SortedBag.of(1 ,ChMap.tickets().get(0), 1, ChMap.tickets().get(1)));
            playerProxy.chooseInitialTickets();
             */

            Map<PlayerId, Player> players = new EnumMap<PlayerId, Player>(PlayerId.class);
            players.put(PlayerId.PLAYER_1, playerProxy);
            players.put(PlayerId.PLAYER_2, LocalPlayer);
            Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random());
        }
        System.out.println("Server done!");
    }
}
