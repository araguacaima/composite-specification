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

package com.araguacaima.specification.interpreter.arithmetic;

import com.araguacaima.commons.utils.StringUtils;
import com.araguacaima.specification.interpreter.Context;
import com.araguacaima.specification.interpreter.Evaluator;
import com.araguacaima.specification.interpreter.Expression;
import com.araguacaima.specification.interpreter.NonTerminalExpression;
import com.araguacaima.specification.interpreter.exception.ContextException;
import com.araguacaima.specification.interpreter.exception.ExpressionException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.TransformerUtils;

import java.util.*;

public class ArithmeticEvaluator implements Evaluator {

    private static final Character ADD = '+';
    private static final Character CLOSING_PARENTHESIS = ')';
    private static final Character DIV = '/';
    private static final Character MUL = '*';
    private static final Character STARTING_PARENTHESIS = '(';
    private static final Character SUB = '-';
    private static final HashMap<Character, Object> operators = new HashMap<>();

    static {
        operators.put(ADD, "1");
        operators.put(SUB, "1");
        operators.put(DIV, "2");
        operators.put(MUL, "2");
        operators.put(STARTING_PARENTHESIS, "0");
        operators.put(CLOSING_PARENTHESIS, "0");
    }

    private Context ctx;
    private boolean evaluateAllTerms;
    private String expressionString;
    private final StringUtils stringUtils = StringUtils.getInstance();
    private int order = 0;
    private Expression expression;

    public ArithmeticEvaluator() {
        this(false);
    }

    public ArithmeticEvaluator(boolean evaluateAllTerms) {
        super();
        setEvaluateAllTerms(evaluateAllTerms);
    }

    public static Map getFullOperators() {
        return operators;
    }

    private static HashSet<Character> getOperators() {
        return new HashSet<>(operators.keySet());
    }

    public void addToContext(String key, String value) {
        ((ArithmeticContext) ctx).assign(key, Double.valueOf(value));
    }

    @Override
    public Double evaluate(Context c) throws ExpressionException, ContextException {
        setContext(c);
        return evaluate();
    }

    private double evaluate() throws ExpressionException, ContextException {
        return (Double) (expression.evaluate(ctx)).getValue();
    }

    private Expression buildExpressionTree() throws ExpressionException {
        String pfExpr = infixToPostFix(expressionString);
        return buildTree(pfExpr);
    }

    public String infixToPostFix(String str) {
        Stack<String> s = new Stack<>();
        StringBuilder pfExpr = new StringBuilder();
        String tempStr;
        Collection<Character> symbolOperators = getOperators();
        CollectionUtils.transform(symbolOperators, TransformerUtils.invokerTransformer("toString"));
        if (!StringUtils.isBlank(str)) {
            for (int i = 0; i < str.length(); ) {
                String currChar = str.substring(i, 1);
                if ((!isOperator(currChar)) && (!currChar.equals(STARTING_PARENTHESIS.toString())) && (!currChar.equals(
                        CLOSING_PARENTHESIS.toString()))) {
                    int limit = StringUtils.firstIndexOf(str, symbolOperators);
                    if (limit == -1) {
                        limit = str.length();
                    }
                    pfExpr.append(str.substring(0, limit).trim());
                    str = str.substring(limit).trim();
                    continue;
                }
                if (currChar.equals(STARTING_PARENTHESIS.toString())) {
                    s.push(currChar);
                    str = str.substring(1).trim();
                    continue;
                }
                //for ')' pop all stack contents until '('
                if (currChar.equals(CLOSING_PARENTHESIS.toString())) {
                    tempStr = s.pop();
                    while (!tempStr.equals(STARTING_PARENTHESIS.toString())) {
                        pfExpr.append(tempStr);
                        tempStr = s.pop();
                    }
                    str = str.substring(1).trim();
                    continue;
                }
                //if the current character is an
                // operator
                if (isOperator(currChar)) {
                    if (!s.isEmpty()) {
                        tempStr = s.pop();
                        String strVal1 = (String) operators.get(tempStr.toCharArray()[0]);
                        int val1 = Integer.parseInt(strVal1);
                        String strVal2 = (String) operators.get(currChar.toCharArray()[0]);
                        int val2 = Integer.parseInt(strVal2);

                        while ((val1 >= val2)) {
                            pfExpr.append(tempStr);
                            val1 = -100;
                            if (!s.isEmpty()) {
                                tempStr = s.pop();
                                strVal1 = (String) operators.get(tempStr.toCharArray()[0]);
                                val1 = Integer.parseInt(strVal1);
                            }
                        }
                        if ((val1 < val2) && (val1 != -100)) {
                            s.push(tempStr);
                        }
                    }
                    str = str.substring(1).trim();
                    s.push(currChar);
                }//if

            }// for
        }
        while (!s.isEmpty()) {
            tempStr = s.pop();
            pfExpr.append(tempStr);
        }
        log.info(" Expression in postFix: " + pfExpr);
        return pfExpr.toString();
    }

