package com.araguacaima.specification;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.araguacaima.specification", "com.araguacaima.commons.utils"})
public class SpecificationSpringConfig {

    @Bean(name = "reflections")
    public Reflections reflectionsModel() {
        return new Reflections("com.araguacaima.specification",
                new SubTypesScanner(false),
                new TypeAnnotationsScanner());
    }
}
