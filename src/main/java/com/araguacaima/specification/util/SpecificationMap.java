/*
 * Copyright 2020 araguacaima
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

import com.araguacaima.specification.AbstractSpecificationImpl;
import com.araguacaima.specification.Specification;
import com.araguacaima.specification.common.MapUtils;
import com.araguacaima.specification.common.StringUtils;
import com.araguacaima.specification.interpreter.Expression;
import com.araguacaima.specification.interpreter.NonTerminalExpression;
import com.araguacaima.specification.interpreter.TerminalExpression;
import com.araguacaima.specification.interpreter.exception.ExpressionException;
import com.araguacaima.specification.interpreter.logical.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("WeakerAccess")

public class SpecificationMap implements Comparable<SpecificationMap> {

    private static final Logger log = LoggerFactory.getLogger(SpecificationMap.class);
    private final Map<String, Specification> specificationMap = new HashMap<>();
    private final Predicate<Object> notNullOrEmptyStringObjectPredicate = object -> StringUtils.isNotBlank(object.toString());
    private String className;
    private LogicalEvaluator logicalEvaluator;
    private Properties properties = new Properties();

    private SpecificationMap() {

    }

    public SpecificationMap(LogicalEvaluator logicalEvaluator) {
        this.logicalEvaluator = logicalEvaluator;
    }

    public void addProperties(Properties properties) {
        this.properties.putAll(properties);
    }

    private Specification buildSpecification(boolean evaluateAllTerms, int order, String expression, ClassLoader classLoader)
            throws ExpressionException {
        Specification result = new AbstractSpecificationImpl(evaluateAllTerms);
        logicalEvaluator.setEvaluateAllTerms(evaluateAllTerms);
        logicalEvaluator.setExpressionString(expression);
        logicalEvaluator.setOrder(order);
        return buildSpecificationFromExpression(logicalEvaluator.getExpression(), result, classLoader);
    }

    private LogicalEvaluator getLogicalEvaluator() {
        return logicalEvaluator;
    }

    private Specification buildSpecificationFromExpression(Expression node, Specification spec, ClassLoader classLoader)
            throws ExpressionException {
        if (node != null) {
            if (node instanceof TerminalExpression) {
                String nodeclassName = (String) node.getValue();
                try {
                    @SuppressWarnings("unchecked")
                    Class<Specification> clazz = (Class<Specification>) classLoader.loadClass(nodeclassName);
                    try {
                        spec = clazz.getConstructor(new Class[]{Boolean.TYPE}).newInstance(spec
                                .getEvaluateAllTerms());
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
                                    "" + "" + "" + "" + "" + "is: " + e.getMessage();
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
                            "" + "" + "" + "" + "" + "concrete object " + "for it because of an Exception of type '"
                            + e.getClass().getName() + "'. The class that declares the underlying constructor " +
                            "represents" + " an " + "abstract class. " + " The associated message for this Exception " +
                            "" + "is: " + e.getMessage();
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

    void buildSpecificationMap(final ClassLoader classLoader) {
        Collection<Object> classes = new ArrayList<>(properties.keySet());

        CollectionUtils.transform(classes, o -> {
            String key = ((String) o);
            return key.substring(0, key.lastIndexOf("."));
        });
        Set<Object> classesSet = new HashSet<>(classes);
        final Map<Object, Object> propertiesByClass = new HashMap<>();
        IterableUtils.forEach(classesSet, o -> {
            final String className = (String) o;
            propertiesByClass.putAll(MapUtils.select(properties,
                    o1 -> ((String) o1).startsWith(className),
                    notNullOrEmptyStringObjectPredicate));
        });
        Collection<Object> methods = new ArrayList<>(propertiesByClass.keySet());
        CollectionUtils.transform(methods, o -> {
            String key = ((String) o);
            return key.substring(key.lastIndexOf(".") + 1);
        });
        Set<Object> methodsFiltered = new HashSet<>(methods);
        IterableUtils.forEach(methodsFiltered, o -> {
            String methodName = (String) o;
            boolean evaluateAllTerms;
            String[] tokens = methodName.split("_");
            int order = 0;
            if (tokens.length == 2) {
                methodName = tokens[0];
                String token = tokens[1];
                String[] modifiers = token.split("\\|");
                if (modifiers.length == 1) {
                    evaluateAllTerms = token.equals("1");
                } else if (modifiers.length == 0) {
                    evaluateAllTerms = false;
                } else {
                    evaluateAllTerms = modifiers[0].equals("1");
                    order = Integer.parseInt(modifiers[1]);
                }
            } else {
                evaluateAllTerms = false;
            }
            try {
                String property = (String) properties.get(className + "." + o);
                if (StringUtils.isNotBlank(property)) {
                    final Specification value = buildSpecification(evaluateAllTerms, order, property, classLoader);
                    specificationMap.put(methodName, value);
                }
            } catch (Exception t) {
                t.printStackTrace();
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
        return getSpecificationMap().get(method);
    }

    public Map<String, Specification> getSpecificationMap() {
        return specificationMap;
    }

    public Collection<?> getTermsByMethod(String methodName) throws ExpressionException {
        return getTermsByMethod(methodName, false);
    }

    @SuppressWarnings("SameParameterValue")
    private Collection<?> getTermsByMethod(String methodName, boolean evaluateAllTerms) throws ExpressionException {
        String expression = (String) properties.get(className + "." + methodName);
        logicalEvaluator.setEvaluateAllTerms(evaluateAllTerms);
        logicalEvaluator.setExpressionString(expression);
        Collection<Expression> tokens = logicalEvaluator.getTokens();
        CollectionUtils.transform(tokens, o -> {
            String tokenClassName = o.toString();
            try {
                Class<?> clazz = Class.forName(tokenClassName);
                return (Expression) clazz.newInstance();
            } catch (ClassNotFoundException e) {
                String message = "It was not possible to find class '" + className + "' because of an Exception of "
                        + "type '" + e.getClass().getName() + "'. Does that class really exists and its reacheable "
                        + "by" + " " + "the current classloader?." + " The associated message for this Exception is: " +
                        "" + "" + "" + e.getMessage();
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
                Class<?> clazz = classLoader.loadClass(tokenClassName);
                return (Expression) clazz.newInstance();
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

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public int compareTo(SpecificationMap o) {
        if (o == null) {
            return -1;
        }
        int order = this.logicalEvaluator.getOrder();
        int order1 = o.getLogicalEvaluator().getOrder();
        return Integer.compare(order1, order);
    }
}
