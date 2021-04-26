package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import com.sun.javafx.scene.shape.RectangleHelper;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.swing.*;
import javax.swing.text.Element;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;

//import java.awt.*;
import java.util.List;

class MapViewCreator{
    private MapViewCreator(){}
    /*
    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ChooseCardsHandler handler);
    }

     */
    // to do changer le type de retour
    public static Node createMapView(ObservableGameState observableGameState
                                     //ObjectProperty<ClaimRouteHandler> claimRouteHandlerObjectProperty,
                                     /*CardChooser cardChooser*/){
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
                Group case_Group = new Group();
                case_Group.setId(route.id() + "_" + (i + 1));

                int RECTANGLE_WIDTH = 36;
                int RECTANGLE_HEIGHT = 12;
                Rectangle voie = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
                voie.getStyleClass().addAll("track", "filled");

                Group wagon = new Group();
                wagon.getStyleClass().add("car");

                Rectangle wagonRectangle = new Rectangle(RECTANGLE_WIDTH, RECTANGLE_HEIGHT);
                wagonRectangle.getStyleClass().add("filled");

                int CIRCLE_RADIUS = 3;
                Circle wagonCircle1 = new Circle(CIRCLE_RADIUS);
                //wagonCircle1.getStyleClass().add("filled");

                Circle wagonCircle2 = new Circle(CIRCLE_RADIUS);
                //wagonCircle2.getStyleClass().add("filled");

                wagonCircle1.setCenterX(12);
                wagonCircle1.setCenterY(6);
                wagonCircle2.setCenterX(24);
                wagonCircle2.setCenterY(6);

                routeGroup.getChildren().add(case_Group);
                case_Group.getChildren().addAll(voie,wagon);
                wagon.getChildren().addAll(wagonRectangle,wagonCircle1,wagonCircle2);
            }
        }
        return mapPane;

    }

}
