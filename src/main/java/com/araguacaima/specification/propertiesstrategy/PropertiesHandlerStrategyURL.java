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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class PropertiesHandlerStrategyURL extends PropertiesHandlerStrategyWrapper {

    private static final String PROPERTIES_URL_REMOTE_FILE_PATH = "PROPERTIES_URL_REMOTE_FILE_PATH";
    private static final String PROPERTIES_URL_LOCAL_FILE_PATH = "PROPERTIES_URL_LOCAL_FILE_PATH";
    private static final String PROPERTIES_URL_SERVER_DOMAIN_AND_PORT = "PROPERTIES_URL_SERVER_DOMAIN_AND_PORT";
    private static final String PROPERTIES_URL_SERVER_DOMAIN_LOGIN = "PROPERTIES_URL_SERVER_DOMAIN_LOGIN";
    private static final String PROPERTIES_URL_SERVER_DOMAIN_PASSWORD = "PROPERTIES_URL_SERVER_DOMAIN_PASSWORD";

    private String urlRemoteFilePath = StringUtils.EMPTY;
    private String urlLocalFilePath = StringUtils.EMPTY;
    private String urlServerDomainAndPort = StringUtils.EMPTY;
    private String urlServerDomainLogin = StringUtils.EMPTY;
    private String urlServerDomainPassword = StringUtils.EMPTY;

    private static final String PROPERTIES_HANDLER_STRATEGY_NAME
            = PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_URL;
    private PropertiesHandlerStrategyWrapperInterface next;

    public PropertiesHandlerStrategyURL(String applicationName) {
        super(applicationName);
    }

    public PropertiesHandlerStrategyURL(String applicationName,
                                        String urlRemoteFilePath,
                                        String urlLocalFilePath,
                                        String urlServerDomainAndPort,
                                        String urlServerDomainLogin,
                                        String urlServerDomainPassword) {
        super(applicationName);
        this.urlRemoteFilePath = urlRemoteFilePath;
        this.urlLocalFilePath = urlLocalFilePath;
        this.urlServerDomainAndPort = urlServerDomainAndPort;
        this.urlServerDomainLogin = urlServerDomainLogin;
        this.urlServerDomainPassword = urlServerDomainPassword;
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
        originProperties.put(PROPERTIES_URL_REMOTE_FILE_PATH, PROPERTIES_URL_REMOTE_FILE_PATH);
        originProperties.put(PROPERTIES_URL_LOCAL_FILE_PATH, PROPERTIES_URL_LOCAL_FILE_PATH);
        originProperties.put(PROPERTIES_URL_SERVER_DOMAIN_AND_PORT, PROPERTIES_URL_SERVER_DOMAIN_AND_PORT);
        originProperties.put(PROPERTIES_URL_SERVER_DOMAIN_LOGIN, PROPERTIES_URL_SERVER_DOMAIN_LOGIN);
        originProperties.put(PROPERTIES_URL_SERVER_DOMAIN_PASSWORD, PROPERTIES_URL_SERVER_DOMAIN_PASSWORD);
        return originProperties;
    }

    public Properties <String, String> getProperties() {

        File file = getFileFromURL();
        if (file != null) {
            properties = PropertiesHandlerUtil.getInstance(file.getPath(), true).getProperties();
        }

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

    /**
     * @return the file obtained from URL
     * @noinspection ResultOfMethodCallIgnored
     */
    private File getFileFromURL() {

        URL u;
        InputStream is = null;
        BufferedReader dis;
        String s;
        File file = new File(urlLocalFilePath);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            log.error("Exception [" + e.getClass() + "] - " + e.getMessage());
        }
        file.deleteOnExit();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("http://");
            if (!StringUtils.isNullOrEmpty(urlServerDomainLogin)) {
                sb.append(urlServerDomainLogin);
            }
            if (!StringUtils.isNullOrEmpty(urlServerDomainPassword)) {
                sb.append("/").append(urlServerDomainPassword);
            }
            if (!StringUtils.isNullOrEmpty(urlServerDomainLogin)) {
                sb.append("@");
            }
            sb.append(urlServerDomainAndPort).append("/").append(urlRemoteFilePath);
            u = new URL(sb.toString());
            is = u.openStream();
            dis = new BufferedReader(new InputStreamReader(is));
            FileOutputStream fos = new FileOutputStream(file);
            while ((s = dis.readLine()) != null) {
                s = s.concat("\n");
                fos.write(s.getBytes());
            }

        } catch (IOException ioe) {
            log.error("Exception [" + ioe.getClass() + "] - " + ioe.getMessage());

        } finally {

            try {
                is.close();
            } catch (Exception e) {
                log.error("Exception [" + e.getClass() + "] - " + e.getMessage());
            }

        }
        return file;
    }

}