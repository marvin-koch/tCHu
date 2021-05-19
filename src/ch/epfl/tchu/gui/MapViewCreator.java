package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import static ch.epfl.tchu.gui.ActionHandlers.*;

import java.util.List;

/**
 * La classe MapViewCreator, finale, représante la vue de la carte
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
final class MapViewCreator{

    /**
     * Constructeur Privée
     */
    private MapViewCreator(){}

    /**
     * Créee la view de la Map
     * @param observableGameState gamestate observable
     * @param claimRouteHandlerObjectProperty claimroute handler
     * @param cardChooser card chooser
     * @return Node mapview
     */
    public static Node createMapView(ObservableGameState observableGameState,
                                     ObjectProperty<ClaimRouteHandler> claimRouteHandlerObjectProperty,
                                     CardChooser cardChooser){
        Pane mapPane = new Pane();
        Image imageMap = new Image("map.png");
        ImageView imageView = new ImageView();

        imageView.setImage(imageMap);

        mapPane.getStylesheets().addAll("map.css","colors.css");
        mapPane.getChildren().add(imageView);


        for(Route route : ChMap.routes()){
            Group routeGroup = new Group();
            routeGroup.setId(route.id());
            String routeColor = route.color() == null ? "NEUTRAL" : route.color().name();
            routeGroup.getStyleClass().addAll("route",route.level().name(), routeColor);
            mapPane.getChildren().add(routeGroup);
            for (int i = 0; i < route.length(); i++) {
                int RECTANGLE_WIDTH = 36;
                int RECTANGLE_HEIGHT = 12;
                int CIRCLE_RADIUS = 3;

                Group caseGroup = new Group();
                caseGroup.setId(route.id() + "_" + (i + 1));

                Rectangle voie = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
                voie.getStyleClass().addAll("track", "filled");

                Group wagon = new Group();
                wagon.getStyleClass().add("car");

                Rectangle wagonRectangle = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
                wagonRectangle.getStyleClass().add("filled");

                Circle wagonCircle1 = new Circle(CIRCLE_RADIUS);
                wagonCircle1.setCenterX(12);
                wagonCircle1.setCenterY(6);

                Circle wagonCircle2 = new Circle(CIRCLE_RADIUS);
                wagonCircle2.setCenterX(24);
                wagonCircle2.setCenterY(6);

                routeGroup.getChildren().add(caseGroup);
                caseGroup.getChildren().addAll(voie,wagon);
                wagon.getChildren().addAll(wagonRectangle,wagonCircle1,wagonCircle2);
            }

            observableGameState.getRoutePlayerIdProperty(route).addListener(
                    (l, oV, nV) -> routeGroup.getStyleClass().add(nV.name()));

            routeGroup.disableProperty().bind(claimRouteHandlerObjectProperty.isNull().or(observableGameState.getRouteBooleanProperty(route).not()));

            routeGroup.setOnMouseClicked(event -> {
                List<SortedBag<Card>> possibleClaimCards = observableGameState.possibleClaimCards(route);
                ClaimRouteHandler claimRouteH = claimRouteHandlerObjectProperty.get();
                if(possibleClaimCards.size() == 1){
                    claimRouteH.onClaimRoute(route,possibleClaimCards.get(0));
                }else {
                    cardChooser.chooseCards(possibleClaimCards, chosenCards -> claimRouteH.onClaimRoute(route, chosenCards));
                }
            });
        }
        return mapPane;
    }

    /**
     * Functional Interface CardChooser
     */
    @FunctionalInterface
    public interface CardChooser {
        /**
         * Action de choisir des cartes
         * @param options cartes à choix
         * @param handler handler
         */
        void chooseCards(List<SortedBag<Card>> options,
                         ChooseCardsHandler handler);
    }

}
