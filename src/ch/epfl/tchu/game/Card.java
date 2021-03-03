package ch.epfl.tchu.game;

import java.util.List;

/**
 * Type énuméré Card
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
    public static final List<Card> ALL = List.of(Card.values());
    public static final int COUNT = ALL.size();
    /**
     * Retourne une liste de toutes les cartes sauf Locomotive
     */
    public static final List<Card> CARS = List.of(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE);

    /**
     * Constructeur de Card
     * @param color
     */
    private Card(Color color){
        this.color = color;
    }

    /**
     * Retourne la Carte a laquel correspond la couleur
     * @param color
     * @return la carte
     */
    public static Card of(Color color){
        for(int i = 0; i < COUNT; i++){
            if (color.equals(ALL.get(i).color())){
                return ALL.get(i);
            }
        }
        return null;
    }

    /**
     * Retourne la couleur
     * @return la couleur
     */
    public Color color(){
        return color;
    }
}
