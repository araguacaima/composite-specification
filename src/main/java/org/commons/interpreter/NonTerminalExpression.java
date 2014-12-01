package org.commons.interpreter;

public interface NonTerminalExpression extends Expression {

    Expression getLeftNode();

    Expression getRightNode();
}
