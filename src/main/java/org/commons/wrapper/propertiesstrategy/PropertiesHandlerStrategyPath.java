package org.commons.wrapper.propertiesstrategy;

import org.commons.util.PropertiesHandlerUtil;
import org.commons.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class PropertiesHandlerStrategyPath extends PropertiesHandlerStrategyWrapper {

    public String fileInPath = StringUtil.EMPTY_STRING;
    public static final String PROPERTIES_FILE_PATH = "PROPERTIES_FILE_PATH";
    public static final String PROPERTIES_HANDLER_STRATEGY_NAME
            = PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_PATH;
    protected PropertiesHandlerStrategyWrapperInterface next;

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

    public Map /*<String, String>*/ getOriginProperties() {
        Map originProperties = new HashMap();
        originProperties.put(PROPERTIES_FILE_PATH, fileInPath);
        return originProperties;
    }

    public Properties /*<String, String>*/ getProperties() {

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