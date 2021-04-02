package ch.epfl.tchu.game;
/**
 * L'interface StationConnectivity publique, représente ce que nous appellerons la «connectivité» du réseau d'un joueur,*
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public interface StationConnectivity {
    boolean connected(Station s1, Station s2);
}
