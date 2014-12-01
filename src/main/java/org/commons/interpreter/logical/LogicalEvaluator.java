package org.commons.interpreter.logical;

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

public class LogicalEvaluator implements Evaluator {

    private String expression;
    private boolean evaluateAllTerms;
    private static HashMap operators = new HashMap();
    private Context ctx;

    public static final Character AND = new Character('&');
    public static final Character OR = new Character('|');
    public static final Character LE = new Character('≡');
    public static final Character EQ = new Character('=');
    public static final Character NOT = new Character('¬');
    public static final Character STARTING_PARENTHESIS = new Character('(');
    public static final Character CLOSING_PARENTHESIS = new Character(')');

    static {
        operators.put(AND, "1");
        operators.put(OR, "1");
        operators.put(LE, "1");
        operators.put(EQ, "1");
        operators.put(NOT, "2");
        operators.put(STARTING_PARENTHESIS, "0");
        operators.put(CLOSING_PARENTHESIS, "0");
    }

    public LogicalEvaluator() {
        this(false);
    }

    public LogicalEvaluator(boolean evaluateAllTerms) {
        super();
        setEvaluateAllTerms(evaluateAllTerms);
    }

    public void setContext(Context c) {
        ctx = c;
    }

    public void setContext(final Map/*<String, String>*/ contextMap) {
        final LogicalContext c = new LogicalContext();
        if (contextMap != null) {
            CollectionUtils.forAllDo(contextMap.keySet(), new Closure() {
                public void execute(Object o) {
                    String key = (String) o;
                    c.assign(key, ((Boolean) contextMap.get(key)).booleanValue());
                }
            });
        }
        ctx = c;
    }

    public void addToContext(String key, String value) {
        ((LogicalContext) ctx).assign(key, (Boolean.valueOf(value)).booleanValue());
    }

    public void setExpression(String expr) {
        expression = expr;
    }

    public boolean evaluate(Context c) throws ExpressionException, ContextException {
        setContext(c);
        return evaluate();
    }

    public boolean evaluate() throws ExpressionException, ContextException {
        //build the Binary Tree
        Expression rootNode = buildExpressionTree();

        if (rootNode != null && rootNode instanceof LogicalExpression) {
            //Evaluate the tree
            return ((Boolean) (rootNode.evaluate(ctx)).getValue()).booleanValue();

        } else {
            throw new InvalidExpressionException();
        }
    }

    public NonTerminalExpression getNonTerminalExpression(String operation, Expression l, Expression r) {
        String incomingOperation = operation.trim();
        if (incomingOperation.equals(AND.toString())) {
            return new AndExpression(getEvaluateAllTerms(), l, r);
        }
        if (incomingOperation.equals(OR.toString())) {
            return new OrExpression(getEvaluateAllTerms(), l, r);
        }
        if (incomingOperation.equals(LE.toString())) {
            return new LogicalEqExpression(getEvaluateAllTerms(), l, r);
        }
        if (incomingOperation.equals(NOT.toString())) {
            return new NotExpression(getEvaluateAllTerms(), r);
        }
        if (incomingOperation.equals(EQ.toString())) {
            return new EqualExpression(getEvaluateAllTerms(), l, r);
        }
        return null;
    }

    public Expression buildTree(String expr) throws ExpressionException {
        Stack s = new Stack();
        Collection /*<Character>*/symbolOperators = getOperators();
        CollectionUtils.transform(symbolOperators, TransformerUtils.invokerTransformer("toString"));
        Collection tokens = StringUtil.splitBySeparators(expression, symbolOperators);
        for (int i = 0; i < expr.length(); ) {
            String currChar = expr.substring(i, 1);

            if (isNot(currChar)) {
                Expression r = (Expression) s.pop();
                Expression n = getNonTerminalExpression(currChar, null, r);
                s.push(n);
                expr = expr.substring(1).trim();
            } else if (!isOperator(currChar)) {
                int limit = StringUtil.firstIndexOf(expr, symbolOperators);
                if (limit == -1) {
                    limit = expr.length();
                }
                String token = StringUtil.firstToken(expr.substring(0, limit).trim(), tokens);
                tokens.remove(token);
                Expression e = new TerminalLogicalExpression(getEvaluateAllTerms(), token);
                s.push(e);
                expr = expr.substring(token.length()).trim();
            } else {
                Expression r;
                Expression l;
                try {
                    r = (Expression) s.pop();
                } catch (java.util.EmptyStackException ese) {
                    throw new ExpressionException("There is no right element in the expression to evaluating for");
                }
                try {
                    l = (Expression) s.pop();
                } catch (java.util.EmptyStackException ese) {
                    throw new ExpressionException("There is no left element in the expression to evaluating for");
                }
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

    public boolean isNot(String str) {
        String incoming = str.trim();
        return incoming.equals(NOT.toString());

    }

    public String infixToPostFix(String input) {
        String str = input;
        Stack s = new Stack();
        String pfExpr = "";
        String tempStr;
        Collection /*<Character>*/symbolOperators = getOperators();
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
        return pfExpr;
    }

    public static Collection/*<Character>*/ getOperators() {
        return new HashSet(operators.keySet());
    }

    public static Map getFullOperators() {
        return operators;
    }

    public Expression buildExpressionTree() throws ExpressionException {
        String pfExpr = infixToPostFix(expression);
        return buildTree(pfExpr);
    }

    public Collection getTokens() throws ExpressionException {
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
