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
 * NOT decorator, used to create a new specifcation that is the inverse (NOT) of the given specification.
 */
public class NotSpecification extends AbstractSpecification {

    private final Specification spec1;

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

    public Collection<Object> getTerms() {
        Collection<Object> terms = new ArrayList();
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

    public Specification getLeftNode() {
        return spec1;
    }

    public Specification getRightNode() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @param object
     * @param map
     */
    public boolean isSatisfiedBy(Object object, final Map map)
            throws Exception {
        return !spec1.isSatisfiedBy(object, map);
    }

}
