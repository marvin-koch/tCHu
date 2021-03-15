package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * Class PlayerState
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class PlayerState extends PublicPlayerState{
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    /**
     * Construit l'état d'un joueur possédant les billets, cartes et routes donnés
     * @param tickets
     * @param cards
     * @param routes
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes){
        super(tickets.size(),cards.size(),routes);
        this.tickets = tickets;
        this.cards = cards;
    }

    /**
     * Retourne l'état initial d'un joueur auquel les cartes initiales données ont été distribuées ; dans cet état initial, le joueur ne possède encore aucun billet, et ne s'est emparé d'aucune route
     * @param initialCards
     * @throws IllegalArgumentException si le nombre de cartes initiales ne vaut pas 4
     * @return PlayerState
     */
    public static PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument(initialCards.size() == 4);
        return  new PlayerState(SortedBag.of(),initialCards, Collections.EMPTY_LIST);
    }

    /**
     * Retourne les billets du joueur
     * @return tickets
     */
    public SortedBag<Ticket> tickets(){
        return tickets;
    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur possède en plus les billets donnés
     * @param newTickets
     * @return PlayerState
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets){
        return new PlayerState(tickets().union(newTickets),cards(),routes());
    }

    /**
     * Retourne les cartes wagon/locomotive du joueur
     * @return
     */
    public SortedBag<Card> cards(){
        return cards;
    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur possède en plus la carte donnée
     * @param card
     * @return PlayerState
     */
    public PlayerState withAddedCard(Card card){
        return new PlayerState(tickets(), cards().union(SortedBag.of(card)),routes());
    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur possède en plus les cartes données
     * @param additionalCards
     * @return PlayerState
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards){
        return new PlayerState(tickets(), cards().union(additionalCards),routes());
    }


    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur possède en plus les cartes données
     * @param route
     * @return boolean
     */
    public boolean canClaimRoute(Route route){
        if(route.length()> carCount()) {return false;}

        for (SortedBag<Card> bag : route.possibleClaimCards()) {
            if(routes().contains(bag)){
                return true;
            }
        }
        return false;
    }



    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur pourrait utiliser pour s'emparer d'un tunnel,
     * trié par ordre croissant du nombre de cartes locomotives.
     * @param additionalCardsCount nombres de cartes à jouer en plus
     * @param initialCards cartes jouer par le joueur
     * @param drawnCards cartes tirées du sommet
     * @throws IllegalArgumentException si le nombre de cartes additionnelles n'est pas compris entre 1 et 3 (inclus), si l'ensemble des cartes initiales est vide ou contient plus de 2 types de cartes différents,
     * ou si l'ensemble des cartes tirées ne contient pas exactement 3 cartes
     * @return
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards){
        Preconditions.checkArgument(1<= additionalCardsCount && additionalCardsCount <= 3);
        Preconditions.checkArgument(!initialCards.isEmpty());
        int differentCard = 0;
        Color claimColor = null;
        for (Card card: Card.ALL) {
            if(initialCards.contains(card)){
                differentCard++;
            }
            if(card.color() != null){
                claimColor = card.color();
            }
        }
        Preconditions.checkArgument(differentCard <=2);
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        SortedBag<Card> cardsWithoutInitialCards = cards().difference(initialCards);
        SortedBag<Card> usableCards;
        if (claimColor == null){
            usableCards = SortedBag.of(cardsWithoutInitialCards.countOf(Card.of(claimColor)),Card.of(claimColor));
        }else{
            usableCards = SortedBag.of(cardsWithoutInitialCards.countOf(Card.of(claimColor)),Card.of(claimColor),
                    cardsWithoutInitialCards.countOf(null),Card.of(null));
        }
        Set<SortedBag<Card>> possibleCards = usableCards.subsetsOfSize(additionalCardsCount);
        List<SortedBag<Card>> options = new ArrayList<>(possibleCards);
        options.sort(
                Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));

        return options;

    }


    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur s'est de plus emparé de la route donnée au moyen des cartes données
     * @param route
     * @param claimCards
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        List<Route> newRoutes = routes();
        newRoutes.add(route);
        return new PlayerState(tickets(),cards().difference(claimCards),newRoutes);
    }

    /**
     * Retourne le nombre de points—éventuellement négatif—obtenus par le joueur grâce à ses billets
     * @return int
     */
    public int ticketPoints(){
        int max = 0;
        for(Route route : routes()){
            if((route.station1().id() > max)){
                max = route.station1().id();
            }else if((route.station2().id() > max)){
                max = route.station2().id();
            }
        }
        StationPartition.Builder builder = new StationPartition.Builder(max + 1);
        for(Route route : routes()){
            builder.connect(route.station1(), route.station2());
        }
        StationPartition partition = builder.build();

        int ticketCount = 0;
        for(Ticket billet : tickets()){
            ticketCount += billet.points(partition);
        }
        return ticketCount;

    }

    /**
     * retourne la totalité des points obtenus par le joueur à la fin de la partie, à savoir la somme des points retournés par les méthodes claimPoints et ticketPoints
     * @return int
     */
    public int finalPoints(){
        return ticketPoints() + claimPoints();

    }

}
