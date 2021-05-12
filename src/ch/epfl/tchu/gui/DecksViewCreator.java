package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * La classe DecksViewCreator, finale, représante la vue du Deck du joueur
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
final class DecksViewCreator {

    /**
     * Constructeur privée
     */
    private DecksViewCreator() {
    }

    /**
     * Crée les rectangles
     * @return list de rectangles
     */
    private static List<Rectangle> createRectangles() {
        int OUTSIDE_RECTANGLE_WIDTH = 60;
        int OUTSIDE_RECTANGLE_HEIGHT = 90;
        int INSIDE_RECTANGLE_WIDTH = 40;
        int INSIDE_RECTANGLE_HEIGHT = 70;

        Rectangle rectangle1 = new Rectangle(OUTSIDE_RECTANGLE_WIDTH, OUTSIDE_RECTANGLE_HEIGHT);
        rectangle1.getStyleClass().add("outside");

        Rectangle rectangle2 = new Rectangle(INSIDE_RECTANGLE_WIDTH, INSIDE_RECTANGLE_HEIGHT);
        rectangle2.getStyleClass().addAll("filled", "inside");

        Rectangle rectangle3 = new Rectangle(INSIDE_RECTANGLE_WIDTH, INSIDE_RECTANGLE_HEIGHT);
        rectangle3.getStyleClass().add("train-image");

        return List.of(rectangle1, rectangle2, rectangle3);
    }

    /**
     * Crée un button
     * @param observableGameState game state
     * @param prct pourcentage
     * @param string nom du button
     * @return Button
     */
    private static Button createButton(ObservableGameState observableGameState, ReadOnlyIntegerProperty prct, String string){
        int BUTTON_RECTANGLE_WIDTH = 50;
        int BUTTON_RECTANGLE_HEIGHT = 5;
        Rectangle fg = new Rectangle(BUTTON_RECTANGLE_WIDTH, BUTTON_RECTANGLE_HEIGHT);
        Rectangle bg = new Rectangle(BUTTON_RECTANGLE_WIDTH, BUTTON_RECTANGLE_HEIGHT);
        fg.getStyleClass().add("foreground");
        bg.getStyleClass().add("background");
        fg.widthProperty().bind(prct.multiply(50).divide(100));
        Group jauge = new Group(bg, fg);
        Button button = new Button(string);
        button.getStyleClass().add("gauged");
        button.setGraphic(jauge);
        return button;
    }

    /**
     * Crée la vue de la main
     * @param observableGameState état du jeu observable
     * @return Node
     */
    public static Node createHandView(ObservableGameState observableGameState) {
        HBox hBox = new HBox();
        hBox.getStylesheets().addAll("decks.css", "colors.css");

        ListView<Ticket> billets = new ListView<>(observableGameState.getPlayerTicketsList());
        billets.setId("tickets");
        HBox hBoxSon = new HBox();
        hBoxSon.setId("hand-pane");

        hBox.getChildren().addAll(billets, hBoxSon);
        for (Card card : Card.ALL) {
            StackPane carteAndCompteur = new StackPane();
            String cardColor = card.color() == null ? "NEUTRAL" : card.color().name();
            carteAndCompteur.getStyleClass().addAll(cardColor, "card");

            Text compteur = new Text();
            compteur.getStyleClass().add("count");

            ReadOnlyIntegerProperty count = observableGameState.getCardProperty(card);
            carteAndCompteur.visibleProperty().bind(Bindings.greaterThan(count, 0));
            compteur.textProperty().bind(Bindings.convert(count));
            compteur.visibleProperty().bind(Bindings.greaterThan(count, 1));

            carteAndCompteur.getChildren().addAll(createRectangles());
            carteAndCompteur.getChildren().add(compteur);
            hBoxSon.getChildren().add(carteAndCompteur);

        }
        return hBox;
    }

    /**
     * Crée la vue des FaceUpCards et les buttons
     * @param observableGameState état du jeu observable
     * @param drawTicket DrawTicketsHandler
     * @param drawCard DrawCardHandler
     * @return Node
     */
    public static Node createCardsView(ObservableGameState observableGameState, ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicket,
                                       ObjectProperty<ActionHandlers.DrawCardHandler> drawCard) {

        VBox vBox = new VBox();
        vBox.getStylesheets().addAll("decks.css", "colors.css");
        vBox.setId("card-pane");

        Button piocheBillets = createButton(observableGameState, observableGameState.ticketPourcentageProperty(), StringsFr.TICKETS);
        piocheBillets.disableProperty().bind(drawTicket.isNull());
        piocheBillets.setOnMouseClicked(event -> drawTicket.get().onDrawTickets());

        Button piocheCartes = createButton(observableGameState, observableGameState.cartePourcentageProperty(), StringsFr.CARDS);
        piocheCartes.disableProperty().bind(drawCard.isNull());
        piocheCartes.setOnMouseClicked(event -> drawCard.get().onDrawCard(-1));

        vBox.getChildren().addAll(piocheBillets);

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            ReadOnlyObjectProperty<Card> cardProperty = observableGameState.getFaceUpCards(slot);
            StackPane cartePane = new StackPane();
            ObservableList<String> styleClass = cartePane.getStyleClass();
            styleClass.addAll(null, "card");
            if(!(cardProperty.get() == null)){
                Card card = cardProperty.get();
                String color = card.color() == null ? "NEUTRAL" : card.color().name();
                styleClass.set(0, color);
            }
            cartePane.getChildren().addAll(createRectangles());
            vBox.getChildren().add(cartePane);

            cartePane.disableProperty().bind(drawCard.isNull());
            cardProperty.addListener((l,oV, nV) -> cartePane.getStyleClass().set(0, nV.color() == null ? "NEUTRAL" : nV.color().name()));
            cartePane.setOnMouseClicked(event -> drawCard.get().onDrawCard(slot));
        }
        vBox.getChildren().add(piocheCartes);
        return vBox;
    }

}
