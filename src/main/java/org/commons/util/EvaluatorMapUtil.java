package org.commons.util;

import org.commons.interpreter.Evaluator;
import org.commons.interpreter.arithmetic.ArithmeticContext;
import org.commons.interpreter.arithmetic.ArithmeticEvaluator;
import org.commons.interpreter.exception.EvaluatorException;
import org.commons.interpreter.exception.ExpressionException;
import org.commons.interpreter.logical.LogicalContext;
import org.commons.interpreter.logical.LogicalEvaluator;
import org.commons.interpreter.logicalArithmetic.LogicalArithmeticContext;
import org.commons.interpreter.logicalArithmetic.LogicalArithmeticEvaluator;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: ClienteFinal
 * Date: Sep 15, 2010
 * Time: 7:17:10 AM
 */
public class EvaluatorMapUtil {

    private static Properties properties = new Properties();
    private static EvaluatorMapUtil instance;
    private static Map
            /*<String, <Map <String, LogicalEvaluator>>*/
            logicalEvaluatorMap = new HashMap/*<String, <Map <String, LogicalEvaluator>>*/();

    private EvaluatorMapUtil() {
        instance = this;
    }

    private EvaluatorMapUtil(Properties prop) {
        if (prop != null) {
            properties.putAll(prop);
        }
        if (instance == null) {
            instance = new EvaluatorMapUtil();
        }
        buildEvaluatorMap(properties);
    }

