package org.commons.specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * AND specification, used to create a new specifcation that is the AND of two other specifications.
 */
public class AndSpecification extends AbstractSpecification {

    private Specification spec1;
    private Specification spec2;

    /**
     * Create a new AND specification based on two other specification.
     *
     * @param spec1 Specification one.
     * @param spec2 Specification two.
     */
    public AndSpecification(final Specification spec1, final Specification spec2) {
        this(false, spec1, spec2);
    }

    /**
     * Create a new AND specification based on two other specification.
     *
     * @param evaluateAllTerms Indicates if its required to evaluate individually all terms before determine the final
     *                         logical result of the expression
     * @param spec1            Specification one.
     * @param spec2            Specification two.
     */
    public AndSpecification(boolean evaluateAllTerms, final Specification spec1, final Specification spec2) {
        super(evaluateAllTerms);
        this.spec1 = spec1;
        this.spec2 = spec2;
    }

    /**
     * {@inheritDoc}
     *
     * @param object
     * @param map
     */
    public boolean isSatisfiedBy(Object object, final Map map) throws Exception {
        if (getEvaluateAllTerms()) {
            boolean result1 = spec1.isSatisfiedBy(object, map);
            boolean result2 = spec2.isSatisfiedBy(object, map);
            return result1 && result2;
        } else {
            return spec1.isSatisfiedBy(object, map) && spec2.isSatisfiedBy(object, map);
        }
    }

    public Specification getLeftNode() {
        return spec1;
    }

    public Specification getRightNode() {
        return spec2;
    }

    public Collection/*<Object>*/ getTerms() {
        Collection/*<Object>*/ terms = new ArrayList();
        terms.addAll(getLeftNode().getTerms());
        terms.addAll(getRightNode().getTerms());
        return terms;
    }
}
