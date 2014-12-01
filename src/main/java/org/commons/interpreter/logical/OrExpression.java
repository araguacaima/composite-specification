package org.commons.interpreter.logical;

import org.commons.interpreter.Context;
import org.commons.interpreter.Expression;
import org.commons.interpreter.exception.ContextException;
import org.commons.interpreter.exception.ExpressionException;
import org.commons.interpreter.logicalArithmetic.LogicalArithmeticExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class OrExpression extends NonTerminalLogicalExpression {
    public Expression evaluate(Context c) throws ExpressionException, ContextException {
        if (getEvaluateAllTerms()) {
            Expression expressionLeftEvaluated = getLeftNode().evaluate(c);
            Object objectLeft;
            if (expressionLeftEvaluated instanceof LogicalArithmeticExpression) {
                objectLeft = expressionLeftEvaluated.getCondition();
            } else if (expressionLeftEvaluated instanceof LogicalExpression) {
                objectLeft = expressionLeftEvaluated.getValue();
            } else {
                objectLeft = new Object();
            }
            Boolean resultLeft = (Boolean) objectLeft;
            Expression expressionRightEvaluated = getRightNode().evaluate(c);
            Object objectRight;
            if (expressionRightEvaluated instanceof LogicalArithmeticExpression) {
                objectRight = expressionRightEvaluated.getCondition();
            } else if (expressionRightEvaluated instanceof LogicalExpression) {
                objectRight = expressionRightEvaluated.getValue();
            } else {
                objectRight = new Object();
            }
            Boolean resultRight = (Boolean) objectRight;
            return new LogicalExpressionImpl(resultLeft.booleanValue() | resultRight.booleanValue());
        } else {
            Expression expressionLeftEvaluated = getLeftNode().evaluate(c);
            Object objectLeft;
            if (expressionLeftEvaluated instanceof LogicalArithmeticExpression) {
                objectLeft = expressionLeftEvaluated.getCondition();
            } else if (expressionLeftEvaluated instanceof LogicalExpression) {
                objectLeft = expressionLeftEvaluated.getValue();
            } else {
                objectLeft = new Object();
            }

            Boolean resultLeft = (Boolean) objectLeft;
            if (Boolean.FALSE.equals(resultLeft)) {
                Expression expressionRightEvaluated = getRightNode().evaluate(c);
                Object objectRight;
                if (expressionRightEvaluated instanceof LogicalArithmeticExpression) {
                    objectRight = expressionRightEvaluated.getCondition();
                } else if (expressionRightEvaluated instanceof LogicalExpression) {
                    objectRight = expressionRightEvaluated.getValue();
                } else {
                    objectRight = new Object();
                }

                Boolean resultRight = (Boolean) objectRight;
                return new LogicalExpressionImpl(resultLeft.booleanValue() | resultRight.booleanValue());
            } else {
                return new LogicalExpressionImpl(true);
            }
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

    public OrExpression(Expression l, Expression r) {
        this(false, l, r);
    }

    public OrExpression(boolean evaluateAllTerms, Expression l, Expression r) {
        super(evaluateAllTerms, l, r);
    }
}// OrExpression
