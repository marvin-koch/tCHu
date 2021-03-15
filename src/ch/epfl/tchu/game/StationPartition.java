package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;

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
     * @param array
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
        if((s1.id() > gares.length) || (s2.id() > gares.length)){
           if(s1.id() == s2.id()){
               return true;
           }else{
               return false;
           }
        }
        if(gares[s1.id()] == gares[s2.id()]){
            return true;
        }else{
            return false;
        }
    }

    /**
     * StationPartition Builder
     */
    public final static class Builder{
        private int[] gares;
       public Builder(int stationCount){
           Preconditions.checkArgument(stationCount >= 0) ;
           gares = new int[stationCount - 1];
           for(int i = 0; i < gares.length; ++i){
               gares[i] = i;
           }

       }
       public Builder connect(Station s1, Station s2){
           gares[representative(s1.id())] = s2.id();
           return this;
       }

       public StationPartition build(){
           for(int i = 0; i < gares.length; ++i){
               gares[i] = representative(gares[i]);
           }
           return new StationPartition(gares);
       }

       private int representative(int id){
            while(id != gares[id]){
                id = gares[id];
            }
           return id;
       }
    }
}
