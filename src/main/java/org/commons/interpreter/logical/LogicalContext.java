package org.commons.interpreter.logical;

import org.commons.interpreter.Context;
import org.commons.interpreter.exception.ContextException;
import org.commons.specification.Specification;

import java.util.HashMap;
import java.util.Map;

public class LogicalContext implements Context {
    private HashMap/*<String, Boolean>*/ varList = new HashMap/*<String, Boolean>*/();
    private Specification specification;

    public void assign(String var, boolean value) {
        varList.put(var, Boolean.valueOf(value));
    }

    public void assign(String var, Specification specification) {
        this.specification = specification;
        varList.put(var, this.specification);
    }

    public void assignParameterObject(String var, Object parameter) {
        varList.put(var, parameter);
    }

    public Object getContextObject(String var) {
        return varList.get(var);
    }

    public boolean getValue(String var) throws ContextException {
        try {
            Boolean objBoolean = (Boolean) varList.get(var);
            return objBoolean.booleanValue();
        } catch (NullPointerException npe) {
            throw new ContextException("There is no context setted for term '"
                    + var
                    + "'. Please initialize a valid value for it");
        } catch (ClassCastException cce) {
            throw new ContextException("There is no a valid value for term '"
                    + var
                    + "'. Please ensure that initialized value corresponds to a boolean one");
        }
    }

    public LogicalContext() {
        initialize();
    }

    private void initialize() {
    }

    public Map getContextElements() {
        return varList;
    }
}
