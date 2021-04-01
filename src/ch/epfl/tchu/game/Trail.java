package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    /**
     * Constructeur privée de Trail
     * @param station1 station 1
     * @param station2 station 2
     * @param routes liste de route
     */
    private Trail(Station station1, Station station2, List<Route> routes) {
        this.station1 = station1;
        this.station2 = station2;
        this.routes = routes;
        int length = 0;
        if(!routes.isEmpty()){
            length = routes.stream()
                    .map(Route::length)
                    .reduce(0, Integer::sum);
        }
        this.length = length;
    }

    /**
     * Retourne le plus long chemin du réseau constitué des routes données
     * @param routes liste de route
     * @return le plus long Trail
     */
    public static Trail longest(List<Route> routes){
        Trail longestTrail = new Trail(null,null, new ArrayList<>());
        int longestLength = 0;

        List<Trail> cs = routes.stream()
                .flatMap(route -> Stream.of(new Trail(route.station1(), route.station2(), List.of(route)),
                        new Trail(route.station2(), route.station1(), List.of(route))))
                .collect(Collectors.toList());

        if(!cs.isEmpty())
            longestTrail = Collections.max(cs, Comparator.comparingInt(t -> t.length));

        if(routes.isEmpty()){
            return new Trail(null,null, new ArrayList<>());
        }else{
            while(!cs.isEmpty()){
                List<Trail> listeVide = new ArrayList<>();
                for(Trail trail : cs) {
                    for (Route route : routes) {
                        if ((trail.station2.equals(route.station1()) || trail.station2.equals(route.station2())) && !trail.routes.contains(route)) {
                            List<Route> newRoute = new ArrayList<>(trail.routes);
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

    /**
     * Retourne la longueur du chemin
     * @return length
     */
    public int length(){
        return length;
    }

    /**
     * Retourne la première gare du chemian, ou null ssi le chemin est de longueur zéro
     * @return station 2
     */
    public Station station1(){
        return length == 0 ? null : station1;
    }

    /**
     * Retourne la deuxième gare du chemin, ou null ssi le chemin est de longueur zéro
     * @return station 1
     */
    public Station station2(){
        return length == 0 ? null : station2;
    }

    /**
     * Retourne la représentation textuelle du chemin
     * @return String de la représentation textuelle
     */
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        if(!routes.isEmpty()){
            string.append(station1.toString());
            Station actualStation = station1;
            for(Route route : routes){
                string.append(" - ");
                if(route.station1().equals(actualStation)){
                    string.append(route.station2().toString());
                    actualStation = route.station2();
                }else{
                    string.append(route.station1().toString());
                    actualStation = route.station1();
                }
            }
        }else{
            string.append("Empty Trail");
        }
        string.append(" (")
                .append(length)
                .append(")");

        return string.toString();
    }
}
