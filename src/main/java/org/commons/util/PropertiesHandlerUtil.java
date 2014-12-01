package org.commons.util;

import org.commons.exceptions.PropertiesUtilException;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class PropertiesHandlerUtil {

    private static Map /*<String, PropertiesHandler>*/ instancesMap = new HashMap();
    private static PropertiesHandler instance;

    public static PropertiesHandler getInstance() {
        if (instance == null) {
            instance = new PropertiesHandler();
        }
        return instance;
    }

    public static PropertiesHandler getInstance(String logFileSourceName) {
        return getInstance(logFileSourceName, false);
    }

    public static PropertiesHandler getInstance(String logFileSourceName, boolean forceRenew) {
        PropertiesHandler instance;
        if (forceRenew || instancesMap.get(logFileSourceName) == null) {
            instancesMap.remove(logFileSourceName);
            instance = new PropertiesHandler(logFileSourceName);
            if (instance.getProperties() != null && instance.getProperties().size() > 0) {
                instancesMap.put(logFileSourceName, instance);
            } else {
                instance = new PropertiesHandler();
            }
        } else {
            instance = (PropertiesHandler) instancesMap.get(logFileSourceName);
        }
        return instance;
    }

    public static PropertiesHandler getInstance(File propertiesFile, ClassLoader classLoader) {
        return getInstance(propertiesFile, classLoader, false);
    }

    public static PropertiesHandler getInstance(File propertiesFile, ClassLoader classLoader, boolean forceRenew) {
        PropertiesHandler instance;
        String logFileSourceName = propertiesFile.getPath();
        if (forceRenew || instancesMap.get(logFileSourceName) == null) {
            instancesMap.remove(logFileSourceName);
            instance = new PropertiesHandler(logFileSourceName, classLoader);
            if (instance.getProperties() != null && instance.getProperties().size() > 0) {
                instancesMap.put(logFileSourceName, instance);
            } else {
                instance = new PropertiesHandler();
            }
        } else {
            instance = (PropertiesHandler) instancesMap.get(logFileSourceName);
        }
        return instance;
    }

    public static PropertiesHandler getInstance(String logFileSourceName, ClassLoader classLoader) {
        return getInstance(logFileSourceName, classLoader, false);
    }

    public static PropertiesHandler getInstance(String logFileSourceName, ClassLoader classLoader, boolean forceRenew) {
        PropertiesHandler instance;
        if (forceRenew || instancesMap.get(logFileSourceName) == null) {
            instancesMap.remove(logFileSourceName);
            instance = new PropertiesHandler(logFileSourceName, classLoader);
            if (instance.getProperties() != null && instance.getProperties().size() > 0) {
                instancesMap.put(logFileSourceName, instance);
            } else {
                instance = new PropertiesHandler();
            }
        } else {
            instance = (PropertiesHandler) instancesMap.get(logFileSourceName);
        }
        return instance;
    }

    public static Collection loadConfig(String logFileSourceName,
                                        Class clazz,
                                        final String propertyName,
                                        final String tokenSeparator) throws PropertiesUtilException {

        final Properties properties = getInstance(logFileSourceName, clazz.getClassLoader()).getProperties();
        final Object[] propertyValues = new Object[1];
        Map propertiesStartedWith = MapUtil.find(properties, new Predicate() {
            public boolean evaluate(Object o) {
                return ((String) o).equalsIgnoreCase(propertyName);
            }
        }, NotNullOrEmptyStringPredicate.getInstance(), MapUtil.EVALUATE_BOTH_KEY_AND_VALUE);

        CollectionUtils.forAllDo(propertiesStartedWith.values(), new Closure() {
            public void execute(Object o) {
                propertyValues[0] = ((String) o).split(tokenSeparator);
            }
        });
        Collection result;
        try {
            result = Arrays.asList((Object[]) propertyValues[0]);
        } catch (Exception e) {
            result = new ArrayList();
        }
        CollectionUtils.transform(result, new Transformer() {
            public Object transform(Object o) {
                return ((String) o).trim();
            }
        });
        return result;
    }

    public Collection loadConfig(String logFileSourceName, Class clazz, String propertyName)
            throws PropertiesUtilException {
        return loadConfig(logFileSourceName, clazz, propertyName, StringUtil.COMMA_SYMBOL);
    }

    public static Map loadConfig(String logFileSourceName,
                                 Class clazz,
                                 String propertyName,
                                 String tokenSeparator,
                                 String valueSeparator) throws PropertiesUtilException {
        Map result = new Hashtable();
        Collection properties = loadConfig(logFileSourceName, clazz, propertyName, tokenSeparator);
        String key;
        String value;
        String property;
        String[] keyValue;
        for (Iterator iter = properties.iterator(); iter.hasNext(); ) {
            property = (String) iter.next();
            key = null;
            value = StringUtil.EMPTY_STRING;
            if (property.indexOf(valueSeparator) > -1) {
                keyValue = property.split(valueSeparator);
                try {
                    key = keyValue[0];
                } catch (Exception ignored) {

                }
                try {
                    value = keyValue[1];
                } catch (Exception ignored) {

                }
            }
            if (!StringUtil.isNullOrEmpty(key)) {
                result.put(key.trim(), value.trim());
            }
        }
        return result;
    }

}




