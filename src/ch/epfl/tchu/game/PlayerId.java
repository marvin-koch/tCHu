package ch.epfl.tchu.game;

import ch.epfl.tchu.gui.GraphicalPlayer;
import ch.epfl.tchu.gui.GraphicalPlayerAdapter;
import ch.epfl.tchu.gui.ServerMain;

import java.util.List;

/**
 * Le type énuméré PlayerId représente l'identité d'un joueur.
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2,
    PLAYER_3;

    /**
     * Retourne une liste de tout les joueurs
     */
    public static List<PlayerId> ALL() {
        return ServerMain.is3Players ? List.of(PlayerId.values()) : List.of(PlayerId.values()).subList(0, 2);
    }

    /**
     * Retourne le nombre de joueur
     */
    public static int COUNT(){
        return ALL().size();
    }

    /**
     * retourne l'identité du joueur qui suit celui auquel on l'applique, c-à-d PLAYER_2 pour PLAYER_1, et PLAYER_1 pour PLAYER_2
     * @return PlayerId
     */
    public PlayerId next(){
        switch (this){
            case PLAYER_1:
                return PLAYER_2;
            case PLAYER_2:
                return ServerMain.is3Players ? PLAYER_3: PLAYER_1;
            case PLAYER_3:
                return PLAYER_1;
            default:
                throw new Error();
        }
    }

    public PlayerId doubleNext() {
        return next().next();
    }
}

