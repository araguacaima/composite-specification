package org.commons.interpreter;

import org.commons.interpreter.exception.ContextException;
import org.commons.interpreter.exception.ExpressionException;
import org.commons.specification.Specification;

import java.util.Collection;

public interface Expression {

    /**
     * Indicates if its required to evaluate individually all terms before determine the final logical result of
     * the expression
     *
     * @param evaluateAllTerms A value of true indicates that its pretended to evaluate all terms indenpendently of is
     *                         logical result before determine the final logical result of the entire expression. A
     *                         value of false indicates that the evaluation breaks in any condition (term) return a
     *                         value that satisfy the entire expression without the need of evaluate the remaining terms.
     *                         The default value is false.
     */
    void setEvaluateAllTerms(boolean evaluateAllTerms);

    /**
     * Obtains the value of the evaluateAllTerms field
     *
     * @return The evaluateAllTerms field
     */
    boolean getEvaluateAllTerms();

    public Expression evaluate(Context c) throws ExpressionException, ContextException;

    public Collection getTerms();

    Object getValue();

    Object getCondition();

    Expression getLeftNode();

    Expression getRightNode();
}
