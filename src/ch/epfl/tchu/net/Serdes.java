package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * La classe Serdes non instanciable, contient la totalité des serdes utiles au projet.
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class Serdes {
    private Serdes(){}

    /**
     * Serde des Entiers
     */
    public static final Serde<Integer> INTEGER_SERDE = Serde.of(i -> Integer.toString(i), Integer::parseInt);

    /**
     * Serde des Chaînes
     */
    public static final Serde<String> STRING_SERDE = Serde.of(
            s -> Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8)),
            b -> new String((Base64.getDecoder().decode(b)), StandardCharsets.UTF_8));

    /**
     * Serde de PlayerID
     */
    public static final Serde<PlayerId> PLAYER_ID_SERDE= Serde.oneOf(PlayerId.ALL);

    /**
     * Serde de TurnKind
     */
    public static final Serde<Player.TurnKind> TURN_KIND_SERDE= Serde.oneOf(Player.TurnKind.ALL);

    /**
     * Serde de Card
     */
    public static final Serde<Card> CARD_SERDE= Serde.oneOf(Card.ALL);

    /**
     * Serde de Route
     */
    public static final Serde<Route> ROUTE_SERDE= Serde.oneOf(ChMap.routes());

    /**
     * Serde de Ticket
     */
    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    /**
     * Serde d'une liste de String
     */
    public static final Serde<List<String>> LIST_STRING_SERDE = Serde.listOf(STRING_SERDE,",");

    /**
     * Serde d'une liste de Card
     */
    public static final Serde<List<Card>> LIST_CARD_SERDE = Serde.listOf(CARD_SERDE,",");

    /**
     * Serde d'une liste de Route
     */
    public static final Serde<List<Route>> LIST_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, ",");

    /**
     * Serde d'un multiensemble de Card
     */
    public static final Serde<SortedBag<Card>> SORTEDBAG_CARD_SERDE = Serde.bagOf(CARD_SERDE, ",");
    /**
     * Serde d'un SortedBag de Ticket
     */
    public static final Serde<SortedBag<Ticket>> SORTEDBAG_TICKET_SERDE = Serde.bagOf(TICKET_SERDE, ",");

    /**
     * Serde d'une liste de SortedBag de Card
     */
    public static final Serde<List<SortedBag<Card>>> LIST_SORTEBAG_CARD_SERD = Serde.listOf(SORTEDBAG_CARD_SERDE,";");

    /**
     *  Serde d'un PublicCardState
     */
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = new Serde<PublicCardState>() {
        @Override
        public String serialize(PublicCardState publicCardState) {

            return String.join(";",
                    LIST_CARD_SERDE.serialize(publicCardState.faceUpCards()),
                    INTEGER_SERDE.serialize(publicCardState.deckSize()),
                    INTEGER_SERDE.serialize(publicCardState.discardsSize()));
        }

        @Override
        public PublicCardState deserialize(String s) {
            String[] splitString = s.split(Pattern.quote(";"), -1);
            Preconditions.checkArgument(splitString.length == 3);
            return new PublicCardState(LIST_CARD_SERDE.deserialize(splitString[0]),
                    INTEGER_SERDE.deserialize(splitString[1]),
                    INTEGER_SERDE.deserialize(splitString[2]));
        }
    };

    /**
     * Serde d'un PublicPlayerState
     */
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = new Serde<PublicPlayerState>() {
        @Override
        public String serialize(PublicPlayerState publicPlayerState) {
            return String.join(";", INTEGER_SERDE.serialize(publicPlayerState.ticketCount()),
                    INTEGER_SERDE.serialize(publicPlayerState.cardCount()),
                    LIST_ROUTE_SERDE.serialize(publicPlayerState.routes()));

        }

        @Override
        public PublicPlayerState deserialize(String s) {
            String[] splitString = s.split(Pattern.quote(";"), -1);
            Preconditions.checkArgument(splitString.length == 3);
            return new PublicPlayerState(INTEGER_SERDE.deserialize(splitString[0]),
                    INTEGER_SERDE.deserialize(splitString[1]),
                    LIST_ROUTE_SERDE.deserialize(splitString[2]));
        }
    };

    /**
     * Serde d'un PlayerState
     */
    public static final Serde<PlayerState> PLAYER_STATE_SERDE = new Serde<PlayerState>() {
        @Override
        public String serialize(PlayerState playerState) {
            return String.join(";",SORTEDBAG_TICKET_SERDE.serialize(playerState.tickets()),
                    SORTEDBAG_CARD_SERDE.serialize(playerState.cards()),
                    LIST_ROUTE_SERDE.serialize(playerState.routes()));
        }

        @Override
        public PlayerState deserialize(String s) {
            String[] splitString = s.split(Pattern.quote(";"), -1);
            Preconditions.checkArgument(splitString.length == 3);
            return new PlayerState(SORTEDBAG_TICKET_SERDE.deserialize(splitString[0]),
                    SORTEDBAG_CARD_SERDE.deserialize(splitString[1]),
                    LIST_ROUTE_SERDE.deserialize(splitString[2]));
        }
    };

    /**
     * Serde d'un publicGameState
     */
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = new Serde<PublicGameState>() {
        @Override
        public String serialize(PublicGameState publicGameState) {
            return String.join(":", INTEGER_SERDE.serialize(publicGameState.ticketsCount()),
                    PUBLIC_CARD_STATE_SERDE.serialize(publicGameState.cardState()),
                    PLAYER_ID_SERDE.serialize(publicGameState.currentPlayerId()),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(publicGameState.playerState(PlayerId.PLAYER_1)),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(publicGameState.playerState(PlayerId.PLAYER_2)),
                    PLAYER_ID_SERDE.serialize(publicGameState.lastPlayer()));
        }

        @Override
        public PublicGameState deserialize(String s) {
            String[] splitString = s.split(Pattern.quote(":"), -1);
            Preconditions.checkArgument(splitString.length == 6);
            Map<PlayerId, PublicPlayerState> map = new HashMap<>();
            map.put(PlayerId.PLAYER_1, PUBLIC_PLAYER_STATE_SERDE.deserialize(splitString[3]));
            map.put(PlayerId.PLAYER_2,PUBLIC_PLAYER_STATE_SERDE.deserialize(splitString[4]));
            return new PublicGameState(INTEGER_SERDE.deserialize(splitString[0]),
                    PUBLIC_CARD_STATE_SERDE.deserialize(splitString[1]),
                    PLAYER_ID_SERDE.deserialize(splitString[2]),
                    map,
                    PLAYER_ID_SERDE.deserialize(splitString[5]));
        }
    };

}
