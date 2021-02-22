package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Trip {
    private Station from,to;
    private int points;

    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points >0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    public List<Trip> all(List<Station> from, List<Station> to, int points){
        List<Trip> trip = new ArrayList<>();
        Preconditions.checkArgument(!from.isEmpty());
        Preconditions.checkArgument(!to.isEmpty());
        Preconditions.checkArgument(points>0);
        for (Station stationfrom : from)
        {
            for (Station stationto : to)
            {
                trip.add(new Trip(stationfrom  ,stationto,points)); // c'est vraiment point ?
            }
        }
        return trip;
    }

    public Station from(){
        return from;
    }

    public Station to(){
        return to;
    }

    public int points(){
        return  points;
    }

    public int point(StationConnectivity connectivity){
        if(connectivity.connected(from,to)){
            return points;
        }else{
            return -points;
        }
    }

}