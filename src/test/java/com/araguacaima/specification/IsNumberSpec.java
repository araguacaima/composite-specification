package com.araguacaima.specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class IsNumberSpec extends AbstractSpecification {

    public IsNumberSpec() {
        this(false);
    }

    public IsNumberSpec(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    public boolean isSatisfiedBy(Object object, Map<Object, Object> map) {
        try {
            Double.parseDouble(object.toString());
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public Collection<Object> getTerms() {
        return new ArrayList<>();
    }
}
