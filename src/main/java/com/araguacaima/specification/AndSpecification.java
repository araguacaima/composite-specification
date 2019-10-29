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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * AND specification, used to create a new specifcation that is the AND of two other specifications.
 */
@SuppressWarnings("WeakerAccess")
public class AndSpecification extends AbstractSpecification {

    private final Specification spec1;
    private final Specification spec2;

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

    public Collection<Object> getTerms() {
        Collection<Object> terms = new ArrayList<>();
        terms.addAll(getLeftNode().getTerms());
        terms.addAll(getRightNode().getTerms());
        return terms;
    }

    public Specification getLeftNode() {
        return spec1;
    }

    public Specification getRightNode() {
        return spec2;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSatisfiedBy(Object object, final Map<Object, Object> map)
            throws Exception {
        if (getEvaluateAllTerms()) {
            boolean result1 = spec1.isSatisfiedBy(object, map);
            boolean result2 = spec2.isSatisfiedBy(object, map);
            return result1 && result2;
        } else {
            return spec1.isSatisfiedBy(object, map) && spec2.isSatisfiedBy(object, map);
        }
    }
}
