/*
 * Copyright 2017 araguacaima
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.araguacaima.specification.util;

import com.araguacaima.commons.utils.MapUtils;
import com.araguacaima.commons.utils.NotNullOrEmptyStringObjectPredicate;
import com.araguacaima.commons.utils.StringUtils;
import com.araguacaima.specification.AbstractSpecificationImpl;
import com.araguacaima.specification.Specification;
import com.araguacaima.specification.interpreter.Expression;
import com.araguacaima.specification.interpreter.NonTerminalExpression;
import com.araguacaima.specification.interpreter.TerminalExpression;
import com.araguacaima.specification.interpreter.exception.ExpressionException;
import com.araguacaima.specification.interpreter.logical.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE,
       proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SpecificationMap {

    private static final Logger log = LoggerFactory.getLogger(SpecificationMap.class);
    private final Map<String, Specification> specificationMap = new HashMap<>();
    private String className;
    private MapUtils mapUtils;
    private NotNullOrEmptyStringObjectPredicate notNullOrEmptyStringObjectPredicate;
    private Properties properties = new Properties();

    private SpecificationMap() {

    }

    @Autowired
    private SpecificationMap(NotNullOrEmptyStringObjectPredicate notNullOrEmptyStringObjectPredicate,
                             MapUtils mapUtils) {
        this.notNullOrEmptyStringObjectPredicate = notNullOrEmptyStringObjectPredicate;
        this.mapUtils = mapUtils;
    }

    public SpecificationMap(Class clazz, Properties properties, ClassLoader classLoader) {
        this(clazz.getName(), properties, classLoader);
    }

    private SpecificationMap(String className, Properties properties, ClassLoader classLoader) {
        this.properties = properties;
        this.className = className;
        buildsSpecificationMap(classLoader);
    }

    public void addProperties(Properties properties) {
        this.properties.putAll(properties);
    }

    private Specification buildSpecification(boolean evaluateAllTerms, String expression, ClassLoader classLoader)
            throws ExpressionException {
        Specification result = new AbstractSpecificationImpl(evaluateAllTerms);
        LogicalEvaluator logicalEvaluator = new LogicalEvaluator(evaluateAllTerms);
        logicalEvaluator.setExpression(expression);
        return buildSpecificationFromExpression(logicalEvaluator.buildExpressionTree(), result, classLoader);
    }

    private Specification buildSpecificationFromExpression(Expression node, Specification spec, ClassLoader classLoader)
            throws ExpressionException {
        if (node != null) {
            if (node instanceof TerminalExpression) {
                String nodeclassName = (String) node.getValue();
                try {
                    Class<Specification> clazz = (Class<Specification>) classLoader.loadClass(nodeclassName);
                    try {
                        spec = clazz.getConstructor(new Class[]{Boolean.TYPE}).newInstance(new Object[]{spec
                                .getEvaluateAllTerms()});
                    } catch (InvocationTargetException e) {
                        String message = "The class '" + className + "' exists but, it was not possible to " +
                                "instantiate a concrete object " + "for it because of an Exception of type '" + e
                                .getClass().getName() + "'. The Constructor of boolean parameter threw an uncaught "
                                + "Exception " + " The associated message for this Exception is: " + e.getMessage();
                        log.error(message);
                    } catch (NoSuchMethodException e) {
                        try {
                            spec = (Specification) clazz.getSuperclass().getConstructor(new Class[]{Boolean.TYPE})
                                    .newInstance(
                                    new Object[]{spec.getEvaluateAllTerms()});
                        } catch (InvocationTargetException e1) {
                            String message = "The class '" + className + "' exists but, it was not possible to " +
                                    "instantiate a concrete object " + "for it because of an Exception of type '" + e
                                    .getClass().getName() + "'. The class that declares the underlying constructor "
                                    + "represents an abstract class. " + " The associated message for this Exception " +
                                    "" + "" + "" + "is: " + e.getMessage();
                            log.error(message);
                        } catch (NoSuchMethodException e2) {
                            spec = clazz.newInstance();
                        }
                    }
                } catch (ClassNotFoundException e) {
                    try {
                        return buildSpecificationFromExpression(node,
                                spec,
                                Class.forName(nodeclassName).getClassLoader().getParent());
                    } catch (ClassNotFoundException e1) {
                        try {
                            ClassLoader classLoader1 = Class.forName(className).getClassLoader();
                            if (classLoader1.equals(classLoader)) {
                                return null;
                            } else {
                                return buildSpecificationFromExpression(node, spec, classLoader1);
                            }
                        } catch (ClassNotFoundException e2) {
                            return null;
                        }
                    }

                } catch (InstantiationException e) {
                    String message = "The class '" + className + "' exists but, it was not possible to instantiate a " +
                            "" + "" + "" + "concrete object " + "for it because of an Exception of type '" + e
                            .getClass().getName() + "'. The class that declares the underlying constructor represents" +
                            " an " + "abstract class. " + " The associated message for this Exception is: " + e
                            .getMessage();
                    log.error(message);
                    throw new ExpressionException(message);
                } catch (IllegalAccessException e) {
                    String message = "It was not possible to instantiate a concrete object for class " + className +
                            "' because of an Exception of type '" + e.getClass().getName() + "'. This class is not "
                            + "accesible by Java. The associated message for this Exception is: " + e.getMessage();
                    log.error(message);
                    throw new ExpressionException(message);
                }
            } else if (node instanceof NonTerminalExpression) {
                if (node instanceof AndExpression) {
                    spec = buildSpecificationFromExpression(node.getLeftNode(), spec, classLoader).and(
                            buildSpecificationFromExpression(node.getRightNode(), spec, classLoader));
                } else if (node instanceof NotExpression) {
                    spec = spec.not(buildSpecificationFromExpression(node.getRightNode(), spec, classLoader));
                } else if (node instanceof OrExpression) {
                    spec = buildSpecificationFromExpression(node.getLeftNode(), spec, classLoader).or(
                            buildSpecificationFromExpression(node.getRightNode(), spec, classLoader));
                } else if (node instanceof LogicalEqExpression) {
                    spec = buildSpecificationFromExpression(node.getLeftNode(), spec, classLoader).logicalEq(
                            buildSpecificationFromExpression(node.getRightNode(), spec, classLoader));
                }
            }
        } else {
            return null;
        }
        return spec;
    }

    private void buildsSpecificationMap(final ClassLoader classLoader) {
        Collection<Object> classes = new ArrayList<>(properties.keySet());

        CollectionUtils.transform(classes, o -> {
            String key = ((String) o);
            return key.substring(0, key.lastIndexOf("."));
        });
        Set<Object> classesSet = new HashSet<Object>(classes);
        final Map propertiesByClass = new HashMap();
        CollectionUtils.forAllDo(classesSet, o -> {
            final String className = (String) o;
            propertiesByClass.putAll(mapUtils.select(properties,
                    o1 -> ((String) o1).startsWith(className),
                    notNullOrEmptyStringObjectPredicate));
        });
        Collection methods = new ArrayList(propertiesByClass.keySet());
        CollectionUtils.transform(methods, o -> {
            String key = ((String) o);
            return key.substring(key.lastIndexOf(".") + 1);
        });
        Set methodsFiltered = new HashSet(methods);
        CollectionUtils.forAllDo(methodsFiltered, o -> {
            String methodName = (String) o;
            boolean evaluateAllTerms;
            String[] tokens = methodName.split("_");
            if (tokens.length == 2) {
                methodName = tokens[0];
                evaluateAllTerms = tokens[1].equals("1");
            } else {
                evaluateAllTerms = false;
            }
            try {
                String property = (String) properties.get(className + "." + o);
                if (StringUtils.isNotBlank(property)) {
                    specificationMap.put(methodName, buildSpecification(evaluateAllTerms, property, classLoader));

                }
            } catch (Exception ignored) {
            }
        });

    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Specification getSpecificationFromMethod(String method) {
        return getSpecificationsMap().get(method);
    }

    public Map<String, Specification> getSpecificationsMap() {
        return getSpecificationMap();
    }

    private Map<String, Specification> getSpecificationMap() {
        return specificationMap;
    }

    public Collection getTermsByMethod(String methodName) {
        return getTermsByMethod(methodName, false);
    }

    private Collection getTermsByMethod(String methodName, boolean evaluateAllTerms) {
        String expression = (String) properties.get(className + "." + methodName);
        LogicalEvaluator logicalEvaluator = new LogicalEvaluator(evaluateAllTerms);
        logicalEvaluator.setExpression(expression);
        Collection tokens = new ArrayList();

        try {
            tokens = logicalEvaluator.getTokens();
        } catch (ExpressionException e) {
            e.printStackTrace();
            return tokens;
        }

        CollectionUtils.transform(tokens, o -> {
            String tokenClassName = (String) o;
            try {
                Class clazz = Class.forName(tokenClassName);
                return clazz.newInstance();
            } catch (ClassNotFoundException e) {
                String message = "It was not possible to find class '" + className + "' because of an Exception of "
                        + "type '" + e.getClass().getName() + "'. Does that class really exists and its reacheable "
                        + "by" + " " + "the current classloader?." + " The associated message for this Exception is: " +
                        "" + e.getMessage();
                log.error(message);
            } catch (InstantiationException e) {
                String message = "It was not possible to instantiate a concrete object for class " + className + "' "
                        + "because of an Exception of type '" + e.getClass().getName() + "'. Probably this class " +
                        "represents an abstract class, an interface, an array " + "class, a primitive type, or void "
                        + "or has no nullary constructor." + " The associated message for this Exception is: " + e
                        .getMessage();
                log.error(message);
            } catch (IllegalAccessException e) {
                String message = "It was not possible to instantiate a concrete object for class " + className + "' "
                        + "because of an Exception of type '" + e.getClass().getName() + "'. Probably the class or "
                        + "its " + "nullary constructor is not accessible." + " The associated message for this " +
                        "Exception is: " + e.getMessage();
                log.error(message);
            }
            try {
                ClassLoader classLoader = Class.forName(className).getClassLoader();
                Class clazz = classLoader.loadClass(tokenClassName);
                return clazz.newInstance();
            } catch (ClassNotFoundException e) {
                String message = "It was not possible to find class " + className + "' because of an Exception of " +
                        "type '" + e.getClass().getName() + "'. Does that class really exists and its reacheable by "
                        + "the current classloader?." + " The associated message for this Exception is: " + e
                        .getMessage();
                log.error(message);
            } catch (InstantiationException e) {
                String message = "It was not possible to instantiate a concrete object for class " + className + "' "
                        + "because of an Exception of type '" + e.getClass().getName() + "'. Probably this Class " +
                        "represents an abstract class, an interface, an array " + "class, a primitive type, or void "
                        + "or has no nullary constructor." + " The associated message for this Exception is: " + e
                        .getMessage();
                log.error(message);
            } catch (IllegalAccessException e) {
                String message = "It was not possible to instantiate a concrete object for class " + className + "' "
                        + "because of an Exception of type '" + e.getClass().getName() + "'. Probably the class or "
                        + "its " + "nullary constructor is not accessible." + "The associated message for this " +
                        "Exception is: " + e.getMessage();
                log.error(message);
            }
            return o;
        });
        CollectionUtils.predicatedCollection(tokens, o -> o instanceof Specification);
        return tokens;
    }
}
