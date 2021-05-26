package ch.epfl.tchu.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

//TODO commenter

/**
 * La classe GameMenu
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class GameMenu {
    private static final StringProperty TEXT1_PROPERTY = new SimpleStringProperty();
    private static final StringProperty TEXT2_PROPERTY = new SimpleStringProperty();
    private static final StringProperty TEXT3_PROPERTY = new SimpleStringProperty();

    private GameMenu(){}

    private static GridPane basePane( String text1, String text2, Button button){
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
        return  pane;
    }

    private static Stage baseStage(Pane pane){
        Stage menu = new Stage();
        menu.setTitle("Bienvenue dans tCHu!");
        menu.setOnCloseRequest(Event::consume);
        Scene scene = new Scene(pane, 300, 200);
        menu.setScene(scene);
        menu.show();
        return menu;
    }

    /**
     * Crée une fenêtre avec 2 entrées
     * @param text1 premier texte
     * @param text2 deuxième texte
     * @param button boutton
     * @return stage
     */
    public static Stage createMenuStageWithTwoEntry(String text1, String text2, Button button){
        GridPane pane = basePane(text1,text2,button);
        GridPane.setHalignment(button, HPos.RIGHT);
        return baseStage(pane);
    }

    /**
     * Crée une fenêtre avec 2 entrées
     * @param text1 premier texte
     * @param text2 deuxième texte
     * @param text3 troisième texte
     * @param button boutton
     * @return stage
     */
    public static Stage createMenuStageWithThreeEntry(String text1, String text2, String text3, Button button){

        TextField player3Text = new TextField();

        Bindings.bindBidirectional(player3Text.textProperty(), TEXT3_PROPERTY);

        GridPane pane = basePane(text1, text2, button);
        pane.add(new Label(text3), 0, 2);
        pane.add(player3Text, 1, 2);

        GridPane.setHalignment(button, HPos.RIGHT);

        return baseStage(pane);
    }


    /**
     * Retourne le premier texte
     * @return string
     */
    public static String getText1(){
        return TEXT1_PROPERTY.get();
    }


    /**
     * Retourne le deuxième texte
     * @return string
     */
    public static String getText2(){
        return TEXT2_PROPERTY.get();
    }

    /**
     * Retourne le troisième texte
     * @return string
     */
    public static String getText3(){
        return TEXT3_PROPERTY.get();
    }

}
