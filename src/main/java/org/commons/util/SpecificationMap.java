package org.commons.util;

import org.commons.interpreter.Expression;
import org.commons.interpreter.NonTerminalExpression;
import org.commons.interpreter.TerminalExpression;
import org.commons.interpreter.exception.ExpressionException;
import org.commons.interpreter.logical.*;
import org.commons.specification.AbstractSpecificationImpl;
import org.commons.specification.Specification;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ClienteFinal
 * Date: Sep 15, 2010
 * Time: 7:17:10 AM
 */
public class SpecificationMap {

    private Map
            /*<String, <Map <String, Specification>>*/
            specificationMap = new HashMap/*<String, <Map <String, Specification>>*/();
    private Properties properties = new Properties();
    private String className;
    private static Logger log = Logger.getLogger(SpecificationMap.class);

    public SpecificationMap() {
    }

    public SpecificationMap(Class clazz, Properties properties, ClassLoader classLoader) {
        this(clazz.getName(), properties, classLoader);
    }

    public SpecificationMap(String className, Properties properties, ClassLoader classLoader) {
        this.properties = properties;
        this.className = className;
        buildsSpecificationMap(classLoader);
    }

    public Map /*<String, Specification>*/ getSpecificationsMap() {
        return getSpecificationMap();
    }

    public Specification getSpecificationFromMethod(String method) {
        return (Specification) getSpecificationsMap().get(method);
    }

    private Map getSpecificationMap() {
        return specificationMap;
    }

