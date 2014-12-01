package org.commons.interpreter;

import org.commons.interpreter.exception.ContextException;
import org.commons.interpreter.exception.ExpressionException;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Map;

public interface Evaluator {

    static final Logger log = Logger.getLogger(Evaluator.class);

    boolean isOperator(String str);

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

    Expression buildTree(String expr) throws ExpressionException;

    NonTerminalExpression getNonTerminalExpression(String operation, Expression l, Expression r);

    void setContext(Context c);

    void setContext(Map/*<String, String>*/ contextMap);

    void addToContext(String key, String value);

    void setExpression(String expr);

    String getExpression();

    Context getContext();

    String infixToPostFix(String str);

    Collection getTokens() throws ExpressionException;

    Expression buildExpressionTree() throws ExpressionException;
}
