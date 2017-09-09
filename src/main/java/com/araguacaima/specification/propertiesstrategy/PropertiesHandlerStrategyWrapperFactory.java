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

import com.araguacaima.commons.utils.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;


class PropertiesHandlerStrategyWrapperFactory {

    private static final Logger log = Logger.getLogger(PropertiesHandlerStrategyWrapperFactory.class);
    private String propertiesHandlerStrategyPolicy = StringUtils.EMPTY;
    public static final String PROPERTIES_HANDLER_STRATEGY_POLICY = "PROPERTIES_HANDLER_STRATEGY_POLICY";
    private PropertiesHandlerStrategyWrapperInterface propertiesHandlerStrategy;
    private String applicationName;
    private String defaultFileName;

    private PropertiesHandlerStrategyWrapperFactory(String applicationName, String defaultFileName) {
        this.applicationName = applicationName;
        this.defaultFileName = defaultFileName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getDefaultFileName() {
        return defaultFileName;
    }

    public void setDefaultFileName(String defaultFileName) {
        this.defaultFileName = defaultFileName;
    }

    public String getPropertesHandlerStrategyPolicy() {
        return propertiesHandlerStrategyPolicy;
    }

    public void setPropertesHandlerStrategyPolicy(String propertiesHandlerStrategyPolicy) {
        log.warn("Current properties handler strategy policy was change from: "
                + this.propertiesHandlerStrategyPolicy
                + " to "
                + propertiesHandlerStrategyPolicy);
        this.propertiesHandlerStrategyPolicy = propertiesHandlerStrategyPolicy;
    }

    public static PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyWithoutPoliciesNorFiles(String applicationName) {
        log.warn("The default PropertiesHandlerStrategy will be created with no policies nor properties files");
        PropertiesHandlerStrategyWrapperInterface propertiesHandlerStrategy;
        propertiesHandlerStrategy = new PropertiesHandlerStrategyDefault(applicationName);
        return propertiesHandlerStrategy;
    }

    private static PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyWithoutPolicies(String applicationName,
                                                                                                            String label,
                                                                                                            String defaultFileName) {
        log.info("Creating a PropertiesHandlerStrategy without Policies based on label '"
                + label
                + "', application name '"
                + applicationName
                + "' and default file name '"
                + defaultFileName
                + "'");
        PropertiesHandlerStrategyWrapperInterface propertiesHandlerStrategy;
        if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_DB.equals(label)) {
            propertiesHandlerStrategy = new PropertiesHandlerStrategyDB(applicationName);
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_CLASSPATH.equals(label)) {
            propertiesHandlerStrategy = new PropertiesHandlerStrategyClassPath(applicationName, defaultFileName);
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_PATH.equals(label)) {
            propertiesHandlerStrategy = new PropertiesHandlerStrategyPath(applicationName, defaultFileName);
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_FTP.equals(label)) {
            propertiesHandlerStrategy = new PropertiesHandlerStrategyFTP(applicationName);
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_URL.equals(label)) {
            propertiesHandlerStrategy = new PropertiesHandlerStrategyURL(applicationName);
        } else {
            log.warn("Is not possible to create a PropertiesHandlerStrategy based on label '"
                    + label
                    + "'. The default one will be used");
            propertiesHandlerStrategy = new PropertiesHandlerStrategyDefault(applicationName, defaultFileName);
        }
        log.info("A PropertiesHandlerStrategy of type '"
                + label
                + "' ("
                + propertiesHandlerStrategy.getClass().getName()
                + ") has been created!");

        return propertiesHandlerStrategy;
    }

