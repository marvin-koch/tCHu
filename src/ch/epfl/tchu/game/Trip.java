package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Class Trip
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */

public final class Trip {
    private final Station from,to;
    private final int points;

    /**
     * Construit un trajet d'un point de départ au point d?arivée
     * @param from list des gares de départ
     * @param to list des gares d'arrivées
     * @param points
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
     * Retourne liste de tout les trajets possibles entre les gares de départ et d'arrivée.
     * @param from list des gares de départ
     * @param to list des gares d'arrivées
     * @param points
     * @throws IllegalArgumentException si les listes from ou to sont vides ou le nombre de
     * points n'est pas strictement positif
     * @return liste contenant tout les trajets possibles
     */
    public List<Trip> all(List<Station> from, List<Station> to, int points){
        List<Trip> trip = new ArrayList<>();
        Preconditions.checkArgument(!from.isEmpty());
        Preconditions.checkArgument(!to.isEmpty());
        Preconditions.checkArgument(points>0);
        for (Station stationfrom : from)
        {
            for (Station stationto : to)
            {
                trip.add(new Trip(stationfrom,stationto,points));
            }
        }
        return trip;
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

    /**
     * Retourne le nombre de points du trajet pour la connectivité donnée
     * @param connectivity
     * @return le nombre de points
     */
    public int point(StationConnectivity connectivity){
        if(connectivity.connected(from,to)){
            return points;
        }else{
            return -points;
        }
    }

}