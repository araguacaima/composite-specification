package com.araguacaima.specification.util;

import com.araguacaima.specification.interpreter.Evaluator;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class EvaluatorMapBuilderTest {
    private static final Logger log = LoggerFactory.getLogger(SpecificationMapBuilder.class);
    private Evaluator evaluator;

    @Before
    public void setUp() {
        Map<Object, Object> map = new HashMap<>();
        map.put("test1", "¡(a,b,c)-!(d,e)");
        EvaluatorMapBuilder specificationMapBuilder = new EvaluatorMapBuilder(map);
        evaluator = specificationMapBuilder.getEvaluator("test1");
    }

/*    @Test
    public void testGetSpecificationFromMethod() throws Exception {
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
    }*/

}