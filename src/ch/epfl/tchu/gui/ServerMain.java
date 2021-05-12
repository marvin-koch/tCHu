package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * La classe ServerMain contient le programme principal du serveur tCHu.
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class ServerMain extends Application {

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws Exception if something goes wrong
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> list = getParameters().getRaw();
        ServerSocket serverSocket = new ServerSocket(5108);
        Socket socket = serverSocket.accept();
        String player1Name;
        String player2Name;
        if(list.isEmpty()){
            player1Name = "Ada";
            player2Name = "Charles";
        }else{
            player1Name = list.get(0);
            player2Name = list.get(1);
        }

        Map<PlayerId,String> playerNames = new EnumMap<>(PlayerId.class);
        Map<PlayerId,Player> playerIdPlayerMap = new EnumMap<>(PlayerId.class);
        playerNames.put(PlayerId.PLAYER_1,player1Name);
        playerNames.put(PlayerId.PLAYER_2, player2Name);

        playerIdPlayerMap.put(PlayerId.PLAYER_1,new GraphicalPlayerAdapter());
        playerIdPlayerMap.put(PlayerId.PLAYER_2,new RemotePlayerProxy(socket));
        //playerIdPlayerMap.get(PlayerId.PLAYER_2).initPlayers(PlayerId.PLAYER_2,playerNames);
        //playerIdPlayerMap.get(PlayerId.PLAYER_1).initPlayers(PlayerId.PLAYER_1,playerNames);
        //.forEach((id, player) -> player.initPlayers(id, playerNames));
        Thread thread = new Thread(() ->
                Game.play(playerIdPlayerMap,playerNames, SortedBag.of(ChMap.tickets()), new Random()));
        thread.start();


    }
}
