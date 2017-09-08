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
import org.araguacaima.specification.interpreter.Evaluator;
import org.araguacaima.specification.interpreter.Expression;
import org.araguacaima.specification.interpreter.NonTerminalExpression;
import org.araguacaima.specification.interpreter.arithmetic.ArithmeticEvaluator;
import org.araguacaima.specification.interpreter.exception.ContextException;
import org.araguacaima.specification.interpreter.exception.ExpressionException;
import org.araguacaima.specification.interpreter.exception.InvalidExpressionException;
import org.araguacaima.specification.interpreter.logical.LogicalEvaluator;
import org.araguacaima.specification.interpreter.logical.LogicalExpression;
import org.araguacaima.specification.Specification;
import org.araguacaima.commons.utils.StringUtils;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.TransformerUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

public class LogicalArithmeticEvaluator implements Evaluator {

    private String expression;
    private boolean evaluateAllTerms;
    private static final HashMap operators = new HashMap();
    private Context ctx;

    private static final Character GT = '>';
    private static final Character LT = '<';
    private static final Character GET = ']';
    private static final Character LET = '[';
    private static final Character EQ = '=';
    private static final Character AND = '&';
    private static final Character OR = '|';
    private static final Character LE = '≡';
    private static final Character NOT = '¬';
    private static final Character ADD = '+';
    private static final Character SUB = '-';
    private static final Character DIV = '/';
    private static final Character MUL = '*';
    private static final Character STARTING_PARENTHESIS = '(';
    private static final Character CLOSING_PARENTHESIS = ')';

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

    public LogicalArithmeticEvaluator() {
        this(false);
    }

    public LogicalArithmeticEvaluator(boolean evaluateAllTerms) {
        super();
        setEvaluateAllTerms(evaluateAllTerms);
    }

    public void setContext(Context c) {
        ctx = c;
    }

    public void setContext(final Map<String, String> contextMap) {
        final LogicalArithmeticContext c = new LogicalArithmeticContext();
        if (contextMap != null) {
            CollectionUtils.forAllDo(contextMap.keySet(), o -> {
                String key = (String) o;
                c.assign(key, (Double) contextMap.get(key));
            });
        }
        ctx = c;
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

    public void addParameterObjectToContext(String key, Object value) {
        ctx.assignParameterObject(key, value);
    }

    public void setExpression(String expr) {
        expression = expr;
    }

    public boolean evaluate(Context c) throws ExpressionException, ContextException {
        setContext(c);
        return evaluate();
    }

    private Boolean evaluate() throws ExpressionException, ContextException {

        //build the Binary Tree
        Expression rootNode = buildExpressionTree();

        if (rootNode != null && rootNode instanceof LogicalExpression) {
            //Evaluate the tree
            return (Boolean) (rootNode.evaluate(ctx)).getValue();
        } else if (rootNode != null && rootNode instanceof LogicalArithmeticExpression) {
            //Evaluate the tree
            return (Boolean) (rootNode.evaluate(ctx)).getCondition();
        } else {
            throw new InvalidExpressionException();
        }
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

        NonTerminalExpression nte = new LogicalEvaluator(getEvaluateAllTerms()).getNonTerminalExpression(operation,
                l,
                r);
        if (nte != null) {
            return nte;
        } else {
            return new ArithmeticEvaluator(getEvaluateAllTerms()).getNonTerminalExpression(operation, l, r);
        }
    }

    public Expression buildTree(String expr) {
        Stack s = new Stack();
        Collection <String> symbolOperators = getOperators();
        CollectionUtils.transform(symbolOperators, TransformerUtils.invokerTransformer("toString"));
        Collection tokens = StringUtils.splitBySeparators(expression, symbolOperators);
        for (int i = 0; i < expr.length(); ) {
            String currChar = expr.substring(i, 1);

            if (!isOperator(currChar)) {
                int limit = StringUtils.firstIndexOf(expr, symbolOperators);
                if (limit == -1) {
                    limit = expr.length();
                }
                String token = StringUtils.firstToken(expr.substring(0, limit).trim(), tokens);
                tokens.remove(token);
                Expression e = new TerminalLogicalArithmeticExpression(token);
                s.push(e);
                expr = expr.substring(token.length()).trim();
            } else {
                Expression r = (Expression) s.pop();
                Expression l = (Expression) s.pop();
                Expression n = getNonTerminalExpression(currChar, l, r);
                s.push(n);
                expr = expr.substring(1).trim();
            }
        }//for
        return s.size() == 0
                ? null
                : (Expression) s.pop();
    }

    public boolean isOperator(String str) {
        String incoming = str.trim();
        return operators.containsKey(incoming.charAt(0));
    }

    public String infixToPostFix(String str) {
        Stack s = new Stack();
        StringBuilder pfExpr = new StringBuilder();
        String tempStr;
        Collection <String>symbolOperators = getOperators();
        CollectionUtils.transform(symbolOperators, TransformerUtils.invokerTransformer("toString"));
        if (!StringUtils.isNullOrEmpty(str)) {
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
                    tempStr = (String) s.pop();
                    while (!tempStr.equals(STARTING_PARENTHESIS.toString())) {
                        pfExpr.append(tempStr);
                        tempStr = (String) s.pop();
                    }
                    str = str.substring(1).trim();
                    continue;
                }
                //if the current character is an
                // operator
                if (isOperator(currChar)) {
                    if (!s.isEmpty()) {
                        tempStr = (String) s.pop();
                        String strVal1 = (String) operators.get(tempStr.toCharArray()[0]);
                        int val1 = Integer.parseInt(strVal1);
                        String strVal2 = (String) operators.get(currChar.toCharArray()[0]);
                        int val2 = Integer.parseInt(strVal2);

                        while ((val1 >= val2)) {
                            pfExpr.append(tempStr);
                            val1 = -100;
                            if (!s.isEmpty()) {
                                tempStr = (String) s.pop();
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
            tempStr = (String) s.pop();
            pfExpr.append(tempStr);
        }
        log.info("Expression in postFix: " + pfExpr);
        return pfExpr.toString();
    }

    private static Collection<String> getOperators() {
        return new HashSet(operators.keySet());
    }

    public Expression buildExpressionTree() {
        String pfExpr = infixToPostFix(expression);
        return buildTree(pfExpr);
    }

    public Collection getTokens() {
        Expression exp = buildExpressionTree();
        return exp == null
                ? new ArrayList()
                : exp.getTerms();
    }

    public String getExpression() {
        return expression;
    }

    public Context getContext() {
        return ctx;
    }

    public boolean getEvaluateAllTerms() {
        return evaluateAllTerms;
    }

    public void setEvaluateAllTerms(boolean evaluateAllTerms) {
        this.evaluateAllTerms = evaluateAllTerms;
    }

} // End of class

