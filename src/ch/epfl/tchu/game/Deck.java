package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * La classe Deck publique, finale et immuable, représente un tas de cartes.
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */

public final class Deck<C extends Comparable<C>> {
    private final List<C> deckList;

    /**
     * Constructeur privée
     * @param cards cartes
     */
    private Deck(List<C> cards){
       deckList = cards;
    }

    /**
     * Retourne un tas de cartes ayant les mêmes cartes que cards, mais mélangées.
     * @param cards le tas de cartes à mélanger
     * @param rng instance de la class Random
     * @param <C> type d'Objet
     * @return un deck
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){
        List<C> cardsList = cards.toList();
        Collections.shuffle(cardsList, rng);
        return new Deck<>(cardsList);
    }

    /**
     * Retourne la taille du deck
     * @return size
     */
    public int size(){
        return deckList.size();
    }

    /**
     * Retourne un boolean qui indique si le deck est vide
     * @return un boolean
     */
    public boolean isEmpty(){
        return deckList.isEmpty();
    }

    /**
     * Retourne la carte au sommet du deck
     * @throws IllegalArgumentException si le deck est vide
     * @return l'objet au début du deck
     */
    public C topCard(){
        Preconditions.checkArgument(!deckList.isEmpty());
        return deckList.get(0);
    }

    /**
     * Retourne un nouveau Deck avec les mêmes cartes, mais sans la première carte
     * @throws IllegalArgumentException si le deck est vide
     * @return un deck
     */
    public Deck<C> withoutTopCard(){
        Preconditions.checkArgument(!isEmpty());
        return new Deck<>(deckList.subList(1,deckList.size()));
    }

    /**
     * Retourne un nombre souhaitée de cartes au sommet du deck
     * @param count nombre des cartes à retourner
     * @throws IllegalArgumentException si le nombre n'est pas entre 0 et la taille du deck
     * @return un multiensemble de cartes
     */
    public SortedBag<C> topCards(int count){
        Preconditions.checkArgument(count >= 0 && count <= deckList.size());
        return SortedBag.of(deckList.subList(0,count));
    }

    /**
     * Retourne un nouveau Deck avec les mêmes cartes, mais sans le nombres de cartes données au sommet
     * @param count nombres de cartes à enlever
     * @throws IllegalArgumentException si le nombre n'est pas entre 0 et la taille du deck
     * @return un deck
     */
    public Deck<C> withoutTopCards(int count){
        Preconditions.checkArgument(count >= 0 && count <= deckList.size());
        return new Deck<>(deckList.subList(count, deckList.size()));
    }

}
