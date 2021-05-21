package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * La classe ServerMain contient le programme principal du serveur tCHu.
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class ServerMain extends Application {
    public static Boolean is3Player = true;
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
        Socket socket = serverSocket.accept();
        Socket socket2 = serverSocket.accept();

        /*
        StringProperty text1Property = new SimpleStringProperty();
        StringProperty text2Property = new SimpleStringProperty();

        Stage menu = new Stage();
        menu.setTitle("Bienvenue dans tCHu!");
        menu.setOnCloseRequest(Event::consume);

        TextField player1Text = new TextField();
        TextField player2Text = new TextField();
        Bindings.bindBidirectional(player1Text.textProperty(), text1Property);
        Bindings.bindBidirectional(player2Text.textProperty(), text2Property);

        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(5.5);
        pane.setVgap(5.5);
        pane.add(new Label("Joueur 1:"), 0, 0);
        pane.add(player1Text, 1, 0);
        pane.add(new Label("Joueur 2:"), 0, 1);
        pane.add(player2Text, 1, 1);
        Button playButton = new Button("Play");
        pane.add(playButton, 1, 3);
        GridPane.setHalignment(playButton, HPos.RIGHT);

        Scene scene = new Scene(pane );
        menu.setScene(scene);
        menu.show();

         */
        Button playButton = new Button("Jouer");
        GameMenu.createMenuStage("Joueur 1", "Joueur 2", playButton);

        playButton.setOnAction(e -> {
            playButton.disableProperty().set(true);
            Thread thread = new Thread(() ->{
                String player1Name = GameMenu.getText1() == null ? "Ada" : GameMenu.getText1();
                String player2Name = GameMenu.getText2() == null ? "Charles" : GameMenu.getText2();

                Map<PlayerId,String> playerNames = new EnumMap<>(PlayerId.class);
                Map<PlayerId,Player> playerIdPlayerMap = new EnumMap<>(PlayerId.class);

                playerNames.put(PlayerId.PLAYER_1,player1Name);
                playerNames.put(PlayerId.PLAYER_2, player2Name);
                playerIdPlayerMap.put(PlayerId.PLAYER_1,new GraphicalPlayerAdapter());
                playerIdPlayerMap.put(PlayerId.PLAYER_2,new RemotePlayerProxy(socket));
                Game.play(playerIdPlayerMap, playerNames, SortedBag.of(ChMap.tickets()), new Random());
                //menu.hide();
            });
            thread.start();
            //((Node)(e.getSource())).getScene().getWindow().hide();
        });
    }
}
