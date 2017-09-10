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
package com.araguacaima.specification.util;

import com.araguacaima.commons.utils.MapUtils;
import com.araguacaima.commons.utils.PropertiesHandlerUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("WeakerAccess")
@Service
public class SpecificationMapBuilder implements ApplicationContextAware {

    private final Map<String, SpecificationMap> instancesMap = new HashMap<>();
    private ApplicationContext applicationContext;
    private MapUtils mapUtils;
    private String propertiesFile = "specification.properties";
    private PropertiesHandlerUtils propertiesHandlerUtils;

    @Autowired
    public SpecificationMapBuilder(MapUtils mapUtils, PropertiesHandlerUtils propertiesHandlerUtils) {
        this.mapUtils = mapUtils;
    }

    public SpecificationMap getInstance()
            throws IOException {
        return getInstance(SpecificationMapBuilder.class.getClass());
    }

    private SpecificationMap getInstance(Class clazz)
            throws IOException {
        ClassLoader classLoader = clazz.getClassLoader();
        Properties prop = new Properties();
        prop.load(classLoader.getResourceAsStream(propertiesFile));
        return getInstance(prop, clazz);
    }

    private SpecificationMap getInstance(Properties properties, Class clazz) {
        return buildInstance(properties, clazz, false, clazz.getClassLoader());
    }

    private SpecificationMap buildInstance(Properties properties,
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

    public SpecificationMap getInstance(Map<String, String> map, Class clazz) {
        return buildInstance(mapUtils.toProperties(map), clazz, false, clazz.getClassLoader());
    }

    public SpecificationMap getInstance(Map<String, String> map, Class clazz, boolean replace) {
        return buildInstance(mapUtils.toProperties(map), clazz, replace, clazz.getClassLoader());
    }

    public Map<String, SpecificationMap> getInstancesMap() {
        return instancesMap;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }
}
