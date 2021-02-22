package ch.epfl.tchu;

/**
 * Class Préconditions
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */

public final class Preconditions{
    private Preconditions(){}

    /**
     * Vérifie si le boolean est True, s'il est False il lance une Exceptions
     * @param shouldBeTrue
     * @throws IllegalArgumentException
     */

    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }

    public static void checkNull(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new NullPointerException();
        }
    }

}
