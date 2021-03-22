package ch.epfl.tchu.game;


import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

/**
 * Class GameState
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class GameState extends  PublicGameState{
    private final SortedBag<Ticket> tickets;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;

    /**
     * Constructeur privé de GameState
     * @param tickets
     * @param cardState
     * @param currentPlayerId
     * @param playerState
     * @param lastPlayer
     */
    private GameState(SortedBag<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(tickets.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        this.tickets = tickets;
        this.cardState = cardState;
        this.playerState = playerState;
    }

    /**
     * Copie la Map playerState
     * @param playerId
     * @param state
     * @return
     */
    private Map<PlayerId, PlayerState> copyMap(PlayerId playerId, PlayerState state){
        Map<PlayerId, PlayerState> copy = new EnumMap<>(PlayerId.class);
        copy.put(playerId, state);
        copy.put(playerId.next(), playerState(playerId.next()));
        return copy;
    }

    /**
     * Retourne l'état initial d'une partie de tCHu dans laquelle la pioche des billets contient les billets donnés et la pioche des cartes contient les cartes de Constants.ALL_CARDS, sans les 8 (2×4) du dessus, distribuées aux joueurs; ces pioches sont mélangées au moyen du générateur aléatoire donné, qui est aussi utilisé pour choisir au hasard l'identité du premier joueur.
     * @param tickets
     * @param rng randomizer
     * @return
     */
    static public GameState initial(SortedBag<Ticket> tickets, Random rng){
        Deck<Card> pioche = Deck.of(Constants.ALL_CARDS,rng);
        Map<PlayerId, PlayerState> m = new EnumMap<>(PlayerId.class);
        int nbrCartesInitial = Constants.INITIAL_CARDS_COUNT;
        m.put(PlayerId.PLAYER_1,PlayerState.initial(pioche.topCards(nbrCartesInitial)));
        pioche = pioche.withoutTopCards(nbrCartesInitial);
        m.put(PlayerId.PLAYER_2,PlayerState.initial(pioche.topCards(nbrCartesInitial)));
        pioche = pioche.withoutTopCards(nbrCartesInitial);

        return new GameState(tickets,CardState.of(pioche), PlayerId.ALL.get(rng.nextInt(2)),m,null);
    }

    /**
     * Retourne l'état du joueur d'identité donnée
     * @param playerId
     * @return PlayerState de playerId
     */
    @Override
    public PlayerState playerState(PlayerId playerId){
         return playerState.get(playerId);
    }

    /**
     * Retourne de l'état du joueur courant
     * @return PlayerState de currentPlayer
     */
    @Override
    public PlayerState currentPlayerState(){
        return playerState(currentPlayerId());
    }


    /**
     * Retourne les count billets du sommet de la pioche
     * @param count le nombre de cartes que l'on veut
     * @throws IllegalArgumentException si count n'est pas compris entre 0 et la taille de la pioche (inclus)
     * @return
     */
    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        SortedBag.Builder<Ticket> builder = new SortedBag.Builder<>();
        for(int i = 0; i < count; ++i){
           builder.add(tickets.get(i));
        }
        return builder.build();
    }


    /**
     * Retourne un état identique au récepteur, mais sans les count billets du sommet de la pioche, ou lève IllegalArgumentException si count n'est pas compris entre 0 et la taille de la pioche (inclus),
     * @param count le nombre de cartes que l'on veut
     * @throws IllegalArgumentException si count n'est pas compris entre 0 et la taille de la pioche (inclus)
     * @return GameState actualisé
     */
    public GameState withoutTopTickets(int count){
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return new GameState(tickets.difference(topTickets(count)),cardState, currentPlayerId(), playerState, lastPlayer());
    }


    /**
     * Retourne la carte au sommet de la pioche, ou lève IllegalArgumentException si la pioche est vide
     * @throws IllegalArgumentException si la pioche est vide
     * @return
     */
    public Card topCard(){
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return cardState.topDeckCard();
    }

    /**
     * Retourne un état identique au récepteur mais sans la carte au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     * @return GameState actualisé
     */
    public GameState withoutTopCard(){
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), playerState, lastPlayer());
    }


    /**
     * Retourne un état identique au récepteur mais avec les cartes données ajoutées à la défausse
     * @param discardedCards
     * @return GameState actualisé
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){
        return new GameState(tickets,cardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), playerState, lastPlayer());
    }


    /**
     * Retourne un état identique au récepteur sauf si la pioche de cartes est vide, auquel cas elle est recréée à partir de la défausse, mélangée au moyen du générateur aléatoire donné
     * @param rng randomizer
     * @return GameState actualisé
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng){
        if(cardState.isDeckEmpty()){
            return new GameState(tickets,cardState.withDeckRecreatedFromDiscards(rng), currentPlayerId(), playerState, lastPlayer());
        }else{
            return this;
        }
    }

    /**
     * Retourne un état identique au récepteur mais dans lequel les billets donnés ont été ajoutés à la main du joueur donné
     * @param playerId
     * @param chosenTickets billets donnés
     * @throws IllegalArgumentException si le joueur en question possède déjà au moins un billet
     * @return GameState actualisé
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(playerState(playerId).ticketCount() == 0);
        PlayerState state = playerState(playerId).withAddedTickets(chosenTickets);
        return new GameState(tickets,cardState, currentPlayerId(),copyMap(playerId, state), lastPlayer());
    }

    /**
     * Retourne un état identique au récepteur, mais dans lequel le joueur courant a tiré les billets drawnTickets du sommet de la pioche, et choisi de garder ceux contenus dans chosenTicket
     * @param drawnTickets
     * @param chosenTickets
     * @throws IllegalArgumentException si l'ensemble des billets gardés n'est pas inclus dans celui des billets tirés
     * @return GameState actualisé
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        PlayerState state = currentPlayerState().withAddedTickets(chosenTickets);

        return new GameState(tickets.difference(drawnTickets),cardState, currentPlayerId(),copyMap(currentPlayerId() ,state) , lastPlayer());
    }


    /**
     * Retourne un état identique au récepteur si ce n'est que la carte face retournée à l'emplacement donné a été placée dans la main du joueur courant, et remplacée par celle au sommet de la pioche
     * @param slot
     * @throws IllegalArgumentException s'il n'est pas possible de tirer des cartes, c-à-d si canDrawCards retourne faux
     * @return GameState actualisé
     */
    public GameState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(canDrawCards());
        PlayerState state = currentPlayerState().withAddedCards(SortedBag.of(cardState.faceUpCard(slot)));
        return new GameState(tickets,cardState.withDrawnFaceUpCard(slot), currentPlayerId(), copyMap(currentPlayerId(),state), lastPlayer());
    }


    /**
     * Retourne un état identique au récepteur si ce n'est que la carte du sommet de la pioche a été placée dans la main du joueur courant
     * @throws IllegalArgumentException s'il n'est pas possible de tirer des cartes, c-à-d si canDrawCards retourne faux
     * @return GameState actualisé
     */
    public GameState withBlindlyDrawnCard(){
        Preconditions.checkArgument(canDrawCards());
        PlayerState state = currentPlayerState().withAddedCards(SortedBag.of(cardState.topDeckCard()));
        return new GameState(tickets,cardState.withoutTopDeckCard(), currentPlayerId(), copyMap(currentPlayerId(),state), lastPlayer());
    }


    /**
     * Retourne un état identique au récepteur mais dans lequel le joueur courant s'est emparé de la route donnée au moyen des cartes données
     * @param route route emparés
     * @param cards cartes utilisées
     * @return GameState actualisé
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards){
        PlayerState state = currentPlayerState().withClaimedRoute(route, cards);
        return new GameState(tickets, cardState.withMoreDiscardedCards(cards), currentPlayerId(), copyMap(currentPlayerId(), state), lastPlayer());
    }


    /**
     * Retourne vrai ssi le dernier tour commence, c-à-d si l'identité du dernier joueur est actuellement inconnue mais que le joueur courant n'a plus que deux wagons ou moins
     * @return boolean
     */
    public boolean lastTurnBegins(){
        return lastPlayer() == null && currentPlayerState().carCount() <=2;
    }


    /**
     * Retourne un état identique au récepteur si ce n'est que le joueur courant est celui qui suit le joueur courant actuel; de plus, si lastTurnBegins retourne vrai, le joueur courant actuel devient le dernier joueur
     * @return GameState
     */
    public GameState forNextTurn(){
        if (lastTurnBegins()){
            return new GameState(tickets, cardState, currentPlayerId().next(), playerState, currentPlayerId());
        }else{
            return new GameState(tickets, cardState, currentPlayerId().next(), playerState, lastPlayer());
        }
    }
}
