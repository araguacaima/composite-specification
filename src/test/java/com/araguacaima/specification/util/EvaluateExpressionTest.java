package com.araguacaima.specification.util;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EvaluateExpressionTest {

    @Before
    public void setUp() {

    }

    @Test
    public void testEval() {
        Map<String, Double> variables = new HashMap<>();
        variables.put("a1", 10D);
        variables.put("b", 20D);
        variables.put("c", 30D);
        variables.put("d", 40D);
        variables.put("elementA", 50D);
        double eval = EvaluateExpression.buildExpression("max(a1,b,c)-min(d,elementA)", variables).eval();
        System.out.println(eval);
        assertEquals(-10D, eval, 0);
        variables.put("a1", 50D);
        variables.put("b", 40D);
        variables.put("c", 30D);
        variables.put("d", 20D);
        variables.put("elementA", 10D);
        eval = EvaluateExpression.buildExpression("max(a1,b,c)-min(d,elementA)", variables).eval();
        System.out.println(eval);
        assertEquals(40D, eval, 0);
    }

}