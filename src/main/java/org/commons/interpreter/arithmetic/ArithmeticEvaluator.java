package org.commons.interpreter.arithmetic;

import org.commons.interpreter.Context;
import org.commons.interpreter.Evaluator;
import org.commons.interpreter.Expression;
import org.commons.interpreter.NonTerminalExpression;
import org.commons.interpreter.exception.ContextException;
import org.commons.interpreter.exception.ExpressionException;
import org.commons.interpreter.exception.InvalidExpressionException;
import org.commons.util.StringUtil;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.TransformerUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

public class ArithmeticEvaluator implements Evaluator {

    private String expression;
    private boolean evaluateAllTerms;
    private static HashMap operators = new HashMap();
    private Context ctx;

    public static final Character ADD = new Character('+');
    public static final Character SUB = new Character('-');
    public static final Character DIV = new Character('/');
    public static final Character MUL = new Character('*');
    public static final Character STARTING_PARENTHESIS = new Character('(');
    public static final Character CLOSING_PARENTHESIS = new Character(')');

    static {
        operators.put(ADD, "1");
        operators.put(SUB, "1");
        operators.put(DIV, "2");
        operators.put(MUL, "2");
        operators.put(STARTING_PARENTHESIS, "0");
        operators.put(CLOSING_PARENTHESIS, "0");
    }

    public ArithmeticEvaluator() {
        this(false);
    }

    public ArithmeticEvaluator(boolean evaluateAllTerms) {
        super();
        setEvaluateAllTerms(evaluateAllTerms);
    }

    public void setContext(Context c) {
        ctx = c;
    }

    public void setContext(final Map/*<String, String>*/ contextMap) {
        final ArithmeticContext c = new ArithmeticContext();
        if (contextMap != null) {
            CollectionUtils.forAllDo(contextMap.keySet(), new Closure() {
                public void execute(Object o) {
                    String key = (String) o;
                    c.assign(key, ((Double) contextMap.get(key)).doubleValue());
                }
            });
        }
        ctx = c;
    }

    public void addToContext(String key, String value) {
        ((ArithmeticContext) ctx).assign(key, (Double.valueOf(value)).doubleValue());
    }

    public void setExpression(String expr) {
        expression = expr;
    }

    public Double evaluate(Context c) throws ExpressionException, ContextException {
        setContext(c);
        return evaluate();
    }

    public double evaluate() throws ExpressionException, ContextException {

        //build the Binary Tree
        Expression rootNode = buildExpressionTree();

        if (rootNode != null && rootNode instanceof ArithmeticExpression) {
            //Evaluate the tree
            return ((Double) (rootNode.evaluate(ctx)).getValue()).doubleValue();
        } else {
            throw new InvalidExpressionException();
        }
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

    public Expression buildTree(String expr) {
        Stack s = new Stack();
        Collection /*<String>*/ symbolOperators = getOperators();
        CollectionUtils.transform(symbolOperators, TransformerUtils.invokerTransformer("toString"));
        Collection tokens = StringUtil.splitBySeparators(expression, symbolOperators);
        for (int i = 0; i < expr.length(); ) {
            String currChar = expr.substring(i, 1);

            if (!isOperator(currChar)) {
                int limit = StringUtil.firstIndexOf(expr, symbolOperators);
                if (limit == -1) {
                    limit = expr.length();
                }
                String token = StringUtil.firstToken(expr.substring(0, limit).trim(), tokens);
                tokens.remove(token);
                Expression e = new TerminalArithmeticExpression(token);
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
        return operators.containsKey(new Character(incoming.charAt(0)));
    }

    public String infixToPostFix(String str) {
        Stack s = new Stack();
        String pfExpr = "";
        String tempStr;
        Collection /*<String>*/symbolOperators = getOperators();
        CollectionUtils.transform(symbolOperators, TransformerUtils.invokerTransformer("toString"));
        if (!StringUtil.isNullOrEmpty(str)) {
            for (int i = 0; i < str.length(); ) {
                String currChar = str.substring(i, 1);
                if ((!isOperator(currChar)) && (!currChar.equals(STARTING_PARENTHESIS.toString())) && (!currChar.equals(
                        CLOSING_PARENTHESIS.toString()))) {
                    int limit = StringUtil.firstIndexOf(str, symbolOperators);
                    if (limit == -1) {
                        limit = str.length();
                    }
                    pfExpr = pfExpr + str.substring(0, limit).trim();
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
                        pfExpr = pfExpr + tempStr;
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
                        String strVal1 = (String) operators.get(new Character(tempStr.toCharArray()[0]));
                        int val1 = Integer.parseInt(strVal1);
                        String strVal2 = (String) operators.get(new Character(currChar.toCharArray()[0]));
                        int val2 = Integer.parseInt(strVal2);

                        while ((val1 >= val2)) {
                            pfExpr = pfExpr + tempStr;
                            val1 = -100;
                            if (!s.isEmpty()) {
                                tempStr = (String) s.pop();
                                strVal1 = (String) operators.get(new Character(tempStr.toCharArray()[0]));
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
            pfExpr = pfExpr + tempStr;
        }
        log.info(" Expression in postFix: " + pfExpr);
        return pfExpr;
    }

    public static Collection/*<String>*/ getOperators() {
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

    public static Map getFullOperators() {
        return operators;
    }

} // End of class

