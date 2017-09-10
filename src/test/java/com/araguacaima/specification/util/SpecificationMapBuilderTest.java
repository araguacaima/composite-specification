package com.araguacaima.specification.util;

import com.araguacaima.specification.Specification;
import com.araguacaima.specification.SpecificationSpringConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpecificationSpringConfig.class})
public class SpecificationMapBuilderTest {
    private static Logger log = LoggerFactory.getLogger(SpecificationMapBuilder.class);
    private SpecificationMap specificationMap;
    @Autowired
    private SpecificationMapBuilder specificationMapBuilder;

    @Before
    public void setUp() {
        Map<String, String> map = new HashMap<>();
        map.put("com.araguacaima.specification.util.SpecificationMapBuilderTest.execute_1",
                "com.araguacaima.specification.AlwaysTrueSpec & com.araguacaima.specification.AlwaysFalseSpec");
        map.put("com.araguacaima.specification.util.SpecificationMapBuilderTest.tal_0",
                "com.araguacaima.specification.AlwaysTrueSpec");
        map.put("com.araguacaima.specification.util.SpecificationMapBuilderTest.tal2", null);
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
        Specification specification = specificationMap.getSpecificationFromMethod("tal");
        log.info("specifications: " + specification);
        assertNotNull(specification);
        assertEquals(specification.toString().trim(), "com.araguacaima.specification.AlwaysTrueSpec");
        assertTrue(specification.isSatisfiedBy(new Object(), null));
    }

    @Test
    public void testGetSpecificationFromMethod3()
            throws Exception {
        Specification specification = specificationMap.getSpecificationFromMethod("tal2");
        log.info("specifications: " + specification);
        assertNull(specification);
    }

    @Test
    public void testGetSpecificationsMap()
            throws Exception {
        Map specifications = specificationMap.getSpecificationsMap();
        log.info("specifications: " + specifications);
    }

}