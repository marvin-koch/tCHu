package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * Type énuméré Card, représente les huits différents types de cartes du jeu
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */

public enum Card {
    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    private final Color color;
    /**
     * Retourne une liste de toutes les cartes
     */
    public static final List<Card> ALL = List.of(Card.values());
    /**
     * Retourne le nombre de carte
     */
    public static final int COUNT = ALL.size();
    /**
     * Retourne une liste de toutes les cartes sauf Locomotive
     */
    public static final List<Card> CARS = List.of(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE);

    /**
     * Constructeur de Card
     * @param color couleur
     */
    Card(Color color){
        this.color = color;
    }

    /**
     * Retourne la Carte a laquel correspond la couleur
     * @param color couleur
     * @return la carte
     */
    public static Card of(Color color){
        Preconditions.checkArgument(color != null);
       return ALL.stream()
               .filter(card -> color.equals(card.color()))
               .findAny()
               .orElse(null);
    }

    /**
     * Retourne la couleur
     * @return la couleur
     */
    public Color color(){
        return color;
    }
}
