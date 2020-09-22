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
    private SpecificationMap specificationMap;
    private final SpecificationMapBuilder specificationMapBuilder = new SpecificationMapBuilder();

    @Before
    public void setUp() {
        Map<String, String> map = new HashMap<>();
        map.put("com.araguacaima.specification.util.SpecificationMapBuilderTest.execute_1",
                "com.araguacaima.specification.AlwaysTrueSpec & com.araguacaima.specification.AlwaysFalseSpec");
        map.put("com.araguacaima.specification.util.SpecificationMapBuilderTest.test_0|3",
                "com.araguacaima.specification.AlwaysTrueSpec");
        map.put("com.araguacaima.specification.util.SpecificationMapBuilderTest.test1",
                "com.araguacaima.specification.RandomASpec & com.araguacaima.specification.RandomBSpec & com.araguacaima.specification.RandomCSpec");
        map.put("com.araguacaima.specification.util.SpecificationMapBuilderTest.test2",
                "com.araguacaima.specification.RandomASpec & com.araguacaima.specification.AlwaysTrueWhenOddNumberSpec | com.araguacaima.specification.AlwaysTrueSpec");
        map.put("com.araguacaima.specification.util.SpecificationMapBuilderTest.test3_1",
                "com.araguacaima.specification.RandomASpec & com.araguacaima.specification.RandomBSpec & com.araguacaima.specification.RandomCSpec");

        specificationMap = specificationMapBuilder.getInstance(map, SpecificationMapBuilderTest.class);
    }

    @Test
    public void testGetSpecificationFromMethod()
            throws Exception {
        Specification specification = specificationMap.getSpecificationFromMethod("execute");
        log.info("specifications: " + specification);
        assertNotNull(specification);
        assertEquals(specification.toString().trim(),
                "(com.araguacaima.specification.AlwaysTrueSpec & com.araguacaima.specification.AlwaysFalseSpec)");
        assertFalse(specification.isSatisfiedBy(new Object(), null));
    }

    @Test
    public void testGetSpecificationFromMethod2()
            throws Exception {
        Specification specification = specificationMap.getSpecificationFromMethod("test");
        log.info("specifications: " + specification);
        assertNotNull(specification);
        assertEquals(specification.toString().trim(), "com.araguacaima.specification.AlwaysTrueSpec");
        assertTrue(specification.isSatisfiedBy(new Object(), null));
    }

    @Test
    public void testGetSpecificationFromMethod3() throws Exception {
        Specification specification = specificationMap.getSpecificationFromMethod("test1");
        assertNotNull(specification);
        log.info("specifications: " + specification);
        boolean result = specification.isSatisfiedBy(null, null);
        log.info("result: " + result);
    }

    @Test
    public void testGetSpecificationFromMethod4() throws Exception {
        Specification specification = specificationMap.getSpecificationFromMethod("test3");
        assertNotNull(specification);
        log.info("specifications: " + specification);
        boolean result = specification.isSatisfiedBy(null, null);
        log.info("result: " + result);
    }

    @Test
    public void testGetSpecificationsMap()
            throws Exception {
        Map specifications = specificationMap.getSpecificationMap();
        log.info("specifications: " + specifications);
    }

}