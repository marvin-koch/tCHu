package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Class Station
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class Station {
    private final String name;
    private final int id; // entre 0 et 50


    /**
     *
     * @param id numéro d'identification de la gare
     * @param name nom de la gare
     * @throws IllegalArgumentException
     */
    public Station(int id, String name){
        Preconditions.checkArgument(id >= 0);
        this.name = name;
        this.id = id;
    }

    /**
     * Retourne id
     * @return id
     */
    public int id(){
        return id;
    }

    /**
     * Retourne name
     * @return name
     */
    public String name(){
        return name;
    }

    /**
     * Retourne name
     * @return name
     */
    @Override
    public String toString(){
        return name;
    }
}


