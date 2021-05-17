package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * La classe PublicPlayerState publique et immuable, représente la partie publique de l'état d'un joueur
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */

public class PublicPlayerState {
    private final int ticketCount,cardCount,claimPoint,wagonCount;
    private final List<Route> routes;

    /**
     * Construit l'état public d'un joueur possédant le nombre de billets et de cartes donnés, et s'étant emparé des routes données
     * @param ticketCount le nombre de billets
     * @param cardCount cartes donnés
     * @param routes les routes dont il s'est emparé
     * @throws IllegalArgumentException si le nombre de billets ou le nombre de cartes est strictement négatif (< 0)
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes){
        Preconditions.checkArgument(ticketCount>= 0 && cardCount >= 0);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);

        wagonCount = Constants.INITIAL_CAR_COUNT - routes.stream()
                .mapToInt(Route::length)
                .sum();

        claimPoint = routes.stream()
                .mapToInt(Route::claimPoints)
                .sum();
    }

    /**
     * Retourne le nombre de billets que possède le joueur
     * @return ticketCount
     */
    public int ticketCount(){
        return ticketCount;
    }

    /**
     * Retourne le nombre de cartes que possède le joueur
     * @return cardCount
     */
    public int cardCount(){
        return cardCount;
    }

    /**
     * Retourne les routes dont le joueur s'est emparé
     * @return routes
     */
    public List<Route> routes(){
        return List.copyOf(routes);
    }

    /**
     * Retourne le nombre de wagons que possède le joueur
     * @return wagonCount
     */
    public int carCount(){
        return wagonCount;
    }


    /**
     * Retourne le nombre de points de construction obtenus par le joueur.
     * @return claimPoint
     */
    public int claimPoints(){
        return claimPoint;
    }
    
}
