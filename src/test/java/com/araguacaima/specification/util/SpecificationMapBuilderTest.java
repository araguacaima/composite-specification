package com.araguacaima.specification.util;

import com.araguacaima.specification.Specification;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

public class SpecificationMapBuilderTest {
    private static final Logger log = LoggerFactory.getLogger(SpecificationMapBuilder.class);
    private final SpecificationMapBuilder specificationMapBuilder = new SpecificationMapBuilder();
    private SpecificationMap specificationMap;

    @Before
    public void setUp() {
        Map<String, String> map = new HashMap<>();
        map.put("com.araguacaima.specification.util.SpecificationMapBuilderTest.test1_1",
                "com.araguacaima.specification.AlwaysTrueSpec & com.araguacaima.specification.AlwaysFalseSpec");
        map.put("com.araguacaima.specification.util.SpecificationMapBuilderTest.test2_0",
                "com.araguacaima.specification.AlwaysTrueSpec");
        map.put("com.araguacaima.specification.util.SpecificationMapBuilderTest.test3",
                "(com.araguacaima.specification.AlwaysTrueSpec | com.araguacaima.specification.AlwaysFalseSpec) & com.araguacaima.specification.AlwaysTrueWhenOddNumberSpec");

        specificationMap = specificationMapBuilder.getInstance(map, SpecificationMapBuilderTest.class);
    }

    @Test
    public void testAlwaysResolveFalse()
            throws Exception {
        Specification specification = specificationMap.getSpecificationFromMethod("test1");
        assertNotNull(specification);
        log.info("specifications: " + specification);
        assertEquals(specification.toString().trim(),
                "(com.araguacaima.specification.AlwaysTrueSpec & com.araguacaima.specification.AlwaysFalseSpec)");
        boolean result = specification.isSatisfiedBy(null, null);
        log.info("result: " + result);
        assertFalse(result);
    }

    @Test
    public void testAlwaysResolveTrue()
            throws Exception {
        Specification specification = specificationMap.getSpecificationFromMethod("test2");
        assertNotNull(specification);
        log.info("specifications: " + specification);
        assertEquals(specification.toString().trim(), "com.araguacaima.specification.AlwaysTrueSpec");
        boolean result = specification.isSatisfiedBy(null, null);
        log.info("result: " + result);
        assertTrue(result);
    }

    @Test
    public void testResolveTrueIfOdd() throws Exception {
        Specification specification = specificationMap.getSpecificationFromMethod("test3");
        assertNotNull(specification);
        log.info("specifications: " + specification);
        assertEquals(specification.toString().trim(),
                "((com.araguacaima.specification.AlwaysTrueSpec | com.araguacaima.specification.AlwaysFalseSpec) & com.araguacaima.specification.AlwaysTrueWhenOddNumberSpec)");
        boolean result = specification.isSatisfiedBy(8, null);
        log.info("result: " + result);
        assertTrue(result);
    }

    @Test
    public void testResolveFalseDueIsNotNumber() throws Exception {
        Specification specification = specificationMap.getSpecificationFromMethod("test3");
        assertNotNull(specification);
        log.info("specifications: " + specification);
        assertEquals(specification.toString().trim(),
                "((com.araguacaima.specification.AlwaysTrueSpec | com.araguacaima.specification.AlwaysFalseSpec) & com.araguacaima.specification.AlwaysTrueWhenOddNumberSpec)");
        boolean result = specification.isSatisfiedBy("8", null);
        log.info("result: " + result);
        assertFalse(result);
    }

    @Test
    public void testResolveFalseDueIsEven()
            throws Exception {
        Specification specification = specificationMap.getSpecificationFromMethod("test3");
        assertNotNull(specification);
        log.info("specifications: " + specification);
        assertEquals(specification.toString().trim(),
                "((com.araguacaima.specification.AlwaysTrueSpec | com.araguacaima.specification.AlwaysFalseSpec) & com.araguacaima.specification.AlwaysTrueWhenOddNumberSpec)");
        boolean result = specification.isSatisfiedBy(7, null);
        log.info("result: " + result);
        assertFalse(result);
    }

}