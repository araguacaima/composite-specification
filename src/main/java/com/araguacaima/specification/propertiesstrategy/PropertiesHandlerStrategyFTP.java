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
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class PropertiesHandlerStrategyFTP extends PropertiesHandlerStrategyWrapper {

    private static final String PROPERTIES_FTP_REMOTE_FILE_PATH = "PROPERTIES_FTP_REMOTE_FILE_PATH";
    private static final String PROPERTIES_FTP_LOCAL_FILE_PATH = "PROPERTIES_FTP_LOCAL_FILE_PATH";
    private static final String PROPERTIES_FTP_SERVER_DOMAIN = "PROPERTIES_FTP_SERVER_DOMAIN";
    private static final String PROPERTIES_FTP_SERVER_DOMAIN_LOGIN = "PROPERTIES_FTP_SERVER_DOMAIN_LOGIN";
    private static final String PROPERTIES_FTP_SERVER_DOMAIN_PASSWORD = "PROPERTIES_FTP_SERVER_DOMAIN_PASSWORD";

    private String ftpRemoteFilePath = StringUtils.EMPTY;
    private String ftpLocalFilePath = StringUtils.EMPTY;
    private String ftpServerDomain = StringUtils.EMPTY;
    private String ftpServerDomainLogin = StringUtils.EMPTY;
    private String ftpServerDomainPassword = StringUtils.EMPTY;

    private static final String PROPERTIES_HANDLER_STRATEGY_NAME
            = PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_FTP;
    private PropertiesHandlerStrategyWrapperInterface next;

    public PropertiesHandlerStrategyFTP(String applicationName) {
        super(applicationName);
    }

    public PropertiesHandlerStrategyFTP(String applicationName,
                                        String ftpRemoteFilePath,
                                        String ftpLocalFilePath,
                                        String ftpServerDomain,
                                        String ftpServerDomainLogin,
                                        String ftpServerDomainPassword) {
        super(applicationName);
        this.ftpRemoteFilePath = ftpRemoteFilePath;
        this.ftpLocalFilePath = ftpLocalFilePath;
        this.ftpServerDomain = ftpServerDomain;
        this.ftpServerDomainLogin = ftpServerDomainLogin;
        this.ftpServerDomainPassword = ftpServerDomainPassword;
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
        Map<String, String> originProperties = new HashMap<>();
        originProperties.put(PROPERTIES_FTP_REMOTE_FILE_PATH, ftpRemoteFilePath);
        originProperties.put(PROPERTIES_FTP_LOCAL_FILE_PATH, ftpLocalFilePath);
        originProperties.put(PROPERTIES_FTP_SERVER_DOMAIN, ftpServerDomain);
        originProperties.put(PROPERTIES_FTP_SERVER_DOMAIN_LOGIN, ftpServerDomainLogin);
        originProperties.put(PROPERTIES_FTP_SERVER_DOMAIN_PASSWORD, ftpServerDomainPassword);
        return originProperties;
    }

    public Properties <String, String> getProperties() {
        File file = getFileFromFTP();
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

    private File getFileFromFTP() {

        FTPClient client = new FTPClient();
        FileOutputStream fos = null;

        File file = new File(ftpLocalFilePath);
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
            client.connect(ftpServerDomain);
            client.login(ftpServerDomainLogin, ftpServerDomainPassword);
            fos = new FileOutputStream(file);
            client.retrieveFile(ftpRemoteFilePath, fos);
        } catch (IOException e) {
            log.error("Exception [" + e.getClass() + "] - " + e.getMessage());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                client.disconnect();
            } catch (Exception e) {
                log.error("Exception [" + e.getClass() + "] - " + e.getMessage());
            }
        }
        return file;
    }

}