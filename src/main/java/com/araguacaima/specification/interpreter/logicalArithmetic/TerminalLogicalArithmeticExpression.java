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

package com.araguacaima.specification.interpreter.logicalArithmetic;

import com.araguacaima.commons.utils.StringUtils;
import com.araguacaima.specification.interpreter.Context;
import com.araguacaima.specification.interpreter.Expression;
import com.araguacaima.specification.interpreter.TerminalExpression;
import com.araguacaima.specification.interpreter.exception.ContextException;
import com.araguacaima.specification.interpreter.exception.ContextSpecificationException;
import com.araguacaima.specification.interpreter.exception.ExpressionException;

import java.util.Arrays;
import java.util.Collection;

public class TerminalLogicalArithmeticExpression extends LogicalArithmeticExpression
        implements Expression, TerminalExpression {
    private final String var;

    public TerminalLogicalArithmeticExpression(String v) {
        var = v;
    }

    public Expression evaluate(Context c)
            throws ExpressionException, ContextException {
        try {
            return new LogicalArithmeticExpressionImpl(((LogicalArithmeticContext) c).getValue(var));
        } catch (ContextException cse) {
            String[] tokens = var.split("\\{");
            String specificationParameterName;
            try {
                specificationParameterName = tokens[1].replaceAll("}", StringUtils.EMPTY);
            } catch (Throwable t) {
                specificationParameterName = null;
            }
            Object specificationParameter = c.getContextObject(specificationParameterName);
            if (specificationParameter == null) {
                throw new ContextSpecificationException("There is no Specification parameter setted on this Context");
            }
            return new LogicalArithmeticExpressionImpl(((LogicalArithmeticContext) c).evaluateSpecification(var,
                    specificationParameter));
        }
    }

    public Object getCondition() {
        return null;
    }

    public Expression getLeftNode() {
        return null;
    }

    public Expression getRightNode() {
        return null;
    }

    public Collection getTerms() {
        return Arrays.asList(getValue());
    }

    public Object getValue() {
        return var;
    }
}
