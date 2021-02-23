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

    Ticket(List<Trip> trips){
        Preconditions.checkArgument(!trips.isEmpty());
        boolean samefrom = true;
        for (Trip trip: trips) {// trop de trip ?
            Preconditions.checkArgument(trip.from().equals(trips.get(0).from()));
        }
        this.trips = new ArrayList<>(trips);
        text = computeText(trips);
    }

    Ticket(Station from, Station to, int points){
        this(List.of(new Trip(from, to, points)));
    }

    @Override
    public int compareTo(Ticket that) {
        return this.text().compareTo(that.text());
    }

    public String text() {
        return text;
    }


    private static String computeText(List<Trip> trips){
        if(trips.size() == 1){
            return trips.get(0).from() +" - "+ trips.get(0).to() +" ("+trips.get(0).points() +")";
        }else{
            TreeSet<String> tree = new TreeSet<>();
            for(Trip trip : trips){
                tree.add(trip.to().name()+"("+trip.points()+")");
            }
            return String.format("%s - {%s}", trips.get(0).from().name(),
                    String.join(", ", tree));
        }
    }

}
