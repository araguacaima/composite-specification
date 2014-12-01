package org.commons.interpreter.logicalArithmetic;

import org.commons.interpreter.Context;
import org.commons.interpreter.exception.ContextException;
import org.commons.interpreter.exception.ContextSpecificationException;
import org.commons.specification.Specification;

import java.util.HashMap;
import java.util.Map;

public class LogicalArithmeticContext implements Context {
    private HashMap/*<String, Double>*/ varList = new HashMap/*<String, Double>*/();
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
        } catch (ClassCastException cce) {

            throw new ContextException("There is no a valid value for term '"
                    + var
                    + "'. Please ensure that initialized value corresponds to a double one");

        } catch (NumberFormatException ignored) {

            throw new ContextSpecificationException("There is no a valid value for term '"
                    + var
                    + "'. Please ensure that initialized value corresponds to a double one or "
                    + "to a valid Specification");

        }

    }

    public LogicalArithmeticContext() {
        initialize();
    }

    private void initialize() {
    }

    public Map getContextElements() {
        return varList;
    }

    public boolean evaluateSpecification(String var, Object object) throws ContextSpecificationException {
        try {
            if (specification.getClass().getName().equals(getContextObject(var).toString())) {
                return specification.isSatisfiedBy(object, new HashMap());
            } else {
                throw new ContextSpecificationException("There is no a specification class configured for name '"
                        + var
                        + "'");
            }
        } catch (Throwable ignored) {
            throw new ContextSpecificationException("There is no a valid value for term '"
                    + var
                    + "'. Please ensure that initialized value corresponds to a double one or "
                    + "to a valid Specification");
        }
    }
}

