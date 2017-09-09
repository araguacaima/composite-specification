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


public class PropertiesHandlerStrategyPath extends PropertiesHandlerStrategyWrapper {

    private String fileInPath = StringUtils.EMPTY;
    private static final String PROPERTIES_FILE_PATH = "PROPERTIES_FILE_PATH";
    private static final String PROPERTIES_HANDLER_STRATEGY_NAME
            = PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_PATH;
    private PropertiesHandlerStrategyWrapperInterface next;

    public PropertiesHandlerStrategyPath(String applicationName) {
        super(applicationName);
    }

    public PropertiesHandlerStrategyPath(String applicationName, String fileName) {
        super(applicationName);
        this.fileInPath = fileName;
    }

    public void setNext(PropertiesHandlerStrategyWrapperInterface next) {
        this.next = next;
    }

    public PropertiesHandlerStrategyWrapperInterface getNext() {
        return next;
    }

    public String getPropertiesHandlerStrategyName() {
        return PROPERTIES_HANDLER_STRATEGY_NAME;
    }

    public Map <String, String> getOriginProperties() {
        Map<String, String> originProperties = new HashMap<String, String>();
        originProperties.put(PROPERTIES_FILE_PATH, fileInPath);
        return originProperties;
    }

    public Properties <String, String> getProperties() {

        properties = PropertiesHandlerUtil.getInstance(fileInPath, true).getProperties();

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