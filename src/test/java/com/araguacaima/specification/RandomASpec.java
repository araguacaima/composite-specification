package com.araguacaima.specification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

public class RandomASpec extends AbstractSpecification {
    private static final Logger log = LoggerFactory.getLogger(RandomASpec.class);

    public RandomASpec() {
        this(false);
    }

    public RandomASpec(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    public boolean isSatisfiedBy(Object object, Map map) {
        boolean result = new Random().nextBoolean();
        log.debug("I'm the '" + this.getClass().getSimpleName() + "' specification. I've evaluated in '" + result + "'");
        return result;
    }

    public Collection/*<Object>*/ getTerms() {
        return new ArrayList();
    }

}
