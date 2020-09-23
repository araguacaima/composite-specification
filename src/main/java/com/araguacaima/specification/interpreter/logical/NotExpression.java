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
import com.araguacaima.specification.interpreter.exception.ContextException;
import com.araguacaima.specification.interpreter.exception.ExpressionException;
import com.araguacaima.specification.interpreter.logicalArithmetic.LogicalArithmeticExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class NotExpression extends NonTerminalLogicalExpression {
    public NotExpression(Expression r) {
        this(false, r);
    }

    public NotExpression(boolean evaluateAllTems, Expression r) {
        super(evaluateAllTems, null, r);
    }

    public Expression evaluate(Context c)
            throws ExpressionException, ContextException {
        Expression expressionRightEvaluated = getRightNode();
        if (expressionRightEvaluated != null) {
            expressionRightEvaluated = expressionRightEvaluated.evaluate(c);
            Object objectRight;
            if (expressionRightEvaluated instanceof LogicalArithmeticExpression) {
                objectRight = expressionRightEvaluated.getCondition();
            } else if (expressionRightEvaluated instanceof LogicalExpression) {
                objectRight = expressionRightEvaluated.getValue();
            } else {
                objectRight = new Object();
            }
            Boolean resultRight = (Boolean) objectRight;
            return new LogicalExpressionImpl(!resultRight);
        } else {
            Expression expressionLeftEvaluated = getRightNode();
            if (expressionLeftEvaluated != null) {
                expressionLeftEvaluated = expressionLeftEvaluated.evaluate(c);
                Object objectLeft;
                if (expressionLeftEvaluated instanceof LogicalArithmeticExpression) {
                    objectLeft = expressionLeftEvaluated.getCondition();
                } else if (expressionLeftEvaluated instanceof LogicalExpression) {
                    objectLeft = expressionLeftEvaluated.getValue();
                } else {
                    objectLeft = new Object();
                }
                Boolean resultLeft = (Boolean) objectLeft;
                return new LogicalExpressionImpl(!resultLeft);
            }
        }
        throw new ExpressionException("The expression is not a valid NotExpression");
    }

    public Object getCondition() {
        return null;
    }

    public Collection getTerms() {
        Collection terms = new ArrayList();
        Expression left = getLeftNode();
        if (left != null) {
            terms.addAll(left.getTerms());
        }
        Expression right = getRightNode();
        if (right != null) {
            terms.addAll(right.getTerms());
        }
        return new HashSet(terms);
    }

    public Object getValue() {
        return null;
    }
}// NotExpression
