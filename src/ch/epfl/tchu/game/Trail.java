package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Trail
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */

public final class Trail {
    private final int length;
    private final Station station1;
    private final Station station2;
    private final List<Route> routes;

    private Trail(Station station1, Station station2, List<Route> routes) {
        this.station1 = station1;
        this.station2 = station2;
        this.routes = routes;
        int lengthBuilder = 0;
        for(int i = 0; i<= routes.size(); i++){
            lengthBuilder += routes.get(i).length();
        }
        length = lengthBuilder;
    }

    public Trail longest(List<Route> routes){
        Trail longestTrail = null;
        int longestLength = 0;
        List<Trail> cs = new ArrayList<>();
        for(Route route : routes){
            cs.add(new Trail(route.station1(), route.station2(), List.of(route)));
        }
        if(routes.isEmpty()){
            return new Trail(null,null, null);
        }else{
            while(!cs.isEmpty()){
                List<Trail> listeVide = new ArrayList<>();
                for(Trail trail : cs) {
                    for (Route route : routes) {
                        if ((trail.station2.equals(route.station1()) || trail.station2.equals(route.station2())) && !trail.routes.contains(route)) {
                            List<Route> newRoute = new ArrayList(trail.routes);
                            newRoute.add(route);
                            if(trail.station2.equals(route.station1())){
                                listeVide.add(new Trail(trail.station1, route.station2(), newRoute));
                            }else{
                                listeVide.add(new Trail(trail.station1, route.station1(), newRoute));
                            }
                        }
                    }
                }
               for(Trail trail : listeVide) {
                   if(trail.length() > longestLength){
                       longestTrail = trail;
                       longestLength = trail.length();
                   }
               }
               cs = new ArrayList<>(listeVide);
            }
        }
        return longestTrail;
    }

    public int length(){
        return length;
    }

    public Station station1(){
        if(length == 0){
            return null;
        }else {
            return station1;
        }
    }
    public Station station2(){
        if(length == 0){
            return null;
        }else {
            return station2;
        }
    }


    @Override
    public String toString() {
        //return station1.toString()+" - "+ station2.toString();
        String string = station1.toString();
        for(int i = 0; i<= routes.size(); i++){
            string += " - ";
            string += routes.get(i).station2().toString();
        }
        string+= " ("+length+")";
        return string;

    }


}
