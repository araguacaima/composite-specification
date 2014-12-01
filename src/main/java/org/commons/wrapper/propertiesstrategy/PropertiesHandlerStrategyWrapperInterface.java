package org.commons.wrapper.propertiesstrategy;

import java.util.Map;
import java.util.Properties;


public interface PropertiesHandlerStrategyWrapperInterface {

    public void setNext(PropertiesHandlerStrategyWrapperInterface next);

    public PropertiesHandlerStrategyWrapperInterface getNext();

    public Properties /*<String, String>*/getProperties();

    public Map /*<String, String>*/getOriginProperties();

    public String getPropertiesHandlerStrategyName();

}
