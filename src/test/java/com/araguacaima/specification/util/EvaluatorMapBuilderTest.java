package com.araguacaima.specification.util;

import com.araguacaima.specification.interpreter.Context;
import com.araguacaima.specification.interpreter.Evaluator;
import com.araguacaima.specification.interpreter.Expression;
import com.araguacaima.specification.interpreter.mathFunctions.MathFunctionContext;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public class EvaluatorMapBuilderTest {
    private static Logger log = LoggerFactory.getLogger(SpecificationMapBuilder.class);
    private Evaluator evaluator;

    @Before
    public void setUp() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("test1", "¡(a,b,c)-!(d,e)");
        EvaluatorMapBuilder specificationMapBuilder = new EvaluatorMapBuilder(map);
        evaluator = specificationMapBuilder.getEvaluator("test1");
    }

    @Test
    public void testGetSpecificationFromMethod()
            throws Exception {
        Context context = new MathFunctionContext();
        context.assignParameterObject("a", 24.5);
        context.assignParameterObject("b", 12);
        context.assignParameterObject("c", -3);
        context.assignParameterObject("d", 0);
        context.assignParameterObject("e", -13);
        Expression expression = evaluator.evaluate(context);
        assertNotNull(expression);
        double value = (Double) expression.getValue();
        assertEquals(value, 37.5, 0);
    }

}