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
        List<Trail> trails = new ArrayList<>();
        for(Route route : routes){
            trails.add(new Trail(route.station1(), route.station2(), List.of(route)));
        }
        List<Route> cs = new ArrayList(routes);
        if(routes.isEmpty()){
            return new Trail(null,null, null);
        }else{
            while(!cs.isEmpty()){
                List<Route> routesVide = new ArrayList<>();
                for(Trail trail : trails){

                }
            }
        }
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
