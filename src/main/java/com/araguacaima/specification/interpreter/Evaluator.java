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

package com.araguacaima.specification.interpreter;

import com.araguacaima.specification.interpreter.exception.ExpressionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public interface Evaluator {

    Logger log = LoggerFactory.getLogger(Evaluator.class);

    boolean isOperator(String str);

    /**
     * Indicates if its required to evaluate individually all terms before determine the final logical result of
     * the expression
     *
     * @param evaluateAllTerms A value of true indicates that its pretended to evaluate all terms indenpendently of is
     *                         logical result before determine the final logical result of the entire expression. A
     *                         value of false indicates that the evaluation breaks in any condition (term) return a
     *                         value that satisfy the entire expression without the need of evaluate the remaining terms.
     *                         The default value is false.
     */
    void setEvaluateAllTerms(boolean evaluateAllTerms);

    /**
     * Obtains the value of the evaluateAllTerms field
     *
     * @return The evaluateAllTerms field
     */
    boolean getEvaluateAllTerms();

    Expression buildTree(String expr) throws ExpressionException;

    NonTerminalExpression getNonTerminalExpression(String operation, Expression l, Expression r);

    void setContext(Context c);

    void setContext(Map<String, Object> contextMap);

    void addToContext(String key, String value);

    void setExpression(String expr);

    String getExpression();

    Context getContext();

    String infixToPostFix(String str);

    Collection getTokens() throws ExpressionException;

    Expression buildExpressionTree() throws ExpressionException;
}
