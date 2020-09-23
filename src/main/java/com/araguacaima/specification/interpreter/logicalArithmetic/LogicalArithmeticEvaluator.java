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

package com.araguacaima.specification.interpreter.logicalArithmetic;

import com.araguacaima.commons.utils.StringUtils;
import com.araguacaima.specification.Specification;
import com.araguacaima.specification.interpreter.Context;
import com.araguacaima.specification.interpreter.Evaluator;
import com.araguacaima.specification.interpreter.Expression;
import com.araguacaima.specification.interpreter.NonTerminalExpression;
import com.araguacaima.specification.interpreter.arithmetic.ArithmeticEvaluator;
import com.araguacaima.specification.interpreter.exception.ContextException;
import com.araguacaima.specification.interpreter.exception.ExpressionException;
import com.araguacaima.specification.interpreter.exception.InvalidExpressionException;
import com.araguacaima.specification.interpreter.logical.LogicalEvaluator;
import com.araguacaima.specification.interpreter.logical.LogicalExpression;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.TransformerUtils;

import java.util.*;

public class LogicalArithmeticEvaluator<T> implements Evaluator {

    protected static final Character ADD = '+';
    protected static final Character AND = '&';
    protected static final Character CLOSING_PARENTHESIS = ')';
    protected static final Character DIV = '/';
    protected static final Character EQ = '=';
    protected static final Character GET = ']';
    protected static final Character GT = '>';
    protected static final Character LE = '≡';
    protected static final Character LET = '[';
    protected static final Character LT = '<';
    protected static final Character MUL = '*';
    protected static final Character NOT = '¬';
    protected static final Character OR = '|';
    protected static final Character STARTING_PARENTHESIS = '(';
    protected static final Character SUB = '-';
    protected static final HashMap<Character, Object> operators = new HashMap<>();

    static {
        operators.put(GT, "6");
        operators.put(LT, "6");
        operators.put(GET, "6");
        operators.put(LET, "6");
        operators.put(EQ, "5");
        operators.put(AND, "4");
        operators.put(OR, "4");
        operators.put(LE, "4");
        operators.put(NOT, "3");
        operators.put(ADD, "7");
        operators.put(SUB, "7");
        operators.put(DIV, "8");
        operators.put(MUL, "8");
        operators.put(STARTING_PARENTHESIS, "0");
        operators.put(CLOSING_PARENTHESIS, "0");
    }

    protected Context ctx;
    protected String expressionString;
    protected StringUtils stringUtils = StringUtils.getInstance();
    protected Expression expression;
    private boolean evaluateAllTerms;
    private final LogicalEvaluator logicalEvaluator;
    private int order = 0;

    public LogicalArithmeticEvaluator(LogicalEvaluator logicalEvaluator) {
        this(logicalEvaluator, false);
    }


    public LogicalArithmeticEvaluator(LogicalEvaluator logicalEvaluator, boolean evaluateAllTerms) {
        this.logicalEvaluator = logicalEvaluator;
        this.evaluateAllTerms = evaluateAllTerms;
    }

    private static HashSet<Character> getOperators() {
        return new HashSet<>(operators.keySet());
    }

    public void addParameterObjectToContext(String key, Object value) {
        ctx.assignParameterObject(key, value);
    }

    public void addToContext(String key, String value) {
        try {
            ((LogicalArithmeticContext) ctx).assign(key, Double.valueOf(value));
        } catch (NumberFormatException nfe) {

            Specification specification = null;
            try {
                specification = (Specification) Class.forName(value).newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                log.warn(" Class '" + value + "' does not corresponds to a Specification");
            }
            ctx.assign(key, specification);
        }
    }

    public T evaluate(Context c)
            throws ExpressionException, ContextException {
        setContext(c);
        return (T) evaluate();
    }

    private Boolean evaluate()
            throws ExpressionException, ContextException {

        //build the Binary Tree
        Expression rootNode = buildExpressionTree();

        if (rootNode instanceof LogicalExpression) {
            //Evaluate the tree
            return (Boolean) (rootNode.evaluate(ctx)).getValue();
        } else if (rootNode instanceof LogicalArithmeticExpression) {
            //Evaluate the tree
            return (Boolean) (rootNode.evaluate(ctx)).getCondition();
        } else {
            throw new InvalidExpressionException();
        }
    }

    protected Expression buildExpressionTree() throws ExpressionException {
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
        log.info("Expression in postFix: " + pfExpr);
        return pfExpr.toString();
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
                Expression e = new TerminalLogicalArithmeticExpression(token);
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
        if (operation.trim().equals(GT.toString())) {
            return new GreatherThanExpressionLogicalArithmetic(l, r);
        }
        if (operation.trim().equals(LT.toString())) {
            return new LessThanExpressionLogicalArithmetic(l, r);
        }
        if (operation.trim().equals(LET.toString())) {
            return new LessOrEqualThanExpressionLogicalArithmetic(l, r);
        }
        if (operation.trim().equals(GET.toString())) {
            return new GreatherOrEqualThanExpressionLogicalArithmetic(l, r);
        }
        logicalEvaluator.setEvaluateAllTerms(getEvaluateAllTerms());
        NonTerminalExpression nte = logicalEvaluator.getNonTerminalExpression(operation, l, r);
        if (nte != null) {
            return nte;
        } else {
            return new ArithmeticEvaluator(getEvaluateAllTerms()).getNonTerminalExpression(operation, l, r);
        }
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

    public Context getContext() {
        return ctx;
    }

    public void setContext(Context c) {
        ctx = c;
    }

    public void setContext(final Map<String, Object> contextMap) {
        final LogicalArithmeticContext c = new LogicalArithmeticContext();
        if (contextMap != null) {
            IterableUtils.forEach(contextMap.keySet(), o -> c.assign(o, (Double) contextMap.get(o)));
        }
        ctx = c;
    }

    public String getExpressionString() {
        return expressionString;
    }

    @Override
    public void setExpressionString(String expr) throws ExpressionException {
        //String regex = ".*[" + getFullOperationsRegex() + "].*";
        if (expr == null || !containsAnyOperator(expr, getOperations())) {
            throw new IllegalArgumentException("Incoming expression of '" + expr + "' is not parsable as a Logical Arithmetical type");
        }
        expressionString = expr;
        this.buildExpressionTree();
    }

    protected String getOperationsRegex() {
        final StringBuilder result = new StringBuilder();
        IterableUtils.forEach(Arrays.asList("\\", "\\", ADD, AND, DIV, EQ, GET, GT, LE, "\\", "\\", LET, LT, "\\", "\\", MUL, NOT, "\\", "\\", OR, "\\", "\\", SUB), result::append);
        return result.toString();
    }

    public Collection getTokens() {
        return expression == null ? new ArrayList() : expression.getTerms();
    }

    protected List<Character> getOperations() {
        return Arrays.asList(ADD, AND, DIV, EQ, GET, GT, LE, LET, LT, MUL, NOT, OR, SUB);
    }

    private boolean containsAnyOperator(String expression, List<Character> list) {
        for (Character operator : list) {
            if (expression.contains(String.valueOf(operator))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }
} // End of class

