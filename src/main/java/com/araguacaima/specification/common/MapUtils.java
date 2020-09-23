package com.araguacaima.specification.common;

import org.apache.commons.collections4.Predicate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapUtils {

    public static <E, F> Map<?, ?> select(Map<E, F> map, Predicate<E> keyPredicate, Predicate<F> valuePredicate) {
        Map<Object, Object> newMap = new HashMap<>(map);
        E key;
        F value;
        for (Iterator<Map.Entry<E, F>> it = map.entrySet().iterator(); it.hasNext(); removeFromMap(key,
                value,
                keyPredicate,
                valuePredicate,
                newMap)) {
            java.util.Map.Entry<E, F> entry = it.next();
            key = entry.getKey();
            value = entry.getValue();
        }
        return newMap;
    }

    private static <E, T> void removeFromMap(E key, T value, Predicate<E> keyPredicate, Predicate<T> valuePredicate, Map<Object, Object> map) {
        if (keyPredicate != null && !keyPredicate.evaluate(key)) {
            map.remove(key);
            return;
        }
        if (valuePredicate != null && !valuePredicate.evaluate(value)) {
            map.remove(key);
        }
    }
}
