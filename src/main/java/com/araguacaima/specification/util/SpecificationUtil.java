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

package com.araguacaima.specification.util;

import com.araguacaima.specification.Specification;
import com.araguacaima.specification.common.ReflectionUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;

public class SpecificationUtil {

    public Collection<Object> getClassNamesTerms(Specification specification) {

        Collection<Object> result = new ArrayList<>(specification.getTerms());
        CollectionUtils.transform(result, o -> ((Class<?>) o).getName());
        return result;
    }

    public Collection<String> getSpecificationClassesNamesForObject(Class<?> clazz)
            throws IllegalAccessException, InstantiationException {
        return getSpecificationClassesNamesForObject(clazz.newInstance());
    }

    private Collection<String> getSpecificationClassesNamesForObject(Object object) {
        Collection<String> result = new ArrayList<>();
        Collection<String> specificationFields = ReflectionUtils.getAllFieldsNamesOfType(object.getClass(),
                Specification.class);
        for (Object specificationField : specificationFields) {
            String field = (String) specificationField;
            Specification specification = (Specification) ReflectionUtils.invokeGetter(object, field);
            if (specification != null) {
                Collection<Object> terms = specification.getTerms();
                for (Object term1 : terms) {
                    String term = ((Class<?>) term1).getSimpleName();
                    result.add(term);
                }
            }

        }
        return result;
    }

    public Collection<String> getSpecificationClassesNamesForObject(String clazzName)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        return getSpecificationClassesNamesForObject(Class.forName(clazzName).newInstance());
    }

}
