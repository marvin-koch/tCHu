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
    public static final List<Card> CARS = ALL.subList(0, 7);

    private Card(Color color){
        this.color = color;
    }

    public static Card of(Color color){
        for(int i = 0; i < COUNT; i++){
            if (color == ALL.get(i).color()){
                return ALL.get(i);
            }
        }
        return null;
    }

    public Color color(){
        return color;
    }
}
