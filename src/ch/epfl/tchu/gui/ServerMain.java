package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
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
     * Méthode main qui appelle launch
     * @param args arguments
     */
    public static void main(String[] args){
        launch(args);
    }
    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     *
     *
     * La méthode start du serveur ignore son argument primaryStage et se charge de démarrer le serveur en:
     * - analysant les arguments passés au programme afin de déterminer les noms des deux joueurs,
     * - attendant une connexion de la part du client sur le port 5108,
     * - créant les deux joueurs, le premier étant un joueur graphique, le second un mandataire du joueur distant qui se trouve sur le client,
     * - démarrant le fil d'exécution gérant la partie, qui ne fait rien d'autre qu'exécuter la méthode play de Game.
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws Exception if something goes wrong
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        List<String> parameters = getParameters().getRaw();
        ServerSocket serverSocket = new ServerSocket(5108);
        Socket socket = serverSocket.accept();
        String player1Name;
        String player2Name;
        if(parameters.isEmpty()){
            player1Name = "Ada";
            player2Name = "Charles";
        }else if(parameters.size() == 1) {
            player1Name = parameters.get(0);
            player2Name = "Anonymous";
        }else {
            player1Name = parameters.get(0);
            player2Name = parameters.get(1);
        }
        //TODO Est-ce que la fenetre s'appele Player 1

        Map<PlayerId,String> playerNames = new EnumMap<>(PlayerId.class);
        Map<PlayerId,Player> playerIdPlayerMap = new EnumMap<>(PlayerId.class);
        playerNames.put(PlayerId.PLAYER_1,player1Name);
        playerNames.put(PlayerId.PLAYER_2, player2Name);

        playerIdPlayerMap.put(PlayerId.PLAYER_1,new GraphicalPlayerAdapter());
        playerIdPlayerMap.put(PlayerId.PLAYER_2,new RemotePlayerProxy(socket));

        Thread thread = new Thread(() ->
                Game.play(playerIdPlayerMap,playerNames, SortedBag.of(ChMap.tickets()), new Random()));
        thread.start();
    }
}
