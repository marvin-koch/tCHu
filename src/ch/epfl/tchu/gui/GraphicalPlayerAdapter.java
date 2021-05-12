package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Platform;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.*;
/**
 * La classe instanciable GraphicalPlayerAdapter a pour but d'adapter (au sens du patron Adapter)
 * une instance de GraphicalPlayer en une valeur de type Player
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class GraphicalPlayerAdapter implements Player {
    private GraphicalPlayer graphicalPlayer;
    private final BlockingQueue<SortedBag<Ticket>> bQueueInitialTicket;
    private final BlockingQueue<SortedBag<Ticket>> bQueueChooseTicket;
    private final BlockingQueue<TurnKind> bQueueTurn;
    private final BlockingQueue<Route> bQueueRoute;
    private final BlockingQueue<SortedBag<Card>> bQueueCard;
    private final BlockingQueue<SortedBag<Card>> bQueueAdditionalCard;
    private final BlockingQueue<Integer> bQueueInt;
    private final BlockingQueue<Integer> bQueueIntSecondChance;

    /**
     * Constructeur de GraphicalPlayerAdapter
     */
    public GraphicalPlayerAdapter(){
        bQueueInitialTicket= new ArrayBlockingQueue<>(1);
        bQueueChooseTicket= new ArrayBlockingQueue<>(1);;
        bQueueTurn= new ArrayBlockingQueue<>(1);;
        bQueueRoute = new ArrayBlockingQueue<>(1);
        bQueueCard = new ArrayBlockingQueue<>(1);
        bQueueAdditionalCard = new ArrayBlockingQueue<>(1);
        bQueueInt = new ArrayBlockingQueue<>(1);
        bQueueIntSecondChance = new ArrayBlockingQueue<>(1);

    }
    /**
     * Communique au joueur sa propre identité et les noms des autres
     *
     * @param ownId       identité au joueur
     * @param playerNames noms des joueurs
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
         runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    /**
     * Communique une information au joueur
     *
     * @param info l'information à passée
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() ->graphicalPlayer.receiveInfo(info));
    }

    /**
     * Informe le joueur du nouveau état de jeu ainsi que son propre état
     *
     * @param newState etat du jeu publique
     * @param ownState etat du joueur
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState,ownState));
    }

    /**
     * Communique au joueur les 5 billets qui lui ont été distribués au début de la partie
     *
     * @param tickets tas de 5 billets
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

        runLater(()-> graphicalPlayer.chooseTickets(tickets, bag -> {
            try{
                bQueueInitialTicket.put(bag);
            }catch (InterruptedException e){
                throw new Error();
            }
        }));
    }

    /**
     * Demande au joueur au, début de la partie, quels billets il veut garder
     *
     * @return SortedBag contenant les billets choisis
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets(){
        try{
            return bQueueInitialTicket.take();
        }catch (InterruptedException e){
            throw new Error("chooseInitialTickets() interrupted");
        }
    }

    /**
     * Demande au joueur au début du tour quel action il veut effectuer
     *
     * @return l'action qu'il désire effectuer
     */
    @Override
    public TurnKind nextTurn() {

        runLater(() -> graphicalPlayer.startTurn(() -> {
            try{
                bQueueTurn.put(TurnKind.DRAW_TICKETS);
            }catch (InterruptedException e){
                throw new Error();
            }
        }, slot -> {
            try{
                bQueueTurn.put(TurnKind.DRAW_CARDS);
                bQueueInt.put(slot);
            }catch (InterruptedException e){
                throw new Error();
            }
        }, (route, cards) -> {
            try{
                bQueueTurn.put(TurnKind.CLAIM_ROUTE);
                bQueueRoute.put(route);
                bQueueCard.put(cards);
            }catch (InterruptedException e){
                throw new Error();
            }
        }));
        try{
            return bQueueTurn.take();
        }catch (InterruptedException e){
            throw new Error();
        }
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
        runLater(() -> graphicalPlayer.chooseTickets(options, new ActionHandlers.ChooseTicketsHandler() {
            @Override
            public void onChooseTickets(SortedBag<Ticket> bag) {
                try{
                    bQueueChooseTicket.put(bag);
                }catch (InterruptedException e){
                    throw new Error();
                }
            }
        }));
        try{
            return bQueueChooseTicket.take();
        }catch (InterruptedException e){
            throw new Error();
        }
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
        if(!bQueueInt.isEmpty()){
            try{
                return bQueueInt.take();
            }catch (InterruptedException e){
                throw new Error();
            }
        }else {
            runLater(() -> graphicalPlayer.drawCard((slot -> {
                try{
                    bQueueIntSecondChance.put(slot);
                }catch (InterruptedException e){
                    throw new Error();
                }
            })));
            try{
                return bQueueIntSecondChance.take();
            }catch (InterruptedException e){
                throw new Error();
            }
        }
    }

    /**
     * Appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route, afin de savoir de quelle route il s'agit
     *
     * @return route emparée
     */
    @Override
    public Route claimedRoute() {
        try{
            return bQueueRoute.take();
        }catch (InterruptedException e){
            throw new Error();
        }

    }

    /**
     * Appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route,
     * afin de savoir quelle(s) carte(s) il désire initialement utiliser pour cela,
     *
     * @return cartes utilisées
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        try{
            return bQueueCard.take();
        }catch (InterruptedException e){
            throw new Error();
        }
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
        runLater(()-> graphicalPlayer.chooseAdditionalCards(options, cards -> {
            try{
                bQueueAdditionalCard.put(cards);
            }catch (InterruptedException e){
                throw new Error();
            }
        }));
        try{
            return bQueueAdditionalCard.take();
        }catch (InterruptedException e){
            throw new Error();
        }

    }
}
