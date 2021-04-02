package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;
import java.util.List;
import java.util.stream.Collectors;

import static ch.epfl.tchu.gui.StringsFr.*;


/**
 * La classe Info publique, finale et immuable, permet de générer les textes décrivant le déroulement de la partie.
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class Info {
    private final String playerName;

    /**
     * Constructeur de Info
     * @param playerName nom du joueur
     */
    public Info(String playerName){
        this.playerName = playerName;
    }

    /**
     * Retourne le nom de la carte en sachant la carte et ça multiplicté
     * @param card la carte
     * @param count la multiplicité
     * @return nom de la carte en string
     */
    public static String cardName(Card card, int count){
        StringBuilder name = new StringBuilder();
        switch(card){
            case BLACK:
               name.append(BLACK_CARD);
               break;
            case VIOLET:
                name.append(VIOLET_CARD);
                break;
            case BLUE:
                name.append(BLUE_CARD);
                break;
            case GREEN:
                name.append(GREEN_CARD);
                break;
            case YELLOW:
                name.append(YELLOW_CARD);
                break;
            case ORANGE:
                name.append(ORANGE_CARD);
                break;
            case WHITE:
                name.append(WHITE_CARD);
                break;
            case RED:
                name.append(RED_CARD);
                break;
            default:
                name.append(LOCOMOTIVE_CARD);
        }
        return name.append(plural(count))
                .toString();
    }

    /**
     * Retourne le message déclarant que les joueurs, dont les noms sont ceux donnés, ont terminé la partie ex æqo en ayant chacun remporté les points donnés
     * @param playerNames noms des joueurs
     * @param points les points
     * @return String
     */
    public static String draw(List<String> playerNames, int points){
        return String.format(DRAW, playerNames.get(0)+ AND_SEPARATOR +playerNames.get(1),points );
    }


    /**
     * Retourne le message déclarant que le joueur jouera en premier
     * @return String
     */
    public String willPlayFirst(){
        return String.format(WILL_PLAY_FIRST,playerName);
    }


    /**
     * Retourne le message déclarant que le joueur a gardé le nombre de billets donné
     * @param count nombre de billets
     * @return String
     */
    public String keptTickets(int count){
        return String.format(KEPT_N_TICKETS,playerName,count,plural(count));
    }

    /**
     * Retourne le message déclarant que le joueur peut jouer
     * @return String
     */
    public String canPlay(){
        return String.format(CAN_PLAY,playerName);
    }


    /**
     * Retourne le message déclarant que le joueur a tiré le nombre donné de billets
     * @param count nombre de billets
     * @return String
     */
    public String drewTickets(int count){
        return String.format(DREW_TICKETS,playerName,count,plural(count));
    }

    /**
     * Retourne le message déclarant que le joueur a tiré une carte à l'aveugle», c-à-d du sommet de la pioche
     * @return String
     */
    public String drewBlindCard(){
        return String.format(DREW_BLIND_CARD,playerName);
    }

    /**
     * Retourne le message déclarant que le joueur a tiré la carte disposée face visible donnée
     * @param card la carte
     * @return String
     */
    public String drewVisibleCard(Card card){
        return String.format(DREW_VISIBLE_CARD,playerName,cardName(card,1));
    }

    /**
     * Retourne le message déclarant que le joueur s'est emparé de la route donnée au moyen des cartes données
     * @param route la route emparée
     * @param cards les cartes utilisées
     * @return String
     */
    public String claimedRoute(Route route, SortedBag<Card> cards){
        return String.format(CLAIMED_ROUTE, playerName, routeString(route),cardsString( cards));

    }


    /**
     * Retourne le message déclarant que le joueur désire s'emparer de la route en tunnel donnée en utilisant initialement les cartes données
     * @param route le tunnel emparé
     * @param initialCards les cartes initiales
     * @return String
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards){
        return String.format(ATTEMPTS_TUNNEL_CLAIM,playerName, routeString(route), cardsString(initialCards));
    }


    /**
     * Retourne le message déclarant que le joueur a tiré les trois cartes additionnelles données, et qu'elles impliquent un coût additionel du nombre de cartes donné
     * @param drawnCards les cartes additionnelles
     * @param additionalCost le coût additionel
     * @return String
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost){
        String cost = additionalCost == 0 ? NO_ADDITIONAL_COST : String.format(SOME_ADDITIONAL_COST, additionalCost,plural(additionalCost));
        return String.format(ADDITIONAL_CARDS_ARE, cardsString(drawnCards)) + cost;
    }


    /**
     * Retourne le message déclarant que le joueur n'a pas pu (ou voulu) s'emparer du tunnel donné
     * @param route le tunnel
     * @return String
     */
    public String didNotClaimRoute(Route route){
        return String.format(DID_NOT_CLAIM_ROUTE,playerName, routeString(route));
    }


    /**
     * Retourne le message déclarant que le joueur n'a plus que le nombre donné (et inférieur ou égale à 2) de wagons, et que le dernier tour commence donc
     * @param carCount nombre de wagons
     * @return String
     */
    public String lastTurnBegins(int carCount){
        return String.format(LAST_TURN_BEGINS,playerName, carCount,plural(carCount));
    }

    /**
     * Retourne le message déclarant que le joueur obtient le bonus de fin de partie grâce au chemin donné, qui est le plus long, ou l'un des plus longs
     * @param longestTrail chemin le plus long
     * @return String
     */
    public String getsLongestTrailBonus(Trail longestTrail){
        return String.format(GETS_BONUS,playerName,longestTrail.station1().toString() + EN_DASH_SEPARATOR + longestTrail.station2().toString());
    }

    /**
     * Retourne le message déclarant que le joueur remporte la partie avec le nombre de points donnés, son adversaire n'en ayant obtenu que loserPoints
     * @param points les points du gagnant
     * @param loserPoints les points du perdant
     * @return String
     */
    public String won(int points, int loserPoints){
        return String.format(WINS,playerName,points, plural(points),loserPoints,plural(loserPoints));
    }

    /**
     * Méthode qui calcule le nom d'une route
     * @param route la route
     * @return String
     */
    private static String routeString(Route route){
        return route.station1().toString() + EN_DASH_SEPARATOR + route.station2().toString();
    }

    /**
     * Méthode qui calcule la description d'un ensemble de cartes
     * @param cards les cartes
     * @return String
     */
    private static String cardsString(SortedBag<Card> cards) {
        List<String> strings = cards.toSet().stream()
                .map(card -> String.format("%s %s", cards.countOf(card), cardName(card, cards.countOf(card))))
                .collect(Collectors.toList());
        switch(strings.size()){
            case(1):
                return strings.get(0);
            case(2):
                return String.join(AND_SEPARATOR, strings);
            default:
                return String.join(", ", strings.subList(0, strings.size() - 1)) + AND_SEPARATOR
                        + strings.get(strings.size() - 1);
        }
    }
}
