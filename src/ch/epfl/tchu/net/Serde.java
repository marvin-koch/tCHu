package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import javax.sound.midi.Soundbank;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * L'interface Serde un objet capable de sérialiser et désérialiser des valuers d'un type donné
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
public interface Serde<T>{
    /**
     * Prend en argument l'objet à sérialiser et retournant la chaîne correspondante
     * @param t objet à sérialiser
     * @return chaine
     */
    String serialize(T t);

    /**
     * Prend en argument une chaîne et retournant l'objet correspondant.
     * @param s chaine à deserializer
     * @return objet
     */
    T deserialize(String s);

    /**
     * Méthode générique prenant en arguments une fonction de sérialisation et une fonction de désérialisation,
     * et retournant le serde correspondant; le type de la fonction de sérialisation qu'on lui passe doit être Function<T, String>,
     * tandis que celui de la fonction de désérialisation doit être Function<String, T>, où T est le paramètre de type de la méthode
     * @param encode fonction de sérialisation
     * @param decode la fonction de désérialisation
     * @param <T> paramètre de type de la méthode
     * @return un Serde
     */
    static<T> Serde<T> of(Function<T, String> encode, Function<String, T> decode){
        return new Serde<T> (){
            @Override
            public String serialize(T t) {
                return encode.apply(t);
            }

            @Override
            public T deserialize(String s) {
                return decode.apply(s);
            }
        };
    }

    /**
     * Méthode générique prenant en argument la liste de toutes les valeurs d'un ensemble de valeurs énuméré et retournant le serde correspondant
     * @param list liste d'element d'un type enum
     * @param <T> paramètre de type de la méthode
     * @return un Serde
     */
    static<T> Serde<T> oneOf(List<T> list){
        Preconditions.checkArgument(!list.isEmpty());
        return new Serde<T>(){
            @Override
            public String serialize(T t) {
                /*
                if(t == null){
                    return "";
                }else{
                    return String.valueOf(list.indexOf(t));
                }

                 */
                return t != null ? String.valueOf(list.indexOf(t)) : "";
            }

            @Override
            public T deserialize(String s) {
                if(s.equals("")){
                    return null;
                }else{
                    int pos = Integer.parseInt(s);
                    Preconditions.checkArgument(pos < list.size());
                    return list.get(pos);
                }
            }
        };
    }

    /**
     * Méthode générique listOf prenant en argument un serde et un caractère de séparation et retournant
     * un serde capable de (dé)sérialiser des listes de valeurs (dé)sérialisées par le serde donné
     * @param serde serde
     * @param delimiter un caractère de séparation
     * @param <T> paramètre de type de la méthode
     * @return un Serde
     */
    static<T> Serde<List<T>> listOf(Serde<T> serde, String delimiter){
        Preconditions.checkArgument(!delimiter.isEmpty());
        return new Serde<>() {
            @Override
            public String serialize(List<T> t) {
                return t.stream()
                        .map(serde::serialize)
                        .collect(Collectors.joining(delimiter));
            }

            @Override
            public List<T> deserialize(String s) {
                if (s.equals("")) {
                    return List.of();
                } else {
                    List<String> array = Arrays.asList(s.split(Pattern.quote(delimiter), -1));
                    return array.stream()
                            .map(serde::deserialize)
                            .collect(Collectors.toList());
                }
            }
        };
    }

    /**
     * Méthode générique fonctionnant comme listOf mais pour les multiensembles triés (SortedBag)
     * @param serde serde
     * @param delimiter un caractère de séparation
     * @param <T> paramètre de type de la méthode
     * @return un Serde
     */
    static<T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String delimiter){
        Preconditions.checkArgument(!delimiter.isEmpty());
        return new Serde<SortedBag<T>>(){
            @Override
            public String serialize(SortedBag<T> t) {
                return t.stream()
                        .map(serde::serialize)
                        .collect(Collectors.joining(delimiter));
            }

            @Override
            public SortedBag<T> deserialize(String s){
                if(s.equals("")){
                    return SortedBag.of();
                }else{
                    List<String> array = Arrays.asList(s.split(Pattern.quote(delimiter), -1));
                    return SortedBag.of(array.stream()
                            .map(serde::deserialize)
                            .collect(Collectors.toList()));
                }
            }
        };
    }



}
