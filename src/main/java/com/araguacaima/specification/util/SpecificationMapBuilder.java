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

import com.araguacaima.commons.utils.*;
import com.araguacaima.specification.interpreter.logical.LogicalEvaluator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("WeakerAccess")
@Service
public class SpecificationMapBuilder implements ApplicationContextAware {

    private final Map<String, SpecificationMap> instancesMap = new HashMap<>();
    private ApplicationContext applicationContext;
    private MapUtils mapUtils;
    private ReflectionUtils reflectionUtils = new ReflectionUtils(null);
    private String propertiesFile = "specification.properties";

    @Autowired
    public SpecificationMapBuilder(MapUtils mapUtils) {
        this.mapUtils = mapUtils;
    }

    public SpecificationMap getInstance(Class clazz)
            throws IOException {
        return getInstance(clazz, false);
    }

    public SpecificationMap getInstance(Class clazz, boolean forceReplace)
            throws IOException {
        return getInstance(clazz, forceReplace, false);
    }

    public SpecificationMap getInstance(Class clazz, boolean forceReplace, boolean checkInheritance)
            throws IOException {
        ClassLoader classLoader = clazz.getClassLoader();
        Properties prop = new Properties();
        prop.load(classLoader.getResourceAsStream(propertiesFile));
        return buildInstance(prop, clazz, forceReplace, clazz.getClassLoader(), checkInheritance);
    }

    private SpecificationMap getInstance(Properties properties, Class clazz) {
        return getInstance(properties, clazz, false);
    }

    private SpecificationMap getInstance(Properties properties, Class clazz, boolean checkInheritance) {
        return buildInstance(properties, clazz, false, clazz.getClassLoader(), checkInheritance);
    }

    private SpecificationMap buildInstance(Properties properties,
                                           Class clazz,
                                           boolean replace,
                                           ClassLoader classLoader,
                                           boolean checkInheritance) {
        SpecificationMap instance;
        if (applicationContext != null) {
            instance = applicationContext.getBean(SpecificationMap.class);
        } else {
            NotNullOrEmptyStringObjectPredicate notNullOrEmptyStringObjectPredicate = new NotNullOrEmptyStringObjectPredicate();
            NotNullOrEmptyStringPredicate notNullOrEmptyStringPredicate = new NotNullOrEmptyStringPredicate();
            instance = new SpecificationMap(notNullOrEmptyStringObjectPredicate,
                    MapUtils.getInstance(),
                    new LogicalEvaluator(
                            new StringUtils(
                                    notNullOrEmptyStringPredicate,
                                    new ExceptionUtils())));
        }
        if (checkInheritance) {
            SpecificationMap instance_ = buildInstance(properties, clazz, replace, classLoader, false);
            if (instance_ == null) {
                List<Class> superClasses = reflectionUtils.recursivelyGetAllSuperClasses(clazz);
                for (Class superClazz : superClasses) {
                    instance_ = buildInstance(properties, superClazz, replace, classLoader, false);
                    if (instance_ != null) {
                        break;
                    }
                }
                return instance_;
            }
        } else {
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
        }
        return instancesMap.get(clazz.getName());
    }

    public SpecificationMap getInstance(Map<String, String> map, Class clazz) {
        return getInstance(false, map, clazz);
    }

    public SpecificationMap getInstance(boolean checkInheritance, Map<String, String> map, Class clazz) {
        return buildInstance(mapUtils.toProperties(map), clazz, false, clazz.getClassLoader(), checkInheritance);
    }

    public SpecificationMap getInstance(Map<String, String> map, Class clazz, boolean replace) {
        return getInstance(false, map, clazz);
    }

    public SpecificationMap getInstance(boolean checkInheritance, Map<String, String> map, Class clazz, boolean replace) {
        return buildInstance(mapUtils.toProperties(map), clazz, replace, clazz.getClassLoader(), checkInheritance);
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
