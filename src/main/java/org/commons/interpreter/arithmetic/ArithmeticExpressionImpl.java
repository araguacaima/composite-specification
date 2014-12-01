package org.commons.interpreter.arithmetic;

import org.commons.interpreter.Context;
import org.commons.interpreter.Expression;
import org.commons.interpreter.exception.ExpressionException;

import java.util.Collection;

public class ArithmeticExpressionImpl extends ArithmeticExpression {

    public ArithmeticExpressionImpl(double value) {
        super(value);
    }

    public Expression evaluate(Context c) throws ExpressionException {
        return null;
    }

    public Collection getTerms() {
        return null;
    }

    public Object getValue() {
        return new Double(this.value);
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
}
