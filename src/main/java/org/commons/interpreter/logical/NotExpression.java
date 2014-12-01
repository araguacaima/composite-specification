package org.commons.interpreter.logical;

import org.commons.interpreter.Context;
import org.commons.interpreter.Expression;
import org.commons.interpreter.exception.ContextException;
import org.commons.interpreter.exception.ExpressionException;
import org.commons.interpreter.logicalArithmetic.LogicalArithmeticExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class NotExpression extends NonTerminalLogicalExpression {
    public Expression evaluate(Context c) throws ExpressionException, ContextException {
        Expression expressionRightEvaluated = getRightNode();
        if (expressionRightEvaluated != null) {
            expressionRightEvaluated = expressionRightEvaluated.evaluate(c);
            Object objectRight;
            if (expressionRightEvaluated instanceof LogicalArithmeticExpression) {
                objectRight = expressionRightEvaluated.getCondition();
            } else if (expressionRightEvaluated instanceof LogicalExpression) {
                objectRight = expressionRightEvaluated.getValue();
            } else {
                objectRight = new Object();
            }
            Boolean resultRight = (Boolean) objectRight;
            return new LogicalExpressionImpl(!resultRight.booleanValue());
        } else {
            Expression expressionLeftEvaluated = getRightNode();
            if (expressionLeftEvaluated != null) {
                expressionLeftEvaluated = expressionLeftEvaluated.evaluate(c);
                Object objectLeft;
                if (expressionLeftEvaluated instanceof LogicalArithmeticExpression) {
                    objectLeft = expressionLeftEvaluated.getCondition();
                } else if (expressionLeftEvaluated instanceof LogicalExpression) {
                    objectLeft = expressionLeftEvaluated.getValue();
                } else {
                    objectLeft = new Object();
                }
                Boolean resultLeft = (Boolean) objectLeft;
                return new LogicalExpressionImpl(!resultLeft.booleanValue());
            }
        }
        throw new ExpressionException("The expression is not a valid NotExpression");
    }

    public Collection getTerms() {
        Collection terms = new ArrayList();
        Expression left = getLeftNode();
        if (left != null) {
            terms.addAll(left.getTerms());
        }
        Expression right = getRightNode();
        if (right != null) {
            terms.addAll(right.getTerms());
        }
        return new HashSet(terms);
    }

    public Object getValue() {
        return null;
    }

    public Object getCondition() {
        return null;
    }

    public NotExpression(Expression r) {
        this(false, r);
    }

    public NotExpression(boolean evaluateAllTems, Expression r) {
        super(evaluateAllTems, null, r);
    }
}// NotExpression
