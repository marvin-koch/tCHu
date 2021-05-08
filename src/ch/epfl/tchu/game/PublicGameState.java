package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * La classe PublicGameState publique et immuable, représente la partie publique de l'état d'une partie de tCHu.
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */

public class PublicGameState {
    final private int ticketsCount;
    final private PublicCardState cardState;
    final private PlayerId currentPlayerId;
    final private Map<PlayerId, PublicPlayerState> playerState;
    final private PlayerId lastPlayer;

    /**
     * Construit la partie publique de l'état d'une partie de tCHu dans laquelle la pioche de billets a une taille de ticketsCount,
     * l'état public des cartes wagon/locomotive est cardState, le joueur courant est currentPlayerId,
     * l'état public des joueurs est contenu dans playerState,
     * et l'identité du dernier joueur est lastPlayer (qui peut être null si cette identité est encore inconnue)
     * @param ticketsCount taille de la pioche
     * @param cardState  l'état public des cartes wagon/locomotive
     * @param currentPlayerId le joueur courant
     * @param playerState l'état public des joueurs
     * @param lastPlayer l'identité du dernier joueur
     * @throws IllegalArgumentException si la taille de la pioche est strictement négative ou si playerState ne contient pas exactement deux paires clef/valeur,
     * @throws  NullPointerException si l'un des autres arguments (lastPlayer excepté!) est nul
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer){
        Preconditions.checkArgument(ticketsCount >= 0 && playerState.size() == PlayerId.COUNT);
        this.ticketsCount = ticketsCount;
        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId) ;
        this.playerState = Map.copyOf(playerState);
        this.lastPlayer = lastPlayer;
    }

    /**
     * retourne la taille de la pioche de billets
     * @return taille de la pioche
     */
    public int ticketsCount(){
        return ticketsCount;
    }

    /**
     * retourne vrai ssi il est possible de tirer des billets, c-à-d si la pioche n'est pas vide
     * @return si poche non vide
     */
    public boolean canDrawTickets(){
        return ticketsCount()!= 0;
    }


    /**
     * Retourne la partie publique de l'état des cartes wagon/locomotive
     * @return cardState
     */
    public PublicCardState cardState(){
        return cardState;
    }

    /**
     * Retourne vrai ssi il est possible de tirer des cartes, c-à-d si la pioche et la défausse contiennent entre elles au moins 5 cartes
     * @return pioche + defausse >= 5
     */
    public boolean canDrawCards(){
        return cardState().deckSize() + cardState().discardsSize() >= 5;
    }

    /**
     * Retourne l'identité du joueur actuel
     * @return currentPlayer
     */
    public PlayerId currentPlayerId(){
        return currentPlayerId;
    }

    /**
     * Retourne la partie publique de l'état du joueur d'identité donnée
     * @param playerId id du joueurs
     * @return PublicPlayerState de playerId
     */
    public PublicPlayerState playerState(PlayerId playerId){
        return playerState.get(playerId);
    }

    /**
     * Retourne la partie publique de l'état du joueur courant
     * @return PublicPlayerState de currentPlayer
     */
    public PublicPlayerState currentPlayerState(){
        return playerState.get(currentPlayerId());
    }

    /**
     * Retourne la totalité des routes dont l'un ou l'autre des joueurs s'est emparé
     * @return liste
     */
    public List<Route> claimedRoutes(){
        List<Route> routeList = new ArrayList<>(playerState(PlayerId.PLAYER_1).routes());
        routeList.addAll(playerState(PlayerId.PLAYER_2).routes());
        return routeList;
    }


    /**
     * Retourne l'identité du dernier joueur, ou null si elle n'est pas encore connue car le dernier tour n'a pas commencé.
     * @return PlayerId du lastplayer
     */
    public PlayerId lastPlayer(){
        return lastPlayer;
    }

}
