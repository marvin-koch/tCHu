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

/**
 * La classe ObservableGameState, finale, représante l'état observable de la partie
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class ObservableGameState {
    private final PlayerId id;
    private PublicGameState publicGameState;
    private PlayerState playerState;

    //Propriétés de PublicGameState
    private final IntegerProperty ticketPourcentage;
    private final IntegerProperty cartePourcentage;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route, ObjectProperty<PlayerId>> routesProperties = new HashMap<>();

    //Propriétés publics des PlayerStates
    private final IntegerProperty player1TicketCount;
    private final IntegerProperty player2TicketCount;
    private final IntegerProperty player1CardCount;
    private final IntegerProperty player2CardCount;
    private final IntegerProperty player1WagonCount;
    private final IntegerProperty player2WagonCount;
    private final IntegerProperty player1PointsCount;
    private final IntegerProperty player2PointsCount;

    //Propriétés de privées de PlayerState
    private final ObservableList<Ticket> playerTicketsList;
    private final Map<Card, IntegerProperty> cardsCountMap = new HashMap<>();
    private final Map<Route, BooleanProperty> routeStatusMap = new HashMap<>();

    private static final Map<List<Station>,List<Route>> STATION_PAIRS = createPairs();

    /**
     * Crée 5 propriétés vide
     * @return List de ObjectProperty
     */
    private static List<ObjectProperty<Card>> createFaceUpCards(){
        List<ObjectProperty<Card>> list = new ArrayList<>();
        for(int slot : Constants.FACE_UP_CARD_SLOTS){
            list.add(new SimpleObjectProperty<Card>());
        }
        return list;
    }

    /**
     * Crée toutes les paires de stations dans le jeu
     * @return une map qui associe une paire de stations à une list de routes qui ont les mêmes stations
     */
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

    /**
     * Constructeur de ObservableGameState
     * @param id PlayerId
     */
    public ObservableGameState(PlayerId id) {
        this.id = id;
        publicGameState = null;
        playerState = null;

        ticketPourcentage = new SimpleIntegerProperty(0);
        cartePourcentage = new SimpleIntegerProperty(0);
        faceUpCards = createFaceUpCards();
        ChMap.routes().forEach(route -> routesProperties.put(route, new SimpleObjectProperty<>(null)));

        player1TicketCount = new SimpleIntegerProperty(0);
        player2TicketCount = new SimpleIntegerProperty(0);
        player1CardCount = new SimpleIntegerProperty(0);
        player2CardCount = new SimpleIntegerProperty(0);
        player1WagonCount = new SimpleIntegerProperty(0);
        player2WagonCount = new SimpleIntegerProperty(0);
        player1PointsCount = new SimpleIntegerProperty(0);
        player2PointsCount = new SimpleIntegerProperty(0);

        playerTicketsList = FXCollections.observableList(new ArrayList<>());
        Card.ALL.forEach(card -> cardsCountMap.put(card, new SimpleIntegerProperty(0)));
        ChMap.routes().forEach(
                route -> routeStatusMap.put(route, new SimpleBooleanProperty(false)));


    }

    /**
     * Met à jour les propriétés de la classe
     * @param gs la partie publique du jeu
     * @param ps l'état du joueur
     */
    public void setState(PublicGameState gs, PlayerState ps){
        publicGameState = gs;
        playerState = ps;

        ticketPourcentage.set((int)(gs.ticketsCount() / (double)ChMap.tickets().size()*100));
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

        playerTicketsList.setAll(ps.tickets().toList());

        for (Card card : Card.values()) {
            cardsCountMap.get(card).set(ps.cards().countOf(card));
        }

        for (Route route: routeStatusMap.keySet()) {
            if(id == publicGameState.currentPlayerId() && routesProperties.get(route).get() == null && ps.canClaimRoute(route)){
                for(List<Station> pairs : STATION_PAIRS.keySet()){
                    List<Route> voisinage = STATION_PAIRS.get(pairs);
                    if(pairs.get(0) == route.station1() && pairs.get(1) == route.station2()){
                        routeStatusMap.get(route).set(voisinage.stream()
                                .noneMatch(routeV -> routesProperties.get(routeV).get() != null));
                    }
                }
            }else{
                routeStatusMap.get(route).set(false);
            }
        }
    }

    /**
     * Retourne le pourcentage de billets restants
     * @return ticketPercentage Property
     */
    public ReadOnlyIntegerProperty ticketPourcentageProperty() {
        return ticketPourcentage;
    }

    /**
     * Retourne le pourcentage de cartes restants
     * @return carte Pourcentage Property
     */
    public  ReadOnlyIntegerProperty cartePourcentageProperty() {
        return cartePourcentage;
    }

    /**
     * Retourne la carte voulue de FaceUpCards
     * @param slot index désirée
     * @return Object Property Card
     */
    public ReadOnlyObjectProperty<Card> getFaceUpCards(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     * Retourne le PlayerId d'une Route
     * @param route route désirée
     * @return Object Property Playerid (contient null si elle n'a pas de propriétaire)
     */
    public ReadOnlyObjectProperty<PlayerId> getRoutePlayerIdProperty(Route route) {
        return routesProperties.get(route);
    }

    /**
     * Retourne les nombres de billets du PlayerId en argument
     * @param id PlayerId
     * @return Integer Property
     */
    public ReadOnlyIntegerProperty playerTicketCountProperty(PlayerId id){
        return id == PlayerId.PLAYER_1 ? player1TicketCount : player2TicketCount;
    }

    /**
     * Retourne les nombres de cartes du PlayerId
     * @param id PlayerId
     * @return Integer Property
     */

    public ReadOnlyIntegerProperty playerCardCountProperty(PlayerId id){
        return id == PlayerId.PLAYER_1 ? player1CardCount : player2CardCount;
    }

    /**
     * Retourne les nombres de wagons du PlayerId en argument
     * @param id PlayerId
     * @return Integer Property
     */
    public ReadOnlyIntegerProperty playerWagonCountProperty(PlayerId id){
        return id == PlayerId.PLAYER_1 ? player1WagonCount : player2WagonCount;
    }

    /**
     * Retourne les nombres de points du PlayerId en argument
     * @param id PlayerId
     * @return Integer Property
     */
    public ReadOnlyIntegerProperty playerPointCountProperty(PlayerId id){
       return id == PlayerId.PLAYER_1 ? player1PointsCount : player2PointsCount;
    }

    /**
     * Retourne les billets du playerState
     * @return Observable List des billets
     */
    public ObservableList<Ticket> getPlayerTicketsList() {
        return playerTicketsList;
    }

    /**
     * Retourne le nombre de cartes de couleur choisi que le joueur possède
     * @param card la carte désirée
     * @return Integer Property du nombre
     */
    public ReadOnlyIntegerProperty getCardProperty(Card card) {
        return cardsCountMap.get(card);
    }

    /**
     * Retourne le status de la route càd si la route peut être prise ou pas par le joueur
     * @param route la route désirée
     * @return Boolean Property
     */
    public ReadOnlyBooleanProperty getRouteBooleanProperty(Route route) {
        return routeStatusMap.get(route);
    }

    /**
     * Retourne la méthode canDrawCards() du game state
     * @return boolean
     */
    public boolean canDrawCards(){
        return publicGameState.canDrawCards();
    }

    /**
     * Retourne la méthode canDrawTickets() du game state
     * @return boolean
     */
    public boolean canDrawTickets(){
        return publicGameState.canDrawTickets();
    }

    /**
     * Retourne la méthode possibleClaimCards du playerState
     * @param route la route désirée
     * @return une list des multiensemble de cartes permettant le joueur de s'emparer de la route voulue
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        return List.copyOf(playerState.possibleClaimCards(route));
    }

}
