package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

public final class Ticket implements Comparable<Ticket>{
    // abstract ???
    private Station from,to;
    private int points;
    // il faut peut-être mettre des listes je sais pas trop

    Ticket(List<Trip> trips){
        Preconditions.checkArgument(!trips.isEmpty());
        // apres pas compris comment check les noms
    }
    Ticket(Station from, Station to, int points){

    }
    @Override
    public int compareTo(Ticket that) {
        return 0; // c'est quoi l'entier ?
    }

    public String text(){
        return from+" - "+ to+" ("+points+")";// ville à ville du coup forcèment abstract nn ?
    }
}
