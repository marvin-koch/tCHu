package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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
        this.name = name;
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
                                     US_ASCII));){
            String line;
            while((line = r.readLine())!= null){
                String[] strings = line.split(Pattern.quote(" "));
                switch (MessageId.valueOf(strings[0])){
                    case INIT_PLAYERS:
                        List<String> playernames = LIST_STRING_SERDE.deserialize(strings[2]);
                        Map<PlayerId, String> map = new EnumMap<>(PlayerId.class);
                        map.put(PlayerId.PLAYER_1, playernames.get(0));
                        map.put(PlayerId.PLAYER_2, playernames.get(1));
                        player.initPlayers(PLAYER_ID_SERDE.deserialize(strings[1]), map);
                        break;
                    case RECEIVE_INFO:
                        player.receiveInfo(STRING_SERDE.deserialize(strings[1]));
                        break;
                    case UPDATE_STATE:
                        player.updateState(PUBLIC_GAME_STATE_SERDE.deserialize(strings[1]),PLAYER_STATE_SERDE.deserialize(strings[2]));
                        break;
                    case CHOOSE_INITIAL_TICKETS:
                        w.write(SORTEDBAG_TICKET_SERDE.serialize(player.chooseInitialTickets()));
                        w.write('\n');
                        w.flush();
                        break;
                    case NEXT_TURN:
                        w.write(TURN_KIND_SERDE.serialize(player.nextTurn()));
                        w.write('\n');
                        w.flush();
                        break;
                    case CHOOSE_TICKETS:
                        SortedBag<Ticket> options = SORTEDBAG_TICKET_SERDE.deserialize(strings[1]);
                        w.write(SORTEDBAG_TICKET_SERDE.serialize(player.chooseTickets(options)));
                        w.write('\n');
                        w.flush();
                        break;
                    case DRAW_SLOT:
                        w.write(INTEGER_SERDE.serialize(player.drawSlot()));
                        w.write('\n');
                        w.flush();
                        break;
                    case ROUTE:
                        w.write(ROUTE_SERDE.serialize(player.claimedRoute()));
                        w.write('\n');
                        w.flush();
                        break;
                    case CARDS:
                        w.write(SORTEDBAG_CARD_SERDE.serialize(player.initialClaimCards()));
                        w.write('\n');
                        w.flush();
                        break;
                    case CHOOSE_ADDITIONAL_CARDS:
                        List<SortedBag<Card>> list = LIST_SORTEDBAG_CARD_SERDE.deserialize(strings[1]);
                        w.write(SORTEDBAG_CARD_SERDE.serialize(player.chooseAdditionalCards(list)));
                        w.write('\n');
                        w.flush();
                        break;
                    case SET_INITIAL_TICKETS:
                        player.setInitialTicketChoice(SORTEDBAG_TICKET_SERDE.deserialize(strings[1]));
                        break;
                }
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
