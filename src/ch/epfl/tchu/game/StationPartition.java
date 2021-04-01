package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Class StationPartition
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public final class StationPartition implements StationConnectivity {
    private final int[] gares;

    /**
     * Constructeur privé
     * @param array liste d'entier
     */
    private StationPartition(int[] array){
        gares = array;
    }


    /**
     * Méthode indiquant si 2 stations dans la partition sont connectées.
     * Si une de leurs id est plus grande que la partition, alors elle retourne vrai si les 2 gares sont identiques.
     * @param s1 première station
     * @param s2 deuxième station
     * @return true si elles sont connectées, false sinon
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        if((s1.id() >= gares.length) || (s2.id() >= gares.length)){
           return (s1.id() == s2.id());
        }else {
            return (gares[s1.id()] == gares[s2.id()]);
        }
    }

    /**
     * StationPartition Builder
     * @author Shangeeth Poobalasingam (329307)
     * @author Marvin Koch (324448)
     */
    public final static class Builder{
        private int[] gares;

        /**
         * Calcule le représentant de la partition d'une station
         * @param id id
         * @return id du représentant
         */
        private int representative(int id){
            int avant = id;
            int apres = 0;
            while(id != gares[id]){
                id = gares[id];
            }
            return id;
        }

        /**
         * Constructeur du Bâtisseur
         * @param stationCount
         * @throws IllegalArgumentException si stationCount est négatif
         */
       public Builder(int stationCount){
           Preconditions.checkArgument(stationCount >= 0) ;
           gares = new int[stationCount];
           for(int i = 0; i < gares.length; ++i){
               gares[i] = i;
           }
       }

        /**
         * Méthode qui joint 2 stations dans la partition en choissiant l'un des deux représentants comme représentant du sous-ensemble joint
         * @param s1 station 1
         * @param s2 station 2
         * @return la même instance de Builder
         */
       public Builder connect(Station s1, Station s2){
               gares[representative(s1.id())] = representative(s2.id());
           return this;
       }

        /**
         * Construit la partition aplatie a partir de la partition profonde
         * @return StationPartition
         */
       public StationPartition build(){
           for(int i = 0; i < gares.length; ++i){
               gares[i] = representative(gares[i]);

           }
           return new StationPartition(gares);
       }
    }
}
