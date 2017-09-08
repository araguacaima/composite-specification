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
import org.araguacaima.specification.interpreter.exception.ContextException;
import org.araguacaima.specification.interpreter.exception.ExpressionException;
import org.araguacaima.specification.interpreter.logicalArithmetic.LogicalArithmeticExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class AndExpression extends NonTerminalLogicalExpression {
    public Expression evaluate(Context c) throws ExpressionException, ContextException {
        if (getEvaluateAllTerms()) {
            Expression expressionLeftEvaluated = getLeftNode().evaluate(c);
            Object objectLeft;
            if (expressionLeftEvaluated instanceof LogicalArithmeticExpression) {
                objectLeft = expressionLeftEvaluated.getCondition();
            } else if (expressionLeftEvaluated instanceof LogicalExpression) {
                objectLeft = expressionLeftEvaluated.getValue();
            } else {
                objectLeft = new Object();
            }
            Boolean resultLeft = (Boolean) objectLeft;
            Expression expressionRightEvaluated = getRightNode().evaluate(c);
            Object objectRight;
            if (expressionRightEvaluated instanceof LogicalArithmeticExpression) {
                objectRight = expressionRightEvaluated.getCondition();
            } else if (expressionRightEvaluated instanceof LogicalExpression) {
                objectRight = expressionRightEvaluated.getValue();
            } else {
                objectRight = new Object();
            }
            Boolean resultRight = (Boolean) objectRight;
            return new LogicalExpressionImpl(resultLeft & resultRight);
        } else {
            Expression expressionLeftEvaluated = getLeftNode().evaluate(c);
            Object objectLeft;
            if (expressionLeftEvaluated instanceof LogicalArithmeticExpression) {
                objectLeft = expressionLeftEvaluated.getCondition();
            } else if (expressionLeftEvaluated instanceof LogicalExpression) {
                objectLeft = expressionLeftEvaluated.getValue();
            } else {
                objectLeft = new Object();
            }

            Boolean resultLeft = (Boolean) objectLeft;
            if (Boolean.TRUE.equals(resultLeft)) {
                Expression expressionRightEvaluated = getRightNode().evaluate(c);
                Object objectRight;
                if (expressionRightEvaluated instanceof LogicalArithmeticExpression) {
                    objectRight = expressionRightEvaluated.getCondition();
                } else if (expressionRightEvaluated instanceof LogicalExpression) {
                    objectRight = expressionRightEvaluated.getValue();
                } else {
                    objectRight = new Object();
                }

                Boolean resultRight = (Boolean) objectRight;
                return new LogicalExpressionImpl(resultLeft & resultRight);
            } else {
                return new LogicalExpressionImpl(false);
            }
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

    public AndExpression(Expression l, Expression r) {
        this(false, l, r);
    }

    public AndExpression(boolean evaluateAllTerms, Expression l, Expression r) {
        super(evaluateAllTerms, l, r);
    }
}// AddExpressionLogical
