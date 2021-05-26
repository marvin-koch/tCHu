package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * La classe ServerMain contient le programme principal du serveur tCHu.
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class ServerMain extends Application {
    //public static boolean is3Players  = true;//attention ne pas changer

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
        ServerSocket serverSocket = new ServerSocket(5108);
        List<String> choices = new ArrayList<>();
        choices.add("2");
        choices.add("3");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("3", choices);
        ImageView image = new ImageView("train-chelou.png");
        image.setPreserveRatio(true);
        image.setFitHeight(80);
        //image.setFitWidth(100);
        dialog.setGraphic(image);
        dialog.setTitle("tChu!");
        dialog.setHeaderText("Bienvenue dans tChu!");
        dialog.setContentText("Choisisez le nombre de joueur  :");
// Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(letter -> {
                PlayerId.initNbrPlayers((Integer.parseInt(letter) != 2));
        });
        //TODO 2 sockets


        Socket socket = serverSocket.accept();
        Map<PlayerId,String> playerNames = new HashMap<>();
        Map<PlayerId,Player> playerIdPlayerMap = new HashMap<>();

        if(PlayerId.is3Players()){
            Socket socket2 = serverSocket.accept();
            Button playButton = new Button("Jouer");
            Stage menu = GameMenu.createMenuStageWithThreeEntry("Joueur 1", "Joueur 2","Joueur 3", playButton);

            playButton.setOnAction(e -> {
                playButton.disableProperty().set(true);
                menu.setOnCloseRequest(Event-> menu.hide());
                Thread thread = new Thread(() ->{
                    setUpGameMenu(socket, playerNames, playerIdPlayerMap);
                    String player3Name = GameMenu.getText3() == null ? "Zora" : GameMenu.getText3();;
                    playerNames.put(PlayerId.PLAYER_3, player3Name);
                    playerIdPlayerMap.put(PlayerId.PLAYER_3, new RemotePlayerProxy(socket2));
                    Game.play(playerIdPlayerMap, playerNames, SortedBag.of(ChMap.tickets()), new Random(),PlayerId.is3Players());
                });
                thread.start();
            });
        }else {
            Button playButton = new Button("Jouer");

            Stage menu = GameMenu.createMenuStageWithTwoEntry("Joueur 1", "Joueur 2", playButton);


            playButton.setOnAction(e -> {
                menu.setOnCloseRequest(Event-> menu.hide());
                playButton.disableProperty().set(true);
                Thread thread = new Thread(() ->{
                    setUpGameMenu(socket, playerNames, playerIdPlayerMap);
                    Game.play(playerIdPlayerMap, playerNames, SortedBag.of(ChMap.tickets()), new Random(),PlayerId.is3Players());
                });
                thread.start();
            });
        }

    }
    private static void setUpGameMenu(Socket socket, Map<PlayerId, String> playerNames, Map<PlayerId, Player> playerMap){
        String player1Name = GameMenu.getText1() == null ? "Ada" : GameMenu.getText1();
        String player2Name = GameMenu.getText2() == null ? "Charles" : GameMenu.getText2();
        playerNames.put(PlayerId.PLAYER_1,player1Name);
        playerNames.put(PlayerId.PLAYER_2, player2Name);
        playerMap.put(PlayerId.PLAYER_1,new GraphicalPlayerAdapter());
        playerMap.put(PlayerId.PLAYER_2,new RemotePlayerProxy(socket));
    }
}
