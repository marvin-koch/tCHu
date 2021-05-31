package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static ch.epfl.tchu.net.Serdes.*;
/**
 * La classe instanciable RemotePlayerProxy reèrésente un mandatoire (proxy en anglais) de joueur distant.
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class RemotePlayerProxy implements Player {
    private final BufferedWriter w;
    private final BufferedReader r;

    /**
     * Constructeur de socket
     * @param socket socket
     */
    public RemotePlayerProxy(Socket socket){
        try {
              w = new BufferedWriter(
                             new OutputStreamWriter(socket.getOutputStream(),
                                     US_ASCII));
             r = new BufferedReader(
                            new InputStreamReader(socket.getInputStream(),
                                    US_ASCII));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }


    /**
     * Communique au joueur sa propre identité et les noms des autres
     *
     * @param ownId       identité au joueur
     * @param playerNames noms des joueurs
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        List<String> list = new ArrayList<>();
        PlayerId.ALL().forEach(id -> list.add(playerNames.get(id)));

        write(MessageId.INIT_PLAYERS,
                PLAYER_ID_SERDE.serialize(ownId) + " " + LIST_STRING_SERDE.serialize(list));
    }

    /**
     * Communique une information au joueur
     *
     * @param info l'information à passée
     */
    @Override
    public void receiveInfo(String info) {
        write(MessageId.RECEIVE_INFO,STRING_SERDE.serialize(info));
    }

    /**
     * Informe le joueur du nouveau état de jeu ainsi que son propre état
     *
     * @param newState etat du jeu publique
     * @param ownState etat du joueur
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        write(MessageId.UPDATE_STATE, PUBLIC_GAME_STATE_SERDE.serialize(newState) + " " + PLAYER_STATE_SERDE.serialize(ownState));

    }

    /**
     * Communique au joueur les 5 billets qui lui ont été distribués au début de la partie
     *
     * @param tickets tas de 5 billets
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        write(MessageId.SET_INITIAL_TICKETS, SORTEDBAG_TICKET_SERDE.serialize(tickets));
    }

    /**
     * Demande au joueur au, début de la partie, quels billets il veut garder
     *
     * @return SortedBag contenant les billets choisis
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        write(MessageId.CHOOSE_INITIAL_TICKETS);
        return SORTEDBAG_TICKET_SERDE.deserialize(read());
    }

    /**
     * Demande au joueur au début du tour quel action il veut effectuer
     *
     * @return l'action qu'il désire effectuer
     */
    @Override
    public TurnKind nextTurn() {
        write(MessageId.NEXT_TURN);
        return TURN_KIND_SERDE.deserialize(read());
    }

    /**
     * Appelée lorsque le joueur a décidé de tirer des billets supplémentaires en cours de partie,
     * afin de lui communiquer les billets tirés et de savoir lesquels il garde
     *
     * @param options tas de billets tirés
     * @return les billets tirés gardés
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        write(MessageId.CHOOSE_TICKETS, SORTEDBAG_TICKET_SERDE.serialize(options));
        return SORTEDBAG_TICKET_SERDE.deserialize(read());
    }

    /**
     * Appelée lorsque le joueur a décidé de tirer des cartes wagon/locomotive,
     * afin de savoir d'où il désire les tirer
     *
     * @return S'il désire tirer d'un des emplacements contenant une carte face visible — la valeur retourne est comprise entre 0 et 4 inclus —,
     * s'il désire de tirer de la pioche - la valeur retournée vaut Constants.DECK_SLOT (c-à-d -1)
     */
    @Override
    public int drawSlot() {
        write(MessageId.DRAW_SLOT);
        return INTEGER_SERDE.deserialize(read());
    }

    /**
     * Appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route, afin de savoir de quelle route il s'agit
     *
     * @return route emparée
     */
    @Override
    public Route claimedRoute() {
        write(MessageId.ROUTE);
        return ROUTE_SERDE.deserialize(read());
    }

    /**
     * Appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route,
     * afin de savoir quelle(s) carte(s) il désire initialement utiliser pour cela,
     *
     * @return cartes utilisées
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        write(MessageId.CARDS);
        return SORTEDBAG_CARD_SERDE.deserialize(read());
    }

    /**
     * Appelée lorsque le joueur a décidé de tenter de s'emparer d'un tunnel et que des cartes additionnelles sont nécessaires,
     * afin de savoir quelle(s) carte(s) il désire utiliser pour cela, les possibilités lui étant passées en argument
     *
     * @param options les possibilités
     * @return cartes utilisées, si le multiensemble retourné est vide,
     * cela signifie que le joueur ne désire pas (ou ne peut pas) choisir l'une de ces possibilités
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        write(MessageId.CHOOSE_ADDITIONAL_CARDS, LIST_SORTEDBAG_CARD_SERDE.serialize(options));
        return SORTEDBAG_CARD_SERDE.deserialize(read());
    }

    /**
     * Iniitialise le nombre de joueurs
     * @param is3Player vrai si il y a 3 joueurs
     */
    @Override
    public void initNbrOfPlayer(boolean is3Player) {
        write(MessageId.NBR_OF_PLAYER,INTEGER_SERDE.serialize(is3Player? 1 : 0));
    }

    //TDOO commenter
    @Override
    public int showEndMenu(String name){
        write(MessageId.END, STRING_SERDE.serialize(name));
        return INTEGER_SERDE.deserialize(read());
    }

    /**
     * Ecrit un message
     * @param id MessageId
     * @param arguments string
     */
    private void write(MessageId id, String arguments){
        try{
            w.write(id.name() + " " + arguments);
            w.write('\n');
            w.flush();
        }catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * ecris un message sans argument
     * @param id MessageId
     */
    private void write(MessageId id){
        write(id,"");
    }

    /**
     * Lit message
     * @return String
     */
    private String read(){
        try{
            return r.readLine();
        }catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
