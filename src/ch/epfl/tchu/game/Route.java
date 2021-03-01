package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Route
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

    public enum Level{
        OVERGROUND,
        UNDERGROUND;
    }

    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        if(station1 == null || station2 == null || level == null){
            throw new NullPointerException("Station ou Level est null");
        }
        Preconditions.checkArgument(!station1.equals(station2));
        Preconditions.checkArgument(length <= Constants.MAX_ROUTE_LENGTH && length >= Constants.MIN_ROUTE_LENGTH);
        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.length = length;
        this.level = level;
        this.color = color;
    }

    public String Id() {
        return id;
    }

    public Station station1() {
        return station1;
    }

    public Station station2() {
        return station2;
    }

    public int length() {
        return length;
    }

    public Level level() {
        return level;
    }

    public Color color() {
        return color;
    }
    public List<Station> stations(){
        return List.of(station1, station2);
    }

    public Station stationOpposite(Station station){
        Preconditions.checkArgument(station.equals(station1)||station.equals(station2));
        if(station.equals(station1)){
            return station2;
        }else{
            return station1;
        }
    }

    public List<SortedBag<Card>> possibleClaimCard(){
       List<SortedBag<Card>> list = new ArrayList<>();
        if(color == null){
            for(Card card : Card.CARS){
                list.add(SortedBag.of(length, card));
            }
        }else{
            list.add(SortedBag.of(length, Card.of(color)));
        }

        if(level == Level.UNDERGROUND){
            if(color == null){
                for(int i = 1; i <= length; i++){
                    for(Card card : Card.CARS){
                        list.add(SortedBag.of(length - i, card, i, Card.LOCOMOTIVE));
                    }
                }
            }else{
                for(int i = 1; i <= length; i++){
                    list.add(SortedBag.of(length - i, Card.of(color), i, Card.LOCOMOTIVE));
                }
            }
        }
        return list;
    }

    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards){
        Preconditions.checkArgument(drawnCards.size()==3);
        Preconditions.checkArgument(this.level() == Level.UNDERGROUND);
        if(claimCards.countOf(Card.LOCOMOTIVE) == claimCards.size() ){
            return drawnCards.countOf(Card.LOCOMOTIVE);
        }else{
            Color claimColor;
            for(int i = 1; i<= claimCards.size();i++){
                if(claimCards.get(i) != Card.LOCOMOTIVE){
                    claimColor = claimCards.get(i).color();
                }
            }
            return drawnCards.countOf(Card.of(claimColor)) + drawnCards.countOf(Card.LOCOMOTIVE);
        }
    }

    public int claimPoints(){
        int points;
        switch(this.length()) {
            case 1:
                points = 1;
                break;
            case 2:
                points = 2;
                break;
            case 3:
                points = 4;
                break;
            case 4:
                points = 7;
                break;
            case 5:
                points = 10;
                break;
            case 6:
                points = 15;
                break;
            default:
                points = 0; // jamais dans ce cas
        }
        return points;
    }

}
