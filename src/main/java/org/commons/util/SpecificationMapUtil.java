package org.commons.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: ClienteFinal
 * Date: Sep 15, 2010
 * Time: 7:17:10 AM
 */
public class SpecificationMapUtil {

    private static Map
            /*<String, <Map <String, Specification>>*/
            instancesMap = new HashMap/*<String, <Map <String, Specification>>*/();

    public static SpecificationMap getInstance(Class clazz) throws IOException {
        ClassLoader classLoader = clazz.getClassLoader();
        Properties prop = new Properties();
        prop.load(classLoader.getResourceAsStream("specification.properties"));
        return getInstance(prop, clazz);
    }

    public static SpecificationMap getInstance() throws IOException {
        return getInstance(SpecificationMapUtil.class.getClass());
    }

    public static SpecificationMap getInstance(Map /*<String, String>*/ map, Class clazz) {
        return buildInstance(MapUtil.toProperties(map), clazz, false, clazz.getClassLoader());
    }

    public static SpecificationMap getInstance(Map /*<String, String>*/ map, Class clazz, boolean replace) {
        return buildInstance(MapUtil.toProperties(map), clazz, replace, clazz.getClassLoader());
    }

    public static SpecificationMap getInstance(File propertiesFile, Class clazz) {
        return buildInstance(PropertiesHandlerUtil.getInstance(propertiesFile, clazz.getClassLoader()).getProperties(),
                clazz,
                false,
                clazz.getClassLoader());
    }

    public static SpecificationMap getInstance(File propertiesFile, Class clazz, boolean replace) {
        return buildInstance(PropertiesHandlerUtil.getInstance(propertiesFile, clazz.getClassLoader()).getProperties(),
                clazz,
                replace,
                clazz.getClassLoader());
    }

    private static SpecificationMap buildInstance(Properties properties,
                                                  Class clazz,
                                                  boolean replace,
                                                  ClassLoader classLoader) {
        if (instancesMap.get(clazz.getName()) == null) {
            SpecificationMap instance = new SpecificationMap(clazz, properties, classLoader);
            instancesMap.put(clazz.getName(), instance);
        } else {
            if (replace) {
                SpecificationMap newInstance = new SpecificationMap(clazz, properties, classLoader);
                SpecificationMap oldInstance = (SpecificationMap) instancesMap.get(clazz.getName());
                newInstance.getProperties().putAll(oldInstance.getProperties());
                instancesMap.remove(clazz.getName());
                instancesMap.put(clazz.getName(), newInstance);
            }
        }
        return (SpecificationMap) instancesMap.get(clazz.getName());
    }

    private SpecificationMapUtil() {
    }

    public static Map getInstancesMap() {
        return instancesMap;
    }

}
