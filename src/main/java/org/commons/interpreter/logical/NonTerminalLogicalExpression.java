package org.commons.interpreter.logical;

import org.commons.interpreter.Expression;
import org.commons.interpreter.NonTerminalExpression;

public abstract class NonTerminalLogicalExpression extends LogicalExpression implements NonTerminalExpression {
    private Expression leftNode;

    private Expression rightNode;

    public NonTerminalLogicalExpression(Expression l, Expression r) {
        this(false, l, r);
    }

    public NonTerminalLogicalExpression(boolean evaluateAllTerms, Expression l, Expression r) {
        super();
        setEvaluateAllTerms(evaluateAllTerms);
        setLeftNode(l);
        setRightNode(r);
    }

    public NonTerminalLogicalExpression(boolean value) {
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