    public Expression buildTree(String expr) throws ExpressionException {
        Stack<Expression> s = new Stack<>();
        Collection<Character> symbolOperators = getOperators();
        CollectionUtils.transform(symbolOperators, TransformerUtils.invokerTransformer("toString"));
        Collection tokens = stringUtils.splitBySeparators(expressionString, symbolOperators);
        for (int i = 0; i < expr.length(); ) {
            String currChar = expr.substring(i, 1);
            if (!isOperator(currChar)) {
                int limit = StringUtils.firstIndexOf(expr, symbolOperators);
                if (limit == -1) {
                    limit = expr.length();
                }
                String token = stringUtils.firstToken(expr.substring(0, limit).trim(), tokens);
                tokens.remove(token);
                Expression e = new TerminalArithmeticExpression(token);
                s.push(e);
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

    public boolean isOperator(String str) {
        String incoming = str.trim();
        return operators.containsKey(incoming.charAt(0));
    }

    public NonTerminalExpression getNonTerminalExpression(String operation, Expression l, Expression r) {
        if (operation.trim().equals(ADD.toString())) {
            return new AddExpression(l, r);
        }
        if (operation.trim().equals(SUB.toString())) {
            return new SubtractExpression(l, r);
        }
        if (operation.trim().equals(MUL.toString())) {
            return new MultiplyExpression(l, r);
        }
        if (operation.trim().equals(DIV.toString())) {
            return new DivideExpression(l, r);
        }
        return null;
    }

    public Context getContext() {
        return ctx;
    }

    public void setContext(Context c) {
        ctx = c;
    }

    public void setContext(final Map<String, Object> contextMap) {
        final ArithmeticContext c = new ArithmeticContext();
        if (contextMap != null) {
            IterableUtils.forEach(contextMap.keySet(), o -> c.assign(o, (Double) contextMap.get(o)));
        }
        ctx = c;
    }

    public boolean getEvaluateAllTerms() {
        return evaluateAllTerms;
    }

    public void setEvaluateAllTerms(boolean evaluateAllTerms) {
        this.evaluateAllTerms = evaluateAllTerms;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    public String getExpressionString() {
        return expressionString;
    }

    @Override
    public void setExpressionString(String expr) throws ExpressionException {
        //String regex = ".*[" + getFullOperationsRegex() + "].*";
        if (expr == null || !containsAnyOperator(expr, getOperations())) {
            throw new IllegalArgumentException("Incoming expression of '" + expr + "' is not parsable as a Arithmetic type");
        }
        expressionString = expr;
        buildExpressionTree();
    }

    protected List<Character> getOperations() {
        return Arrays.asList(ADD, DIV, MUL, SUB);
    }

    private boolean containsAnyOperator(String expression, List<Character> list) {
        for (Character operator : list) {
            if (expression.contains(String.valueOf(operator))) {
                return true;
            }
        }
        return false;
    }

    private String getOperationsRegex() {
        final StringBuilder result = new StringBuilder();
        IterableUtils.forEach(Arrays.asList("\\", "\\", ADD, "\\", "\\", SUB, DIV, "\\", "\\", MUL), result::append);
        return result.toString();
    }

    public Collection getTokens() {
        return expression == null ? new ArrayList() : expression.getTerms();
    }

    @Override
    public Expression getExpression() {
        return expression;
    }
}

