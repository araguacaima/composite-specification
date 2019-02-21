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
import com.araguacaima.specification.interpreter.Evaluator;
import com.araguacaima.specification.interpreter.arithmetic.ArithmeticContext;
import com.araguacaima.specification.interpreter.arithmetic.ArithmeticEvaluator;
import com.araguacaima.specification.interpreter.exception.EvaluatorException;
import com.araguacaima.specification.interpreter.exception.ExpressionException;
import com.araguacaima.specification.interpreter.logical.LogicalContext;
import com.araguacaima.specification.interpreter.logical.LogicalEvaluator;
import com.araguacaima.specification.interpreter.logicalArithmetic.LogicalArithmeticContext;
import com.araguacaima.specification.interpreter.logicalArithmetic.LogicalArithmeticEvaluator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;

@SuppressWarnings("WeakerAccess")
@Service
public class EvaluatorMapBuilder implements ApplicationContextAware {

    final String propertiesFile = "evaluator.properties";
    private ArithmeticEvaluator arithmeticEvaluator;
    private LogicalArithmeticEvaluator logicalArithmeticEvaluator;
    private LogicalEvaluator logicalEvaluator;
    private Map<String, Evaluator> logicalEvaluatorMap = new HashMap<>();
    private MapUtils mapUtils;
    private Properties properties = new Properties();
    private ApplicationContext applicationContext;

    @Autowired
    private EvaluatorMapBuilder(ArithmeticEvaluator arithmeticEvaluator,
                                LogicalArithmeticEvaluator logicalArithmeticEvaluator,
                                LogicalEvaluator logicalEvaluator) {
        this.arithmeticEvaluator = arithmeticEvaluator;
        this.logicalArithmeticEvaluator = logicalArithmeticEvaluator;
        this.logicalEvaluator = logicalEvaluator;
    }

    private EvaluatorMapBuilder(Properties prop) {
        if (prop != null) {
            properties.putAll(prop);
        }
        buildEvaluatorMap(properties);
    }

    private Map buildEvaluatorMap(Properties prop) {
        return buildInstance(prop, null, false);
    }

    private Map buildInstance(Properties properties, String label, boolean replace) {
        return buildInstance(properties, label, replace, false);
    }

