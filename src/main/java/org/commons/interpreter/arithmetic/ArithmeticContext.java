package org.commons.interpreter.arithmetic;

import org.commons.interpreter.Context;
import org.commons.interpreter.exception.ContextException;
import org.commons.specification.Specification;

import java.util.HashMap;
import java.util.Map;

public class ArithmeticContext implements Context {
    private HashMap varList = new HashMap();
    private Specification specification;

    public void assign(String var, double value) {
        varList.put(var, new Double(value));
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

    public double getValue(String var) throws ContextException {
        try {
            Double objDouble = (Double) varList.get(var);
            return objDouble.doubleValue();
        } catch (NullPointerException npe) {
            throw new ContextException("There is no context setted for term '"
                    + var
                    + "'. Please initialize a valid value for it");
        }

    }

    public ArithmeticContext() {
        initialize();
    }

    private void initialize() {
    }

    public Map getContextElements() {
        return varList;
    }
}

