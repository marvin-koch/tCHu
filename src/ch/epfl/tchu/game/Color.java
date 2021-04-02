package ch.epfl.tchu.game;

import java.util.List;

/**
 * Type énuméré Color, répresente les huits couleurs dans le jeu
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */

public enum Color {
    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    /**
     * Retourne une liste de toutes les couleurs
     */
    public static final List<Color> ALL = List.of(Color.values());
    /**
     * Retourne le nombre de couleurs
     */
    public static final int COUNT = ALL.size();


}
