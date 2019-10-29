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

package com.araguacaima.specification.interpreter.logical;

import com.araguacaima.specification.Specification;
import com.araguacaima.specification.interpreter.Context;
import com.araguacaima.specification.interpreter.exception.ContextException;

import java.util.HashMap;
import java.util.Map;

public class LogicalContext implements Context {
    private final HashMap<String, Object> varList = new HashMap<>();
    private Specification specification;

    public LogicalContext() {

    }

    public void assign(String var, boolean value) {
        varList.put(var, value);
    }

    public void assign(String var, Specification specification) {
        this.specification = specification;
        varList.put(var, this.specification);
    }

    public void assignParameterObject(String var, Object parameter) {
        varList.put(var, parameter);
    }

    public Map getContextElements() {
        return varList;
    }

    public Object getContextObject(String var) {
        return varList.get(var);
    }

    public boolean getValue(String var)
            throws ContextException {
        try {
            Boolean objBoolean = (Boolean) varList.get(var);
            return objBoolean;
        } catch (NullPointerException npe) {
            throw new ContextException("There is no context setted for term '" + var + "'. Please initialize a valid " +
                    "" + "value for it");
        } catch (ClassCastException cce) {
            throw new ContextException("There is no a valid value for term '" + var + "'. Please ensure that " +
                    "initialized value corresponds to a boolean one");
        }
    }
}
