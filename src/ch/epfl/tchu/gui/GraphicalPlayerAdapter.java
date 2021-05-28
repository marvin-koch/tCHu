package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

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
    private final BlockingQueue<Integer> bQueueIntRestart;

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
        bQueueIntRestart = new ArrayBlockingQueue<>(1);

    }


    /**
     * Iniitialise le nombre de joueurs
     * @param is3Player vrai si il y a 3 joueurs
     */
    @Override
    public void initNbrOfPlayer(boolean is3Player) {
        PlayerId.initNbrPlayers(is3Player);
    }

    //TODO commenter
    public int showEndMenu(String s){
        runLater(() -> graphicalPlayer.showEndMenu( s, choice -> putInQueue(bQueueIntRestart, choice)));
        return takeFromQueue(bQueueIntRestart);
    }

    /**
     * Communique au joueur sa propre identité et les noms des autres
     *
     * @param ownId identité au joueur
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
        runLater(() -> graphicalPlayer.receiveInfo(info));
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
        runLater(() -> graphicalPlayer.chooseTickets(tickets, bag -> putInQueue(bQueueInitialTicket, bag)));
    }

    /**
     * Demande au joueur au, début de la partie, quels billets il veut garder
     *
     * @return SortedBag contenant les billets choisis
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets(){
        return takeFromQueue(bQueueInitialTicket);
    }

    /**
     * Demande au joueur au début du tour quel action il veut effectuer
     *
     * @return l'action qu'il désire effectuer
     */
    @Override
    public TurnKind nextTurn() {
        runLater(() -> graphicalPlayer.startTurn(() -> putInQueue(bQueueTurn, TurnKind.DRAW_TICKETS),
                slot -> {putInQueue(bQueueTurn, TurnKind.DRAW_CARDS);
                         putInQueue(bQueueInt, slot);},
                (route, cards) -> {putInQueue(bQueueTurn, TurnKind.CLAIM_ROUTE);
                                   putInQueue(bQueueRoute, route);
                                    putInQueue(bQueueCard, cards);}));

        return takeFromQueue(bQueueTurn);
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
        runLater(() -> graphicalPlayer.chooseTickets(options, bag -> putInQueue(bQueueChooseTicket, bag)));
        return takeFromQueue(bQueueChooseTicket);
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
            return takeFromQueue(bQueueInt);
        }else {
            runLater(() -> graphicalPlayer.drawCard(slot -> putInQueue(bQueueIntSecondChance, slot)));
            return takeFromQueue(bQueueIntSecondChance);
        }
    }

    /**
     * Appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route, afin de savoir de quelle route il s'agit
     *
     * @return route emparée
     */
    @Override
    public Route claimedRoute() {
        return takeFromQueue(bQueueRoute);
    }

    /**
     * Appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route,
     * afin de savoir quelle(s) carte(s) il désire initialement utiliser pour cela,
     *
     * @return cartes utilisées
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        return takeFromQueue(bQueueCard);
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
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, cards -> putInQueue(bQueueAdditionalCard, cards)));
        return takeFromQueue(bQueueAdditionalCard);

    }

    /**
     * Méthode privée pour insérer un élément dans une BlockingQueue
     * @param queue Queue
     * @param objects objets
     * @param <T> class
     */
    private <T> void putInQueue(BlockingQueue<T> queue, T objects){
        try{
            queue.put(objects);
        }catch (InterruptedException e){
            throw new Error();
        }
    }

    /**
     * Méthode privée pour prendre un élément dans une BlockingQueue
     * @param queue Queue
     * @param <T> class
     * @return T
     */
    private <T> T takeFromQueue(BlockingQueue<T> queue){
        try{
            return queue.take();
        }catch (InterruptedException e){
            throw new Error("Not able to take from Queue");
        }
    }
}