    private void buildsSpecificationMap(final ClassLoader classLoader) {
        Collection classes = new ArrayList(properties.keySet());

        CollectionUtils.transform(classes, new Transformer() {
            public Object transform(Object o) {
                String key = ((String) o);
                return key.substring(0, key.lastIndexOf("."));
            }
        });
        Set classesSet = new HashSet(classes);
        final Map propertiesByClass = new HashMap();
        CollectionUtils.forAllDo(classesSet, new Closure() {
            public void execute(Object o) {
                final String className = (String) o;
                propertiesByClass.putAll(MapUtil.select(properties, new Predicate() {
                    public boolean evaluate(Object o) {
                        return ((String) o).startsWith(className);
                    }
                }, NotNullOrEmptyStringPredicate.getInstance()));
            }
        });
        Collection methods = new ArrayList(propertiesByClass.keySet());
        CollectionUtils.transform(methods, new Transformer() {
            public Object transform(Object o) {
                String key = ((String) o);
                return key.substring(key.lastIndexOf(".") + 1);
            }
        });
        Set methodsFiltered = new HashSet(methods);
        CollectionUtils.forAllDo(methodsFiltered, new Closure() {
            public void execute(Object o) {
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
                    if (!StringUtil.isNullOrEmpty(property)) {
                        specificationMap.put(methodName, buildSpecification(evaluateAllTerms,
                                property,
                                classLoader));

                    }
                } catch (Exception ignored) {
                }
            }
        });

    }

    public Collection getTermsByMethod(String methodName) {
        return getTermsByMethod(methodName, false);
    }

    public Collection getTermsByMethod(String methodName, boolean evaluateAllTerms) {
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

        CollectionUtils.transform(tokens, new Transformer() {
            public Object transform(Object o) {
                String tokenClassName = (String) o;
                try {
                    Class clazz = Class.forName(tokenClassName);
                    return clazz.newInstance();
                } catch (ClassNotFoundException e) {
                    String message = "It was not possible to find class '"
                            + className
                            + "' because of an Exception of type '"
                            + e.getClass().getName()
                            + "'. Does that class really exists and its reacheable by the current classloader?."
                            + " The associated message for this Exception is: "
                            + e.getMessage();
                    log.error(message);
                } catch (InstantiationException e) {
                    String message = "It was not possible to instantiate a concrete object for class "
                            + className
                            + "' because of an Exception of type '"
                            + e.getClass().getName()
                            + "'. Probably this class represents an abstract class, an interface, an array "
                            + "class, a primitive type, or void or has no nullary constructor."
                            + " The associated message for this Exception is: "
                            + e.getMessage();
                    log.error(message);
                } catch (IllegalAccessException e) {
                    String message = "It was not possible to instantiate a concrete object for class "
                            + className
                            + "' because of an Exception of type '"
                            + e.getClass().getName()
                            + "'. Probably the class or its nullary constructor is not accessible."
                            + " The associated message for this Exception is: "
                            + e.getMessage();
                    log.error(message);
                }
                try {
                    ClassLoader classLoader = Class.forName(className).getClassLoader();
                    Class clazz = classLoader.loadClass(tokenClassName);
                    return clazz.newInstance();
                } catch (ClassNotFoundException e) {
                    String message = "It was not possible to find class "
                            + className
                            + "' because of an Exception of type '"
                            + e.getClass().getName()
                            + "'. Does that class really exists and its reacheable by the current classloader?."
                            + " The associated message for this Exception is: "
                            + e.getMessage();
                    log.error(message);
                } catch (InstantiationException e) {
                    String message = "It was not possible to instantiate a concrete object for class "
                            + className
                            + "' because of an Exception of type '"
                            + e.getClass().getName()
                            + "'. Probably this Class represents an abstract class, an interface, an array "
                            + "class, a primitive type, or void or has no nullary constructor."
                            + " The associated message for this Exception is: "
                            + e.getMessage();
                    log.error(message);
                } catch (IllegalAccessException e) {
                    String message = "It was not possible to instantiate a concrete object for class "
                            + className
                            + "' because of an Exception of type '"
                            + e.getClass().getName()
                            + "'. Probably the class or its nullary constructor is not accessible."
                            + "The associated message for this Exception is: "
                            + e.getMessage();
                    log.error(message);
                }
                return o;
            }
        });
        CollectionUtils.predicatedCollection(tokens, new Predicate() {
            public boolean evaluate(Object o) {
                return o instanceof Specification;
            }
        });
        return tokens;
    }

    private Specification buildSpecification(boolean evaluateAllTerms, String expression, ClassLoader classLoader)
            throws ExpressionException {
        Specification result = new AbstractSpecificationImpl(evaluateAllTerms);
        LogicalEvaluator logicalEvaluator = new LogicalEvaluator(evaluateAllTerms);
        logicalEvaluator.setExpression(expression);
        return buildSpecificationFromExpression(logicalEvaluator.buildExpressionTree(), result, classLoader);
    }

    public Specification buildSpecificationFromExpression(Expression node, Specification spec, ClassLoader classLoader)
            throws ExpressionException {
        if (node != null) {
            if (node instanceof TerminalExpression) {
                String nodeclassName = (String) node.getValue();
                try {
                    Class clazz = classLoader.loadClass(nodeclassName);
                    try {
                        spec = (Specification) clazz.getConstructor(new Class[]{Boolean.TYPE})
                                .newInstance(new Object[]{Boolean.valueOf(spec.getEvaluateAllTerms())});
                    } catch (InvocationTargetException e) {
                        String message = "The class '"
                                + className
                                + "' exists but, it was not possible to instantiate a concrete object "
                                + "for it because of an Exception of type '"
                                + e.getClass().getName()
                                + "'. The Constructor of boolean parameter threw an uncaught Exception "
                                + " The associated message for this Exception is: "
                                + e.getMessage();
                        log.error(message);
                    } catch (NoSuchMethodException e) {
                        try {
                            spec = (Specification) clazz.getSuperclass()
                                    .getConstructor(new Class[]{Boolean.TYPE})
                                    .newInstance(new Object[]{Boolean.valueOf(spec.getEvaluateAllTerms())});
                        } catch (InvocationTargetException e1) {
                            String message = "The class '"
                                    + className
                                    + "' exists but, it was not possible to instantiate a concrete object "
                                    + "for it because of an Exception of type '"
                                    + e.getClass().getName()
                                    + "'. The class that declares the underlying constructor represents an abstract class. "
                                    + " The associated message for this Exception is: "
                                    + e.getMessage();
                            log.error(message);
                        } catch (NoSuchMethodException e2) {
                            spec = (Specification) clazz.newInstance();
                        }
                    }
                } catch (ClassNotFoundException e) {
                    try {
                        return buildSpecificationFromExpression(node,
                                spec,
                                Class.forName(nodeclassName)
                                        .getClassLoader()
                                        .getParent());
                    } catch (ClassNotFoundException e1) {
                        try {
                            ClassLoader classLoader1 = Class.forName(className).getClassLoader();
                            if (classLoader1.equals(classLoader)) {
                                return null;
                            } else {
                                return buildSpecificationFromExpression(node,
                                        spec,
                                        classLoader1);
                            }
                        } catch (ClassNotFoundException e2) {
                            return null;
                        }
                    }

                } catch (InstantiationException e) {
                    String message = "The class '"
                            + className
                            + "' exists but, it was not possible to instantiate a concrete object "
                            + "for it because of an Exception of type '"
                            + e.getClass().getName()
                            + "'. The class that declares the underlying constructor represents an abstract class. "
                            + " The associated message for this Exception is: "
                            + e.getMessage();
                    log.error(message);
                    throw new ExpressionException(message);
                } catch (IllegalAccessException e) {
                    String message = "It was not possible to instantiate a concrete object for class "
                            + className
                            + "' because of an Exception of type '"
                            + e.getClass().getName()
                            + "'. This class is not accesible by Java. The associated message for this Exception is: "
                            + e.getMessage();
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

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void addProperties(Properties properties) {
        this.properties.putAll(properties);
    }
}
