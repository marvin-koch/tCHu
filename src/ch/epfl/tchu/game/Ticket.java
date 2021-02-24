package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Class Ticket
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */

public final class Ticket implements Comparable<Ticket>{
    private final List<Trip> trips;
    private final String text;

    /**
     * Contructeur primaire de Ticket
     * @param trips liste de trips du billet
     * @throws IllegalArgumentException si la liste de trips est vide
     */
    public Ticket(List<Trip> trips){
        Preconditions.checkArgument(!trips.isEmpty());
        boolean samefrom = true;
        for (Trip trip: trips) {
            Preconditions.checkArgument(trip
                    .from()
                    .name()
                    .equals(trips.get(0).from().name()));
        }
        this.trips = new ArrayList<>(trips);
        text = computeText(trips);
    }

    /**
     * Constructeur secondaire de Ticket, utilisé s'il contient qu'un trajet
     * @param from station de départ trajet
     * @param to station d'arrivée trajet
     * @param points nombre de points du trajet
     */
    public Ticket(Station from, Station to, int points){
        this(List.of(new Trip(from, to, points)));
    }

    @Override
    public int compareTo(Ticket that) {
        return this
                .text()
                .compareTo(that.text());
    }

    /**
     * Retourne la représentation textuelle
     * @return text représentation textuelle
     */
    public String text() {
        return text;
    }

    /**
     * Calcule et la représentation textuelle du billet
     * @param trips liste des trajets
     * @return le string qui représentent le billet
     */
    private static String computeText(List<Trip> trips){
        if(trips.size() == 1){
            return trips.get(0).from() +" - "+ trips.get(0).to() +" ("+trips.get(0).points() +")";
        }else{
            TreeSet<String> tree = new TreeSet<>();
            for(Trip trip : trips){
                tree.add(trip.to().name()+" ("+trip.points()+")");
            }
            return String.format("%s - {%s}", trips.get(0).from().name(),
                    String.join(", ", tree));
        }
    }

    /**
     * Calcule et retourne le nombre de points du billet
     * @param connectivity
     * @return le nombre de points
     */
    public int points(StationConnectivity connectivity){
        int maximum = trips.get(0).points(connectivity);
        for(Trip trip : trips){
            if(trip.points(connectivity) > maximum){
                maximum = trip.points(connectivity);
            }
        }
        return maximum;
    }
}
