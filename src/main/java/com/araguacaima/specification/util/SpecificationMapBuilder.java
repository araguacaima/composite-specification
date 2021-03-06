/*
 * Copyright 2020 araguacaima
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

import com.araguacaima.specification.common.ReflectionUtils;
import com.araguacaima.specification.common.StringUtils;
import com.araguacaima.specification.interpreter.logical.LogicalEvaluator;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.Predicate;

import java.io.IOException;
import java.util.*;

@SuppressWarnings("WeakerAccess")
public class SpecificationMapBuilder {

    private final Map<String, SpecificationMap> instancesMap = new HashMap<>();
    private final String propertiesFile = "specification.properties";

    public SpecificationMapBuilder() {

    }

    public SpecificationMap getInstance(Class<?> clazz)
            throws IOException {
        return getInstance(clazz, false);
    }

    public SpecificationMap getInstance(Class<?> clazz, boolean forceReplace)
            throws IOException {
        ClassLoader classLoader = clazz.getClassLoader();
        Properties prop = new Properties();
        prop.load(classLoader.getResourceAsStream(propertiesFile));
        return buildInstance(prop, clazz, forceReplace, clazz.getClassLoader());
    }

    private SpecificationMap getInstance(Properties properties, Class<?> clazz) {
        return buildInstance(properties, clazz, false, clazz.getClassLoader());
    }

    public SpecificationMap getInstance(Map<String, String> map, Class<?> clazz) {
        return buildInstance(MapUtils.toProperties(map), clazz, false, clazz.getClassLoader());
    }

    public SpecificationMap getInstance(Map<String, String> map, Class<?> clazz, ClassLoader classLoader) {
        return buildInstance(MapUtils.toProperties(map), clazz, false, classLoader);
    }


    public SpecificationMap getInstance(Map<String, String> map, Class<?> clazz, boolean replace) {
        return buildInstance(MapUtils.toProperties(map), clazz, replace, clazz.getClassLoader());
    }

    private SpecificationMap buildInstance(Properties properties,
                                           Class<?> clazz,
                                           boolean replace,
                                           ClassLoader classLoader) {
        SpecificationMap instance;

        Predicate<Object> notNullOrEmptyStringObjectPredicate = object -> StringUtils.isNotBlank(object.toString());
        Predicate<String> notNullOrEmptyStringPredicate = StringUtils::isNotBlank;
        instance = new SpecificationMap(new LogicalEvaluator());

        if (instancesMap.get(clazz.getName()) == null) {
            instance.setClassName(clazz.getName());
            instance.setProperties(properties);
            instance.buildSpecificationMap(classLoader);
            instancesMap.put(clazz.getName(), instance);
        } else {
            if (replace) {
                instance.setClassName(clazz.getName());
                instance.setProperties(properties);
                instance.buildSpecificationMap(classLoader);
                SpecificationMap oldInstance = instancesMap.get(clazz.getName());
                instance.getProperties().putAll(oldInstance.getProperties());
                instancesMap.remove(clazz.getName());
                instancesMap.put(clazz.getName(), instance);
            }
        }

        return instancesMap.get(clazz.getName());
    }

    public List<SpecificationMap> getInstances(Class<?> clazz)
            throws IOException {
        return getInstances(clazz, false);
    }

    public List<SpecificationMap> getInstances(Class<?> clazz, boolean forceReplace)
            throws IOException {
        ClassLoader classLoader = clazz.getClassLoader();
        Properties prop = new Properties();
        prop.load(classLoader.getResourceAsStream(propertiesFile));
        return buildInstances(prop, clazz, forceReplace, clazz.getClassLoader());
    }

    private List<SpecificationMap> getInstances(Properties properties, Class<?> clazz) {
        return buildInstances(properties, clazz, false, clazz.getClassLoader());
    }

    public List<SpecificationMap> getInstances(Map<String, String> map, Class<?> clazz) {
        return buildInstances(MapUtils.toProperties(map), clazz, false, clazz.getClassLoader());
    }

    public List<SpecificationMap> getInstances(Map<String, String> map, Class<?> clazz, boolean replace) {
        return buildInstances(MapUtils.toProperties(map), clazz, replace, clazz.getClassLoader());
    }

    private List<SpecificationMap> buildInstances(Properties properties,
                                                  Class<?> clazz,
                                                  boolean replace,
                                                  ClassLoader classLoader) {
        List<SpecificationMap> instances = new ArrayList<>(buildInstances(properties, clazz, replace, classLoader, true));
        Collections.sort(instances);
        return instances;
    }

    private List<SpecificationMap> buildInstances(Properties properties,
                                                  Class<?> clazz,
                                                  boolean replace,
                                                  ClassLoader classLoader,
                                                  boolean checkInheritance) {
        List<SpecificationMap> instances = new ArrayList<>();
        if (checkInheritance) {
            instances.add(buildInstance(properties, clazz, replace, classLoader));
            List<Class<?>> superClasses = ReflectionUtils.recursivelyGetAllSuperClasses(clazz);
            for (Class<?> superClazz : superClasses) {
                instances.addAll(buildInstances(properties, superClazz, replace, classLoader, false));
            }
        } else {
            instances.add(buildInstance(properties, clazz, replace, classLoader));
        }
        return instances;
    }

    public Map<String, SpecificationMap> getInstancesMap() {
        return instancesMap;
    }

}
