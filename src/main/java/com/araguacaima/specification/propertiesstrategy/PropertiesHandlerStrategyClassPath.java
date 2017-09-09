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

package com.araguacaima.specification.propertiesstrategy;

import com.araguacaima.specification.util.PropertiesHandlerUtil;
import com.araguacaima.commons.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesHandlerStrategyClassPath extends PropertiesHandlerStrategyWrapper {

    private String fileWithinClasspath = StringUtils.EMPTY;
    private static final String PROPERTIES_FILE_NAME_WITHIN_CLASSPATH = "PROPERTIES_FILE_NAME_WITHIN_CLASSPATH";
    private static final String PROPERTIES_HANDLER_STRATEGY_NAME
            = PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_CLASSPATH;
    private PropertiesHandlerStrategyWrapperInterface next;

    public PropertiesHandlerStrategyClassPath(String applicationName) {
        super(applicationName);
    }

    public PropertiesHandlerStrategyClassPath(String applicationName, String fileName) {
        super(applicationName);
        this.fileWithinClasspath = fileName;
    }

    public void setNext(PropertiesHandlerStrategyWrapperInterface next) {
        this.next = next;
    }

    public PropertiesHandlerStrategyWrapperInterface getNext() {
        return this.next;
    }

    public String getPropertiesHandlerStrategyName() {
        return PROPERTIES_HANDLER_STRATEGY_NAME;
    }

    public Map <String, String> getOriginProperties() {
        Map<String, String> originProperties = new HashMap<>();
        originProperties.put(PROPERTIES_FILE_NAME_WITHIN_CLASSPATH, fileWithinClasspath);
        return originProperties;
    }

    public Properties <String, String> getProperties() {

        properties = PropertiesHandlerUtil.getInstance(this.fileWithinClasspath,
                PropertiesHandlerStrategyClassPath.class.getClassLoader(),
                true).getProperties();

        if (properties == null || properties.size() == 0) {
            if (next != null) {
                return next.getProperties();
            } else {
                return new Properties();
            }
        } else {
            return properties;
        }
    }

}