    public PropertiesHandlerStrategyWrapperInterface buildPropertiesHandlerStrategyPolicies(String label) {
        log.info("Building a PropertiesHandlerStrategy Policy based on label '" + label + "'");
        PropertiesHandlerStrategyWrapperInterface propertiesHandlerStrategy;
        if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_DB.equals(label)) {
            propertiesHandlerStrategy = createPropertiesHandlerStrategyDB();
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_CLASSPATH.equals(label)) {
            propertiesHandlerStrategy = createPropertiesHandlerStrategyClassPath();
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_PATH.equals(label)) {
            propertiesHandlerStrategy = createPropertiesHandlerStrategyPath();
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_FTP.equals(label)) {
            propertiesHandlerStrategy = createPropertiesHandlerStrategyFTP();
        } else if (PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_URL.equals(label)) {
            propertiesHandlerStrategy = createPropertiesHandlerStrategyURL();
        } else {
            log.warn("Is not possible to create a PropertiesHandlerStrategy based on label '"
                    + label
                    + "'. The default one will be used");
            propertiesHandlerStrategy = createPropertiesHandlerStrategyDefault();
        }
        log.info("A PropertiesHandlerStrategy of type '"
                + label
                + "' ("
                + propertiesHandlerStrategy.getClass().getName()
                + ") has been created!");
        return propertiesHandlerStrategy;
    }

    private PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyDefault() {
        log.info("Creating a DEFAULT PropertiesHandlerStrategy");
        propertiesHandlerStrategy = new PropertiesHandlerStrategyDefault(applicationName, defaultFileName);
        if (StringUtils.isNullOrEmpty(propertiesHandlerStrategyPolicy)) {
            PropertiesHandlerStrategyWrapperInterface nextDB = new PropertiesHandlerStrategyDB(applicationName);
            PropertiesHandlerStrategyWrapperInterface nextClassPath = new PropertiesHandlerStrategyClassPath(
                    applicationName,
                    defaultFileName);
            PropertiesHandlerStrategyWrapperInterface nextPath = new PropertiesHandlerStrategyPath(applicationName,
                    defaultFileName);
            PropertiesHandlerStrategyWrapperInterface nextURL = new PropertiesHandlerStrategyURL(applicationName);
            PropertiesHandlerStrategyWrapperInterface nextFTP = new PropertiesHandlerStrategyFTP(applicationName);
            nextDB.setNext(nextClassPath);
            nextClassPath.setNext(nextPath);
            nextPath.setNext(nextURL);
            nextURL.setNext(nextFTP);
            propertiesHandlerStrategy.setNext(nextDB);
            log.info(
                    "The default chain of responsibilities has loaded because of no strategy policy was found. The current chain of properties' handlers for DEFAULT strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        } else {
            propertiesHandlerStrategy = buildChainOfResponsibility();
            log.info(
                    "A configured chain of responsibilities has been found. The current chain of properties' handlers for DEFAULT strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        }
        return propertiesHandlerStrategy;
    }

    private PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyFTP() {
        log.info("Creating a FTP PropertiesHandlerStrategy");
        propertiesHandlerStrategy = new PropertiesHandlerStrategyFTP(applicationName);
        if (!StringUtils.isNullOrEmpty(propertiesHandlerStrategyPolicy)) {
            propertiesHandlerStrategy = buildChainOfResponsibility();
            log.info(
                    "A configured chain of responsibilities has been found. The current chain of properties' handlers for FTP strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        } else {
            log.info(
                    "The default chain of responsibilities has loaded because of no strategy policy was found. The current chain of properties' handlers for FTP strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        }
        return propertiesHandlerStrategy;
    }

    private PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyPath() {
        log.info("Creating a PATH PropertiesHandlerStrategy");
        propertiesHandlerStrategy = new PropertiesHandlerStrategyPath(applicationName);
        if (StringUtils.isNullOrEmpty(propertiesHandlerStrategyPolicy)) {
            PropertiesHandlerStrategyWrapperInterface nextURL = new PropertiesHandlerStrategyURL(applicationName);
            PropertiesHandlerStrategyWrapperInterface nextFTP = new PropertiesHandlerStrategyFTP(applicationName);
            nextURL.setNext(nextFTP);
            propertiesHandlerStrategy.setNext(nextURL);
            log.info(
                    "The default chain of responsibilities has loaded because of no strategy policy was found. The current chain of properties' handlers for PATH strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        } else {
            propertiesHandlerStrategy = buildChainOfResponsibility();
            log.info(
                    "A configured chain of responsibilities has been found. The current chain of properties' handlers for PATH strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        }
        return propertiesHandlerStrategy;
    }

    private PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyClassPath() {
        log.info("Creating a CLASSPATH PropertiesHandlerStrategy");
        propertiesHandlerStrategy = new PropertiesHandlerStrategyClassPath(applicationName);
        if (StringUtils.isNullOrEmpty(propertiesHandlerStrategyPolicy)) {
            PropertiesHandlerStrategyWrapperInterface nextPath = new PropertiesHandlerStrategyPath(applicationName);
            PropertiesHandlerStrategyWrapperInterface nextURL = new PropertiesHandlerStrategyURL(applicationName);
            PropertiesHandlerStrategyWrapperInterface nextFTP = new PropertiesHandlerStrategyFTP(applicationName);
            nextPath.setNext(nextURL);
            nextURL.setNext(nextFTP);
            propertiesHandlerStrategy.setNext(nextPath);
            log.info(
                    "The default chain of responsibilities has loaded because of no strategy policy was found. The current chain of properties' handlers for CLASSPATH strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        } else {
            propertiesHandlerStrategy = buildChainOfResponsibility();
            log.info(
                    "A configured chain of responsibilities has been found. The current chain of properties' handlers for CLASSPATH strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        }
        return propertiesHandlerStrategy;
    }

    private PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyURL() {
        log.info("Creating an URL PropertiesHandlerStrategy");
        propertiesHandlerStrategy = new PropertiesHandlerStrategyURL(applicationName);
        if (StringUtils.isNullOrEmpty(propertiesHandlerStrategyPolicy)) {
            PropertiesHandlerStrategyWrapperInterface nextFTP = new PropertiesHandlerStrategyFTP(applicationName);
            propertiesHandlerStrategy.setNext(nextFTP);
            log.info(
                    "The default chain of responsibilities has loaded because of no strategy policy was found. The current chain of properties' handlers for URL strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        } else {
            propertiesHandlerStrategy = buildChainOfResponsibility();
            log.info(
                    "A configured chain of responsibilities has been found. The current chain of properties' handlers for URL strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        }
        return propertiesHandlerStrategy;

    }

    private PropertiesHandlerStrategyWrapperInterface createPropertiesHandlerStrategyDB() {
        log.info("Creating a DB PropertiesHandlerStrategy");
        propertiesHandlerStrategy = new PropertiesHandlerStrategyDB(applicationName);
        if (StringUtils.isNullOrEmpty(propertiesHandlerStrategyPolicy)) {

            PropertiesHandlerStrategyWrapperInterface nextClassPath = new PropertiesHandlerStrategyClassPath(
                    applicationName);
            PropertiesHandlerStrategyWrapperInterface nextPath = new PropertiesHandlerStrategyPath(applicationName);
            PropertiesHandlerStrategyWrapperInterface nextURL = new PropertiesHandlerStrategyURL(applicationName);
            PropertiesHandlerStrategyWrapperInterface nextFTP = new PropertiesHandlerStrategyFTP(applicationName);
            nextClassPath.setNext(nextPath);
            nextPath.setNext(nextURL);
            nextURL.setNext(nextFTP);
            propertiesHandlerStrategy.setNext(nextClassPath);
            log.info(
                    "The default chain of responsibilities has loaded because of no strategy policy was found. The current chain of properties' handlers for DB strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        } else {
            propertiesHandlerStrategy = buildChainOfResponsibility();
            log.info(
                    "A configured chain of responsibilities has been found. The current chain of properties' handlers for DB strategy is: "
                            + getStrategyPolicyChain(propertiesHandlerStrategy));
        }
        return propertiesHandlerStrategy;
    }

    private PropertiesHandlerStrategyWrapperInterface buildChainOfResponsibility() {
        if (!StringUtils.isNullOrEmpty(propertiesHandlerStrategyPolicy)) {
            String[] policy = propertiesHandlerStrategyPolicy.split(";");
            if (policy.length == 1) {
                policy = propertiesHandlerStrategyPolicy.split(",");
            }
            setNext(applicationName, propertiesHandlerStrategy, new ArrayList<>(Arrays.asList(policy)), defaultFileName);
            return propertiesHandlerStrategy.getNext();
        } else {
            return new PropertiesHandlerStrategyWrapperFactory(applicationName,
                    defaultFileName).createPropertiesHandlerStrategyDefault();
        }
    }

    public static PropertiesHandlerStrategyWrapperInterface buildChainOfResponsibility(String applicationName,
                                                                                       String policyString,
                                                                                       String defaultFileName) {
        PropertiesHandlerStrategyWrapperInterface propertiesHandlerStrategy
                = new PropertiesHandlerStrategyWrapperFactory(applicationName,
                defaultFileName).createPropertiesHandlerStrategyDefault();
        if (!StringUtils.isNullOrEmpty(policyString)) {
            String[] policy = policyString.split(";");
            if (policy.length == 1) {
                policy = policyString.split(",");
            }
            setNext(applicationName, propertiesHandlerStrategy, new ArrayList<>(Arrays.asList(policy)), defaultFileName);
            return propertiesHandlerStrategy.getNext();
        } else {
            return new PropertiesHandlerStrategyWrapperFactory(applicationName,
                    defaultFileName).createPropertiesHandlerStrategyDefault();
        }
    }

    private static PropertiesHandlerStrategyWrapperInterface setNext(String applicationName,
                                                                     PropertiesHandlerStrategyWrapperInterface propertiesHandlerStrategy,
                                                                     ArrayList<String> <String> propertiesHandlerStrategyPolicies,
                                                                     String defaultFileName) {
        if (propertiesHandlerStrategy != null) {
            if (propertiesHandlerStrategyPolicies != null && propertiesHandlerStrategyPolicies.size() > 0) {
                PropertiesHandlerStrategyWrapperInterface next
                        = PropertiesHandlerStrategyWrapperFactory.createPropertiesHandlerStrategyWithoutPolicies(
                        applicationName,
                        (propertiesHandlerStrategyPolicies.get(0)).trim(),
                        defaultFileName);
                propertiesHandlerStrategy.setNext(next);
                propertiesHandlerStrategyPolicies.remove(0);
                setNext(applicationName, next, propertiesHandlerStrategyPolicies, defaultFileName);
            }
        }
        return propertiesHandlerStrategy;
    }

    private String getStrategyPolicyChain(PropertiesHandlerStrategyWrapperInterface nextSegment) {
        if (nextSegment != null) {
            return nextSegment.getPropertiesHandlerStrategyName()
                    .concat(getStrategyPolicyChain(nextSegment.getNext()).concat(";"));
        } else {
            return StringUtils.EMPTY;
        }
    }

}