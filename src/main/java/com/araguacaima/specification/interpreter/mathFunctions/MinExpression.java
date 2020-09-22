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

package com.araguacaima.specification.interpreter.mathFunctions;

import com.araguacaima.specification.interpreter.Context;
import com.araguacaima.specification.interpreter.Expression;
import com.araguacaima.specification.interpreter.exception.ContextException;
import com.araguacaima.specification.interpreter.exception.ExpressionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class MinExpression extends NonTerminalMathFunctionExpression {
    public MinExpression(Expression l, Expression r) {
        super(l, r);
    }

    public Expression evaluate(Context c)
            throws ExpressionException, ContextException {
        if (getEvaluateAllTerms()) {
            double result1 = (Double) getLeftNode().evaluate(c).getValue();
            double result2 = (Double) getRightNode().evaluate(c).getValue();
            return new MathFunctionExpressionImpl(Math.min(result1, result2));
        } else {
            return new MathFunctionExpressionImpl(Math.min(Double.parseDouble(String.valueOf(getLeftNode().evaluate(c).getValue()))
                    , Double.parseDouble(String.valueOf(getRightNode().evaluate(c).getValue()))));
        }
    }

    public Object getCondition() {
        return null;
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
}// SubtractExpressionLogical
