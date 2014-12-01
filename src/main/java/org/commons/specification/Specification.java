package org.commons.specification;

import java.util.Collection;
import java.util.Map;

/**
 * Specificaiton interface.
 * <p/>
 * Use {@link AbstractSpecification} as base for creating specifications, and
 * only the method {@link #isSatisfiedBy(Object, java.util.Map)} must be implemented.
 */
public interface Specification {

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

    /**
     * Check if {@code t} is satisfied by the specification.
     *
     * @param object The value to be compared
     * @param map    A set of required values to perform the comparison
     * @return {@code true} if {@code t} satisfies the specification.
     * @throws ClassCastException Thrown if o can not be cast to expected type.
     */
    boolean isSatisfiedBy(Object object, Map map) throws Exception;

    /**
     * Create a new specification that is the AND operation of {@code this} specification and another specification.
     *
     * @param specification Specification to AND.
     * @return A new specification.
     */
    Specification and(Specification specification);

    /**
     * Create a new specification that is the OR operation of {@code this} specification and another specification.
     *
     * @param specification Specification to OR.
     * @return A new specification.
     */
    Specification or(Specification specification);

    /**
     * Create a new specification that is the Logical Equality operation of {@code this} specification and another specification.
     *
     * @param specification Specification to LogicalEq.
     * @return A new specification.
     */
    Specification logicalEq(Specification specification);

    /**
     * Create a new specification that is the NOT operation of {@code this} specification.
     *
     * @param specification Specification to NOT.
     * @return A new specification.
     */
    Specification not(Specification specification);

    /**
     * Builds a expression based on the content of this Specification
     *
     * @return A String that denotes the expression represented by this Specification
     */
    String toString();

    /**
     * Obtains the Left Node for {@code this} specification.
     *
     * @return A new specification.
     */
    Specification getLeftNode();

    /**
     * Obtains the Right Node for {@code this} specification.
     *
     * @return A new specification.
     */
    Specification getRightNode();

    /**
     * Get all terms of the specification
     *
     * @return A list of all terms that conforms the specification
     */
    Collection/*<Object>*/ getTerms();

}
