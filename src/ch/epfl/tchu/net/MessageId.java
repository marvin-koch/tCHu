package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Color;

import java.util.List;

/**
 * Type énuméré MessageId énumère les types de messages que le serveur peut envoyer aux clients.
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public enum MessageId {
    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS,
    END;


    /**
     * Retourne une liste de toutes les messages
     */
    public static final List<MessageId> ALL = List.of(MessageId.values());
    /**
     * Retourne le nombre de messages
     */
    public static final int COUNT = ALL.size();


}

