/*
 * Copyright 2017 araguacaima
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.araguacaima.specification;

import com.araguacaima.commons.utils.StringUtils;
import com.araguacaima.specification.interpreter.logical.LogicalEvaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Abstract base implementation of composite {@link Specification} with default implementations for {@code and},
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
     */
    public Specification and(final Specification specification) {
        return new AndSpecification(getEvaluateAllTerms(), this, specification);
    }

    /**
     * {@inheritDoc}
     */

    public boolean getEvaluateAllTerms() {
        return evaluateAllTerms;
    }

    /**
     * {@inheritDoc}
     */
    public void setEvaluateAllTerms(boolean evaluateAllTerms) {
        this.evaluateAllTerms = evaluateAllTerms;
    }

    private String getExpressionStringFromSpecification(Specification node, String result) {
        if (node != null) {

            if (node instanceof AndSpecification) {
                result = LogicalEvaluator.STARTING_PARENTHESIS + (getExpressionStringFromSpecification(node
                                .getLeftNode(),
                        result) + " " + LogicalEvaluator.AND + " " + getExpressionStringFromSpecification(node
                                .getRightNode(),
                        result)) + LogicalEvaluator.CLOSING_PARENTHESIS;
            } else if (node instanceof NotSpecification) {
                result = " " + LogicalEvaluator.NOT + " " + LogicalEvaluator.STARTING_PARENTHESIS +
                        getExpressionStringFromSpecification(
                                node.getLeftNode(),
                                result) + LogicalEvaluator.CLOSING_PARENTHESIS;
            } else if (node instanceof OrSpecification) {
                result = LogicalEvaluator.STARTING_PARENTHESIS + (getExpressionStringFromSpecification(node
                                .getLeftNode(),
                        result) + " " + LogicalEvaluator.OR + " " + getExpressionStringFromSpecification(node
                                .getRightNode(),
                        result)) + LogicalEvaluator.CLOSING_PARENTHESIS;
            } else if (node instanceof LogicalEqSpecification) {
                result = LogicalEvaluator.STARTING_PARENTHESIS + (getExpressionStringFromSpecification(node
                                .getLeftNode(),
                        result) + " " + LogicalEvaluator.LE + " " + getExpressionStringFromSpecification(node
                                .getRightNode(),
                        result)) + LogicalEvaluator.CLOSING_PARENTHESIS;
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

    public Collection<Object> getTerms() {
        Collection<Object> result = new ArrayList<>();
        result.add(this.getClass());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public abstract boolean isSatisfiedBy(Object object, Map<Object, Object> map)
            throws Exception;

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
    public Specification or(final Specification specification) {
        return new OrSpecification(getEvaluateAllTerms(), this, specification);
    }

    /**
     * {@inheritDoc}
     */

    public String toString() {
        return getExpressionStringFromSpecification(this, StringUtils.EMPTY);
    }

}
