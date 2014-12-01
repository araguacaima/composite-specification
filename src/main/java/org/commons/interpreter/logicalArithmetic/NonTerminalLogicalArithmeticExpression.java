package org.commons.interpreter.logicalArithmetic;

import org.commons.interpreter.Expression;
import org.commons.interpreter.NonTerminalExpression;

public abstract class NonTerminalLogicalArithmeticExpression extends LogicalArithmeticExpression
        implements NonTerminalExpression {
    private Expression leftNode;

    private Expression rightNode;

    public NonTerminalLogicalArithmeticExpression(Expression l, Expression r) {
        this(false, l, r);
    }

    public NonTerminalLogicalArithmeticExpression(boolean evaluateAllTerms, Expression l, Expression r) {
        super();
        setEvaluateAllTerms(evaluateAllTerms);
        setLeftNode(l);
        setRightNode(r);
    }

    public NonTerminalLogicalArithmeticExpression(double value) {
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
