package org.commons.interpreter.arithmetic;

import org.commons.interpreter.Context;
import org.commons.interpreter.Expression;
import org.commons.interpreter.exception.ContextException;
import org.commons.interpreter.exception.ExpressionException;

public abstract class ArithmeticExpression implements Expression {

    double value;

    private boolean evaluateAllTerms = false;

    public ArithmeticExpression() {

    }

    public ArithmeticExpression(double value) {
        this(value, false);

    }

    public ArithmeticExpression(double value, boolean evaluateAllTerms) {
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
