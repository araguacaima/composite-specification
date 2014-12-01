package org.commons.util;

import org.apache.commons.collections.*;
import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @noinspection UnusedDeclaration
 */
public class ReflectionUtil {

    static {
        final StandardToStringStyle tiesStyle = new StandardToStringStyle();
        tiesStyle.setArrayContentDetail(true);
        //Sets whether to output array content detail.
        tiesStyle.setArrayEnd("]");
        //Sets the array end text.
        tiesStyle.setArraySeparator(",");
        //Sets the array separator text.
        tiesStyle.setArrayStart("[");
        //Sets the array start text.
        tiesStyle.setContentEnd("\n");
        //Sets the content end text.
        tiesStyle.setContentStart("\n");
        //Sets the content start text.
        tiesStyle.setDefaultFullDetail(true);
        //Sets whether to use full detail when the caller doesn't specify.
        tiesStyle.setFieldNameValueSeparator(" = ");
        //Sets the field name value separator text.
        tiesStyle.setFieldSeparator("\n");
        //Sets the field separator text.
        tiesStyle.setFieldSeparatorAtEnd(false);
        //Sets whether the field separator should be added at the end of each buffer.
        tiesStyle.setFieldSeparatorAtStart(false);
        //Sets whether the field separator should be added at the start of each buffer.
        tiesStyle.setNullText("null");
        //Sets the text to output when null found.
        tiesStyle.setUseClassName(true);
        //Sets whether to use the class name.
        tiesStyle.setUseFieldNames(true);
        //Sets whether to use the field names passed in.
        tiesStyle.setUseIdentityHashCode(false);
        //Sets whether to use the identity hash code.
        tiesStyle.setUseShortClassName(true);
        //Sets whether to output short or long class names.
        tiesStyle.setArrayContentDetail(true);
        //Sets whether to output array content detail.
        tiesStyle.setDefaultFullDetail(true);
        //Sets whether to use full detail when the caller doesn't specify.

        ToStringBuilder.setDefaultStyle(tiesStyle);
    }

    public static final String UNKNOWN_VALUE = "UNKNOWN_VALUE";
    public static final List PRIMITIVE_TYPES = Arrays.asList(new Object[]{Boolean.TYPE,
            Character.TYPE,
            Byte.TYPE,
            Short.TYPE,
            Integer.TYPE,
            Long.TYPE,
            Float.TYPE,
            Double.TYPE,
            Void.TYPE});

    public static final List BASIC_CLASSES = Arrays.asList(new Object[]{String.class,
            Boolean.class,
            Character.class,
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class});

    public static Map/*<String,Class>*/ PRIMITIVE_NAMES_BASIC_TYPES = new HashMap/*<String,Class>*/();
    public static Map/*<String,Class>*/ PRIMITIVE_NAMES_BASIC_CLASS = new HashMap/*<String,Class>*/();

    public static final Map PRIMITIVE_AND_BASIC_TYPES = new HashMap();
    public static final Map PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES = new HashMap();

    private static final FieldCompare FIELD_COMPARE = new FieldCompare();

    private static Logger log = Logger.getLogger(ReflectionUtil.class);

