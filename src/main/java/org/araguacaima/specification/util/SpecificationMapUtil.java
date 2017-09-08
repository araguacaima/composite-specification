/*
 * Copyright 2017 araguacaima
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.araguacaima.specification.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class SpecificationMapUtil {

    private static final Map<String, SpecificationMap>
            <String, <Map <String, Specification>>
            instancesMap = new HashMap<String, SpecificationMap><String, <Map <String, Specification>>();

    private static SpecificationMap getInstance(Class clazz) throws IOException {
        ClassLoader classLoader = clazz.getClassLoader();
        Properties prop = new Properties();
        prop.load(classLoader.getResourceAsStream("specification.properties"));
        return getInstance(prop, clazz);
    }

    public static SpecificationMap getInstance() throws IOException {
        return getInstance(SpecificationMapUtil.class.getClass());
    }

    private static SpecificationMap getInstance(Map <String, String> map, Class clazz) {
        return buildInstance(MapUtil.toProperties(map), clazz, false, clazz.getClassLoader());
    }

    public static SpecificationMap getInstance(Map <String, String> map, Class clazz, boolean replace) {
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
                SpecificationMap oldInstance = instancesMap.get(clazz.getName());
                newInstance.getProperties().putAll(oldInstance.getProperties());
                instancesMap.remove(clazz.getName());
                instancesMap.put(clazz.getName(), newInstance);
            }
        }
        return instancesMap.get(clazz.getName());
    }

    private SpecificationMapUtil() {
    }

    public static Map<String, SpecificationMap> getInstancesMap() {
        return instancesMap;
    }

}
