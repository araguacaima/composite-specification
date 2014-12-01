package org.commons.interpreter.logicalArithmetic;

import org.commons.interpreter.Context;
import org.commons.interpreter.Expression;
import org.commons.interpreter.exception.ContextException;
import org.commons.interpreter.exception.ExpressionException;

public abstract class LogicalArithmeticExpression implements Expression {

    double value;
    boolean condition;

    private boolean evaluateAllTerms = false;

    public LogicalArithmeticExpression() {

    }

    public LogicalArithmeticExpression(double value) {
        this(value, false);

    }

    public LogicalArithmeticExpression(boolean condition) {
        this(condition, false);
    }

    public LogicalArithmeticExpression(boolean condition, boolean evaluateAllTerms) {
        this.condition = condition;
        setEvaluateAllTerms(evaluateAllTerms);
    }

    public LogicalArithmeticExpression(double value, boolean evaluateAllTerms) {
        this.value = value;
        setEvaluateAllTerms(evaluateAllTerms);
    }

    /**
     * {@inheritDoc}
     *
     * @param evaluateAllTerms
     */
    public void setEvaluateAllTerms(boolean evaluateAllTerms) {
        this.evaluateAllTerms = evaluateAllTerms;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */

    public boolean getEvaluateAllTerms() {
        return evaluateAllTerms;
    }

    public abstract Expression evaluate(Context c) throws ExpressionException, ContextException;

}
