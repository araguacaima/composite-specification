package org.commons.interpreter;

import org.commons.interpreter.exception.ContextException;
import org.commons.interpreter.exception.ExpressionException;

public interface TerminalExpression {
    Expression evaluate(Context c) throws ExpressionException, ContextException;
}
