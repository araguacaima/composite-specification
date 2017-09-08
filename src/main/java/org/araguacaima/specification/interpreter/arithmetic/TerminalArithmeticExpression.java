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

package org.araguacaima.specification.interpreter.arithmetic;

import org.araguacaima.specification.interpreter.Context;
import org.araguacaima.specification.interpreter.Expression;
import org.araguacaima.specification.interpreter.TerminalExpression;
import org.araguacaima.specification.interpreter.exception.ContextException;
import org.araguacaima.specification.interpreter.exception.ExpressionException;

import java.util.Arrays;
import java.util.Collection;

public class TerminalArithmeticExpression extends ArithmeticExpression implements Expression, TerminalExpression {
    private final String var;

    public TerminalArithmeticExpression(String v) {
        var = v;
    }

    public Collection getTerms() {
        return Arrays.asList(getValue());
    }

    public Object getValue() {
        return var;
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

    public Expression evaluate(Context c) throws ExpressionException, ContextException {
        return new ArithmeticExpressionImpl(((ArithmeticContext) c).getValue(var));
    }
}
