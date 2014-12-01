package org.commons.wrapper.propertiesstrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class PropertiesHandlerStrategyDB extends PropertiesHandlerStrategyWrapper {

    public static final String PROPERTIES_HANDLER_STRATEGY_NAME
            = PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_DB;
    protected PropertiesHandlerStrategyWrapperInterface next;
    public static Properties properties;

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

    public Map /*<String, String>*/ getOriginProperties() {
        return new HashMap();
    }

    public Properties /*<String, String>*/ getProperties() {

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