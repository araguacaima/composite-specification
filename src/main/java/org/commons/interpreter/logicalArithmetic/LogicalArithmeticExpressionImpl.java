package org.commons.interpreter.logicalArithmetic;

import org.commons.interpreter.Context;
import org.commons.interpreter.Expression;
import org.commons.interpreter.exception.ExpressionException;

import java.util.Collection;

public class LogicalArithmeticExpressionImpl extends LogicalArithmeticExpression {

    public LogicalArithmeticExpressionImpl(double value) {
        super(value);
    }

    public LogicalArithmeticExpressionImpl(boolean condition) {
        super(condition);
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
        return Boolean.valueOf(this.condition);
    }

    public Expression getLeftNode() {
        return null;
    }

    public Expression getRightNode() {
        return null;
    }
}
