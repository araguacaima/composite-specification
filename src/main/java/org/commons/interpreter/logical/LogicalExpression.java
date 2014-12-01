package org.commons.interpreter.logical;

import org.commons.interpreter.Context;
import org.commons.interpreter.Expression;
import org.commons.interpreter.exception.ContextException;
import org.commons.interpreter.exception.ExpressionException;

public abstract class LogicalExpression implements Expression {

    boolean value;
    private boolean evaluateAllTerms = false;

    public LogicalExpression() {

    }

    public LogicalExpression(boolean value) {
        this(value, false);

    }

    public LogicalExpression(boolean value, boolean evaluateAllTerms) {
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
