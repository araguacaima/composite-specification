package org.commons.interpreter.logical;

import org.commons.interpreter.Context;
import org.commons.interpreter.Expression;
import org.commons.interpreter.exception.ContextException;
import org.commons.interpreter.exception.ExpressionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class LogicalEqExpression extends NonTerminalLogicalExpression {
    public Expression evaluate(Context c) throws ExpressionException, ContextException {

        boolean resutl1 = ((Boolean) getLeftNode().evaluate(c).getValue()).booleanValue();
        boolean resutl2 = ((Boolean) getRightNode().evaluate(c).getValue()).booleanValue();
        return new LogicalExpressionImpl((resutl1 && resutl2) || (!resutl1 && !resutl2));

    }

    public Collection getTerms() {
        Collection terms = new ArrayList();
        terms.addAll(getLeftNode().getTerms());
        terms.addAll(getRightNode().getTerms());
        return new HashSet(terms);
    }

    public Object getValue() {
        return null;
    }

    public Object getCondition() {
        return null;
    }

    public LogicalEqExpression(Expression l, Expression r) {
        this(false, l, r);
    }

    public LogicalEqExpression(boolean evaluateAllTerms, Expression l, Expression r) {
        super(evaluateAllTerms, l, r);
    }
}// LogicalEqExpression