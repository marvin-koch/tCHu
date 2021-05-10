package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * La classe PublicCardState publique et immuable, représente (une partie de) l'état des cartes wagon/locomotive qui ne sont pas en main des joueurs
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */

public class PublicCardState {
    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * Constructeur de PublicCardState
     * @param faceUpCards cartes visibles
     * @param deckSize la pioche
     * @param discardsSize la défausse
     * @throws IllegalArgumentException si faceUpCards ne contient pas le bon nombre d'éléments (5), ou si la taille de la pioche ou de la défausse sont négatives (< 0).
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT && discardsSize >=0 && deckSize >= 0);
        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * Retourne les 5 cartes face visible, sous la forme d'une liste comportant exactement 5 éléments
     * @return Liste de 5 cartes face visible
     */
    public List<Card> faceUpCards(){
        return new ArrayList<>(faceUpCards);

    }

    /**
     * Retourne la carte face visible à l'index donné
     * @param slot index
     * @throws IndexOutOfBoundsException si cet index n'est pas compris entre 0 (inclus) et 5 (exclus)
     * @return Card
     */
    public Card faceUpCard(int slot){
        return faceUpCards.get(Objects.checkIndex(slot,Constants.FACE_UP_CARDS_COUNT));
    }

    /**
     * Retourne la taille de la pioche
     * @return decksize
     */
    public int deckSize(){
        return deckSize;
    }

    /**
     * Retourne vrai ssi la pioche est vide
     * @return boolean
     */
    public boolean isDeckEmpty(){
        return deckSize == 0;
    }

    /**
     * Retourne la taille de la défausse.
     * @return discardsSize
     */
    public int discardsSize(){
        return  discardsSize;
    }


}
