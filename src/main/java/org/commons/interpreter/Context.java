package org.commons.interpreter;

import org.commons.specification.Specification;

import java.util.Map;

public interface Context {

    public Map getContextElements();

    public void assign(String var, Specification specification);

    public void assignParameterObject(String var, Object parameter);

    public Object getContextObject(String var);

}
