package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * Interface Player
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
    public enum TurnKind{
        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;

        /**
         * Retourn une list immuable de tout les membres
         */
        public static final List<TurnKind> ALL = List.of(TurnKind.values());
    }

    /**
     * Communique au joueur sa propre identité et les noms des autres
     * @param ownId identité au joueur
     * @param playerNames noms des joueurs
     */
     public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * Communique une information au joueur
     * @param info l'information à passée
     */
     public void receiveInfo(String info);

    /**
     * Informe le joueur du nouveau état de jeu ainsi que son propre état
     * @param newState
     * @param ownState
     */
     public void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Communique au joueur les 5 billets qui lui ont été distribués au début de la partie
     * @param tickets
     */
     public void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Demande au joueur au, début de la partie, quels billets il veut garder
     * @return SortedBag contenant les billets choisis
     */
     public SortedBag<Ticket> chooseInitialTickets();

    /**
     * Demande au joueur au début du tour quel action il veut effectuer
     * @return l'action qu'il désire effectuer
     */
     public TurnKind nextTurn();

    /**
     * Appelée lorsque le joueur a décidé de tirer des billets supplémentaires en cours de partie,
     * afin de lui communiquer les billets tirés et de savoir lesquels il garde
     * @param options
     * @return les billets tirés gardés
     */
     public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * Appelée lorsque le joueur a décidé de tirer des cartes wagon/locomotive,
     * afin de savoir d'où il désire les tirer
     * @return S'il désire tirer d'un des emplacements contenant une carte face visible — la valeur retourne est comprise entre 0 et 4 inclus —,
     * s'il désire de tirer de la pioche - la valeur retournée vaut Constants.DECK_SLOT (c-à-d -1)
     */
     public int drawSlot();

    /**
     * Appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route, afin de savoir de quelle route il s'agit
     * @return route emparée
     */
     public Route claimedRoute();

    /**
     * Appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route,
     * afin de savoir quelle(s) carte(s) il désire initialement utiliser pour cela,
     * @return cartes utilisées
     */
     public SortedBag<Card> initalClaimCards();

    /**
     * Appelée lorsque le joueur a décidé de tenter de s'emparer d'un tunnel et que des cartes additionnelles sont nécessaires,
     * afin de savoir quelle(s) carte(s) il désire utiliser pour cela, les possibilités lui étant passées en argument
     * @param options les possibilités
     * @return cartes utilisées, si le multiensemble retourné est vide,
     * cela signifie que le joueur ne désire pas (ou ne peut pas) choisir l'une de ces possibilités
     */
     public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

}
