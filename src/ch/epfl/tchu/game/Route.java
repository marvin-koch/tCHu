package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * La classe Route du publique, finale et immuable, représente une route reliant deux villes voisinese
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */

public final class Route {
    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * Enum Level
     */
    public enum Level{
        OVERGROUND,
        UNDERGROUND
    }

    /**
     * Constructeur de route
     * @param id identité
     * @param station1 gare de départ
     * @param station2 gare d'arrivée
     * @param length longueur
     * @param level niveau
     * @param color couleur
     * @throws NullPointerException si station 1 ou 2 ou level ou identité sont nuls
     * @throws IllegalArgumentException si les deux gares sont égales ou si la longueur n'est pas comprise dans les limites acceptables
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!station1.equals(station2));
        Preconditions.checkArgument(length <= Constants.MAX_ROUTE_LENGTH && length >= Constants.MIN_ROUTE_LENGTH);
        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.length = length;
        this.level = Objects.requireNonNull(level);
        this.color = color;
    }

    /**
     * Retourne id
     * @return id
     */
    public String id() {
        return id;
    }

    /**
     * Retourne station 1
     * @return station 1
     */
    public Station station1() {
        return station1;
    }

    /**
     * Retourne station 2
     * @return station 2
     */
    public Station station2() {
        return station2;
    }

    /**
     * Retourne length
     * @return length
     */
    public int length() {
        return length;
    }

    /**
     * Retourne level
     * @return level
     */
    public Level level() {
        return level;
    }

    /**
     * Retourne color
     * @return color
     */
    public Color color() {
        return color;
    }

    /**
     * Retourne une liste des stations dans l'ordre du'entrée du constructeur
     * @return List<Station>
     */
    public List<Station> stations(){
        return List.of(station1, station2);
    }

    /**
     * Retourne la gare de la route qui n'est pas celle donnée
     * @throws IllegalArgumentException si la gare donnée n'est ni la première ni la seconde gare de la route
     * @return station
     */
    public Station stationOpposite(Station station){
        Preconditions.checkArgument(station.equals(station1)||station.equals(station2));
        return station.equals(station1) ? station2 : station1;
    }

    /**
     * Retourne la liste de tous les ensemble de cartes qui pourraient étre joués pour (tenter de) s'emparer de la route,
     * trié par ordre croissant de nombre de carte locomotive, puis par couleur
     * @return liste de tous les ensembles de cartes stockés dans des SortedBag
     */
    public List<SortedBag<Card>> possibleClaimCards(){

        List<SortedBag<Card>> list = new ArrayList<>();
        /*
        if(color == null){
            list = Card.CARS.stream()
                    .map(card -> SortedBag.of(length, card))
                    .collect(Collectors.toList());

        }else{
            list.add(SortedBag.of(length, Card.of(color)));
        }

        if(level == Level.UNDERGROUND){
            if(color == null){
                for(int i = 1; i < length; i++){
                    int finalI = i;
                    list.addAll(Card.CARS.stream()
                            .map(card -> SortedBag.of(length - finalI, card, finalI, Card.LOCOMOTIVE))
                            .collect(Collectors.toList()));
                }
            }else{
                for(int i = 1; i < length; i++){
                    list.add(SortedBag.of(length - i, Card.of(color), i, Card.LOCOMOTIVE));
                }
            }
            list.add(SortedBag.of(length, Card.LOCOMOTIVE));
        }
        */
        List<Card> cards = color == null ? Card.CARS : List.of(Card.of(color));
        list = cards.stream()
                .map(card -> SortedBag.of(length, card))
                .collect(Collectors.toList());
        if(level == Level.UNDERGROUND){
            for(int i = 1; i < length; i++){
                int finalI = i;
                list.addAll(cards.stream()
                        .map(card -> SortedBag.of(length - finalI, card, finalI, Card.LOCOMOTIVE))
                        .collect(Collectors.toList()));
            }
            list.add(SortedBag.of(length, Card.LOCOMOTIVE));
        }
        return list;
    }


    /**
     * Retourne le nombre de cartes additionnelles à jouer pour s'emparer de la route (en tunnel)
     * @param claimCards cartes posé initialement par le joueur
     * @param drawnCards les trois cartes tirées du sommet de la pioche
     * @throws IllegalArgumentException si la route à la route à llaquelle on l'applique n'est pass un tunnel,
     * ou si drawnCards ne contient pas exactement 3 cartes
     * @return les nombre de cartes additionnelles
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards){
        Preconditions.checkArgument(drawnCards.size()==Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(this.level() == Level.UNDERGROUND);
        if(claimCards.countOf(Card.LOCOMOTIVE) == claimCards.size() ){
            return drawnCards.countOf(Card.LOCOMOTIVE);
        }else{
            Color claimColor = claimCards.stream()
                    .map(Card::color)
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(null);

            return drawnCards.countOf(Card.of(claimColor)) + drawnCards.countOf(Card.LOCOMOTIVE);
        }
    }


    /**
     * Retourne le nombre de points de construction qu'un joueur obtient lorsqu'il s'empare de la route
     * @return le nombre de point
     */
    public int claimPoints(){
        return Constants.ROUTE_CLAIM_POINTS.get(this.length());
    }

}
