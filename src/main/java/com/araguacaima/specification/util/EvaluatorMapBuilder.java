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

import com.araguacaima.specification.interpreter.Evaluator;
import com.araguacaima.specification.interpreter.arithmetic.ArithmeticEvaluator;
import com.araguacaima.specification.interpreter.exception.ExpressionException;
import com.araguacaima.specification.interpreter.logical.LogicalEvaluator;
import com.araguacaima.specification.interpreter.logicalArithmetic.LogicalArithmeticEvaluator;
import com.araguacaima.specification.interpreter.mathFunctions.MathFunctionEvaluator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal"})

public class EvaluatorMapBuilder {

    private final String propertiesFile = "evaluator.properties";

    private Map<String, Evaluator> logicalEvaluatorMap = new HashMap<>();
    private Properties properties = new Properties();

    public EvaluatorMapBuilder() throws IOException {
        ClassLoader classLoader = EvaluatorMapBuilder.class.getClassLoader();
        properties.load(classLoader.getResourceAsStream(propertiesFile));
        buildEvaluatorMap(properties);
    }

    public EvaluatorMapBuilder(Map<Object, Object> map) {
        if (map != null) {
            properties.putAll(map);
        }
        buildEvaluatorMap(properties);
    }

    private Map<String, Evaluator> buildEvaluatorMap(Properties prop) {
        return buildInstance(prop, null, false);
    }

    private Map<String, Evaluator> buildInstance(Properties properties, String label, boolean replace) {
        return buildInstance(properties, label, replace, false);
    }

    private Map<String, Evaluator> buildInstance(Properties properties, String label, boolean replace, final boolean evaluateAllTerms) {
        if (getEvaluator(label) == null) {
            fillLogicalEvaluatorMap(properties);
        } else {
            if (replace) {
                logicalEvaluatorMap.remove(label);
                try {
                    logicalEvaluatorMap.put(label, buildExpression((String) properties.get(label), evaluateAllTerms));
                } catch (ExpressionException e) {
                    e.printStackTrace();
                }
            }
        }
        return getLogicalEvaluatorMap();
    }

    public Evaluator getEvaluator(String key) {
        return logicalEvaluatorMap.get(key);
    }

    private void fillLogicalEvaluatorMap(final Properties properties) {
        fillLogicalEvaluatorMap(properties, false);
    }

    private Evaluator buildExpression(String expression, boolean evaluateAllTerms)
            throws ExpressionException {
        Evaluator evaluator = null;
        try {
            evaluator = new MathFunctionEvaluator();
            evaluator.setEvaluateAllTerms(evaluateAllTerms);
            evaluator.setExpressionString(expression);
        } catch (Throwable ignored) {
        }
        try {
            evaluator = new ArithmeticEvaluator();
            evaluator.setEvaluateAllTerms(evaluateAllTerms);
            evaluator.setExpressionString(expression);
        } catch (Throwable ignored) {
        }
        try {
            evaluator = new LogicalArithmeticEvaluator<>(new LogicalEvaluator());
            evaluator.setEvaluateAllTerms(evaluateAllTerms);
            evaluator.setExpressionString(expression);
        } catch (Throwable ignored) {
        }
        try {
            evaluator = new LogicalEvaluator();
            evaluator.setEvaluateAllTerms(evaluateAllTerms);
            evaluator.setExpressionString(expression);
        } catch (Throwable ignored) {
        }
        if (evaluator == null) {
            throw new ExpressionException("Expression '" + expression + "' is not a valid Logical, Arithmetic, " +
                    "Logical/Arithmetic or Math function expression");
        }
        return evaluator;
    }

    private Map<String, Evaluator> getLogicalEvaluatorMap() {
        return logicalEvaluatorMap;
    }

    public void setLogicalEvaluatorMap(Map<String, Evaluator> map) {
        logicalEvaluatorMap = map;
    }

    private void fillLogicalEvaluatorMap(final Properties properties, final boolean evaluateAllTerms) {
        IterableUtils.forEach(properties.keySet(), o -> {
            String key = (String) o;
            try {
                logicalEvaluatorMap.put(key, buildExpression((String) properties.get(key), evaluateAllTerms));
            } catch (Exception ignored) {
            }
        });
    }

    public void addLogicalEvaluator(String label, LogicalEvaluator evaluator) {
        logicalEvaluatorMap.put(label, evaluator);
    }

    public void addLogicalEvaluators(Map<String, LogicalEvaluator> logicalEvaluators) {
        logicalEvaluatorMap.putAll(logicalEvaluators);
    }

    public void addProperty(String label, Properties prop) {
        properties.put(label, prop);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties prop) {
        properties = prop;
    }

    public Collection<?> getTermsByLabel(String methodName) {
        return getTermsByLabel(methodName, false);
    }

    private Collection<?> getTermsByLabel(String methodName, boolean evaluateAllTerms) {
        LogicalEvaluator logicalEvaluator = (LogicalEvaluator) logicalEvaluatorMap.get(methodName);
        logicalEvaluator.setEvaluateAllTerms(evaluateAllTerms);
        Collection<?> tokens = logicalEvaluator.getTokens();
        CollectionUtils.predicatedCollection(tokens, o -> o instanceof Evaluator);
        return tokens;
    }
}
