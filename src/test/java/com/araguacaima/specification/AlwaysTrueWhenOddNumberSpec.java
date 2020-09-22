package com.araguacaima.specification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class AlwaysTrueWhenOddNumberSpec extends AbstractSpecification {
    private static final Logger log = LoggerFactory.getLogger(AlwaysTrueWhenOddNumberSpec.class);

    public AlwaysTrueWhenOddNumberSpec() {
        this(false);
    }

    public AlwaysTrueWhenOddNumberSpec(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    public boolean isSatisfiedBy(Object object, Map map) {
        boolean result = false;

        if (Number.class.isAssignableFrom(object.getClass())) {
            Number obj = (Number) object;
            if (obj.longValue() % 2 == 0) {
                result = true;
            }
        }
        log.debug("I'm the '" + this.getClass().getSimpleName() + "' specification. I've evaluated in '" + result + "'");
        return result;
    }

    public Collection/*<Object>*/ getTerms() {
        return new ArrayList();
    }

}
