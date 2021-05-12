package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;
import javafx.event.EventHandler;

/**
 * L'interface ActionHandlers a pour unique but de contenir cinq interfaces
 * fonctionnelles imbriquées représentant différents «gestionnaires d'actions»
 */
interface ActionHandlers {
    //TODO COMMENTER les interfaces

    /**
     * Gestionnaire de ?????? todo faire ça
     */
    @FunctionalInterface
    interface DrawTicketsHandler{
        /**
         * Méthode appelée lorsque le joueur désire tirer des billet
         */
        void onDrawTickets();
    }
    @FunctionalInterface
    interface DrawCardHandler{
        /**
         * Méthode appelée lorsque le joueur désire tirer une carte de l'emplacement donné
         * @param slot un numéro d'emplacement (0 à 4, ou -1 pour la pioche)
         */
        void onDrawCard(int slot);
    }
    @FunctionalInterface
    interface ClaimRouteHandler{
        /**
         * Méthode appelée lorsque le joueur désire s'emparer de la route donnée au moyen des cartes (initiales) données
         * @param route une route
         * @param cards un multiensemble de cartes
         */
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }

    @FunctionalInterface
    interface ChooseTicketsHandler{
        /**
         * Méthode appelée lorsque le joueur a choisi de garder les billets donnés suite à un tirage de billets
         * @param bag un multiensemble de billets
         */
        void onChooseTickets(SortedBag<Ticket> bag);
    }

    @FunctionalInterface
    interface ChooseCardsHandler{
        /**
         *  Méthode appelée lorsque le joueur a choisi d'utiliser les cartes données comme cartes initiales
         *  ou additionnelles lors de la prise de possession d'une route;
         *  s'il s'agit de cartes additionnelles, alors le multiensemble peut être vide,
         *  ce qui signifie que le joueur renonce à s'emparer du tunnel.
         * @param cards un multiensemble de cartes
         */
        void onChooseCards(SortedBag<Card> cards);
    }
}
