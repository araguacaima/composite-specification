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

package org.araguacaima.specification.propertiesstrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesHandlerStrategyDefault extends PropertiesHandlerStrategyWrapper {

    private static final String PROPERTIES_HANDLER_STRATEGY_NAME
            = PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_DEFAULT;
    private final PropertiesHandlerStrategyWrapper propertiesHandlerStrategyDefault;
    protected PropertiesHandlerStrategyWrapperInterface next;

    public PropertiesHandlerStrategyDefault(String applicationName, String defaultPath) {
        super(applicationName);
        propertiesHandlerStrategyDefault = new PropertiesHandlerStrategyPath(applicationName, defaultPath);
    }


    public PropertiesHandlerStrategyDefault(String applicationName) {
        super(applicationName);
        propertiesHandlerStrategyDefault = new PropertiesHandlerStrategyPath(applicationName);
    }

    public String getPropertiesHandlerStrategyName() {
        return PROPERTIES_HANDLER_STRATEGY_NAME;
    }

    public void setNext(PropertiesHandlerStrategyWrapperInterface next) {
        propertiesHandlerStrategyDefault.setNext(next);
    }

    public PropertiesHandlerStrategyWrapperInterface getNext() {
        return propertiesHandlerStrategyDefault.getNext();
    }

    public Properties getProperties() {
        return propertiesHandlerStrategyDefault.getProperties();
    }

    public Map getOriginProperties() {
        Map<String, String> originProperties = new HashMap<>();
        originProperties.put(PROPERTIES_HANDLER_STRATEGY_NAME, propertiesHandlerStrategyDefault.getClass().getName());
        return originProperties;
    }
}
