package com.araguacaima.specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class AlwaysTrueSpec extends AbstractSpecification {

    public AlwaysTrueSpec() {
        this(false);
    }

    public AlwaysTrueSpec(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    public boolean isSatisfiedBy(Object object, Map map) {
        return true;
    }

    public Collection/*<Object>*/ getTerms() {
        return new ArrayList();
    }
}
