package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * La classe CardState publique, finale et immuable, représente l'état des cartes wagon/locomotive qui ne sont pas en main des joueurs.
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class CardState extends PublicCardState{
    private final Deck<Card> pioche;
    private final SortedBag<Card> discards;

    /**
     * Constructeur privé de CardState
     * @param faceUpCards carte visible
     * @param pioche pioche
     * @param discards défausse
     */
     private CardState(List<Card> faceUpCards, Deck<Card> pioche , SortedBag<Card> discards){
        super(faceUpCards,pioche.size(), discards.size());
        this.pioche = pioche;
        this.discards = discards;
    }

    /**
     * Retourne un état dans lequel les 5 cartes disposées faces visibles sont les 5 premières du tas donné,
     * la pioche est constituée des cartes du tas restantes, et la défausse est vide
     * @param deck tas de carte
     * @throws IllegalArgumentException si le tas donné contient moins de 5 cartes
     * @return CardState
     */
    public static CardState of(Deck<Card> deck){
        Preconditions.checkArgument(deck.size()>= Constants.FACE_UP_CARDS_COUNT);
        return new CardState(deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList(), deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT), SortedBag.of());
    }


    /**
     * Retourne un ensemble de cartes identique au récepteur (this),
     * si ce n'est que la carte face visible d'index slot a été remplacée par celle se trouvant au sommet de la pioche,
     * qui en est du même coup retirée.
     * @param slot emplacement
     * @return CardState
     */
    public CardState withDrawnFaceUpCard(int slot){
        Objects.checkIndex(slot,Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(!pioche.isEmpty());
        List<Card> faceUp = faceUpCards();
        faceUp.set(slot,pioche.topCard());
        return new CardState(faceUp,pioche.withoutTopCard(),discards);
    }

    /**
     * Retourne la carte se trouvant au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     * @return Card
     */
    public Card topDeckCard(){
        Preconditions.checkArgument(!pioche.isEmpty());
        return pioche.topCard();
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur (this), mais sans la carte se trouvant au sommet de la pioche
     * @return CardState
     */
    public CardState withoutTopDeckCard(){
        Preconditions.checkArgument(!pioche.isEmpty());
        return new CardState(faceUpCards(),pioche.withoutTopCard(),discards);

    }


    /**
     * Retourne un ensemble de cartes identique au récepteur (this),
     * si ce n'est que les cartes de la défausse ont été mélangées au moyen du générateur aléatoire donné afin de constituer la nouvelle pioche
     * @param rng instance de la classe Random
     * @throws IllegalArgumentException si la pioche du récepteur n'est pas vide
     * @return CardState
     */
    public CardState withDeckRecreatedFromDiscards(Random rng){
        Preconditions.checkArgument(pioche.isEmpty());
        return new CardState(faceUpCards(),Deck.of(discards, rng),SortedBag.of());
    }


    /**
     * Retourne un ensemble de cartes identique au récepteur (this), mais avec les cartes données ajoutées à la défausse
     * @param additionalDiscards carte à ajouter à la défausse
     * @return CardState
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        return new CardState(faceUpCards(),pioche, discards.union(additionalDiscards));
    }



}
