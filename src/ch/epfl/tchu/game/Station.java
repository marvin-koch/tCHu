package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Class Station
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public class Station {
    private final String name;
    private final int id; // entre 0 et 50

    public Station(int id, String name){
        /*
        if(id < 0 || id >50){ // je sais pas si le > 50 je dois le mettre
            throw new IllegalArgumentException();
        }*/
        Preconditions.checkArgument(id >= 0 && id <=50);
        this.name = name;
        this.id = id;
    }

    public int id(){
        return id;
    }

    public String name(){
        return name;
    }

    @Override
    public String toString(){
        return name;
    }
}


