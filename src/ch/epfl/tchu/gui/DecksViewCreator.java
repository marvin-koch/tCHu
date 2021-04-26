package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Ticket;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;

class DecksViewCreator {
    private static int OUTSIDE_RECTANGLE_WIDTH = 60;
    private static int OUTSIDE_RECTANGLE_HEIGHT = 90;
    private static int INSIDE_RECTANGLE_WIDTH = 40;
    private static int INSIDE_RECTANGLE_HEIGHT = 70;
    private DecksViewCreator(){}
    public static Node createHandView(ObservableGameState observableGameState){
        HBox hBox = new HBox();
        hBox.getStylesheets().addAll("decks.css","colors.css");

        ListView<Ticket> billets = new ListView<Ticket>();
        billets.setId("tickets");

        HBox hBoxSon = new HBox();
        hBoxSon.setId("hand-pane");

        hBox.getChildren().addAll(billets,hBoxSon);
        for (Card card : Card.ALL) {
            StackPane carteAndCompteur = new StackPane();
            String cardColor = card.color() == null ? "NEUTRAL" : card.color().name();
            carteAndCompteur.getStyleClass().addAll(cardColor,"card");

            Text compteur = new Text();
            compteur.getStyleClass().add("count");

            Rectangle rectangle1 = new Rectangle(OUTSIDE_RECTANGLE_WIDTH, OUTSIDE_RECTANGLE_HEIGHT);
            rectangle1.getStyleClass().add("outside");

            Rectangle rectangle2 = new Rectangle(INSIDE_RECTANGLE_WIDTH, INSIDE_RECTANGLE_HEIGHT);
            rectangle2.getStyleClass().addAll("filled", "inside");

            Rectangle rectangle3 = new Rectangle(INSIDE_RECTANGLE_WIDTH, INSIDE_RECTANGLE_HEIGHT);
            rectangle3.getStyleClass().add("train-image");

            carteAndCompteur.getChildren().addAll(compteur,rectangle1,rectangle2,rectangle3);
            hBoxSon.getChildren().add(carteAndCompteur);
       }
        return hBox;
    }
    public static Node createCardsView(ObservableGameState observableGameState //TODO add TicketChooser
                                       /*MapViewCreator.CardChooser chooser*/){

        VBox vBox = new VBox();
        vBox.getStylesheets().addAll("decks.css","colors.css");
        vBox.setId("card-pane");

        int BUTTON_RECTANGLE_WIDTH = 50;
        int BUTTON_RECTANGLE_HEIGHT = 5;
        Group nodeJauge = new Group(new Rectangle(BUTTON_RECTANGLE_WIDTH, BUTTON_RECTANGLE_HEIGHT),
                new Rectangle(BUTTON_RECTANGLE_WIDTH, BUTTON_RECTANGLE_HEIGHT));// todo voir comment faire pour jauger
        Group nodeJauge2 = new Group(new Rectangle(BUTTON_RECTANGLE_WIDTH, BUTTON_RECTANGLE_HEIGHT),
                new Rectangle(BUTTON_RECTANGLE_WIDTH, BUTTON_RECTANGLE_HEIGHT));

        Button piocheBillets = new Button();
        piocheBillets.getStyleClass().add("gauged");
        piocheBillets.setGraphic(nodeJauge);

        Button piocheCartes = new Button();
        piocheCartes.getStyleClass().add("gauged");
        piocheCartes.setGraphic(nodeJauge2);

        vBox.getChildren().addAll(piocheBillets);
        //SortedBag<Card> playerCards = observableGameState.getPs().cards();
        List<Card> listFausse = List.of(Card.BLUE, Card.ORANGE, Card.LOCOMOTIVE);
        for (Card card: listFausse) {
            StackPane carte = new StackPane();
            String color = card.color() == null ? "NEUTRAL" : card.color().name();
            carte.getStyleClass().addAll(color,"card");

            // todo code reutillsez !!!

            Rectangle rectangle1 = new Rectangle(OUTSIDE_RECTANGLE_WIDTH, OUTSIDE_RECTANGLE_HEIGHT);
            rectangle1.getStyleClass().add("outside");

            Rectangle rectangle2 = new Rectangle(INSIDE_RECTANGLE_WIDTH, INSIDE_RECTANGLE_HEIGHT);
            rectangle2.getStyleClass().addAll("filled", "inside");

            Rectangle rectangle3 = new Rectangle(INSIDE_RECTANGLE_WIDTH, INSIDE_RECTANGLE_HEIGHT);
            rectangle3.getStyleClass().add("train-image");

            carte.getChildren().addAll(rectangle1, rectangle2, rectangle3);
            vBox.getChildren().add(carte);
        }
        vBox.getChildren().add(piocheCartes);
        return vBox;
    }
}
