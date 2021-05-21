package ch.epfl.tchu.game;

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
    public static final List<PlayerId> ALL = List.of(PlayerId.values());

    /**
     * Retourne le nombre de joueur
     */
    public static final int COUNT = ALL.size();

    /**
     * retourne l'identité du joueur qui suit celui auquel on l'applique, c-à-d PLAYER_2 pour PLAYER_1, et PLAYER_1 pour PLAYER_2
     * @return PlayerId
     */
    public PlayerId next(){
        return this.equals(PLAYER_1) ? PLAYER_2 : PLAYER_1;
    }
}

