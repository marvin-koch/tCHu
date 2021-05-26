package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.GraphicalPlayerAdapter;
import ch.epfl.tchu.gui.ObservableGameState;
import ch.epfl.tchu.gui.ServerMain;

import java.util.List;
import java.util.Map;

/**
 * L'interface Player représente un joueur de tCHu
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public interface Player {

    /**
     * Type énuméré imbriquée TurnKind
     *
     * @author Shangeeth Poobalasingam (329307)
     * @author Marvin Koch (324448)
     */
    enum TurnKind{
        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;

        /**
         * Retourn une list immuable de tout les membres
         */
        public static final List<TurnKind> ALL = List.of(TurnKind.values());
    }

    /**
     * Iniitialise le nombre de joueurs
     * @param is3Player vrai si il y a 3 joueurs
     */
    void initNbrOfPlayer(boolean is3Player);

    //TODO commenter
    int endMenu(String name);
    /**
     * Communique au joueur sa propre identité et les noms des autres
     * @param ownId identité au joueur
     * @param playerNames noms des joueurs
     */
     void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * Communique une information au joueur
     * @param info l'information à passée
     */
     void receiveInfo(String info);

    /**
     * Informe le joueur du nouveau état de jeu ainsi que son propre état
     * @param newState etat du jeu publique
     * @param ownState etat du joueur
     */
     void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Communique au joueur les 5 billets qui lui ont été distribués au début de la partie
     * @param tickets tas de 5 billets
     */
     void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Demande au joueur au, début de la partie, quels billets il veut garder
     * @return SortedBag contenant les billets choisis
     */
     SortedBag<Ticket> chooseInitialTickets();

    /**
     * Demande au joueur au début du tour quel action il veut effectuer
     * @return l'action qu'il désire effectuer
     */
     TurnKind nextTurn();

    /**
     * Appelée lorsque le joueur a décidé de tirer des billets supplémentaires en cours de partie,
     * afin de lui communiquer les billets tirés et de savoir lesquels il garde
     * @param options tas de billets tirés
     * @return les billets tirés gardés
     */
     SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * Appelée lorsque le joueur a décidé de tirer des cartes wagon/locomotive,
     * afin de savoir d'où il désire les tirer
     * @return S'il désire tirer d'un des emplacements contenant une carte face visible — la valeur retourne est comprise entre 0 et 4 inclus —,
     * s'il désire de tirer de la pioche - la valeur retournée vaut Constants.DECK_SLOT (c-à-d -1)
     */
     int drawSlot();

    /**
     * Appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route, afin de savoir de quelle route il s'agit
     * @return route emparée
     */
     Route claimedRoute();

    /**
     * Appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route,
     * afin de savoir quelle(s) carte(s) il désire initialement utiliser pour cela,
     * @return cartes utilisées
     */
     SortedBag<Card> initialClaimCards();

    /**
     * Appelée lorsque le joueur a décidé de tenter de s'emparer d'un tunnel et que des cartes additionnelles sont nécessaires,
     * afin de savoir quelle(s) carte(s) il désire utiliser pour cela, les possibilités lui étant passées en argument
     * @param options les possibilités
     * @return cartes utilisées, si le multiensemble retourné est vide,
     * cela signifie que le joueur ne désire pas (ou ne peut pas) choisir l'une de ces possibilités
     */
     SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

}
