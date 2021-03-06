/*
 * Copyright 2020 araguacaima
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

    public boolean isSatisfiedBy(Object object, Map<Object, Object> map) {
        boolean result = false;

        if (object != null && Number.class.isAssignableFrom(object.getClass())) {
            Number obj = (Number) object;
            if (obj.longValue() % 2 == 0) {
                result = true;
            }
        }
        log.debug("I'm the '" + this.getClass().getSimpleName() + "' specification. I've evaluated in '" + result + "'");
        return result;
    }

    public Collection<Object> getTerms() {
        return new ArrayList();
    }

}
