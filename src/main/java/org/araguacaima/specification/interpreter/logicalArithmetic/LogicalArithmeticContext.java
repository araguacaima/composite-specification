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

package org.araguacaima.specification.interpreter.logicalArithmetic;

import org.araguacaima.specification.interpreter.Context;
import org.araguacaima.specification.interpreter.exception.ContextException;
import org.araguacaima.specification.interpreter.exception.ContextSpecificationException;
import org.araguacaima.specification.Specification;

import java.util.HashMap;
import java.util.Map;

public class LogicalArithmeticContext implements Context {
    private final HashMap<String, Double> varList = new HashMap<String, Double>();
    private Specification specification;

    public void assign(String var, double value) {
        varList.put(var, value);
    }

    public void assign(String var, Specification specification) {
        this.specification = specification;
        varList.put(var, this.specification);
    }

    public void assignParameterObject(String var, Object parameter) {
        varList.put(var, parameter);
    }

    public Object getContextObject(String var) {
        return varList.get(var);
    }

    public double getValue(String var) throws ContextException {
        try {
            Double objDouble = (Double) varList.get(var);
            return objDouble;
        } catch (NullPointerException npe) {
            throw new ContextException("There is no context setted for term '"
                    + var
                    + "'. Please initialize a valid value for it");
        } catch (ClassCastException cce) {

            throw new ContextException("There is no a valid value for term '"
                    + var
                    + "'. Please ensure that initialized value corresponds to a double one");

        } catch (NumberFormatException ignored) {

            throw new ContextSpecificationException("There is no a valid value for term '"
                    + var
                    + "'. Please ensure that initialized value corresponds to a double one or "
                    + "to a valid Specification");

        }

    }

    public LogicalArithmeticContext() {

    }

    public Map getContextElements() {
        return varList;
    }

    public boolean evaluateSpecification(String var, Object object) throws ContextSpecificationException {
        try {
            if (specification.getClass().getName().equals(getContextObject(var).toString())) {
                return specification.isSatisfiedBy(object, new HashMap());
            } else {
                throw new ContextSpecificationException("There is no a specification class configured for name '"
                        + var
                        + "'");
            }
        } catch (Throwable ignored) {
            throw new ContextSpecificationException("There is no a valid value for term '"
                    + var
                    + "'. Please ensure that initialized value corresponds to a double one or "
                    + "to a valid Specification");
        }
    }
}

