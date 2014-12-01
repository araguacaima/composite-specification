package org.commons.interpreter.arithmetic;

import org.commons.interpreter.Context;
import org.commons.interpreter.Expression;
import org.commons.interpreter.TerminalExpression;
import org.commons.interpreter.exception.ContextException;
import org.commons.interpreter.exception.ExpressionException;

import java.util.Arrays;
import java.util.Collection;

public class TerminalArithmeticExpression extends ArithmeticExpression implements Expression, TerminalExpression {
    private String var;

    public TerminalArithmeticExpression(String v) {
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
        return new ArithmeticExpressionImpl(((ArithmeticContext) c).getValue(var));
    }
}
