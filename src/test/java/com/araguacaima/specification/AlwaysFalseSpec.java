package com.araguacaima.specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class AlwaysFalseSpec extends AbstractSpecification {

    public AlwaysFalseSpec() {
        this(false);
    }

    public AlwaysFalseSpec(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    public boolean isSatisfiedBy(Object object, Map map) {
        return false;
    }

    public Collection/*<Object>*/ getTerms() {
        return new ArrayList();
    }

}
