package org.commons.interpreter.logical;

import org.commons.interpreter.Context;
import org.commons.interpreter.Expression;
import org.commons.interpreter.arithmetic.ArithmeticContext;
import org.commons.interpreter.exception.ContextException;
import org.commons.interpreter.exception.ExpressionException;
import org.commons.interpreter.exception.InvalidExpressionException;
import org.commons.interpreter.logicalArithmetic.LogicalArithmeticContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class EqualExpression extends NonTerminalLogicalExpression {

    public Expression evaluate(Context c) throws ExpressionException, ContextException {
        if (c instanceof ArithmeticContext || c instanceof LogicalContext || c instanceof LogicalArithmeticContext) {
            Object objectLeft = getLeftNode().evaluate(c).getValue();
            Object objectRight = getRightNode().evaluate(c).getValue();
            return new LogicalExpressionImpl(objectLeft.equals(objectRight));
        } else {
            throw new InvalidExpressionException();
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

    public EqualExpression(Expression l, Expression r) {
        this(false, l, r);
    }

    public EqualExpression(boolean evaluateAllTerms, Expression l, Expression r) {
        super(evaluateAllTerms, l, r);
    }
}// EqualExpression
