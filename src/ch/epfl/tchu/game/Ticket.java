package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * La classe Ticket publique, finale et immuable, représente un billet
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class Ticket implements Comparable<Ticket>{
    private final List<Trip> trips;
    private final String text;

    /**
     * Calcule et la représentation textuelle du ticket
     * @param trips liste des trajets
     * @return le string qui représentent le ticket
     */
    private static String computeText(List<Trip> trips){
        Trip firstTrip = trips.get(0);
        if(trips.size() == 1){
            return String.format("%s - %s (%s)", firstTrip.from(), firstTrip.to(), firstTrip.points());
        }else{
            TreeSet<String> tree = trips.stream()
                    .map(trip -> trip.to().name() + " (" + trip.points() + ")")
                    .collect(Collectors.toCollection(TreeSet::new));

            return String.format("%s - {%s}", firstTrip.from().name(),
                    String.join(", ", tree));
        }
    }

    /**
     * Contructeur primaire de Ticket
     * @param trips liste de trips du ticket
     * @throws IllegalArgumentException si la liste de trips est vide
     */
    public Ticket(List<Trip> trips){
        Preconditions.checkArgument(!trips.isEmpty());
        trips.forEach(trip -> Preconditions.checkArgument(trip.from().name().equals(trips.get(0).from().name())));
        this.trips = new ArrayList<>(trips);
        text = computeText(trips);
    }

    /**
     * Constructeur secondaire de Ticket, utilisé s'il contient qu'un trajet
     * @param from station de départ du trajet
     * @param to station d'arrivée du trajet
     * @param points nombre de points du trajet
     */
    public Ticket(Station from, Station to, int points){
        this(List.of(new Trip(from, to, points)));
    }

    /**
     * Retourne la représentation textuelle
     * @return text représentation textuelle
     */
    public String text() { return text; }

    /**
     * Calcule et retourne le nombre de points du ticket
     * @param connectivity instance qui implemente StationConnectivity
     * @return le nombre de points
     */
    public int points(StationConnectivity connectivity){
        return trips.stream()
                .map(trip -> trip.points(connectivity))
                .max(Integer::compare)
                .orElse(0);
    }

    /**
     * Méthode qui compare 2 tickets
     * @param that le ticket auquel il compare
     * @return retourne un entier négatif quelconque si la première vient avant la seconde dans l'ordre alphabétique,
     * zéro si les deux sont égales, et un entier positif quelconque sinon.
     */
    @Override
    public int compareTo(Ticket that) {
        return this.text()
                .compareTo(that.text());
    }

    /**
     * Rédefinition de toString()
     * @return text
     */
    @Override
    public String toString(){
        return text;
    }
}
