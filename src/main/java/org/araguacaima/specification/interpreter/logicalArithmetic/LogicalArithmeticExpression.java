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
import org.araguacaima.specification.interpreter.Expression;
import org.araguacaima.specification.interpreter.exception.ContextException;
import org.araguacaima.specification.interpreter.exception.ExpressionException;

public abstract class LogicalArithmeticExpression implements Expression {

    double value;
    boolean condition;

    private boolean evaluateAllTerms = false;

    LogicalArithmeticExpression() {

    }

    LogicalArithmeticExpression(double value) {
        this(value, false);

    }

    LogicalArithmeticExpression(boolean condition) {
        this(condition, false);
    }

    private LogicalArithmeticExpression(boolean condition, boolean evaluateAllTerms) {
        this.condition = condition;
        setEvaluateAllTerms(evaluateAllTerms);
    }

    private LogicalArithmeticExpression(double value, boolean evaluateAllTerms) {
        this.value = value;
        setEvaluateAllTerms(evaluateAllTerms);
    }

    /**
     * {@inheritDoc}
     *
     * @param evaluateAllTerms
     */
    public void setEvaluateAllTerms(boolean evaluateAllTerms) {
        this.evaluateAllTerms = evaluateAllTerms;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */

    public boolean getEvaluateAllTerms() {
        return evaluateAllTerms;
    }

    public abstract Expression evaluate(Context c) throws ExpressionException, ContextException;

}
