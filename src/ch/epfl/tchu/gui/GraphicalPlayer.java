package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import static ch.epfl.tchu.gui.ActionHandlers.*;
import static ch.epfl.tchu.gui.StringsFr.AND_SEPARATOR;

import java.util.*;
import java.util.stream.Collectors;

public final class GraphicalPlayer{
    private final ObservableList<Text> infos;
    private final ObservableGameState observableGameState;
    private final ObjectProperty<DrawTicketsHandler> drawTicketsHandlerProperty;
    private final ObjectProperty<DrawCardHandler> drawCardHandlerProperty;
    private final ObjectProperty<ClaimRouteHandler> drawClaimRouteHandlerProperty;
    Stage mainStage;
    public GraphicalPlayer(PlayerId id, Map<PlayerId, String> namesMap){
        assert Platform.isFxApplicationThread();
        observableGameState = new ObservableGameState(id);
        infos = FXCollections.observableList(new ArrayList<>());
        drawTicketsHandlerProperty = new SimpleObjectProperty<>();
        drawCardHandlerProperty = new SimpleObjectProperty<>();
        drawClaimRouteHandlerProperty = new SimpleObjectProperty<>();
        mainStage = new Stage();
        mainStage.setTitle(String.format("%s \u2014 %s","tCHu", id.name()));
        BorderPane pane = new BorderPane(MapViewCreator.createMapView(observableGameState, drawClaimRouteHandlerProperty, this::chooseClaimCards),
                null,
                DecksViewCreator.createCardsView(observableGameState, drawTicketsHandlerProperty, drawCardHandlerProperty),
                DecksViewCreator.createHandView(observableGameState),
                InfoViewCreator.createInfoView(id,namesMap,observableGameState,infos));
        Scene scene = new Scene(pane);
        mainStage.setScene(scene);
        mainStage.show();

    }

    public void setState(PublicGameState gs, PlayerState ps){
        assert Platform.isFxApplicationThread();
        observableGameState.setState(gs, ps);
    }

    public void receiveInfo(String s){
        assert Platform.isFxApplicationThread();
        Preconditions.checkArgument(infos.size()<=5);
        Text text = new Text(s);
        if(infos.size() == 5)
            infos.remove(0);
        infos.add(text);
    }

    public void startTurn(DrawTicketsHandler ticketsHandler, DrawCardHandler cardHandler, ClaimRouteHandler routeHandler){
        assert Platform.isFxApplicationThread();
        if(observableGameState.canDrawTickets())
            drawTicketsHandlerProperty.set(() -> {
                ticketsHandler.onDrawTickets();
                viderHandlers();
            });
        if(observableGameState.canDrawCards())
            drawCardHandlerProperty.set(slot -> {
                cardHandler.onDrawCard(slot);
                viderHandlers();
            });
        drawClaimRouteHandlerProperty.set((route, cards) -> {
            routeHandler.onClaimRoute(route, cards);
            viderHandlers();
        });
    }

    public void chooseTickets(SortedBag<Ticket> bag, ActionHandlers.ChooseTicketsHandler handler){
        assert Platform.isFxApplicationThread();
        Preconditions.checkArgument(bag.size() == 5 || bag.size() == 3);

        Text text = new Text(String.format(StringsFr.CHOOSE_TICKETS, bag.size() - 2, StringsFr.plural(bag.size()-2)));
        TextFlow textFlow = new TextFlow(text);

        ListView<Ticket> listView = new ListView<>(FXCollections.observableList(bag.toList()));
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);// todo: pour le moment tjrs à multiple est-ce juste ?

        Button button = new Button(StringsFr.CHOOSE);
        button.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).lessThan((bag.size()-2)));


        Scene scene = new Scene(new VBox(textFlow,
                listView,
                button));


        scene.getStylesheets().add("chooser.css");
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setTitle(StringsFr.TICKETS_CHOICE);
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(Event::consume);

        button.setOnAction( c -> {
            stage.hide();
            handler.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems()));
        });
    }

    public void drawCard(DrawCardHandler handler){
        assert Platform.isFxApplicationThread();
        //TODO
        drawCardHandlerProperty.set(slot -> {
            handler.onDrawCard(slot);
            viderHandlers();
        });
    }

    public void chooseClaimCards(List<SortedBag<Card>> list, ChooseCardsHandler handler){
        assert Platform.isFxApplicationThread();
        Text text = new Text(StringsFr.CHOOSE_CARDS);
        TextFlow textFlow = new TextFlow(text);

        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(list));
        //listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button button = new Button(StringsFr.CHOOSE);
        button.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).isEqualTo(0));

        Scene scene = new Scene(new VBox(textFlow,
                listView,
                button));


        scene.getStylesheets().add("chooser.css");
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setTitle(StringsFr.CARDS_CHOICE);
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(Event::consume);

        button.setOnAction( c -> {
            stage.hide();
            handler.onChooseCards(listView.getSelectionModel().getSelectedItem());
        });

    }

    public void chooseAdditionalCards(List<SortedBag<Card>> list, ChooseCardsHandler handler){
        assert Platform.isFxApplicationThread();
        Text text = new Text(StringsFr.CHOOSE_ADDITIONAL_CARDS);
        TextFlow textFlow = new TextFlow(text);

        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(list));
        //listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button button = new Button(StringsFr.CHOOSE);

        Scene scene = new Scene(new VBox(textFlow,
                listView,
                button));

        scene.getStylesheets().add("chooser.css");
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setTitle(StringsFr.CARDS_CHOICE);
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(Event::consume);

        button.setOnAction( c -> {
            stage.hide();
            if(listView.getSelectionModel().getSelectedItems().isEmpty()){
                handler.onChooseCards(SortedBag.of());
            }else {
                handler.onChooseCards(listView.getSelectionModel().getSelectedItem());
            }//todo test si faut vraiment mettre un if else
        });

    }
    private void viderHandlers(){
        drawTicketsHandlerProperty.set(null);
        drawCardHandlerProperty.set(null);
        drawClaimRouteHandlerProperty.set(null);
    }

    private static class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

        /**
         * Converts the object provided into its string form.
         * Format of the returned string is defined by the specific converter.
         *
         * @param object the object of type {@code T} to convert
         * @return a string representation of the object passed in.
         */
        @Override
        public String toString(SortedBag<Card> object) {
            List<String> strings = object.toSet().stream()
                    .map(card -> String.format("%s %s", object.countOf(card), Info.cardName(card, object.countOf(card))))
                    .collect(Collectors.toList());
            switch(strings.size()){
                case(1):
                    return strings.get(0);
                case(2):
                    return String.join(AND_SEPARATOR, strings);
                default:
                    return String.join(", ", strings.subList(0, strings.size() - 1)) + AND_SEPARATOR
                            + strings.get(strings.size() - 1);
            }
        }

        /**
         * Converts the string provided into an object defined by the specific converter.
         * Format of the string and type of the resulting object is defined by the specific converter.
         *
         * @param string the {@code String} to convert
         * @return an object representation of the string passed in.
         */
        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }
}
