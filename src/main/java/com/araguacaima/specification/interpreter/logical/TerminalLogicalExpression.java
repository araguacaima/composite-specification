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

package com.araguacaima.specification.interpreter.logical;

import com.araguacaima.specification.interpreter.Context;
import com.araguacaima.specification.interpreter.Expression;
import com.araguacaima.specification.interpreter.TerminalExpression;
import com.araguacaima.specification.interpreter.exception.ContextException;
import com.araguacaima.specification.interpreter.exception.ExpressionException;

import java.util.Arrays;
import java.util.Collection;

public class TerminalLogicalExpression extends LogicalExpression implements Expression, TerminalExpression {
    private final String var;

    public TerminalLogicalExpression(String v) {
        this(false, v);
    }

    public TerminalLogicalExpression(boolean evaluateAllTerms, String v) {
        super();
        setEvaluateAllTerms(evaluateAllTerms);
        var = v;
    }

    public Expression evaluate(Context c)
            throws ExpressionException, ContextException {
        return new LogicalExpressionImpl(((LogicalContext) c).getValue(var));
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
