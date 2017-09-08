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


public class PropertiesHandlerStrategyDB extends PropertiesHandlerStrategyWrapper {

    private static final String PROPERTIES_HANDLER_STRATEGY_NAME
            = PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_DB;
    private PropertiesHandlerStrategyWrapperInterface next;
    private static Properties properties;

    public PropertiesHandlerStrategyDB(String applicationName) {
        super(applicationName);
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
        return new HashMap();
    }

    public Properties <String, String> getProperties() {

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