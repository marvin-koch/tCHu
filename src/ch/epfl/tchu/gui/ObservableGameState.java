package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class ObservableGameState {
    private PlayerId id;
    private PublicGameState publicGameState;
    private PlayerState playerState;

    //Propriétés de PublicGameState
    private IntegerProperty ticketPourcentage;
    private IntegerProperty cartePourcentage;
    private List<ObjectProperty<Card>> faceUpCards;
    private Map<Route, ObjectProperty<PlayerId>> routesProperties = new HashMap<>();

    //Propriétés publics des PlayerStates
    private IntegerProperty player1TicketCount;
    private IntegerProperty player2TicketCount;
    private IntegerProperty player1CardCount;
    private IntegerProperty player2CardCount;
    private IntegerProperty player1WagonCount;
    private IntegerProperty player2WagonCount;
    private IntegerProperty player1PointsCount;
    private IntegerProperty player2PointsCount;

    //Propriétés de privées de PlayerState
    private ObservableList<String> playerTicketsList;
    private Map<Card, IntegerProperty> cardsCountMap = new HashMap<>();
    private Map<Route, BooleanProperty> routeStatusMap = new HashMap<>();

    private static final Map<List<Station>,List<Route>> STATION_PAIRS = createPairs();

    //TODO check immuabilité
    public ObservableGameState(PlayerId id) {
        this.id = id;
        publicGameState = null;
        playerState = null;

        ticketPourcentage = new SimpleIntegerProperty(0);
        cartePourcentage = new SimpleIntegerProperty(0);
        faceUpCards = createEmptyList(Constants.FACE_UP_CARDS_COUNT);//TODO est-ce que ca marche?
        ChMap.routes().forEach(route -> routesProperties.put(route, new SimpleObjectProperty<>(null)));

        player1TicketCount = new SimpleIntegerProperty(0);
        player2TicketCount = new SimpleIntegerProperty(0);
        player1CardCount = new SimpleIntegerProperty(0);
        player2CardCount = new SimpleIntegerProperty(0);
        player1WagonCount = new SimpleIntegerProperty(0);
        player2WagonCount = new SimpleIntegerProperty(0);
        player1PointsCount = new SimpleIntegerProperty(0);
        player2PointsCount = new SimpleIntegerProperty(0);

        playerTicketsList = FXCollections.observableList(new ArrayList<>());//TODO Pas sur
        Card.ALL.forEach(card -> cardsCountMap.put(card, new SimpleIntegerProperty(0)));
        ChMap.routes().forEach(
                route -> routeStatusMap.put(route, new SimpleBooleanProperty(false)));


    }

    public void setState(PublicGameState gs, PlayerState ps){
        publicGameState = gs;
        playerState = ps;

        ticketPourcentage.set((int)(gs.ticketsCount()/(double)ChMap.tickets().size()*100));
        cartePourcentage.set((int)(gs.cardState().deckSize() / (double)Constants.TOTAL_CARDS_COUNT*100));
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = gs.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }
        ps.routes().forEach(route -> routesProperties.get(route).set(id));
        gs.playerState(id.next()).routes().forEach(route -> routesProperties.get(route).set(id.next()));

        player1TicketCount.set(ps.ticketCount());
        player2TicketCount.set(gs.playerState(id.next()).ticketCount());

        player1CardCount.set(ps.cardCount());
        player2CardCount.set(gs.playerState(id.next()).cardCount());

        player1WagonCount.set(ps.carCount());
        player2WagonCount.set(gs.playerState(id.next()).carCount());

        player1PointsCount.set(ps.claimPoints());
        player2PointsCount.set(gs.playerState(id.next()).claimPoints());

        //TODO pas sur String ou List???
        playerTicketsList.setAll(ps.tickets().stream().map(Ticket::text).collect(Collectors.toList()));

        for (Card card : Card.values()) {
            cardsCountMap.get(card).set(ps.cards().countOf(card));
        }

        for (Route route: routeStatusMap.keySet()) {
            if(id == publicGameState.currentPlayerId() && routesProperties.get(route).get() == null && ps.canClaimRoute(route)){
                for(List<Station> list : STATION_PAIRS.keySet()){
                    if(list.get(0) == route.station1() && list.get(1) == route.station2()){
                        if(STATION_PAIRS.get(list).size()== 1){
                            routeStatusMap.get(route).set(true);
                        }else{
                            Preconditions.checkArgument(STATION_PAIRS.get(list).size() == 2);
                            boolean bool = (routesProperties.get((STATION_PAIRS.get(list).get(0))).get() == null) && (routesProperties.get(STATION_PAIRS.get(list).get(1)).get() == null);
                            routeStatusMap.get(route).set(bool);

                        }
                    }
                }
            }else{
                routeStatusMap.get(route).set(false);
            }
        }

    }

    private static<T> List<ObjectProperty<T>> createEmptyList(int size){
        List<ObjectProperty<T>> list = new ArrayList<>();
        for(int i = 0; i < size; ++i){
            list.add(new SimpleObjectProperty<T>());
        }
        return list;
    }

    private static Map<List<Station>, List<Route>> createPairs(){
       Map<List<Station>, List<Route>> map = new HashMap<>();
        for(Station station1: ChMap.stations()){
            for(Station station2 : ChMap.stations()){
                map.put(List.of(station1, station2), new ArrayList<>());
            }
        }
        for (Route route: ChMap.routes()) {
            for(List<Station> list : map.keySet()){
                if(list.get(0) == route.station1() && list.get(1) == route.station2()){
                    map.get(list).add(route);
                }
            }
        }
        return map;
    }

    public ReadOnlyIntegerProperty ticketPourcentageProperty() {
        return ticketPourcentage;
    }

    public  ReadOnlyIntegerProperty cartePourcentageProperty() {
        return cartePourcentage;
    }

    public ReadOnlyObjectProperty<Card> getFaceUpCards(int slot) {
        return faceUpCards.get(slot);
    }

    public ReadOnlyObjectProperty<PlayerId> getRoutePlayerIdProperty(Route route) {
        return routesProperties.get(route);
    }

    public ReadOnlyIntegerProperty player1TicketCountProperty() {
        return player1TicketCount;
    }

    public ReadOnlyIntegerProperty player2TicketCountProperty() {
        return player2TicketCount;
    }

    public ReadOnlyIntegerProperty player1CardCountProperty() {
        return player1CardCount;
    }

    public ReadOnlyIntegerProperty player2CardCountProperty() {
        return player2CardCount;
    }

    public ReadOnlyIntegerProperty player1WagonCountProperty() {
        return player1WagonCount;
    }

    public ReadOnlyIntegerProperty player2WagonCountProperty() {
        return player2WagonCount;
    }

    public ReadOnlyIntegerProperty player1PointsCountProperty() {
        return player1PointsCount;
    }

    public ReadOnlyIntegerProperty player2PointsCountProperty() {
        return player2PointsCount;
    }

    public ObservableList<String> getPlayerTicketsList() {
        return playerTicketsList;
    }

    public ReadOnlyIntegerProperty getCardProperty(Card card) {
        return cardsCountMap.get(card);
    }

    public ReadOnlyBooleanProperty getRouteBooleanProperty(Route route) {
        return routeStatusMap.get(route);
    }
    public boolean canDrawCards(){
        return publicGameState.canDrawCards();
    }
    public boolean canDrawTickets(){
        return publicGameState.canDrawTickets();
    }
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        return playerState.possibleClaimCards(route);
    }

}
