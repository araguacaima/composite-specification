package org.commons.interpreter.logical;

import org.commons.interpreter.Context;
import org.commons.interpreter.Expression;
import org.commons.interpreter.exception.ExpressionException;

import java.util.Collection;

public class LogicalExpressionImpl extends LogicalExpression {

    public LogicalExpressionImpl(boolean value) {
        super(value);
    }

    public Expression evaluate(Context c) throws ExpressionException {
        return null;
    }

    public Collection getTerms() {
        return null;
    }

    public Object getValue() {
        return Boolean.valueOf(this.value);
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
