package org.commons.interpreter.arithmetic;

import org.commons.interpreter.Expression;
import org.commons.interpreter.NonTerminalExpression;

public abstract class NonTerminalArithmeticExpression extends ArithmeticExpression implements NonTerminalExpression {
    private Expression leftNode;

    private Expression rightNode;

    public NonTerminalArithmeticExpression(Expression l, Expression r) {
        this(false, l, r);
    }

    public NonTerminalArithmeticExpression(boolean evaluateAllTerms, Expression l, Expression r) {
        super();
        setEvaluateAllTerms(evaluateAllTerms);
        setLeftNode(l);
        setRightNode(r);
    }

    public NonTerminalArithmeticExpression(double value) {
        super(value);
    }

    public void setLeftNode(Expression node) {
        leftNode = node;
    }

    public void setRightNode(Expression node) {
        rightNode = node;
    }

    public Expression getLeftNode() {
        return leftNode;
    }

    public Expression getRightNode() {
        return rightNode;
    }
}// NonTerminalExpression
