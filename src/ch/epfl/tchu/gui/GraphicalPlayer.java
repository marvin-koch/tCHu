package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import static ch.epfl.tchu.gui.ActionHandlers.*;
import static ch.epfl.tchu.gui.StringsFr.*;
import static javafx.application.Platform.isFxApplicationThread;

import java.util.*;
import java.util.stream.Collectors;

/**
 * La classe GraphicalPlayer, finale, représente l'interface graphique d'un joueur
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class GraphicalPlayer{
    private final ObservableList<Text> infos;
    private final ObservableGameState observableGameState;
    private final ObjectProperty<DrawTicketsHandler> drawTicketsHandlerProperty;
    private final ObjectProperty<DrawCardHandler> drawCardHandlerProperty;
    private final ObjectProperty<ClaimRouteHandler> drawClaimRouteHandlerProperty;
    private final Stage mainStage;

    /**
     * Constructeur de GraphicalPlayer
     * @param id PlayerId
     * @param namesMap noms des joueurs
     */
    public GraphicalPlayer(PlayerId id, Map<PlayerId, String> namesMap){
        assert isFxApplicationThread();
        observableGameState = new ObservableGameState(id);
        infos = FXCollections.observableList(new ArrayList<>());
        drawTicketsHandlerProperty = new SimpleObjectProperty<>();
        drawCardHandlerProperty = new SimpleObjectProperty<>();
        drawClaimRouteHandlerProperty = new SimpleObjectProperty<>();
        mainStage = new Stage();
        mainStage.setTitle(String.format("%s \u2014 %s","tCHu", namesMap.get(id)));
        BorderPane pane = new BorderPane(MapViewCreator.createMapView(observableGameState, drawClaimRouteHandlerProperty, this::chooseClaimCards),
                null,
                DecksViewCreator.createCardsView(observableGameState, drawTicketsHandlerProperty, drawCardHandlerProperty),
                DecksViewCreator.createHandView(observableGameState),
                InfoViewCreator.createInfoView(id,namesMap,observableGameState,infos));
        Scene scene = new Scene(pane);
        mainStage.setScene(scene);
        mainStage.show();
    }

    /**
     * Méthode prenant les mêmes arguments que la méthode setState de ObservableGameState
     * et ne faisant rien d'autre que d'appeler cette méthode sur l'état observable du joueur
     * @param gs un PublicGameState
     * @param ps un PlayerState
     */
    public void setState(PublicGameState gs, PlayerState ps){
        assert isFxApplicationThread();
        observableGameState.setState(gs, ps);
    }

    /**
     * Méthode prenant un message - de type String —
     * et l'ajoutant au bas des informations sur le déroulement de la partie,
     * qui sont présentées dans la partie inférieure de la vue des informations
     * @param s info
     */
    public void receiveInfo(String s){
        assert isFxApplicationThread();
        Preconditions.checkArgument(infos.size()<=5);
        Text text = new Text(s);
        if(infos.size() == 5)
            infos.remove(0);
        infos.add(text);
    }

    /**
     * Méthode qui prend en arguments trois gestionnaires d'action,
     * un par types d'actions que le joueur peut effectuer lors d'un tour,
     * et qui permet au joueur d'en effectuer une
     * @param ticketsHandler
     * @param cardHandler
     * @param routeHandler
     */
    public void startTurn(DrawTicketsHandler ticketsHandler, DrawCardHandler cardHandler, ClaimRouteHandler routeHandler){
        assert isFxApplicationThread();
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

    /**
     * Ouvre une fenêtre permettant au joueur de faire son choix.
     * Une fois celui-ci confirmé, le gestionnaire de choix est appelé avec ce choix en argument
     * @param bag multiensemble de 5 ou 3 billets
     * @param handler gestionnaire de tickets
     * @throws IllegalArgumentException si le multiensemble n'est pas de taille 5 ou 3
     * @throws AssertionError si le fil d'éxecution s'arrête
     */
    public void chooseTickets(SortedBag<Ticket> bag, ChooseTicketsHandler handler){
        assert isFxApplicationThread();
        Preconditions.checkArgument(bag.size() == Constants.INITIAL_TICKETS_COUNT || bag.size() == Constants.IN_GAME_TICKETS_COUNT);

        Text text = new Text(String.format(StringsFr.CHOOSE_TICKETS, bag.size() - 2, plural(bag.size()-2)));
        TextFlow textFlow = new TextFlow(text);

        ListView<Ticket> listView = new ListView<>(FXCollections.observableList(bag.toList()));
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button button = new Button(CHOOSE);
        button.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).lessThan((bag.size()-2)));

        Stage stage = createStage(StringsFr.TICKETS_CHOICE,
                new VBox(textFlow, listView, button));

        button.setOnAction( c -> {
            stage.hide();
            handler.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems()));
        });
    }

    /**
     * Méthode qui autorise le joueur a choisir une carte wagon/locomotive,
     * soit l'une des cinq dont la face est visible,
     * soit celle du sommet de la pioche; une fois que le joueur a cliqué sur l'une de ces cartes,
     * le gestionnaire est appelé avec le choix du joueur;
     * cette méthode est destinée à être appelée lorsque le joueur a déjà tiré une première carte et doit maintenant tirer la seconde
     * @param handler un gestionnaire de tirage
     * @throws AssertionError si le fil d'execution est arrêtée
     */
    public void drawCard(DrawCardHandler handler){
        assert isFxApplicationThread();
        drawCardHandlerProperty.set(slot -> {
            handler.onDrawCard(slot);
            viderHandlers();
        });
    }

    /**
     * Ouvre une fenêtre permettant au joueur de faire son choix.
     * Une fois celui-ci confirmé, le gestionnaire de choix est appelé avec ce choix en argument
     * @param list list de multiensemble de cartes initiales pour s'emparer d'une route
     * @param handler gestionnaire de cartes
     * @throws AssertionError si le fil d'éxecution s'arrête
     */
    public void chooseClaimCards(List<SortedBag<Card>> list, ChooseCardsHandler handler){
        createCardWindow(CHOOSE_CARDS, list, handler);
    }



    /**
     * Ouvre une fenêtre permettant au joueur de faire son choix.
     * Une fois celui-ci confirmé, le gestionnaire de choix est appelé avec ce choix en argument
     * @param list list de multiensemble de cartes
     * @param handler gestionnaire de cartes
     * @throws AssertionError si le fil d'éxecution s'arrête
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> list, ChooseCardsHandler handler){
        createCardWindow(CHOOSE_ADDITIONAL_CARDS, list, handler);
    }

    /**
     * Crée le Stage
     * @param title
     * @param vbox
     * @return Stage
     */
    private Stage createStage(String title, VBox vbox){
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add("chooser.css");
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setTitle(title);
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(Event::consume);
        stage.setScene(scene);
        stage.show();
        return stage;
    }

    /**
     * Crée la fenêtre pour choisir des cartes
     * @param string
     * @param list
     * @param handler
     */
    private void createCardWindow(String string, List<SortedBag<Card>> list, ChooseCardsHandler handler){
        assert isFxApplicationThread();
        Text text = new Text(string);
        TextFlow textFlow = new TextFlow(text);

        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(list));
        listView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button button = new Button(CHOOSE);
        Stage stage = createStage(CARDS_CHOICE,
                new VBox(textFlow, listView, button));

        boolean stringContent = string.equals(CHOOSE_CARDS);
        ObservableList<SortedBag<Card>> selectedItems = listView.getSelectionModel().getSelectedItems();

        if (stringContent)
            button.disableProperty().bind(Bindings.size(selectedItems).isEqualTo(0));

        button.setOnAction( c -> {
            stage.hide();
            SortedBag<Card> cards = (!selectedItems.isEmpty()) || stringContent
                    ? listView.getSelectionModel().getSelectedItem()
                    : SortedBag.of();
            handler.onChooseCards(cards);
        });
    }

    /**
     * Vide les ObjectProperties contenant les handlers
     */
    private void viderHandlers(){
        drawTicketsHandlerProperty.set(null);
        drawCardHandlerProperty.set(null);
        drawClaimRouteHandlerProperty.set(null);
    }

    /**
     * Class privée CardBagStringConverter
     */
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
