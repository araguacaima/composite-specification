package com.araguacaima.specification;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

public class SpecificationSpringConfig {

    public Reflections reflectionsModel() {
        return new Reflections("com.araguacaima.specification",
                new SubTypesScanner(false),
                new TypeAnnotationsScanner());
    }
}
