package org.commons.specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * NOT decorator, used to create a new specifcation that is the inverse (NOT) of the given specification.
 */
public class NotSpecification extends AbstractSpecification {

    private Specification spec1;

    /**
     * Create a new NOT specification based on another specification.
     *
     * @param spec1 Specification instance to not.
     */
    public NotSpecification(final Specification spec1) {
        this(false, spec1);
    }

    public NotSpecification(boolean evaluateAllTerms, final Specification spec1) {
        super(evaluateAllTerms);
        this.spec1 = spec1;
    }

    /**
     * {@inheritDoc}
     *
     * @param object
     * @param map
     */
    public boolean isSatisfiedBy(Object object, final Map map) throws Exception {
        return !spec1.isSatisfiedBy(object, map);
    }

    public Specification getLeftNode() {
        return spec1;
    }

    public Specification getRightNode() {
        return null;
    }

    public Collection/*<Object>*/ getTerms() {
        Collection/*<Object>*/ terms = new ArrayList();
        Specification left = getLeftNode();
        if (left != null) {
            terms.addAll(left.getTerms());
        }
        Specification right = getRightNode();
        if (right != null) {
            terms.addAll(right.getTerms());
        }
        return terms;
    }

}
