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
    private final Map<PlayerId, IntegerProperty> playerTicketCount = new HashMap<>();
    private final Map<PlayerId, IntegerProperty> playerCardCount = new HashMap<>();
    private final Map<PlayerId, IntegerProperty> playerWagonCount = new HashMap<>();
    private final Map<PlayerId, IntegerProperty> playerPointsCount = new HashMap<>();

    //Propriétés de privées de PlayerState
    private final ObservableList<Ticket> playerTicketsList;
    private final Map<Card, IntegerProperty> cardsCountMap = new HashMap<>();
    private final Map<Route, BooleanProperty> routeStatusMap = new HashMap<>();

    private static final Map<Route,Route> ROUTE_PAIRS = routeSister();
    /**
     * Crée 5 propriétés vide
     * @return List de ObjectProperty
     */
    private static List<ObjectProperty<Card>> createFaceUpCards(){
        List<ObjectProperty<Card>> faceUpCardProperties = new ArrayList<>();
        for(int slot : Constants.FACE_UP_CARD_SLOTS){
            faceUpCardProperties.add(new SimpleObjectProperty<>());
        }
        return faceUpCardProperties;
    }

    /**
     * Map toutes les route voisine dans le jeu
     * @return
     */
    private static Map<Route, Route> routeSister(){
        Map<Route, Route> routeMapToSister = new HashMap<>();
        for(Route route1 : ChMap.routes()){
            ChMap.routes().stream().filter(route2 -> route1.station1() == route2.station1() && route1.station2() == route2.station2() && (!route1.id().equals(route2.id())))
                    .findAny()
                    .ifPresent(sister -> routeMapToSister.put(route1, sister));
        }
        return routeMapToSister;
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

        /*
        player1TicketCount = new SimpleIntegerProperty(0);
        player2TicketCount = new SimpleIntegerProperty(0);
        player1CardCount = new SimpleIntegerProperty(0);
        player2CardCount = new SimpleIntegerProperty(0);
        player1WagonCount = new SimpleIntegerProperty(0);
        player2WagonCount = new SimpleIntegerProperty(0);
        player1PointsCount = new SimpleIntegerProperty(0);
        player2PointsCount = new SimpleIntegerProperty(0);

         */
        PlayerId.ALL.forEach(playerId -> playerTicketCount.put(playerId, new SimpleIntegerProperty(0)));
        PlayerId.ALL.forEach(playerId -> playerCardCount.put(playerId, new SimpleIntegerProperty(0)));
        PlayerId.ALL.forEach(playerId -> playerWagonCount.put(playerId, new SimpleIntegerProperty(0)));
        PlayerId.ALL.forEach(playerId -> playerPointsCount.put(playerId, new SimpleIntegerProperty(0)));

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

        PlayerId.ALL.forEach(id -> gs.playerState(id).routes().forEach(route -> routesProperties.get(route).set(id)));


        /*
        player1TicketCount.set(ps.ticketCount());
        player2TicketCount.set(gs.playerState(id.next()).ticketCount());
         */
        playerTicketCount.forEach((playerId, integerProperty) -> integerProperty.set(gs.playerState(id).ticketCount()));

        /*
        player1CardCount.set(ps.cardCount());
        player2CardCount.set(gs.playerState(id.next()).cardCount());

         */
        playerCardCount.forEach((playerId, integerProperty) -> integerProperty.set(gs.playerState(id).cardCount()));

        /*
        player1WagonCount.set(ps.carCount());
        player2WagonCount.set(gs.playerState(id.next()).carCount());

         */
        playerWagonCount.forEach((playerId, integerProperty) -> integerProperty.set(gs.playerState(id).carCount()));

        /*
        player1PointsCount.set(ps.claimPoints());
        player2PointsCount.set(gs.playerState(id.next()).claimPoints());

         */
        playerPointsCount.forEach((playerId, integerProperty) -> integerProperty.set(gs.playerState(id).claimPoints()));

        playerTicketsList.setAll(ps.tickets().toList());

        Card.ALL.forEach(card -> cardsCountMap.get(card).set(ps.cards().countOf(card)));

        for (Route route: routeStatusMap.keySet()) {
            boolean hasASister = ROUTE_PAIRS.containsKey(route);
            //le joueur est le joueur courant,
            //la route n'appartient à personne et, dans le cas d'une route double, sa voisine non plus,
            //le joueur a les wagons et les cartes nécessaires pour s'emparer de la route—ou en tout cas tenter de le faire s'il s'agit d'un tunnel.
            routeStatusMap.get(route).set(id == publicGameState.currentPlayerId() && routesProperties.get(route).get() == null && ps.canClaimRoute(route) && (!hasASister || routesProperties.get(ROUTE_PAIRS.get(route)).get() == null));
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
    public ReadOnlyIntegerProperty cartePourcentageProperty() {
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
        return playerTicketCount.get(id);
    }

    /**
     * Retourne les nombres de cartes du PlayerId
     * @param id PlayerId
     * @return Integer Property
     */

    public ReadOnlyIntegerProperty playerCardCountProperty(PlayerId id){
        return playerCardCount.get(id);
    }

    /**
     * Retourne les nombres de wagons du PlayerId en argument
     * @param id PlayerId
     * @return Integer Property
     */
    public ReadOnlyIntegerProperty playerWagonCountProperty(PlayerId id){
        return playerWagonCount.get(id);
    }

    /**
     * Retourne les nombres de points du PlayerId en argument
     * @param id PlayerId
     * @return Integer Property
     */
    public ReadOnlyIntegerProperty playerPointCountProperty(PlayerId id){
       return playerPointsCount.get(id);

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
