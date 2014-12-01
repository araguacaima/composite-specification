package org.commons.wrapper.propertiesstrategy;

import org.commons.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesHandlerStrategyDefault extends PropertiesHandlerStrategyWrapper {

    public static final String PROPERTIES_HANDLER_STRATEGY_NAME
            = PropertiesHandlerStrategyWrapper.PROPERTIES_HANDLER_STRATEGY_DEFAULT;
    private PropertiesHandlerStrategyWrapper propertiesHandlerStrategyDefault;
    protected PropertiesHandlerStrategyWrapperInterface next;

    public PropertiesHandlerStrategyDefault(String applicationName, String defaultPath) {
        super(applicationName);
        propertiesHandlerStrategyDefault = new PropertiesHandlerStrategyPath(applicationName, defaultPath);
    }


    public PropertiesHandlerStrategyDefault(String applicationName) {
        super(applicationName);
        propertiesHandlerStrategyDefault = new PropertiesHandlerStrategyPath(applicationName);
    }

    public String getPropertiesHandlerStrategyName() {
        return PROPERTIES_HANDLER_STRATEGY_NAME;
    }

    public void setNext(PropertiesHandlerStrategyWrapperInterface next) {
        propertiesHandlerStrategyDefault.setNext(next);
    }

    public PropertiesHandlerStrategyWrapperInterface getNext() {
        return propertiesHandlerStrategyDefault.getNext();
    }

    public Properties getProperties() {
        return propertiesHandlerStrategyDefault.getProperties();
    }

    public Map getOriginProperties() {
        Map originProperties = new HashMap();
        originProperties.put(PROPERTIES_HANDLER_STRATEGY_NAME, propertiesHandlerStrategyDefault.getClass().getName());
        return originProperties;
    }
}
