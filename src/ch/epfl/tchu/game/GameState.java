package ch.epfl.tchu.game;


import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * La classe GameState publique, finale et immuable, représente l'état d'une partie de tCHu.
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class GameState extends  PublicGameState{
    private final Deck<Ticket> tickets;
    private final CardState cardState;
    private final Map<PlayerId, PlayerState> playerState;

    /**
     * Constructeur privé de GameState
     * @param tickets billets
     * @param cardState état des cartes
     * @param currentPlayerId id du joueur actuel
     * @param playerState map des états des joueurs
     * @param lastPlayer le dernier joueur à jouer dans la partie
     */
    private GameState(Deck<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(tickets.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        this.tickets = Objects.requireNonNull(tickets);
        this.cardState = Objects.requireNonNull(cardState);
        this.playerState = playerState;
    }

    /**
     * Copie la Map playerState
     * @param playerId id du player
     * @param state state du player
     * @return copy de la Map
     */
    private Map<PlayerId, PlayerState> copyMap(PlayerId playerId, PlayerState state){
        Map<PlayerId, PlayerState> copy = new HashMap<>();
        /*
        copy.put(playerId, state);
        copy.put(playerId.next(), playerState(playerId.next()));
        copy.put(playerId.next().next(),playerState(playerId.next().next()));//todo 3 player
        */
        for(PlayerId id : PlayerId.ALL){
            if(id != playerId){
                copy.put(id,playerState(id));
            }else {
                copy.put(playerId,state);
            }
        }
        return copy;
    }

    /**
     * Retourne l'état initial d'une partie de tCHu dans laquelle la pioche des billets contient les billets donnés et la pioche des cartes contient les cartes de Constants.ALL_CARDS,
     * sans les 8 (2×4) du dessus, distribuées aux joueurs;
     * ces pioches sont mélangées au moyen du générateur aléatoire donné, qui est aussi utilisé pour choisir au hasard l'identité du premier joueur.
     * @param tickets billets
     * @param rng randomizer
     * @return Game State initialisée
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng){
        Deck<Card> pioche = Deck.of(Constants.ALL_CARDS,rng);
        Map<PlayerId, PlayerState> m = new HashMap<>();

        for(PlayerId id : PlayerId.ALL){
            m.put(id,PlayerState.initial(pioche.topCards(Constants.INITIAL_CARDS_COUNT)));
            pioche = pioche.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        }

        return new GameState(Deck.of(tickets, rng),CardState.of(pioche), PlayerId.ALL.get(rng.nextInt(2)),m,null);
    }

    /**
     * Retourne l'état du joueur d'identité donnée
     * @param playerId id du player
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
     * @return SortedBag des billets du sommet
     */
    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return tickets.topCards(count);
    }


    /**
     * Retourne un état identique au récepteur, mais sans les count billets du sommet de la pioche, ou lève IllegalArgumentException si count n'est pas compris entre 0 et la taille de la pioche (inclus),
     * @param count le nombre de cartes que l'on veut
     * @throws IllegalArgumentException si count n'est pas compris entre 0 et la taille de la pioche (inclus)
     * @return GameState actualisé
     */
    public GameState withoutTopTickets(int count){
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return new GameState(tickets.withoutTopCards(count),cardState, currentPlayerId(), playerState, lastPlayer());
    }


    /**
     * Retourne la carte au sommet de la pioche, ou lève IllegalArgumentException si la pioche est vide
     * @throws IllegalArgumentException si la pioche est vide
     * @return la carte au sommet
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
     * @param discardedCards cartes défaussés
     * @return GameState actualisé
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){
        return new GameState(tickets,cardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), playerState, lastPlayer());
    }


    /**
     * Retourne un état identique au récepteur sauf si la pioche de cartes est vide,
     * auquel cas elle est recréée à partir de la défausse, mélangée au moyen du générateur aléatoire donné
     * @param rng randomizer
     * @return GameState actualisé
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng){
        return cardState.isDeckEmpty() ? new GameState(tickets,cardState.withDeckRecreatedFromDiscards(rng), currentPlayerId(), playerState, lastPlayer()) : this;
    }

    /**
     * Retourne un état identique au récepteur mais dans lequel les billets donnés ont été ajoutés à la main du joueur donné
     * @param playerId id du player
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
     * @param drawnTickets cartes tirées
     * @param chosenTickets cartes choisis
     * @throws IllegalArgumentException si l'ensemble des billets gardés n'est pas inclus dans celui des billets tirés
     * @return GameState actualisé
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        PlayerState state = currentPlayerState().withAddedTickets(chosenTickets);
        return new GameState(tickets.withoutTopCards(drawnTickets.size()),cardState, currentPlayerId(),copyMap(currentPlayerId() ,state) , lastPlayer());
    }


    /**
     * Retourne un état identique au récepteur si ce n'est que la carte face retournée à l'emplacement donné a été placée dans la main du joueur courant,
     * et remplacée par celle au sommet de la pioche
     * @param slot le slot choisi par le joueur
     * @throws IllegalArgumentException s'il n'est pas possible de tirer des cartes, c-à-d si canDrawCards retourne faux
     * @return GameState actualisé
     */
    public GameState withDrawnFaceUpCard(int slot){
        PlayerState state = currentPlayerState().withAddedCard(cardState.faceUpCard(slot));
        return new GameState(tickets,cardState.withDrawnFaceUpCard(slot), currentPlayerId(), copyMap(currentPlayerId(),state), lastPlayer());
    }


    /**
     * Retourne un état identique au récepteur si ce n'est que la carte du sommet de la pioche a été placée dans la main du joueur courant
     * @throws IllegalArgumentException s'il n'est pas possible de tirer des cartes, c-à-d si canDrawCards retourne faux
     * @return GameState actualisé
     */
    public GameState withBlindlyDrawnCard(){
        PlayerState state = currentPlayerState().withAddedCard(cardState.topDeckCard());
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
     * Retourne un état identique au récepteur si ce n'est que le joueur courant est celui qui suit le joueur courant actuel;
     * de plus, si lastTurnBegins retourne vrai, le joueur courant actuel devient le dernier joueur
     * @return GameState
     */
    public GameState forNextTurn(){
        PlayerId id = lastTurnBegins() ? currentPlayerId() : lastPlayer();
        return new GameState(tickets, cardState, currentPlayerId().next(), playerState, id);
    }
}