    static {
        PRIMITIVE_AND_BASIC_TYPES.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_AND_BASIC_TYPES.put(Character.TYPE, Character.class);
        PRIMITIVE_AND_BASIC_TYPES.put(Byte.TYPE, Byte.class);
        PRIMITIVE_AND_BASIC_TYPES.put(Short.TYPE, Short.class);
        PRIMITIVE_AND_BASIC_TYPES.put(Integer.TYPE, Integer.class);
        PRIMITIVE_AND_BASIC_TYPES.put(Long.TYPE, Long.class);
        PRIMITIVE_AND_BASIC_TYPES.put(Float.TYPE, Float.class);
        PRIMITIVE_AND_BASIC_TYPES.put(Double.TYPE, Double.class);
        PRIMITIVE_NAMES_BASIC_TYPES.put("int", Integer.TYPE);
        PRIMITIVE_NAMES_BASIC_TYPES.put("long", Long.TYPE);
        PRIMITIVE_NAMES_BASIC_TYPES.put("double", Double.TYPE);
        PRIMITIVE_NAMES_BASIC_TYPES.put("float", Float.TYPE);
        PRIMITIVE_NAMES_BASIC_TYPES.put("boolean", Boolean.TYPE);
        PRIMITIVE_NAMES_BASIC_TYPES.put("char", Character.TYPE);
        PRIMITIVE_NAMES_BASIC_TYPES.put("byte", Byte.TYPE);
        PRIMITIVE_NAMES_BASIC_TYPES.put("short", Short.TYPE);
        PRIMITIVE_NAMES_BASIC_CLASS.put("int", Integer.class);
        PRIMITIVE_NAMES_BASIC_CLASS.put("long", Long.class);
        PRIMITIVE_NAMES_BASIC_CLASS.put("double", Double.class);
        PRIMITIVE_NAMES_BASIC_CLASS.put("float", Float.class);
        PRIMITIVE_NAMES_BASIC_CLASS.put("boolean", Boolean.class);
        PRIMITIVE_NAMES_BASIC_CLASS.put("char", Character.class);
        PRIMITIVE_NAMES_BASIC_CLASS.put("byte", Byte.class);
        PRIMITIVE_NAMES_BASIC_CLASS.put("short", Short.class);

        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Boolean.TYPE, Boolean.FALSE);
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Character.TYPE, new Character(' '));
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Byte.TYPE, new Byte((byte) -1));
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Short.TYPE, new Short((short) -1));
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Integer.TYPE, new Integer(-1));
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Long.TYPE, new Long(-1));
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Float.TYPE, new Float(-1));
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Double.TYPE, new Double(-1));
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Boolean.class, Boolean.FALSE);
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Character.class, new Character(' '));
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Byte.class, new Byte((byte) -1));
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Short.class, new Short((short) -1));
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Integer.class, new Integer(-1));
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Long.class, new Long(-1));
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Float.class, new Float(-1));
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(Double.class, new Double(-1));
        PRIMITIVE_AND_BASIC_TYPE_DEFAULT_VALUES.put(String.class, StringUtil.EMPTY_STRING);
    }

    public static FieldCompare getFieldCompare() {
        return FIELD_COMPARE;
    }

    public static String toString(Object object) {
        try {
            return "\n".concat(ToStringBuilder.reflectionToString(object));
        } catch (Exception ignored) {
            return toString(object, false);
        }
    }

    public static String toString(Object object, boolean includeHeader) {
        try {
            return formatObjectValues(object, includeHeader, true, StringUtil.EMPTY_STRING, false);
        } catch (Exception ignored) {
            return object.toString();
        }
    }

    //    public static String toString(Object object) {
    //        return formatObjectValues(object, true, true, StringUtil.EMPTY_STRING, false);
    //    }

    public static String toString(Object object, boolean includeHeader, boolean considerHierarchy) {
        try {
            return formatObjectValues(object, includeHeader, true, StringUtil.EMPTY_STRING, considerHierarchy);
        } catch (Exception ignored) {
            return object.toString();
        }
    }

    public static String formatObjectValues(Object object,
                                            boolean includeHeader,
                                            boolean newLine,
                                            String identation,
                                            boolean considerHierarchy) {
        try {
            StringBuffer objectValuesFormatted = new StringBuffer();
            try {
                if (object != null) {
                    Class clazz = object.getClass();
                    Collection fieldNames = getFieldNames(clazz);
                    if (newLine) {
                        objectValuesFormatted.append(StringUtil.NEW_LINE);
                    }
                    if (isPrimitive(clazz) || isBasic(clazz)) {
                        if (newLine) {
                            objectValuesFormatted.append(getPrimitive(clazz))
                                    .append(StringUtil.BLANK_SPACE)
                                    .append(StringUtil.EQUAL_SYMBOL)
                                    .append(StringUtil.BLANK_SPACE)
                                    .append(object)
                                    .append(StringUtil.NEW_LINE);
                        } else {
                            objectValuesFormatted.append(object);
                        }
                    } else {
                        if (includeHeader) {
                            objectValuesFormatted.append(
                                    "==============================================================")
                                    .append(StringUtil.NEW_LINE)
                                    .append(StringUtil.TAB)
                                    .append("Values retrieved for incoming object ")
                                    .append(StringUtil.BLANK_SPACE)
                                    .append("(that belongs to: ")
                                    .append(getSimpleClassName(clazz))
                                    .append(")")
                                    .append(StringUtil.NEW_LINE)
                                    .append("--------------------------------------------------------------")
                                    .append(StringUtil.NEW_LINE);
                        }
                        Collection/*<Method>*/ getterMethods = considerHierarchy
                                ? getGetterMethods(clazz)
                                : getDeclaredGetterMethods(clazz);
                        for (Iterator getterMethod = getterMethods.iterator(); getterMethod.hasNext(); ) {
                            final Method method = (Method) getterMethod.next();
                            final String fieldName = (String) CollectionUtils.find(fieldNames, new Predicate() {
                                public boolean evaluate(Object o) {
                                    return ((String) o).equalsIgnoreCase(method.getName()
                                            .replaceFirst("get",
                                                    StringUtil.EMPTY_STRING));
                                }
                            });
                            Object value;
                            if (object instanceof Collection) {
                                if (!method.getName().equals("getClass")) {
                                    objectValuesFormatted.append(formatObjectValues(((Collection) object).toArray(),
                                            false,
                                            false,
                                            identation,
                                            false));
                                }
                            } else if (object instanceof Object[]) {
                                int collectionSize = ((Object[]) object).length;
                                for (int i = 0; i < collectionSize; i++) {
                                    try {
                                        value = ((Object[]) object)[i];
                                        objectValuesFormatted.append(identation)
                                                .append("[")
                                                .append(i)
                                                .append("]")
                                                .append(StringUtil.BLANK_SPACE)
                                                .append("(")
                                                .append(getSimpleClassName(value.getClass()))
                                                .append(")")
                                                .append(StringUtil.BLANK_SPACE)
                                                .append(StringUtil.EQUAL_SYMBOL)
                                                .append(StringUtil.BLANK_SPACE)
                                                .append(!StringUtil.isNullOrEmpty(identation)
                                                        && !isBasic(value.getClass())
                                                        ? ((new StringBuffer()).append(StringUtil.NEW_LINE)).toString()
                                                        : StringUtil.EMPTY_STRING)
                                                .append(formatObjectValues(value,
                                                        false,
                                                        false,
                                                        !isBasic(value.getClass())
                                                                ? ((new StringBuffer()).append(identation)
                                                                .append(StringUtil.TAB)
                                                                .append(StringUtil.TAB)).toString()
                                                                : StringUtil.EMPTY_STRING,
                                                        false))
                                                .append(StringUtil.NEW_LINE);
                                    } catch (Exception e) {
                                        objectValuesFormatted.append("[")
                                                .append(i)
                                                .append("]")
                                                .append(StringUtil.BLANK_SPACE)
                                                .append(StringUtil.EQUAL_SYMBOL)
                                                .append(StringUtil.BLANK_SPACE)
                                                .append(UNKNOWN_VALUE)
                                                .append(StringUtil.NEW_LINE);
                                    }
                                }
                            } else {
                                if (!method.getName().equals("getClass")) {
                                    Object[] args = {};
                                    try {
                                        value = method.invoke(object, args);
                                        objectValuesFormatted.append(fieldName)
                                                .append(StringUtil.BLANK_SPACE)
                                                .append("(")
                                                .append(getSimpleClassName(value.getClass()))
                                                .append(")")
                                                .append(StringUtil.BLANK_SPACE)
                                                .append(StringUtil.EQUAL_SYMBOL)
                                                .append(StringUtil.BLANK_SPACE)
                                                .append(!isBasic(value.getClass())
                                                        ? ((new StringBuffer()).append(StringUtil.NEW_LINE)).toString()
                                                        : StringUtil.EMPTY_STRING)
                                                .append(value != object
                                                        ? formatObjectValues(value,
                                                        false,
                                                        false,
                                                        !isBasic(value.getClass())
                                                                ? ((new StringBuffer()).append(StringUtil.TAB)
                                                                .append(StringUtil.TAB)).toString()
                                                                : StringUtil.EMPTY_STRING,
                                                        false)
                                                        : toString(object))
                                                .append(StringUtil.NEW_LINE);
                                    } catch (IllegalAccessException e) {
                                        Object valueOfField = UNKNOWN_VALUE;
                                        Field field;
                                        try {
                                            field = getFieldByFieldName(object, fieldName);
                                            field.setAccessible(true);
                                            valueOfField = field.get(object);
                                        } catch (IllegalAccessException e1) {
                                            log.error("Impossible to get the value of the field: "
                                                    + fieldName
                                                    + " directly from the object: "
                                                    + object
                                                    + " because of an IllegalAccessException");
                                        }
                                        objectValuesFormatted.append(getSimpleClassName(valueOfField.getClass()))
                                                .append(")")
                                                .append(StringUtil.BLANK_SPACE)
                                                .append(StringUtil.EQUAL_SYMBOL)
                                                .append(StringUtil.BLANK_SPACE)
                                                .append(valueOfField)
                                                .append(StringUtil.NEW_LINE);
                                    } catch (InvocationTargetException e) {
                                        Object valueOfField = UNKNOWN_VALUE;
                                        Field field;
                                        try {
                                            field = getFieldByFieldName(object, fieldName);
                                            field.setAccessible(true);
                                            valueOfField = field.get(object);
                                        } catch (IllegalAccessException e1) {
                                            log.error("Impossible to get the value of the field: "
                                                    + fieldName
                                                    + " directly from the object: "
                                                    + object
                                                    + " because of an IllegalAccessException");
                                        }
                                        objectValuesFormatted.append(getSimpleClassName(valueOfField.getClass()))
                                                .append(")")
                                                .append(StringUtil.BLANK_SPACE)
                                                .append(StringUtil.EQUAL_SYMBOL)
                                                .append(StringUtil.BLANK_SPACE)
                                                .append(valueOfField)
                                                .append(StringUtil.NEW_LINE);
                                    } catch (NullPointerException e) {
                                        Object valueOfField = UNKNOWN_VALUE;
                                        Field field = getFieldByFieldName(object, fieldName);
                                        objectValuesFormatted.append(field == null
                                                ? getSimpleClassName(method.getReturnType()) : getSimpleClassName(field.getType())).append(")")
                                                .append(StringUtil.BLANK_SPACE)
                                                .append(StringUtil.EQUAL_SYMBOL)
                                                .append(StringUtil.BLANK_SPACE)
                                                .append("null")
                                                .append(StringUtil.NEW_LINE);
                                    } catch (Exception e) {
                                        Object valueOfField = UNKNOWN_VALUE;
                                        Field field;
                                        try {
                                            field = getFieldByFieldName(object, fieldName);

                                            if (field != null) {
                                                field.setAccessible(true);
                                                valueOfField = field.get(object);
                                            }
                                        } catch (IllegalAccessException e1) {
                                            log.error("Impossible to get the value of the field: "
                                                    + fieldName
                                                    + " directly from the object: "
                                                    + object
                                                    + " because of an IllegalAccessException");
                                        }
                                        objectValuesFormatted.append(valueOfField == null
                                                ? getSimpleClassName(method.getReturnType()) : getSimpleClassName(valueOfField.getClass()))
                                                .append(")")
                                                .append(StringUtil.BLANK_SPACE)
                                                .append(StringUtil.EQUAL_SYMBOL)
                                                .append(StringUtil.BLANK_SPACE)
                                                .append(valueOfField)
                                                .append(StringUtil.NEW_LINE);
                                    }
                                }
                            }
                        }
                        if (includeHeader) {
                            objectValuesFormatted.append(
                                    "==============================================================");
                        }
                    }
                } else {
                    return "null";
                }
            } catch (Exception e) {
                log.error("ERROR: " + e.getMessage());
            }
            return objectValuesFormatted.toString();
        } catch (Exception ignored) {
            return object.toString();
        }
    }

    public static Map getFieldValueMap(Object object) {
        Map objectFieldValueMap = new HashMap();
        try {
            if (object != null) {
                Class clazz = object.getClass();
                if (!isPrimitive(clazz) && !isBasic(clazz)) {
                    Collection/*<Field>*/ fields = getFields(clazz);
                    String fieldName;
                    Object[] args = new Object[0];
                    for (Iterator objectFields = fields.iterator(); objectFields.hasNext(); ) {
                        Field field = (Field) objectFields.next();
                        fieldName = field.getName();
                        objectFieldValueMap.put(fieldName, invokeGetter(object, fieldName));
                    }
                } else {
                    objectFieldValueMap.put("[".concat(ReflectionUtil.getSimpleClassName(clazz)).concat("]"), object);
                }
            }
        } catch (Exception e) {
            log.error("ERROR: " + e.getMessage());
        }
        return objectFieldValueMap;
    }

    public static void encloseStringValuesWithCDATA(Object object) {
        if (object != null) {
            Class clazz = object.getClass();
            Collection/*<Method>*/ declaredGetterMethods = getDeclaredGetterMethods(clazz);
            Collection/*<Method>*/ declaredSetterMethods = getDeclaredSetterMethods(clazz);
            for (Iterator declaredGetterMethod = declaredGetterMethods.iterator(); declaredGetterMethod.hasNext(); ) {
                final Method getterMethod = (Method) declaredGetterMethod.next();
                Object[] argsGetter = {};
                Object[] argsSetter = {String.class};
                Object value;
                Class returnedType = getterMethod.getReturnType();
                Method setterMethod = (Method) CollectionUtils.find(declaredSetterMethods, new Predicate() {
                    public boolean evaluate(Object o) {
                        return ((Method) o).getName().equals(getterMethod.getName().replaceFirst("get", "set"));
                    }
                });
                if (setterMethod != null) {
                    if (returnedType.equals(String.class)) {

                        try {
                            value = getterMethod.invoke(object, argsGetter);
                            argsSetter[0] = StringUtil.enclose((String) value,
                                    StringUtil.CDATA_START,
                                    StringUtil.CDATA_END);
                            setterMethod.invoke(object, argsSetter);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                    } else if (implementsClass(returnedType, Collection.class)) {
                        log.info("Is Collection");
                    } else if (isString(returnedType)) {
                        log.info("Is String");
                    } else if (isPrimitive(returnedType)) {
                        log.info("Is Primitive");
                    }
                }
            }
        }
    }

    public static Collection/*<Field>*/ getFields(Class clazz, Collection/*<Field>*/ excludeFields) {
        Collection/*<Method>*/ result = getFields(clazz);
        final Collection/*<Method>*/ exclusions = CollectionUtils.transformedCollection(excludeFields == null
                ? new ArrayList() : excludeFields,
                new Transformer() {
                    public Object transform(
                            Object o) {
                        return ((Field) o).getName();
                    }
                });
        return CollectionUtils.select(result, new Predicate() {
            public boolean evaluate(Object o) {
                Field field = ((Field) o);
                String fieldName = field.getName();
                return !exclusions.contains(fieldName);
            }
        });

    }

    public static Collection/*<Field>*/ getFields(Class clazz) {
        return CollectionUtils.union(Arrays.asList(clazz.getFields()), getDeclaredFields(clazz));
    }

    public static Collection/*<Method>*/ getMethods(Class clazz) {
        return getMethods(clazz, null);
    }

    public static Collection/*<String>*/ getMethods(Class clazz, Collection/*<String>*/ excludeMethods) {
        Collection/*<Method>*/ result = new ArrayList(CollectionUtils.union(Arrays.asList(clazz.getDeclaredMethods()),
                Arrays.asList(clazz.getMethods())));
        final Collection/*<Method>*/ exclusions = excludeMethods == null
                ? new ArrayList()
                : excludeMethods;
        return CollectionUtils.select(result, new Predicate() {
            public boolean evaluate(Object o) {
                Method method = ((Method) o);
                String methodName = method.getName();
                return !exclusions.contains(methodName);
            }
        });
    }

    public static Collection/*<String>*/ getMethodsNames(Class clazz) {
        return getMethodsNames(clazz, null);
    }

    public static Collection/*<String>*/ getMethodsNames(Class clazz, Collection/*<String>*/ excludeMethods) {
        Collection result = new ArrayList(CollectionUtils.union(Arrays.asList(clazz.getDeclaredMethods()),
                Arrays.asList(clazz.getMethods())));
        final Collection exclusions = excludeMethods == null
                ? new ArrayList()
                : excludeMethods;
        CollectionUtils.transform(result, new Transformer() {
            public Object transform(Object o) {
                Method method = ((Method) o);
                String methodName = method.getName();
                if (exclusions.contains(methodName)) {
                    return null;
                } else {
                    return methodName;
                }
            }
        });
        CollectionUtils.filter(result, PredicateUtils.notNullPredicate());
        Collections.sort((List) result);
        return result;
    }

    public static Collection/*<Method>*/ getDeclaredMethodsNames(Class clazz) {
        return getDeclaredMethodsNames(clazz, null);
    }

    public static Collection/*<String>*/ getDeclaredMethodsNames(Class clazz, Collection/*<String>*/ excludeMethods) {
        Collection/*<Method>*/ result = new ArrayList(Arrays.asList(clazz.getDeclaredMethods()));
        final Collection/*<Method>*/ exclusions = excludeMethods == null
                ? new ArrayList()
                : excludeMethods;
        CollectionUtils.transform(result, new Transformer() {
            public Object transform(Object o) {
                Method method = ((Method) o);
                String methodName = method.getName();
                if (exclusions.contains(methodName)) {
                    return null;
                } else {
                    return methodName;
                }
            }
        });
        CollectionUtils.filter(result, PredicateUtils.notNullPredicate());
        Collections.sort((List) result);
        return result;
    }

    public static Collection/*<Method>*/ getDeclaredGetterMethods(Class clazz) {
        return CollectionUtils.select(Arrays.asList(clazz.getDeclaredMethods()), new Predicate() {
            public boolean evaluate(Object o) {
                return ((Method) o).getName().startsWith("get");
            }
        });
    }

    public static Collection/*<Field>*/ getDeclaredFields(Object object) {
        return getDeclaredFields(object.getClass());
    }

    public static Collection/*<Field>*/ getDeclaredFields(Class clazz) {
        return Arrays.asList(clazz.getDeclaredFields());
    }

    public static Collection/*<Method>*/ getGetterMethods(Class clazz) {
        return CollectionUtils.select(getMethods(clazz), new Predicate() {
            public boolean evaluate(Object o) {
                return ((Method) o).getName().startsWith("get");
            }
        });
    }

    public static Collection/*<Method>*/ getSetterMethods(Class clazz) {
        return CollectionUtils.select(getMethods(clazz), new Predicate() {
            public boolean evaluate(Object o) {
                return ((Method) o).getName().startsWith("set");
            }
        });
    }

    public static Collection/*<Method>*/ getGetterMethodsForField(Class clazz, final String fieldName) {
        return CollectionUtils.select(Arrays.asList(clazz.getMethods()), new Predicate() {
            public boolean evaluate(Object o) {
                return ((Method) o).getName().equals("get".concat(StringUtil.capitalize(fieldName)));
            }
        });
    }

    public static Collection/*<Method>*/ getSetterMethodsForField(Class clazz, final String fieldName) {
        return CollectionUtils.select(Arrays.asList(clazz.getMethods()), new Predicate() {
            public boolean evaluate(Object o) {
                return ((Method) o).getName().equals("set".concat(StringUtil.capitalize(fieldName)));
            }
        });
    }

    public static Field getFieldByFieldName(Class clazz, final String fieldName) {
        return (Field) CollectionUtils.find(getFields(clazz), new Predicate() {
            public boolean evaluate(Object o) {
                return ((Field) o).getName().equals(fieldName);
            }
        });
    }

    public static Field getFieldByFieldName(Object object, final String fieldName) {
        return getFieldByFieldName(object.getClass(), fieldName);
    }

    public static Collection/*<Method>*/  getDeclaredSetterMethods(Class clazz) {
        return CollectionUtils.select(Arrays.asList(clazz.getDeclaredMethods()), new Predicate() {
            public boolean evaluate(Object o) {
                return ((Method) o).getName().startsWith("set");
            }
        });
    }

    public static boolean implementsClass(Class clazz, final Class implementedClass) {
        return CollectionUtils.find(Arrays.asList(clazz.getInterfaces()), new Predicate() {
            public boolean evaluate(Object o) {
                return ((Class) o).getName().equals(implementedClass.getName());
            }
        }) != null;
    }

    public static boolean implementsClass(Class clazz, final String implementedClass) {
        return CollectionUtils.find(Arrays.asList(clazz.getInterfaces()), new Predicate() {
            public boolean evaluate(Object o) {
                return ((Class) o).getName().equals(implementedClass);
            }
        }) != null;
    }

    public static boolean isString(Class clazz) {
        return clazz.getName().equalsIgnoreCase("String") || clazz.getName().equalsIgnoreCase("java.lang.String");
    }

    public static boolean isInteger(Class clazz) {
        return clazz.getName().equalsIgnoreCase("Integer") || clazz.getName().equalsIgnoreCase("java.lang.Integer");
    }

    public static boolean isDouble(Class clazz) {
        return clazz.getName().equalsIgnoreCase("Double") || clazz.getName().equalsIgnoreCase("java.lang.Double");
    }

    public static boolean isFloat(Class clazz) {
        return clazz.getName().equalsIgnoreCase("Float") || clazz.getName().equalsIgnoreCase("java.lang.Float");
    }

    public static boolean isBoolean(Class clazz) {
        return clazz.getName().equalsIgnoreCase("Boolean") || clazz.getName().equalsIgnoreCase("java.lang.Boolean");
    }

    public static boolean isPrimitive(final Class clazz) {
        return CollectionUtils.find(PRIMITIVE_TYPES, new Predicate() {
            public boolean evaluate(Object o) {
                return ((Class) o).getName().equals(clazz.getName());
            }
        }) != null;
    }

    public static boolean isBasic(final Class clazz) {
        return CollectionUtils.find(BASIC_CLASSES, new Predicate() {
            public boolean evaluate(Object o) {
                return ((Class) o).getName().equals(clazz.getName());
            }
        }) != null;
    }

    public static Class getPrimitive(final Class clazz) {
        return (Class) CollectionUtils.find(PRIMITIVE_TYPES, new Predicate() {
            public boolean evaluate(Object o) {
                return ((Class) o).getName().equals(clazz.getName());
            }
        });
    }

    public static Object invokeGetter(Object object, final String fieldName) {
        Object value = null;
        Method method = (Method) CollectionUtils.find(getGetterMethods(object.getClass()), new Predicate() {
            public boolean evaluate(Object o) {
                Method innerMethod = (Method) o;
                return innerMethod.getName().toUpperCase().equals(("get" + fieldName).toUpperCase());
            }
        });
        Object[] args = {};
        try {
            value = method.invoke(object, args);
        } catch (IllegalAccessException e) {
            log.error("Impossible to invoke method: " + "get" + fieldName + " because of an IllegalAccessException");
            try {
                Field field = getFieldByFieldName(object, fieldName);
                field.setAccessible(true);
                value = field.get(object);
            } catch (IllegalAccessException e1) {
                log.error("Impossible to get the value of the field: "
                        + fieldName
                        + " directly from the object: "
                        + object
                        + " because of an IllegalAccessException");
            }
        } catch (InvocationTargetException e) {
            log.error("Impossible to invoke method: " + "get" + fieldName + " because of an InvocationTargetException");
            try {
                Field field = getFieldByFieldName(object, fieldName);
                field.setAccessible(true);
                value = field.get(object);
            } catch (IllegalAccessException e1) {
                log.error("Impossible to get the value of the field: "
                        + fieldName
                        + " directly from the object: "
                        + object
                        + " because of an IllegalAccessException");
            }
        } catch (NullPointerException e) {
            log.error("Impossible to invoke method: "
                    + "get"
                    + fieldName
                    + " because of an NullPointerException, "
                    + "may be the incoming field :"
                    + fieldName
                    + " have not a getter");
            try {
                Field field = getFieldByFieldName(object, fieldName);
                if (field != null) {
                    field.setAccessible(true);
                    value = field.get(object);
                }
            } catch (IllegalAccessException e1) {
                log.error("Impossible to get the value of the field: "
                        + fieldName
                        + " directly from the object: "
                        + object
                        + " because of an IllegalAccessException");
            }
        }
        return value;
    }

    public static Class getClassFromPrimitive(Class clazz) {
        Class newClazz = (Class) PRIMITIVE_AND_BASIC_TYPES.get(clazz);
        if (newClazz != null) {
            return newClazz;
        } else {
            return clazz;
        }

    }

    public static void invokeVoid(Object object, final String voidName, final Object[] args) {
        final Collection /*<Object>*/ objectClasses = args == null ? new ArrayList() : new ArrayList(Arrays.asList(args));
        CollectionUtils.transform(objectClasses, new Transformer() {
            public Object transform(Object o) {
                return o.getClass();
            }
        });

        Collection/*<Method>*/ methods = CollectionUtils.select(getMethods(object.getClass()), new Predicate() {
            public boolean evaluate(Object o) {
                return ((Method) o).getName().equals(voidName);
            }
        });

        Method method = (Method) CollectionUtils.find(methods, new Predicate() {
            public boolean evaluate(Object o) {
                Method innerMethod = (Method) o;

                final Collection /*<Class>*/ parameterClasses = Arrays.asList(innerMethod.getParameterTypes());
                CollectionUtils.transform(parameterClasses, new Transformer() {
                    public Object transform(Object o) {
                        return getClassFromPrimitive((Class) o);
                    }
                });

                boolean result = innerMethod.getName().toUpperCase().equals((voidName).toUpperCase());
                if (args == null) {
                    return true;
                }
                if (parameterClasses.size() == objectClasses.size()) {
                    Class[] parameterClassesArray = (Class[]) parameterClasses.toArray();
                    Object[] objectClassesArray = objectClasses.toArray();

                    for (int i = 0; i < parameterClassesArray.length; i++) {
                        Class parameterClass = parameterClassesArray[i];
                        Class objectClass = (Class) objectClassesArray[i];
                        result = result && (checkWheterOrNotSuperclassesExtendsCriteria(objectClass, parameterClass)
                                || checkWheterOrNotSuperclassesImplementsCriteria(objectClass,
                                parameterClass));
                    }
                } else {
                    result = false;
                }

                return result;
            }
        });

        try {
            method.invoke(object, args);
        } catch (IllegalArgumentException e) {
            log.error("Impossible to invoke void method: " + voidName + " because of an IllegalArgumentException");
        } catch (IllegalAccessException e) {
            log.error("Impossible to invoke void method: " + voidName + " because of an IllegalAccessException");
        } catch (InvocationTargetException e) {
            log.error("Impossible to invoke void method: " + voidName + " because of an InvocationTargetException");
            Throwable exception = e.getTargetException();
            exception.printStackTrace();
        } catch (NullPointerException e) {
            log.error("Impossible to invoke void method: "
                    + voidName
                    + " because of an NullPointerException, "
                    + "may be the incoming void name:"
                    + voidName
                    + " does not exists");
        }
    }

    public static Object invokeMethodThrowException(Object object, final String methodName, final Object[] args)
            throws Exception {

        Object result;
        final Collection /*<Object>*/ objectClasses = new ArrayList(Arrays.asList(args));
        CollectionUtils.transform(objectClasses, new Transformer() {

            public Object transform(Object o) {
                return o.getClass();
            }
        });
        Collection/*<Method>*/ methods = CollectionUtils.select(getMethods(object.getClass()), new Predicate() {
            public boolean evaluate(Object o) {
                return ((Method) o).getName().equals(methodName);
            }
        });

        Method method = (Method) CollectionUtils.find(methods, new Predicate() {
            public boolean evaluate(Object o) {
                Method innerMethod = (Method) o;

                final Collection /*<Class>*/ parameterClasses = Arrays.asList(innerMethod.getParameterTypes());
                CollectionUtils.transform(parameterClasses, new Transformer() {
                    public Object transform(Object o) {
                        return getClassFromPrimitive((Class) o);
                    }
                });

                return (innerMethod.getName().toUpperCase().equals((methodName).toUpperCase())) & (Arrays.equals(
                        parameterClasses.toArray(),
                        objectClasses.toArray()));
            }
        });

        try {
            result = method.invoke(object, args);
        } catch (IllegalAccessException e) {
            log.error("Impossible to invoke method: "
                    + methodName
                    + " because of an IllegalAccessException. "
                    + e.getMessage());
            throw e;
        } catch (InvocationTargetException e) {
            log.error("Impossible to invoke method: "
                    + methodName
                    + " because of an InvocationTargetException. "
                    + e.getMessage());
            throw e;
        } catch (NullPointerException e) {
            log.error("Impossible to invoke method: "
                    + methodName
                    + " because of an NullPointerException, "
                    + "maybe the incoming method name: '"
                    + methodName
                    + "' does not exists");
            throw e;
        }
        return result;
    }

    public static Object invokeMethod(Object object, final String methodName, final Object[] args) {
        Object result = new Object();
        try {
            return invokeMethodThrowException(object, methodName, args);
        } catch (Throwable e) {
            log.error("Impossible to invoke method: "
                    + methodName
                    + " because of an Exception of type: "
                    + e.getClass()
                    + ". The exception message is: "
                    + e.getMessage());
        }
        return result;
    }

    /**
     * @param object    The object for simple setter invokation
     * @param fieldName The field on which the simple setter will be performed
     * @param value     The value to be assigned as parameter to the setter method
     * @noinspection UnusedAssignment
     */
    public static void invokeSimpleSetter(Object object, final String fieldName, final Object value) {
        Method method = (Method) CollectionUtils.find(getDeclaredSetterMethods(object.getClass()), new Predicate() {
            public boolean evaluate(Object o) {
                Method innerMethod = (Method) o;
                boolean result = false;
                boolean setterFound = (innerMethod.getName().toUpperCase().equals(("set" + fieldName).toUpperCase()));
                if (setterFound) {
                    result = innerMethod.getParameterTypes()[0].getName().equals(value.getClass().getName());
                    if (!result) {
                        Class clazz = getPrimitive(innerMethod.getParameterTypes()[0]);
                        if (clazz != null) {
                            String primitive = getClassFromPrimitive(clazz).getName();
                            if (!StringUtil.isNullOrEmpty(primitive)) {
                                result = (innerMethod.getName().toUpperCase().equals(("set" + fieldName).toUpperCase()))
                                        && (primitive.equals(value.getClass().getName()));
                            }
                        }
                    }
                }
                return result;
            }
        });
        Object[] args = {value};

        try {
            object = method.invoke(object, args);
        } catch (IllegalArgumentException e) {
            log.error("Impossible to invoke method: " + fieldName + " because of an IllegalArgumentException");
        } catch (IllegalAccessException e) {
            log.error("Impossible to invoke method: " + fieldName + " because of an IllegalAccessException");
        } catch (InvocationTargetException e) {
            log.error("Impossible to invoke method: " + fieldName + " because of an InvocationTargetException");
        } catch (NullPointerException e) {
            log.error("Impossible to invoke method: "
                    + fieldName
                    + " because of an NullPointerException, "
                    + "may be the incoming field:"
                    + fieldName
                    + " have not a setter");
        }
    }

    public static void fillObjectWithMap(Object object, Map arg) {
        String key;
        String value;
        for (Iterator iter = arg.keySet().iterator(); iter.hasNext(); ) {
            key = (String) iter.next();
            value = (String) arg.get(key);
            ReflectionUtil.invokeSimpleSetter(object, key, value);
        }
    }

    public static Collection/*<String>*/ getFieldNames(Class clazz) {
        return getFieldNames(clazz, null);
    }

    public static Collection/*<String>*/ getFieldNames(Class clazz, Collection/*<String>*/ excludeFields) {
        Collection/*<Field>*/ result = getFields(clazz);
        final Collection/*<Field>*/ exclusions = excludeFields == null
                ? new ArrayList()
                : excludeFields;
        CollectionUtils.transform(result, new Transformer() {
            public Object transform(Object o) {
                Field field = ((Field) o);
                String fieldName = field.getName();
                if (fieldName.startsWith("class$")
                    /*|| (field.toString().indexOf("final") > 0
                 && field.toString().indexOf("final") < field.toString().indexOf(fieldName))*/ || exclusions.contains(
                        fieldName)) {
                    return null;
                } else {
                    return fieldName;
                }
            }
        });
        CollectionUtils.filter(result, PredicateUtils.notNullPredicate());
        Collections.sort((List) result);
        return result;
    }

    /**
     * @param object The object to obtain the class name
     * @return The class name
     * @deprecated
     */
    public static String getSimpleClassName(Object object) {
        return getSimpleClassName(object.getClass());
    }

    public static String getSimpleClassName(Class clazz) {
        String className = null;
        if (clazz != null) {
            className = clazz.getName();
            try {
                className = clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1);
                className = className.substring(className.lastIndexOf("$") + 1);
                className = className.replaceAll(";", "[]");
            } catch (Exception e) {
                log.error("Impossible to get the simple name for the class: "
                        + clazz.getName()
                        + ". It'll be taken: "
                        + className);
            }
        }
        return className;
    }

    public static boolean isNullOrEmpty(Object object) {
        //TODO AMM 30072010: Este metodo explota para todos los tipos Map o Collection. Quizas sea bueno preguntar si el la clase al que pertenece el objeto es una instancia de Collection o Map e invocar el m�todo "isEmpty"
        boolean result = false;
        if (object != null) {
            Class clazz = object.getClass();
            if (isPrimitive(clazz)) {
                result = false;
            } else if (isString(clazz)) {
                result = StringUtil.isNullOrEmpty((String) object);
            } else if (object instanceof Collection || object instanceof Map) {
                try {
                    Method method = clazz.getMethod("isEmpty", new Class[]{});
                    Object value = method.invoke(object, new Object[]{});
                    result = ((Boolean) value).booleanValue();
                } catch (NoSuchMethodException e) {
                    log.error("Impossible to obtain the isEmpty method from object: " + object + ".");
                } catch (InvocationTargetException e) {
                    log.error("Impossible to invoke the isEmpty method to object: " + object + ".");
                } catch (IllegalAccessException e) {
                    log.error("Impossible to invoke the isEmpty method to object: " + object + ".");
                }
            }
        } else {
            return true;
        }
        return result;
    }

    public static boolean allFieldsAreNotEmptyOrNull(Object object) {
        String fieldName;
        if (object != null) {
            for (Iterator parameterListIter = getFieldNames(object.getClass()).iterator();
                 parameterListIter.hasNext(); ) {
                fieldName = (String) parameterListIter.next();
                Object value = invokeGetter(object, fieldName);
                if (isNullOrEmpty(value)) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }

    }

    public static Map getAllEmptyFields(Object object) {
        return getAllEmptyFields(object, null);
    }

    public static Map getAllEmptyFields(Object object, Collection/*<String>*/ excludeFields) {
        String fieldName;
        Map result = new Hashtable();
        if (excludeFields == null) {
            excludeFields = new ArrayList();
        }
        if (object != null) {
            for (Iterator parameterListIter = getFieldNames(object.getClass(), excludeFields).iterator();
                 parameterListIter.hasNext(); ) {
                fieldName = (String) parameterListIter.next();
                Object value = invokeGetter(object, fieldName);
                if (isNullOrEmpty(value) && !excludeFields.contains(fieldName)) {
                    result.put(fieldName,
                            "El objecto de tipo '"
                                    + getSimpleClassName(object.getClass())
                                    + "' contiene el campo '"
                                    + fieldName
                                    + "' nulo o vac�o");
                }
            }
        }
        return result;
    }

    public static Object getFieldType(Object object, final String fieldName) {
        Object value = null;
        if (ReflectionUtil.isBasic(object.getClass()) || StringUtil.isNullOrEmpty(fieldName)) {
            return object.getClass().getName();
        }
        Field field = (Field) CollectionUtils.find(getDeclaredFields(object), new Predicate() {
            public boolean evaluate(Object o) {
                Field innerField = (Field) o;
                return innerField.getName().toUpperCase().equals((fieldName).toUpperCase());
            }
        });
        return field.getType().getName();
    }

    public static Collection/*<Field>*/ getAllFieldsOfType(Class clazz,
                                                           Collection/*<Field>*/ excludeFields,
                                                           final Class type) {
        final Collection/*<Field>*/ result = new ArrayList();

        if (clazz != null) {
            CollectionUtils.forAllDo(getFields(clazz, excludeFields), new Closure() {
                public void execute(Object o) {
                    Field field = (Field) o;
                    if (field.getType().getName().equals(type.getName())) {
                        result.add(field);
                    }
                }
            });
        }
        return result;
    }

    public static Collection/*<Field>*/ getAllFieldsOfType(Object object,
                                                           Collection/*<Field>*/ excludeFields,
                                                           final Class type) {
        Collection/*<Field>*/ result = new ArrayList();

        if (object != null) {
            result = getAllFieldsOfType(object.getClass(), excludeFields, type);
        }
        return result;
    }

    public static Collection/*<Field>*/ getAllFieldsOfType(Object object, final Class type) {
        return getAllFieldsOfType(object, null, type);
    }

    public static Collection/*<Field>*/ getAllFieldsOfType(Class clazz, final Class type) {
        return getAllFieldsOfType(clazz, null, type);
    }

    public static Collection/*<String>*/ getAllFieldsNamesOfType(Object object,
                                                                 Collection/*<String>*/ excludeFields,
                                                                 final Class type) {
        final Collection result = getAllFieldsOfType(object, excludeFields, type);
        CollectionUtils.transform(result, new Transformer() {
            public Object transform(Object o) {
                return ((Field) o).getName();
            }
        });
        return result;
    }

    public static Collection/*<String>*/ getAllFieldsNamesOfType(Class clazz,
                                                                 Collection/*<String>*/ excludeFields,
                                                                 final Class type) {
        final Collection result = getAllFieldsOfType(clazz, excludeFields, type);
        CollectionUtils.transform(result, new Transformer() {
            public Object transform(Object o) {
                return ((Field) o).getName();
            }
        });
        return result;
    }

    public static Collection/*<String>*/ getAllFieldsNamesOfType(Object object, final Class type) {
        return getAllFieldsNamesOfType(object, null, type);
    }

    public static Collection/*<String>*/ getAllFieldsNamesOfType(Class clazz, final Class type) {
        return getAllFieldsNamesOfType(clazz, null, type);
    }

    public static Collection/*<Field>*/ getAllMethodsOfType(Object object,
                                                            Collection/*<Field>*/ excludeFields,
                                                            final Class type) {
        Collection/*<Field>*/ result = new ArrayList();

        if (object != null) {
            result = getAllMethodsOfType(object.getClass(), excludeFields, type);
        }
        return result;
    }

    public static Collection/*<Method>*/ getAllMethodsOfType(Class clazz,
                                                             Collection/*<Method>*/ excludeMethods,
                                                             final Class type) {
        final Collection/*<Method>*/ result = new ArrayList();
        if (clazz != null) {
            CollectionUtils.forAllDo(getMethods(clazz, excludeMethods), new Closure() {
                public void execute(Object o) {
                    Method field = (Method) o;
                    if (field.getReturnType().equals(type)) {
                        result.add(field);
                    }
                }
            });
        }
        return result;
    }

    public static Collection/*<String>*/ getAllMethodsNamesOfType(Object object,
                                                                  Collection/*<String>*/ excludeMethods,
                                                                  final Class type) {
        final Collection result = getAllMethodsOfType(object, excludeMethods, type);
        CollectionUtils.transform(result, new Transformer() {
            public Object transform(Object o) {
                return ((Method) o).getName();
            }
        });
        return result;
    }

    public static Collection/*<String>*/ getAllMethodsNamesOfType(Class clazz,
                                                                  Collection/*<String>*/ excludeMethods,
                                                                  final Class type) {
        final Collection result = getAllMethodsOfType(clazz, excludeMethods, type);
        CollectionUtils.transform(result, new Transformer() {
            public Object transform(Object o) {
                return ((Method) o).getName();
            }
        });
        return result;
    }

    public static Collection/*<String>*/ getAllMethodsNamesOfType(Object object, final Class type) {
        return getAllMethodsNamesOfType(object, null, type);
    }

    public static Collection/*<String>*/ getAllMethodsNamesOfType(Class clazz, final Class type) {
        return getAllMethodsNamesOfType(clazz, null, type);
    }

    private static class FieldCompare implements Comparator {
        public int compare(Object o1, Object o2) {
            String path1 = ((Field) o1).getName();
            String path2 = ((Field) o2).getName();
            return path1.compareTo(path2);
        }
    }

    public static boolean checkWheterOrNotSuperclassesImplementsCriteria(final Class clazz,
                                                                         final Class interfaceCriteria) {
        boolean result;
        if (clazz != null && clazz != Object.class) {
            Collection /*<Class>*/ incomingInterfaces = Arrays.asList(clazz.getInterfaces());
            if (incomingInterfaces.isEmpty()) {
                Class superClass = clazz.getSuperclass();
                return checkWheterOrNotSuperclassesImplementsCriteria(superClass, interfaceCriteria);
            }
            result = CollectionUtils.find(incomingInterfaces, new Predicate() {
                public boolean evaluate(Object o) {
                    String clazzName = ((Class) o).getName();
                    return clazzName.equals(interfaceCriteria.getName());
                }
            }) != null;
            if (!result) {
                Class superClass = clazz.getSuperclass();
                return checkWheterOrNotSuperclassesImplementsCriteria(superClass, interfaceCriteria);
            }
            return result;
        }
        return false;
    }

    public static boolean checkWheterOrNotSuperclassesExtendsCriteria(Class clazz, Class superClassCriteria) {
        boolean result;
        if (clazz != null && clazz != Object.class) {
            String clazzName = clazz.getName();
            result = clazzName.equals(superClassCriteria.getName());
            if (!result) {
                Class superClass = clazz.getSuperclass();
                return checkWheterOrNotSuperclassesExtendsCriteria(superClass, superClassCriteria);
            }
            return result;
        }
        return false;
    }

}
