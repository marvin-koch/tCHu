package ch.epfl.tchu.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//TODO commenter
public final class GameMenu {
    private static final StringProperty TEXT1_PROPERTY = new SimpleStringProperty();
    private static final StringProperty TEXT2_PROPERTY = new SimpleStringProperty();
    private static final StringProperty TEXT3_PROPERTY = new SimpleStringProperty();

    private GameMenu(){}

    public static void createMenuStage(String text1, String text2, Button button){
        Stage menu = new Stage();
        menu.setTitle("Bienvenue dans tCHu!");
        //menu.setOnCloseRequest(Event::consume);

        TextField player1Text = new TextField();
        TextField player2Text = new TextField();
        Bindings.bindBidirectional(player1Text.textProperty(), TEXT1_PROPERTY);
        Bindings.bindBidirectional(player2Text.textProperty(), TEXT2_PROPERTY);

        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(5.5);
        pane.setVgap(5.5);
        pane.add(new Label(text1), 0, 0);
        pane.add(player1Text, 1, 0);
        pane.add(new Label(text2), 0, 1);
        pane.add(player2Text, 1, 1);
        pane.add(button, 1, 3);
        GridPane.setHalignment(button, HPos.RIGHT);

        Scene scene = new Scene(pane, 300, 200);
        menu.setScene(scene);
        menu.show();

    }
    public static void createMenuStageFor3Player(String text1, String text2,String text3, Button button){
        Stage menu = new Stage();
        menu.setTitle("Bienvenue dans tCHu!");
        //menu.setOnCloseRequest(Event::consume);

        TextField player1Text = new TextField();
        TextField player2Text = new TextField();
        TextField player3Text = new TextField();
        Bindings.bindBidirectional(player1Text.textProperty(), TEXT1_PROPERTY);
        Bindings.bindBidirectional(player2Text.textProperty(), TEXT2_PROPERTY);
        Bindings.bindBidirectional(player3Text.textProperty(), TEXT3_PROPERTY);

        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(5.5);
        pane.setVgap(5.5);
        pane.add(new Label(text1), 0, 0);
        pane.add(player1Text, 1, 0);
        pane.add(new Label(text2), 0, 1);
        pane.add(player2Text, 1, 1);
        pane.add(new Label(text3), 0, 2);
        pane.add(player3Text, 1, 2);
        pane.add(button, 1, 3);
        GridPane.setHalignment(button, HPos.RIGHT);

        Scene scene = new Scene(pane, 300, 200);
        menu.setScene(scene);
        menu.show();

    }


    public static String getText1(){
        return TEXT1_PROPERTY.get();
    }

    public static String getText2(){
        return TEXT2_PROPERTY.get();
    }

    public static String getText3(){
        return TEXT3_PROPERTY.get();
    }

}
