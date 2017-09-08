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

package org.araguacaima.specification.interpreter.logical;

import org.araguacaima.specification.interpreter.Context;
import org.araguacaima.specification.interpreter.Expression;
import org.araguacaima.specification.interpreter.arithmetic.ArithmeticContext;
import org.araguacaima.specification.interpreter.exception.ContextException;
import org.araguacaima.specification.interpreter.exception.ExpressionException;
import org.araguacaima.specification.interpreter.exception.InvalidExpressionException;
import org.araguacaima.specification.interpreter.logicalArithmetic.LogicalArithmeticContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class EqualExpression extends NonTerminalLogicalExpression {

    public Expression evaluate(Context c) throws ExpressionException, ContextException {
        if (c instanceof ArithmeticContext || c instanceof LogicalContext || c instanceof LogicalArithmeticContext) {
            Object objectLeft = getLeftNode().evaluate(c).getValue();
            Object objectRight = getRightNode().evaluate(c).getValue();
            return new LogicalExpressionImpl(objectLeft.equals(objectRight));
        } else {
            throw new InvalidExpressionException();
        }
    }

    public Collection getTerms() {
        Collection terms = new ArrayList();
        terms.addAll(getLeftNode().getTerms());
        terms.addAll(getRightNode().getTerms());
        return new HashSet(terms);
    }

    public Object getValue() {
        return null;
    }

    public Object getCondition() {
        return null;
    }

    public EqualExpression(Expression l, Expression r) {
        this(false, l, r);
    }

    public EqualExpression(boolean evaluateAllTerms, Expression l, Expression r) {
        super(evaluateAllTerms, l, r);
    }
}// EqualExpression
