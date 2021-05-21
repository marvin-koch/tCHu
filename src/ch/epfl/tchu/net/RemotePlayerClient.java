package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import static ch.epfl.tchu.net.Serdes.*;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * La classe instanciable RemotePlayerClient représente un client de joueur distant.
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */

public final class RemotePlayerClient{
    private final Player player;
    private final String name;
    private final int port;

    /**
     * Constructeur de RemotePlayerClient
     * @param player Player
     * @param name le nom
     * @param port nombre du port
     */
    public RemotePlayerClient(Player player, String name, int port){
        this.player = player;
        this.name = Objects.requireNonNull(name);
        this.port = port;
    }

    /**
     * Cette méthode effectue une boucle durant laquelle elle:
     *    - attend un message en provenance du mandataire,
     *    - le découpe en utilisant le caractère d'espacement comme séparateur,
     *    - détermine le type du message en fonction de la première chaîne résultant du découpage,
     *      en fonction de ce type de message, désérialise les arguments, appelle la méthode correspondante du joueur;
     *      si cette méthode retourne un résultat, le sérialise pour le renvoyer au mandataire en réponse.
     */
    public void run(){
        try(Socket s = new Socket(name, port);
             BufferedReader r =
                     new BufferedReader(
                             new InputStreamReader(s.getInputStream(),
                                     US_ASCII));
             BufferedWriter w =
                     new BufferedWriter(
                             new OutputStreamWriter(s.getOutputStream(),
                                     US_ASCII))) {

            String line;
            while((line = r.readLine())!= null){
                String[] strings = line.split(Pattern.quote(" "));
                switch (MessageId.valueOf(strings[0])){
                    case INIT_PLAYERS:
                        List<String> playerNames = LIST_STRING_SERDE.deserialize(strings[2]);
                        Map<PlayerId, String> mapNames = new EnumMap<>(PlayerId.class);
                        mapNames.put(PlayerId.PLAYER_1, playerNames.get(0));
                        mapNames.put(PlayerId.PLAYER_2, playerNames.get(1));
                        player.initPlayers(PLAYER_ID_SERDE.deserialize(strings[1]), mapNames);
                        break;
                    case RECEIVE_INFO:
                        player.receiveInfo(STRING_SERDE.deserialize(strings[1]));
                        break;
                    case UPDATE_STATE:
                        player.updateState(PUBLIC_GAME_STATE_SERDE.deserialize(strings[1]),PLAYER_STATE_SERDE.deserialize(strings[2]));
                        break;
                    case CHOOSE_INITIAL_TICKETS:
                        writeMessage(SORTEDBAG_TICKET_SERDE.serialize(player.chooseInitialTickets()), w);
                        break;
                    case NEXT_TURN:
                        writeMessage(TURN_KIND_SERDE.serialize(player.nextTurn()), w);
                        break;
                    case CHOOSE_TICKETS:
                        SortedBag<Ticket> options = SORTEDBAG_TICKET_SERDE.deserialize(strings[1]);
                        writeMessage(SORTEDBAG_TICKET_SERDE.serialize(player.chooseTickets(options)), w);
                        break;
                    case DRAW_SLOT:
                        writeMessage(INTEGER_SERDE.serialize(player.drawSlot()), w);
                        break;
                    case ROUTE:
                        writeMessage(ROUTE_SERDE.serialize(player.claimedRoute()), w);
                        break;
                    case CARDS:
                        writeMessage(SORTEDBAG_CARD_SERDE.serialize(player.initialClaimCards()), w);
                        break;
                    case CHOOSE_ADDITIONAL_CARDS:
                        List<SortedBag<Card>> listSortedBagCards = LIST_SORTEDBAG_CARD_SERDE.deserialize(strings[1]);
                        writeMessage(SORTEDBAG_CARD_SERDE.serialize(player.chooseAdditionalCards(listSortedBagCards)), w);
                        break;
                    case SET_INITIAL_TICKETS:
                        player.setInitialTicketChoice(SORTEDBAG_TICKET_SERDE.deserialize(strings[1]));
                        break;
                    case END:
                        writeMessage(INTEGER_SERDE.serialize(player.endMenu(STRING_SERDE.deserialize(strings[1]), INTEGER_SERDE.deserialize(strings[2]))), w);
                        break;
                }
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Ecrit un message
     * @param s String
     * @param w BufferedWriter
     */
    private void writeMessage(String s, BufferedWriter w){
        try{
            w.write(s);
            w.write('\n');
            w.flush();
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
}
