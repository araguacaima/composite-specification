package org.commons.specification;

import org.commons.interpreter.logical.LogicalEvaluator;
import org.commons.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Abstract base implementation of composite {@link org.commons.specification.Specification} with default implementations for {@code and},
 * {@code or} and {@code not}
 */
public abstract class AbstractSpecification implements Specification {

    private boolean evaluateAllTerms = false;

    public AbstractSpecification() {
        this(false);
    }

    public AbstractSpecification(boolean evaluateAllTerms) {
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

    /**
     * {@inheritDoc}
     *
     * @param object
     * @param map
     */
    public abstract boolean isSatisfiedBy(Object object, Map map) throws Exception;

    /**
     * {@inheritDoc}
     */
    public Specification and(final Specification specification) {
        return new AndSpecification(getEvaluateAllTerms(), this, specification);
    }

    /**
     * {@inheritDoc}
     */
    public Specification or(final Specification specification) {
        return new OrSpecification(getEvaluateAllTerms(), this, specification);
    }

    /**
     * {@inheritDoc}
     */
    public Specification logicalEq(final Specification specification) {
        return new LogicalEqSpecification(getEvaluateAllTerms(), this, specification);
    }

    /**
     * {@inheritDoc}
     */
    public Specification not(final Specification specification) {
        return new NotSpecification(getEvaluateAllTerms(), specification);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getExpressionStringFromSpecification(this, StringUtil.EMPTY_STRING);
    }

    private String getExpressionStringFromSpecification(Specification node, String result) {
        if (node != null) {

            if (node instanceof AndSpecification) {
                result = LogicalEvaluator.STARTING_PARENTHESIS
                        + (getExpressionStringFromSpecification(node.getLeftNode(), result)
                        + " "
                        + LogicalEvaluator.AND
                        + " "
                        + getExpressionStringFromSpecification(node.getRightNode(), result))
                        + LogicalEvaluator.CLOSING_PARENTHESIS;
            } else if (node instanceof NotSpecification) {
                result = " "
                        + LogicalEvaluator.NOT
                        + " "
                        + LogicalEvaluator.STARTING_PARENTHESIS
                        + getExpressionStringFromSpecification(node.getLeftNode(), result)
                        + LogicalEvaluator.CLOSING_PARENTHESIS;
            } else if (node instanceof OrSpecification) {
                result = LogicalEvaluator.STARTING_PARENTHESIS
                        + (getExpressionStringFromSpecification(node.getLeftNode(), result)
                        + " "
                        + LogicalEvaluator.OR
                        + " "
                        + getExpressionStringFromSpecification(node.getRightNode(), result))
                        + LogicalEvaluator.CLOSING_PARENTHESIS;
            } else if (node instanceof LogicalEqSpecification) {
                result = LogicalEvaluator.STARTING_PARENTHESIS
                        + (getExpressionStringFromSpecification(node.getLeftNode(), result)
                        + " "
                        + LogicalEvaluator.LE
                        + " "
                        + getExpressionStringFromSpecification(node.getRightNode(), result))
                        + LogicalEvaluator.CLOSING_PARENTHESIS;
            } else {
                result = node.getClass().getName();
            }
        }
        return result;
    }

    public Specification getLeftNode() {
        return null;
    }

    public Specification getRightNode() {
        return null;
    }

    public Collection/*<Object>*/ getTerms() {
        Collection/*<Class>*/ result = new ArrayList();
        result.add(this.getClass());
        return result;
    }

}
