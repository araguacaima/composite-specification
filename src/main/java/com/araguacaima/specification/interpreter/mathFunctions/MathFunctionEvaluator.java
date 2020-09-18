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

package com.araguacaima.specification.interpreter.mathFunctions;

import com.araguacaima.commons.utils.StringUtils;
import com.araguacaima.specification.interpreter.Context;
import com.araguacaima.specification.interpreter.Expression;
import com.araguacaima.specification.interpreter.NonTerminalExpression;
import com.araguacaima.specification.interpreter.exception.ContextException;
import com.araguacaima.specification.interpreter.exception.ExpressionException;
import com.araguacaima.specification.interpreter.logical.LogicalEvaluator;
import com.araguacaima.specification.interpreter.logicalArithmetic.LogicalArithmeticEvaluator;
import com.araguacaima.specification.interpreter.logicalArithmetic.TerminalLogicalArithmeticExpression;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.TransformerUtils;

import java.util.*;

public class MathFunctionEvaluator extends LogicalArithmeticEvaluator<Double> {

    private static final Character MAX = '¡';
    private static final Character MIN = '!';

    static {
        operators.put(MAX, "1");
        operators.put(MIN, "1");
    }

    public MathFunctionEvaluator() {
        super(new LogicalEvaluator(), false);
    }

    public static Map getFullOperators() {
        return operators;
    }

    private static HashSet<Character> getOperators() {
        return new HashSet<>(operators.keySet());
    }

    public void addToContext(String key, String value) {
        ((MathFunctionContext) ctx).assign(key, Double.valueOf(value));
    }

    @Override
    public Double evaluate(Context c) throws ExpressionException, ContextException {
        setContext(c);
        return evaluate();
    }

    private double evaluate() throws ExpressionException, ContextException {
        return (Double) (expression.evaluate(ctx)).getValue();
    }

    @Override
    public NonTerminalExpression getNonTerminalExpression(String operation, Expression l, Expression r) {
        if (operation.trim().equals(MAX.toString())) {
            return new MaxExpression(l, r);
        }
        if (operation.trim().equals(MIN.toString())) {
            return new MinExpression(l, r);
        }
        return null;
    }

    @Override
    protected Expression buildExpressionTree() throws ExpressionException {
        String pfExpr = infixToPostFix(expressionString);
        return buildTree(pfExpr);
    }

    private Expression buildTree(String expr) throws ExpressionException {
        Stack<Expression> s = new Stack<>();
        Collection<Character> symbolOperators = getOperators();
        CollectionUtils.transform(symbolOperators, TransformerUtils.invokerTransformer("toString"));
        Collection<String> tokens = stringUtils.splitBySeparators(expressionString, symbolOperators);
        for (int i = 0; i < expr.length(); ) {
            String currChar = expr.substring(i, 1);

            if (!isOperator(currChar)) {
                int limit = StringUtils.firstIndexOf(expr, symbolOperators);
                if (limit == -1) {
                    limit = expr.length();
                }
                String token = stringUtils.firstToken(expr.substring(0, limit).trim(), tokens);
                tokens.remove(token);
                for (String element : token.split(",")) {
                    Expression e = new TerminalLogicalArithmeticExpression(element);
                    s.push(e);
                }
                expr = expr.substring(token.length()).trim();
            } else {
                Expression r;
                Expression l;
                try {
                    r = s.pop();
                } catch (java.util.EmptyStackException ese) {
                    throw new ExpressionException("There is no right element in the expression to evaluating for");
                }
                try {
                    l = s.pop();
                } catch (java.util.EmptyStackException ese) {
                    throw new ExpressionException("There is no left element in the expression to evaluating for");
                }
                Expression n = getNonTerminalExpression(currChar, l, r);
                s.push(n);
                expr = expr.substring(1).trim();
            }
        }
        expression = s.size() == 0 ? null : s.pop();
        return expression;
    }


    @Override
    public void setExpressionString(String expr) throws ExpressionException {
        //String regex = ".*[" + getFullOperationsRegex() + "].*";
        if (expr == null || !containsAnyOperator(expr, getOperations())) {
            throw new IllegalArgumentException("Incoming expression of '" + expr + "' is not parsable as a Math function type");
        }
        expressionString = expr;
        this.buildExpressionTree();
    }

    private String getFullOperationsRegex() {
        final StringBuilder result = new StringBuilder(super.getOperationsRegex());
        IterableUtils.forEach(Arrays.asList(MAX, MIN), result::append);
        return result.toString();
    }

    private List<Character> getFullOperations() {
        List<Character> result = super.getOperations();
        result.add(MAX);
        result.add(MIN);
        return result;
    }

    @Override
    protected List<Character> getOperations() {
        return Arrays.asList(MAX, MIN);
    }

    private boolean containsAnyOperator(String expression, List<Character> list) {
        for (Character operator : list) {
            if (expression.contains(String.valueOf(operator))) {
                return true;
            }
        }
        return false;
    }

}