    public static EvaluatorMapUtil getInstance() {
        if (instance == null) {
            instance = new EvaluatorMapUtil();
        }
        try {
            buildEvaluatorMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public static EvaluatorMapUtil getInstance(File file) {
        if (instance == null) {
            instance = new EvaluatorMapUtil();
        }
        buildEvaluatorMap(file, null);
        return instance;
    }

    public static EvaluatorMapUtil getInstance(String fileName) {
        if (instance == null) {
            instance = new EvaluatorMapUtil();
        }
        buildEvaluatorMap(fileName, null);
        return instance;
    }

    public static EvaluatorMapUtil getInstance(Map properties) {
        if (instance == null) {
            instance = new EvaluatorMapUtil(MapUtil.toProperties(properties));
        } else {
            addProperties(MapUtil.toProperties(properties));
            buildEvaluatorMap(properties);
        }
        return instance;
    }

    private static void fillLogicalEvaluatorMap(final Properties properties) {
        fillLogicalEvaluatorMap(properties, false);
    }

    private static void fillLogicalEvaluatorMap(final Properties properties, final boolean evaluateAllTerms) {
        CollectionUtils.forAllDo(properties.keySet(), new Closure() {
            public void execute(Object o) {
                String key = (String) o;
                try {
                    logicalEvaluatorMap.put(key, buildExpression((String) properties.get(key), evaluateAllTerms));
                } catch (Exception ignored) {
                }
            }
        });
    }

    public Collection getTermsByLabel(String methodName) {
        return getTermsByLabel(methodName, false);
    }

    public Collection getTermsByLabel(String methodName, boolean evaluateAllTerms) {
        LogicalEvaluator logicalEvaluator = (LogicalEvaluator) logicalEvaluatorMap.get(methodName);
        logicalEvaluator.setEvaluateAllTerms(evaluateAllTerms);
        Collection tokens = new ArrayList();

        try {
            tokens = logicalEvaluator.getTokens();
        } catch (ExpressionException e) {
            e.printStackTrace();
            return tokens;
        }

        CollectionUtils.predicatedCollection(tokens, new Predicate() {
            public boolean evaluate(Object o) {
                return o instanceof Evaluator;
            }
        });
        return tokens;
    }

    private static Evaluator buildExpression(String expression, boolean evaluateAllTerms) throws ExpressionException {
        try {
            LogicalEvaluator logicalEvaluator = new LogicalEvaluator(evaluateAllTerms);
            logicalEvaluator.setExpression(expression);
            logicalEvaluator.buildExpressionTree();
            return logicalEvaluator;
        } catch (Throwable ignored) {
        }
        try {
            ArithmeticEvaluator arithmeticEvaluator = new ArithmeticEvaluator(evaluateAllTerms);
            arithmeticEvaluator.setExpression(expression);
            arithmeticEvaluator.buildExpressionTree();
            return arithmeticEvaluator;
        } catch (Throwable ignored) {
        }
        try {
            LogicalArithmeticEvaluator logicalArithmeticEvaluator = new LogicalArithmeticEvaluator(evaluateAllTerms);
            logicalArithmeticEvaluator.setExpression(expression);
            logicalArithmeticEvaluator.buildExpressionTree();
            return logicalArithmeticEvaluator;
        } catch (Throwable ignored) {
        }

        throw new ExpressionException("Expression '"
                + expression
                + "' is not a valid Logical, Arithmetic or Logical/Arithmetic expression");
    }

    public static void setLogicalEvaluatorMap(Map map) {
        logicalEvaluatorMap = map;
    }

    public static Map getLogicalEvaluatorMap() {
        return logicalEvaluatorMap;
    }

    public static void addLogicalEvaluators(Map /*<String, LogicalEvaluator>*/ logicalEvaluators) {
        logicalEvaluatorMap.putAll(logicalEvaluators);
    }

    public static void addLogicalEvaluator(String label, LogicalEvaluator evaluator) {
        logicalEvaluatorMap.put(label, evaluator);
    }

    public static void setProperties(Properties prop) {
        properties = prop;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static void addProperties(Properties prop) {
        properties.putAll(prop);
    }

    public static void addProperty(String label, Properties prop) {
        properties.put(label, prop);
    }

    public static Map buildEvaluatorMap() throws IOException {
        return buildEvaluatorMap(PropertiesHandlerUtil.getInstance("evaluator.properties",
                EvaluatorMapUtil.class.getClassLoader())
                .getProperties());
    }

    public static Map buildEvaluatorMap(Map /*<String, String>*/ map) {
        return buildInstance(MapUtil.toProperties(map), null, false);
    }

    public static Map buildEvaluatorMap(File propertiesFile, String label) {
        return buildInstance(PropertiesHandlerUtil.getInstance(propertiesFile.getPath()).getProperties(), label, false);
    }

    public static Map buildEvaluatorMap(String fileName, String label) {
        return buildInstance(PropertiesHandlerUtil.getInstance(fileName, EvaluatorMapUtil.class.getClassLoader())
                .getProperties(), label, false);
    }

    public static Map getInstance(File propertiesFile, String label, boolean replace) {
        return buildInstance(PropertiesHandlerUtil.getInstance(propertiesFile.getPath()).getProperties(),
                label,
                replace);
    }

    private static Map buildInstance(Properties properties, String label, boolean replace) {
        return buildInstance(properties, label, replace, false);
    }

    private static Map buildInstance(Properties properties,
                                     String label,
                                     boolean replace,
                                     final boolean evaluateAllTerms) {
        if (instance.getEvaluator(label) == null) {
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
        return (Evaluator) logicalEvaluatorMap.get(key);
    }

    public LogicalEvaluator getLogicalEvaluator(String logicalEvaluatorKey, final Map context)
            throws EvaluatorException {
        try {
            LogicalEvaluator logicalEvaluator = (LogicalEvaluator) getEvaluator(logicalEvaluatorKey);
            if (logicalEvaluator == null) {
                return new LogicalEvaluator();
            }
            final LogicalContext logicalContext = new LogicalContext();

            CollectionUtils.forAllDo(context.keySet(), new Closure() {
                public void execute(Object o) {
                    String key = (String) o;
                    logicalContext.assign(key, ((Boolean.valueOf(context.get(key).toString()))).booleanValue());
                }
            });

            logicalEvaluator.setContext(logicalContext);
            return logicalEvaluator;
        } catch (Throwable t) {
            throw new EvaluatorException("There is no a valid LogicalArithmetic evaluator mapped with key '"
                    + logicalEvaluatorKey
                    + "'");
        }
    }

    public LogicalArithmeticEvaluator getLogicalArithmeticEvaluator(String logicalArithmeticEvaluatorKey,
                                                                    final Map context) throws EvaluatorException {
        try {
            LogicalArithmeticEvaluator logicalArithmeticEvaluator = (LogicalArithmeticEvaluator) getEvaluator(
                    logicalArithmeticEvaluatorKey);
            if (logicalArithmeticEvaluator == null) {
                return new LogicalArithmeticEvaluator();
            }
            final LogicalArithmeticContext logicalArithmeticContext = new LogicalArithmeticContext();

            CollectionUtils.forAllDo(context.keySet(), new Closure() {
                public void execute(Object o) {
                    String key = (String) o;
                    logicalArithmeticContext.assign(key, ((Double.valueOf(context.get(key).toString()))).doubleValue());
                }
            });

            logicalArithmeticEvaluator.setContext(logicalArithmeticContext);
            return logicalArithmeticEvaluator;
        } catch (Throwable t) {
            throw new EvaluatorException("There is no a valid LogicalArithmetic evaluator mapped with key '"
                    + logicalArithmeticEvaluatorKey
                    + "'");
        }
    }

    public ArithmeticEvaluator getArithmeticEvaluator(String arithmeticEvaluatorKey, final Map context)
            throws EvaluatorException {
        try {
            ArithmeticEvaluator arithmeticEvaluator = (ArithmeticEvaluator) getEvaluator(arithmeticEvaluatorKey);
            if (arithmeticEvaluator == null) {
                return new ArithmeticEvaluator();
            }
            final ArithmeticContext arithmeticContext = new ArithmeticContext();

            CollectionUtils.forAllDo(context.keySet(), new Closure() {
                public void execute(Object o) {
                    String key = (String) o;
                    arithmeticContext.assign(key, ((Double.valueOf(context.get(key).toString()))).doubleValue());
                }
            });

            arithmeticEvaluator.setContext(arithmeticContext);
            return arithmeticEvaluator;
        } catch (Throwable t) {
            throw new EvaluatorException("There is no a valid LogicalArithmetic evaluator mapped with key '"
                    + arithmeticEvaluatorKey
                    + "'");
        }
    }

}
