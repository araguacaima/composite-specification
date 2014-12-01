package org.commons.interpreter.logicalArithmetic;

import org.commons.interpreter.Context;
import org.commons.interpreter.Expression;
import org.commons.interpreter.exception.ContextException;
import org.commons.interpreter.exception.ExpressionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class LessOrEqualThanExpressionLogicalArithmetic extends NonTerminalLogicalArithmeticExpression {
    public Expression evaluate(Context c) throws ExpressionException, ContextException {
        try {
            double result1 = ((Double) getLeftNode().evaluate(c).getValue()).doubleValue();
            double result2 = ((Double) getRightNode().evaluate(c).getValue()).doubleValue();
            return new LogicalArithmeticExpressionImpl(result1 <= result2);
        } catch (ArithmeticException ae) {
            throw new ExpressionException(ae.getMessage());
        }
    }

    public Collection getTerms() {
        Collection terms = new ArrayList();
        terms.addAll(getLeftNode().getTerms());
        terms.addAll(getRightNode().getTerms());
        return new HashSet(terms);
    }

    public Object getValue() {
        return null;
    }

    public Object getCondition() {
        return null;
    }

    public LessOrEqualThanExpressionLogicalArithmetic(Expression l, Expression r) {
        super(l, r);
    }

}
