package org.commons.interpreter.logicalArithmetic;

import org.commons.interpreter.Context;
import org.commons.interpreter.Expression;
import org.commons.interpreter.TerminalExpression;
import org.commons.interpreter.exception.ContextException;
import org.commons.interpreter.exception.ContextSpecificationException;
import org.commons.interpreter.exception.ExpressionException;
import org.commons.util.StringUtil;

import java.util.Arrays;
import java.util.Collection;

public class TerminalLogicalArithmeticExpression extends LogicalArithmeticExpression
        implements Expression, TerminalExpression {
    private String var;

    public TerminalLogicalArithmeticExpression(String v) {
        var = v;
    }

    public Collection getTerms() {
        return Arrays.asList(new Object[]{getValue()});
    }

    public Object getValue() {
        return var;
    }

    public Object getCondition() {
        return null;
    }

    public Expression getLeftNode() {
        return null;
    }

    public Expression getRightNode() {
        return null;
    }

    public Expression evaluate(Context c) throws ExpressionException, ContextException {
        try {
            return new LogicalArithmeticExpressionImpl(((LogicalArithmeticContext) c).getValue(var));
        } catch (ContextException cse) {
            String[] tokens = var.split("\\{");
            String specificationParameterName;
            try {
                specificationParameterName = tokens[1].replaceAll("\\}", StringUtil.EMPTY_STRING);
            } catch (Throwable t) {
                specificationParameterName = null;
            }
            Object specificationParameter = c.getContextObject(specificationParameterName);
            if (specificationParameter == null) {
                throw new ContextSpecificationException("There is no Specification parameter setted on this Context");
            }
            return new LogicalArithmeticExpressionImpl(((LogicalArithmeticContext) c).evaluateSpecification(var,
                    specificationParameter));
        }
    }
}
