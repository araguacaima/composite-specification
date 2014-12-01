package org.commons.wrapper.propertiesstrategy;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Properties;


public abstract class PropertiesHandlerStrategyWrapper implements PropertiesHandlerStrategyWrapperInterface {

    protected static final Logger log = Logger.getLogger(PropertiesHandlerStrategyWrapper.class);
    public static final String PROPERTIES_HANDLER_STRATEGY_DB = "DB";
    public static final String PROPERTIES_HANDLER_STRATEGY_PATH = "PATH";
    public static final String PROPERTIES_HANDLER_STRATEGY_CLASSPATH = "CLASSPATH";
    public static final String PROPERTIES_HANDLER_STRATEGY_FTP = "FTP";
    public static final String PROPERTIES_HANDLER_STRATEGY_URL = "URL";
    public static final String PROPERTIES_HANDLER_STRATEGY_DEFAULT = "DEFAULT";
    public static boolean isInitialized = false;
    protected Properties properties;

    protected long applicationId;
    protected String PROPERTY_KEY_PREFIX;


    public PropertiesHandlerStrategyWrapper(String applicationName) {
        applicationId = Long.parseLong(applicationName);
    }

    public abstract String getPropertiesHandlerStrategyName();

    public abstract void setNext(PropertiesHandlerStrategyWrapperInterface next);

    public abstract PropertiesHandlerStrategyWrapperInterface getNext();

    public String getProperty(String key) {
        return String.valueOf(properties.get(key));
    }

    public abstract Properties /*<String, String>*/getProperties();

    public abstract Map /*<String, String>*/getOriginProperties();

}