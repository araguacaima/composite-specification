package org.commons.wrapper.propertiesstrategy;

import org.commons.util.PropertiesHandlerUtil;
import org.commons.util.StringUtil;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class PropertiesHandlerStrategyFTP extends PropertiesHandlerStrategyWrapper {

    public static final String PROPERTIES_FTP_REMOTE_FILE_PATH = "PROPERTIES_FTP_REMOTE_FILE_PATH";
    public static final String PROPERTIES_FTP_LOCAL_FILE_PATH = "PROPERTIES_FTP_LOCAL_FILE_PATH";
    public static final String PROPERTIES_FTP_SERVER_DOMAIN = "PROPERTIES_FTP_SERVER_DOMAIN";
    public static final String PROPERTIES_FTP_SERVER_DOMAIN_LOGIN = "PROPERTIES_FTP_SERVER_DOMAIN_LOGIN";
    public static final String PROPERTIES_FTP_SERVER_DOMAIN_PASSWORD = "PROPERTIES_FTP_SERVER_DOMAIN_PASSWORD";

    public String ftpRemoteFilePath = StringUtil.EMPTY_STRING;
    public String ftpLocalFilePath = StringUtil.EMPTY_STRING;
    public String ftpServerDomain = StringUtil.EMPTY_STRING;
    public String ftpServerDomainLogin = StringUtil.EMPTY_STRING;
    public String ftpServerDomainPassword = StringUtil.EMPTY_STRING;

    public static final String PROPERTIES_HANDLER_STRATEGY_NAME
            = PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_FTP;
    protected PropertiesHandlerStrategyWrapperInterface next;

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

    public Map /*<String, String>*/ getOriginProperties() {
        Map originProperties = new HashMap();
        originProperties.put(PROPERTIES_FTP_REMOTE_FILE_PATH, ftpRemoteFilePath);
        originProperties.put(PROPERTIES_FTP_LOCAL_FILE_PATH, ftpLocalFilePath);
        originProperties.put(PROPERTIES_FTP_SERVER_DOMAIN, ftpServerDomain);
        originProperties.put(PROPERTIES_FTP_SERVER_DOMAIN_LOGIN, ftpServerDomainLogin);
        originProperties.put(PROPERTIES_FTP_SERVER_DOMAIN_PASSWORD, ftpServerDomainPassword);
        return originProperties;
    }

    public Properties /*<String, String>*/ getProperties() {
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

    public File getFileFromFTP() {

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