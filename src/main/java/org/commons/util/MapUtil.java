package org.commons.util;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.log4j.Logger;

import java.util.*;

public class MapUtil extends org.apache.commons.collections.MapUtils {

    public static final int EVALUATE_BOTH_KEY_AND_VALUE = 0;
    public static final int EVALUATE_JUST_KEY = 1;
    public static final int EVALUATE_JUST_VALUE = 2;
    public static final int EVALUATE_BOTH_KEY_OR_VALUE = 3;
    public static final int DEFAULT_EVALUATION_TYPE = EVALUATE_BOTH_KEY_AND_VALUE;
    public static final StringKeyHashMapUtil stringKeyHashMapUtil = new StringKeyHashMapUtil();
    private static Logger log = Logger.getLogger(MapUtil.class);

    public static Map select(Map map, Predicate keyPredicate, Predicate valuePredicate) {
        Map newMap = new HashMap(map);
        Object key;
        Object value;
        for (Iterator it = map.entrySet().iterator();
             it.hasNext();
             removeFromMap(key, value, keyPredicate, valuePredicate, newMap)) {
            java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
            key = entry.getKey();
            value = entry.getValue();
        }
        return newMap;
    }

    public static Map find(Map map, Predicate keyPredicate, Predicate valuePredicate, int evaluationType) {
        Map newMap = new HashMap();
        Object key;
        Object value;
        for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
            key = entry.getKey();
            value = entry.getValue();
            if (existsInMap(key, value, keyPredicate, valuePredicate, evaluationType)) {
                newMap.put(key, value);
                break;
            }
        }
        return newMap;
    }

    public static Object findObject(Map map, Predicate keyPredicate, Predicate valuePredicate, int evaluationType) {

        Object key;
        Object value = null;
        for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
            key = entry.getKey();
            value = entry.getValue();
            if (existsInMap(key, value, keyPredicate, valuePredicate, evaluationType)) {
                break;
            }
        }
        return value;
    }

    private static void removeFromMap(Object key,
                                      Object value,
                                      Predicate keyPredicate,
                                      Predicate valuePredicate,
                                      Map map) {
        if (keyPredicate != null && !keyPredicate.evaluate(key)) {
            map.remove(key);
            return;
        }
        if (valuePredicate != null && !valuePredicate.evaluate(value)) {
            map.remove(key);
        }
    }

    private static boolean existsInMap(Object key,
                                       Object value,
                                       Predicate keyPredicate,
                                       Predicate valuePredicate,
                                       int evaluationType) {
        boolean result = false;
        try {
            switch (evaluationType) {
                case EVALUATE_JUST_KEY:
                    result = keyPredicate != null && keyPredicate.evaluate(key);
                    break;
                case EVALUATE_JUST_VALUE:
                    result = valuePredicate != null && valuePredicate.evaluate(value);
                    break;
                case EVALUATE_BOTH_KEY_AND_VALUE:
                    result = keyPredicate != null
                            && keyPredicate.evaluate(key)
                            && valuePredicate != null
                            && valuePredicate.evaluate(value);
                    break;
                case EVALUATE_BOTH_KEY_OR_VALUE:
                    result = keyPredicate != null && keyPredicate.evaluate(key)
                            || valuePredicate != null && valuePredicate.evaluate(value);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.debug("Error evaluating predicates. " + e.getMessage());
        }
        return result;
    }

    public static Properties toProperties(final Map/*<String, String>*/ map) {
        if (map instanceof Properties) {
            return (Properties) map;
        }
        final Properties properties = new Properties();
        CollectionUtils.forAllDo(map.keySet(), new Closure() {
            public void execute(Object o) {
                Object key = o;
                Object value = map.get(key);
                if (key != null) {
                    key = String.valueOf(o);
                    if (value != null) {
                        value = String.valueOf(value);
                    } else {
                        value = StringUtil.EMPTY_STRING;
                    }
                    properties.setProperty((String) key, (String) value);
                }
            }
        });
        return properties;
    }

    private static void insertIntoMap(Object key,
                                      Object value,
                                      Transformer keyTransformer,
                                      Transformer valueTransformer,
                                      Map map) {
        Object transformedKey = null;
        Object transformedValue = null;
        if (keyTransformer != null) {
            transformedKey = keyTransformer.transform(key);
        }
        if (valueTransformer != null) {
            transformedValue = valueTransformer.transform(value);
        }
        if (transformedKey != null) {
            map.put(transformedKey, transformedValue);
        }
    }

    private static void appendIntoMap(Object key,
                                      Object value,
                                      Transformer keyTransformer,
                                      Transformer valueTransformer,
                                      Map map) {
        map.remove(key);
        insertIntoMap(key, value, keyTransformer, valueTransformer, map);

    }

    public static Map transform(Map map, Transformer keyTransformer, Transformer valueTransformer) {
        Map newMap = new HashMap(map);
        Object key;
        Object value;
        for (Iterator it = map.entrySet().iterator();
             it.hasNext();
             appendIntoMap(key, value, keyTransformer, valueTransformer, newMap)) {
            java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
            key = entry.getKey();
            value = entry.getValue();
        }
        return newMap;
    }

    public static void removeAll(final Map map, Collection keys) {

        CollectionUtils.forAllDo(keys, new Closure() {
            public void execute(Object o) {
                map.remove(o);
            }
        });
    }

    public static StringKeyHashMapUtil getStringKeyHashMapUtil() {
        return stringKeyHashMapUtil;
    }

    public static class StringKeyHashMapUtil extends HashMap {

        public StringKeyHashMapUtil() {
            super();
        }

        private static final long serialVersionUID = -8603163772769655779L;

        /**
         * Returns <tt>true</tt> if this map contains a mapping for the
         * specified key, that match with the incoming substring.
         *
         * @param substring The substring in the key whose presence in this map is to be tested
         * @return <tt>true</tt> if this map contains a mapping for the specified substring
         * key.
         */
        public boolean containsKeySubstring(String substring) {
            for (Iterator iter = this.keySet().iterator(); iter.hasNext(); ) {
                String key = (String) iter.next();
                if (key.indexOf(substring) != -1) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns <tt>true</tt> if this map contains a mapping for the
         * specified key, that match with the incoming substring.
         *
         * @param substring The substring in the key whose presence in this map is to be tested
         * @return <tt>true</tt> if this map contains a mapping for the specified substring
         * key.
         */
        public Object getKeySubstringValue(String substring) {
            for (Iterator iter = this.keySet().iterator(); iter.hasNext(); ) {
                String key = (String) iter.next();
                if (key.indexOf(substring) != -1) {
                    return this.get(key);
                }
            }
            return null;
        }

        /**
         * Returns <tt>true</tt> if this map contains a mapping for the
         * specified substring, that match with the incoming key.
         *
         * @param key The key whose presence in this map is to be tested
         * @return <tt>true</tt> if this map contains a mapping for the specified substring
         * key.
         */
        public Object getSubstringKeyValue(String key) {
            for (Iterator iter = this.keySet().iterator(); iter.hasNext(); ) {
                String substring = (String) iter.next();
                if (key.indexOf(substring) != -1) {
                    return this.get(substring);
                }
            }
            return null;
        }

    }

}
