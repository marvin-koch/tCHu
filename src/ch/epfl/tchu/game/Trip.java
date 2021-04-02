package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * La classe Trip finale et immuable, représente ce que nous avons appelé un trajet.*
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */

public final class Trip {
    private final Station from,to;
    private final int points;

    /**
     * Retourne liste de tout les trajets possibles entre les gares de départ et d'arrivée.
     * @param from list des gares de départ
     * @param to list des gares d'arrivées
     * @param points points
     * @throws IllegalArgumentException si les listes from ou to sont vides ou le nombre de
     * points n'est pas strictement positif
     * @return liste contenant tout les trajets possibles
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points){
        List<Trip> trip = new ArrayList<>();
        Preconditions.checkArgument(!from.isEmpty());
        Preconditions.checkArgument(!to.isEmpty());
        Preconditions.checkArgument(points>0);
        for (Station stationfrom : from) {
            for (Station stationto : to) {
                trip.add(new Trip(stationfrom,stationto,points));
            }
        }
        return trip;
    }

    /**
     * Construit un trajet d'un point de départ au point d?arivée
     * @param from list des gares de départ
     * @param to list des gares d'arrivées
     * @param points points
     * @throws IllegalArgumentException si le nombre de points est négatif
     * @throws NullPointerException si la liste est null
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points >0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Retourne le nombre de points du trajet pour la connectivité donnée
     * @param connectivity instance qui implement StationConnectivity
     * @return le nombre de points
     */
    public int points(StationConnectivity connectivity){
        return connectivity.connected(from, to) ? points : -points;
    }

    /**
     * Retourne from
     * @return from
     */
    public Station from(){
        return from;
    }

    /**
     * Retourne to
     * @return to
     */
    public Station to(){
        return to;
    }

    /**
     * Retourne points
     * @return points
     */
    public int points(){
        return  points;
    }

}