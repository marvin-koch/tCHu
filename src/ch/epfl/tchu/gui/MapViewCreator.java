package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;
import com.sun.javafx.scene.shape.RectangleHelper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.swing.*;
import javax.swing.text.Element;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;

import java.util.List;

class MapViewCreator{
    private MapViewCreator(){}

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandlers.ChooseCardsHandler handler);
    }

    public static Node createMapView(ObservableGameState observableGameState,
                                     ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandlerObjectProperty,
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

                Group case_Group = new Group();
                case_Group.setId(route.id() + "_" + (i + 1));

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

                routeGroup.getChildren().add(case_Group);
                case_Group.getChildren().addAll(voie,wagon);
                wagon.getChildren().addAll(wagonRectangle,wagonCircle1,wagonCircle2);
            }
            observableGameState.getRoutePlayerIdProperty(route).addListener(
                    (l, oV, nV) -> routeGroup.getStyleClass().add(nV.name()));
                    /*
                    (l, oV, nV) -> mapPane.getChildren()
                            .get(ChMap.routes().indexOf(route) + 1)
                            .getStyleClass()
                            .add(nV.toString()));

                     */


            /*Group routeGroupe = (Group) mapPane.getChildren()
                    .get(ChMap.routes().indexOf(route) + 1);

             */

            routeGroup.disableProperty().bind(claimRouteHandlerObjectProperty.isNull().or(observableGameState.getRouteBooleanProperty(route)).not());

            routeGroup.setOnMouseClicked(event -> {
                List<SortedBag<Card>> possibleClaimCards = observableGameState.possibleClaimCards(route);
                ActionHandlers.ClaimRouteHandler claimRouteH = claimRouteHandlerObjectProperty.get();
                if(possibleClaimCards.size() == 1){
                    claimRouteH.onClaimRoute(route,possibleClaimCards.get(0));
                }else {
                    ActionHandlers.ChooseCardsHandler chooseCardsH = chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
                    cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                }
            });
        }
        return mapPane;

    }

}