    private Map buildInstance(Properties properties, String label, boolean replace, final boolean evaluateAllTerms) {
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

    private Evaluator getEvaluator(String key) {
        return logicalEvaluatorMap.get(key);
    }

    private void fillLogicalEvaluatorMap(final Properties properties) {
        fillLogicalEvaluatorMap(properties, false);
    }

    private Evaluator buildExpression(String expression, boolean evaluateAllTerms)
            throws ExpressionException {
        try {
            logicalEvaluator.setEvaluateAllTerms(evaluateAllTerms);
            logicalEvaluator.setExpression(expression);
            logicalEvaluator.buildExpressionTree();
            return logicalEvaluator;
        } catch (Throwable ignored) {
        }
        try {
            arithmeticEvaluator.setEvaluateAllTerms(evaluateAllTerms);
            arithmeticEvaluator.setExpression(expression);
            arithmeticEvaluator.buildExpressionTree();
            return arithmeticEvaluator;
        } catch (Throwable ignored) {
        }
        try {
            logicalArithmeticEvaluator.setEvaluateAllTerms(evaluateAllTerms);
            logicalArithmeticEvaluator.setExpression(expression);
            logicalArithmeticEvaluator.buildExpressionTree();
            return logicalArithmeticEvaluator;
        } catch (Throwable ignored) {
        }

        throw new ExpressionException("Expression '" + expression + "' is not a valid Logical, Arithmetic or " +
                "Logical/Arithmetic expression");
    }

    private Map getLogicalEvaluatorMap() {
        return logicalEvaluatorMap;
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

    public void setLogicalEvaluatorMap(Map map) {
        logicalEvaluatorMap = map;
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

    public ArithmeticEvaluator getArithmeticEvaluator(String arithmeticEvaluatorKey, final Map context)
            throws EvaluatorException {
        try {
            ArithmeticEvaluator arithmeticEvaluator = (ArithmeticEvaluator) getEvaluator(arithmeticEvaluatorKey);
            if (arithmeticEvaluator == null) {
                return this.arithmeticEvaluator;
            }
            final ArithmeticContext arithmeticContext = new ArithmeticContext();

            IterableUtils.forEach(context.keySet(), o -> {
                String key = (String) o;
                arithmeticContext.assign(key, Double.valueOf(context.get(key).toString()));
            });

            arithmeticEvaluator.setContext(arithmeticContext);
            return arithmeticEvaluator;
        } catch (Throwable t) {
            throw new EvaluatorException("There is no a valid LogicalArithmetic evaluator mapped with key '" +
                    arithmeticEvaluatorKey + "'");
        }
    }

    public EvaluatorMapBuilder getInstance(Map properties) {
        addProperties(mapUtils.toProperties(properties));
        buildEvaluatorMap(properties);
        return this;
    }

    private void addProperties(Properties prop) {
        properties.putAll(prop);
    }

    private Map buildEvaluatorMap(Map<String, String> map) {
        return buildInstance(mapUtils.toProperties(map), null, false);
    }

    public LogicalArithmeticEvaluator getLogicalArithmeticEvaluator(String logicalArithmeticEvaluatorKey,
                                                                    final Map context)
            throws EvaluatorException {
        try {
            LogicalArithmeticEvaluator logicalArithmeticEvaluator = (LogicalArithmeticEvaluator) getEvaluator(
                    logicalArithmeticEvaluatorKey);
            if (logicalArithmeticEvaluator == null) {
                return this.logicalArithmeticEvaluator;
            }
            final LogicalArithmeticContext logicalArithmeticContext = new LogicalArithmeticContext();

            IterableUtils.forEach(context.keySet(), o -> {
                String key = (String) o;
                logicalArithmeticContext.assign(key, Double.valueOf(context.get(key).toString()));
            });

            logicalArithmeticEvaluator.setContext(logicalArithmeticContext);
            return logicalArithmeticEvaluator;
        } catch (Throwable t) {
            throw new EvaluatorException("There is no a valid LogicalArithmetic evaluator mapped with key '" +
                    logicalArithmeticEvaluatorKey + "'");
        }
    }

    public LogicalEvaluator getLogicalEvaluator(String logicalEvaluatorKey, final Map context)
            throws EvaluatorException {
        try {
            LogicalEvaluator logicalEvaluator = (LogicalEvaluator) getEvaluator(logicalEvaluatorKey);
            if (logicalEvaluator == null) {
                return this.logicalEvaluator;
            }
            final LogicalContext logicalContext = new LogicalContext();

            IterableUtils.forEach(context.keySet(), o -> {
                String key = (String) o;
                logicalContext.assign(key, Boolean.valueOf(context.get(key).toString()));
            });

            logicalEvaluator.setContext(logicalContext);
            return logicalEvaluator;
        } catch (Throwable t) {
            throw new EvaluatorException("There is no a valid LogicalArithmetic evaluator mapped with key '" +
                    logicalEvaluatorKey + "'");
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties prop) {
        properties = prop;
    }

    public Collection getTermsByLabel(String methodName) {
        return getTermsByLabel(methodName, false);
    }

    private Collection getTermsByLabel(String methodName, boolean evaluateAllTerms) {
        LogicalEvaluator logicalEvaluator = (LogicalEvaluator) logicalEvaluatorMap.get(methodName);
        logicalEvaluator.setEvaluateAllTerms(evaluateAllTerms);
        Collection tokens = new ArrayList();

        try {
            tokens = logicalEvaluator.getTokens();
        } catch (ExpressionException e) {
            e.printStackTrace();
            return tokens;
        }

        CollectionUtils.predicatedCollection(tokens, o -> o instanceof Evaluator);
        return tokens;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }
}
