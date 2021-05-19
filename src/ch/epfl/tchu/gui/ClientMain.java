package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

/**
 * La classe ClientMain contient le programme principal du client tCHu.
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class ClientMain extends Application {
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
     * la méthode start se charge de démarrer le client en:
     * - analysant les arguments passés au programme afin de déterminer le nom de l'hôte et le numéro de port du serveur,
     * - créant un client distant—une instance de RemotePlayerClient — associé à un joueur graphique—une instance de GraphicalPlayerAdapter,
     * - démarrant le fil gérant l'accès au réseau, qui ne fait rien d'autre qu'exécuter la méthode run du client créé précédemment.
     *
     * la méthode n'utilise pas son argument primaryStage.
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws Exception if something goes wrong
     */
    @Override
    public void start( Stage primaryStage) {
        List<String> list = getParameters().getRaw();
        RemotePlayerClient remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(),list.get(0), Integer.parseInt(list.get(1)));
        Thread thread = new Thread(remotePlayerClient::run);
        thread.start();
    }
}
