package org.commons.specification;

import java.util.Map;

public class AbstractSpecificationImpl extends AbstractSpecification {

    public AbstractSpecificationImpl() {
        super(false);
    }

    public AbstractSpecificationImpl(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    public boolean isSatisfiedBy(Object object, Map map) throws Exception {
        return false;
    }
}
