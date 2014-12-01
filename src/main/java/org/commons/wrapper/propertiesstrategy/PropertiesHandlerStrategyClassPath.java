package org.commons.wrapper.propertiesstrategy;

import org.commons.util.PropertiesHandlerUtil;
import org.commons.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesHandlerStrategyClassPath extends PropertiesHandlerStrategyWrapper {

    public String fileWithinClasspath = StringUtil.EMPTY_STRING;
    public static final String PROPERTIES_FILE_NAME_WITHIN_CLASSPATH = "PROPERTIES_FILE_NAME_WITHIN_CLASSPATH";
    public static final String PROPERTIES_HANDLER_STRATEGY_NAME
            = PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_CLASSPATH;
    protected PropertiesHandlerStrategyWrapperInterface next;

    public PropertiesHandlerStrategyClassPath(String applicationName) {
        super(applicationName);
    }

    public PropertiesHandlerStrategyClassPath(String applicationName, String fileName) {
        super(applicationName);
        this.fileWithinClasspath = fileName;
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

    public Map /*<String, String>*/ getOriginProperties() {
        Map originProperties = new HashMap();
        originProperties.put(PROPERTIES_FILE_NAME_WITHIN_CLASSPATH, fileWithinClasspath);
        return originProperties;
    }

    public Properties /*<String, String>*/ getProperties() {

        properties = PropertiesHandlerUtil.getInstance(this.fileWithinClasspath,
                PropertiesHandlerStrategyClassPath.class.getClassLoader(),
                true).getProperties();

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