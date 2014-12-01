package org.commons.wrapper.propertiesstrategy;

import org.commons.util.PropertiesHandlerUtil;
import org.commons.util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class PropertiesHandlerStrategyURL extends PropertiesHandlerStrategyWrapper {

    public static final String PROPERTIES_URL_REMOTE_FILE_PATH = "PROPERTIES_URL_REMOTE_FILE_PATH";
    public static final String PROPERTIES_URL_LOCAL_FILE_PATH = "PROPERTIES_URL_LOCAL_FILE_PATH";
    public static final String PROPERTIES_URL_SERVER_DOMAIN_AND_PORT = "PROPERTIES_URL_SERVER_DOMAIN_AND_PORT";
    public static final String PROPERTIES_URL_SERVER_DOMAIN_LOGIN = "PROPERTIES_URL_SERVER_DOMAIN_LOGIN";
    public static final String PROPERTIES_URL_SERVER_DOMAIN_PASSWORD = "PROPERTIES_URL_SERVER_DOMAIN_PASSWORD";

    public String urlRemoteFilePath = StringUtil.EMPTY_STRING;
    public String urlLocalFilePath = StringUtil.EMPTY_STRING;
    public String urlServerDomainAndPort = StringUtil.EMPTY_STRING;
    public String urlServerDomainLogin = StringUtil.EMPTY_STRING;
    public String urlServerDomainPassword = StringUtil.EMPTY_STRING;

    public static final String PROPERTIES_HANDLER_STRATEGY_NAME
            = PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_URL;
    protected PropertiesHandlerStrategyWrapperInterface next;

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

    public Map /*<String, String>*/ getOriginProperties() {
        Map originProperties = new HashMap();
        originProperties.put(PROPERTIES_URL_REMOTE_FILE_PATH, PROPERTIES_URL_REMOTE_FILE_PATH);
        originProperties.put(PROPERTIES_URL_LOCAL_FILE_PATH, PROPERTIES_URL_LOCAL_FILE_PATH);
        originProperties.put(PROPERTIES_URL_SERVER_DOMAIN_AND_PORT, PROPERTIES_URL_SERVER_DOMAIN_AND_PORT);
        originProperties.put(PROPERTIES_URL_SERVER_DOMAIN_LOGIN, PROPERTIES_URL_SERVER_DOMAIN_LOGIN);
        originProperties.put(PROPERTIES_URL_SERVER_DOMAIN_PASSWORD, PROPERTIES_URL_SERVER_DOMAIN_PASSWORD);
        return originProperties;
    }

    public Properties /*<String, String>*/ getProperties() {

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
    public File getFileFromURL() {

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
            StringBuffer sb = new StringBuffer();
            sb.append("http://");
            if (!StringUtil.isNullOrEmpty(urlServerDomainLogin)) {
                sb.append(urlServerDomainLogin);
            }
            if (!StringUtil.isNullOrEmpty(urlServerDomainPassword)) {
                sb.append("/").append(urlServerDomainPassword);
            }
            if (!StringUtil.isNullOrEmpty(urlServerDomainLogin)) {
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

        } catch (MalformedURLException mue) {
            log.error("Exception [" + mue.getClass() + "] - " + mue.getMessage());

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