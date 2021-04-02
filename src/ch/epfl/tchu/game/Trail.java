package ch.epfl.tchu.game;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static ch.epfl.tchu.gui.StringsFr.*;
/**
 * La classe Trail publique, finale et immuable, représente un chemin dans le réseau d'un joueurl
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
        this.length = routes.stream()
                .map(Route::length)
                .reduce(0, Integer::sum);
    }

    /**
     * Retourne le plus long chemin du réseau constitué des routes données
     * @param routes liste de route
     * @return le plus long Trail
     */
    public static Trail longest(List<Route> routes){
        if(routes.isEmpty()){
            return new Trail(null,null, new ArrayList<>());
        }else{
            List<Trail> cs = routes.stream()
                    .flatMap(route -> Stream.of(new Trail(route.station1(), route.station2(), List.of(route)),
                            new Trail(route.station2(), route.station1(), List.of(route))))
                    .collect(Collectors.toList());
            Trail longestTrail = Collections.max(cs, Comparator.comparingInt(t -> t.length));

            while(!cs.isEmpty()){
                List<Trail> listeVide = new ArrayList<>();
                for(Trail trail : cs) {
                    for (Route route : routes) {
                        if ((trail.station2.equals(route.station1()) || trail.station2.equals(route.station2())) && !trail.routes.contains(route)) {
                            List<Route> newRoute = new ArrayList<>(trail.routes);
                            newRoute.add(route);
                            Station station = (trail.station2.equals(route.station1())) ? route.station2() : route.station1();
                            listeVide.add(new Trail(trail.station1, station, newRoute));
                        }
                    }
                }
                Trail finalLongestTrail = longestTrail;
                longestTrail = listeVide.stream()
                        .filter(trail -> trail.length() > finalLongestTrail.length())
                        .max(Comparator.comparingInt(Trail::length))
                        .orElse(longestTrail);

                cs = new ArrayList<>(listeVide);
            }
            return longestTrail;
        }
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
            string.append(station1.toString())
                    .append(routes.stream()
                            .map(route -> route.station1().equals(station1) ? route.station2() : route.station1())
                            .map(Station::toString)
                            .collect(Collectors.joining(EN_DASH_SEPARATOR)));
        }else{
            string.append("Empty Trail");
        }
        string.append(" (")
                .append(length)
                .append(")");

        return string.toString();
    }
}
