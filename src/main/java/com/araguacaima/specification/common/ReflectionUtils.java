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

package com.araguacaima.specification.common;

import com.araguacaima.specification.util.DataTypesConverter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@SuppressWarnings({"UnusedReturnValue"})

public class ReflectionUtils implements Serializable {
    public static final Collection<String> COMMONS_TYPES_PREFIXES = new ArrayList<String>() {
        {
            add("java.lang");
            add("java.util");
            add("java.math");
            add("java.io");
            add("java.sql");
            add("java.text");
            add("java.net");
            add("org.joda.time");
        }
    };
    public static final Collection<String> COMMONS_JAVA_TYPES_EXCLUSIONS = new ArrayList<String>() {
        {
            add("java.util.Currency");
            add("java.util.Calendar");
            add("org.joda.time.Period");
        }
    };
    public static final Transformer<Field, String> FIELD_NAME_TRANSFORMER = Field::getName;
    public static final Predicate<Method> METHOD_IS_GETTER_PREDICATE = method -> method.getName().matches
            ("get[A-Z]+.*") || method.getName().matches(
            "is[A-Z]+.*");
    public static final MethodFilter USER_DECLARED_METHODS =
            (method -> !method.isBridge() && !method.isSynthetic());
    private static final DataTypesConverter dataTypesConverter = new DataTypesConverter();
    private static final Field[] EMPTY_FIELD_ARRAY = new Field[0];

    private static final Method[] EMPTY_METHOD_ARRAY = new Method[0];

    private static final Map<Class<?>, Method[]> declaredMethodsCache = new ConcurrentReferenceHashMap<>(256);


    private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentReferenceHashMap<>(256);

    public static LinkedList<Class<?>> recursivelyGetAllSuperClasses(Class<?> clazz) {
        if (clazz == null || Object.class.getName().equals(clazz.getName()) || isCollectionImplementation(
                clazz) || getFullyQualifiedJavaTypeOrNull(clazz.getName(), true) != null) {
            return new LinkedList<>();
        } else {
            LinkedList<Class<?>> result = new LinkedList<>();
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && !Object.class.getName().equals(superclass.getName())) {
                result.add(superclass);
                result.addAll(recursivelyGetAllSuperClasses(superclass));
            }
            return result;
        }
    }

    public static boolean isCollectionImplementation(Class<?> clazz) {
        return clazz != null && (Collection.class.isAssignableFrom(clazz) || Object[].class.isAssignableFrom(clazz)
                || clazz.isArray());
    }

    public static String getFullyQualifiedJavaTypeOrNull(String type, boolean considerLists) {
        if (type == null) {
            return null;
        }
        DataTypesConverter.DataTypeView dataTypeView = dataTypesConverter.getDataTypeView(type);
        type = dataTypeView.getTransformedDataType();
        String transformedType;
        boolean complexType = DataTypesConverter.DataTypeView.COMPLEX_TYPE.equals(dataTypeView.getDataType());
        if (complexType) {
            transformedType = type;
        } else {
            transformedType = StringUtils.capitalize(type);
        }
        Class<?> clazz;
        if (considerLists) {
            if (isList(type)) {
                String generics = getExtractedGenerics(type);
                final String javaType = getFullyQualifiedJavaTypeOrNull(generics, false);
                if (StringUtils.isBlank(javaType)) {
                    return null;
                } else {
                    return "java.util.List<" + javaType + ">";
                }
            }
        }
        if (!complexType) {
            for (String javaTypePrefix : COMMONS_TYPES_PREFIXES) {
                try {
                    clazz = Class.forName(transformedType.contains(".") ? transformedType : javaTypePrefix + "." +
                            transformedType);
                    if (considerLists) {
                        if (isList(type)) {
                            return "java.util.List<" + clazz.getName() + ">";
                        }
                    }
                    if (!COMMONS_JAVA_TYPES_EXCLUSIONS.contains(clazz.getName())) {
                        return clazz.getName();
                    }
                } catch (ClassNotFoundException ignored) {

                }
            }
        }
        try {
            Method method = Class.class.getDeclaredMethod("getPrimitiveClass", String.class);
            method.setAccessible(true);
            clazz = (Class<?>) method.invoke(Class.forName("java.lang.Class"), StringUtils.uncapitalize(type));
            if (considerLists) {
                if (isList(type)) {
                    return "java.util.List<" + clazz.getName() + ">";
                }
            }
            return clazz.getName();
        } catch (InvocationTargetException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException
                | NullPointerException ignored) {
        }
        return null;
    }

    public static boolean isList(String type) {
        try {
            boolean result = type.equals("List") || type.startsWith("List<") || type.startsWith("java.util.List<") || type.equals(
                    "Collection") || type.startsWith("Collection<") || type.startsWith("java.util.Collection<");
            if (!result) {
                String firstPartType = StringUtils.defaultIfBlank(returnNativeClass(type), type.split("<")[0]);
                Class.forName(firstPartType);
            }
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static String returnNativeClass(String type) {
        Class<?> clazz;
        type = type.split("<")[0];
        for (String javaTypePrefix : COMMONS_TYPES_PREFIXES) {
            try {
                clazz = Class.forName(type.contains(".") ? type : javaTypePrefix + "." + type);
                return clazz.getName();
            } catch (ClassNotFoundException ignored) {

            }
        }
        return null;
    }

    public static String getExtractedGenerics(String s) {
        String s1 = s.trim();
        try {
            String firstPartType = s1.split("<")[1];
            if (firstPartType.endsWith(">")) {
                return firstPartType.substring(0, firstPartType.length() - 1);
            }
        } catch (Throwable ignored) {
        }
        return s1;
    }

    public static Collection<String> getAllFieldsNamesOfType(Object object, final Class<?> type) {
        return getAllFieldsNamesOfType(object.getClass(), null, type);
    }

    public static Collection<String> getAllFieldsNamesOfType(Class<?> clazz,
                                                             Collection<String> excludeFields,
                                                             final Class<?> type) {
        return CollectionUtils.collect(getAllFieldsOfType(clazz, excludeFields, type), FIELD_NAME_TRANSFORMER);
    }

    public static Collection<Field> getAllFieldsOfType(Class<?> clazz,
                                                       Collection<String> excludeFields,
                                                       final Class<?> type) {
        final Collection<Field> result = new ArrayList<>();
        if (clazz != null) {
            IterableUtils.forEach(getAllFieldsIncludingParents(clazz, excludeFields), field -> {
                if (field.getType().getName().equals(type.getName())) {
                    result.add(field);
                }
            });
        }
        return result;
    }

    public static Collection<Field> getAllFieldsIncludingParents(Class<?> clazz, Collection<String> excludeFields) {
        Collection<Field> result = getAllFieldsIncludingParents(clazz);
        if (CollectionUtils.isEmpty(excludeFields)) {
            return result;
        }
        CollectionUtils.filterInverse(result, field -> fieldIsContainedIn(field, excludeFields));
        return result;
    }

    public static Collection<Field> getAllFieldsIncludingParents(Class<?> clazz) {
        return getAllFieldsIncludingParents(clazz,
                null,
                Modifier.STATIC | Modifier.VOLATILE | Modifier.NATIVE | Modifier.TRANSIENT);
    }

    public static Collection<Field> getAllFieldsIncludingParents(Class<?> clazz,
                                                                 final Integer modifiersInclusion,
                                                                 final Integer modifiersExclusion) {
        final Collection<Field> fields = new ArrayList<>();
        FieldFilter fieldFilterModifierInclusion = field -> modifiersInclusion == null || (field.getModifiers() &
                modifiersInclusion) != 0;
        FieldFilter fieldFilterModifierExclusion = field -> modifiersExclusion == null || (field.getModifiers() &
                modifiersExclusion) == 0;
        doWithFields(clazz,
                fields::add,
                modifiersInclusion == null ? (modifiersExclusion == null ? null : fieldFilterModifierExclusion) :
                        fieldFilterModifierInclusion);
        return fields;
    }

    public static Object invokeGetter(Object object, final String fieldName) {
        Object value = null;
        Method method = IterableUtils.find(getGetterMethods(object.getClass()),
                innerMethod -> innerMethod.getName().toUpperCase().equals(("get" + fieldName).toUpperCase()));
        Object[] args = {};
        try {
            value = method.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            try {
                Field field = getField(object, fieldName);
                field.setAccessible(true);
                value = field.get(object);
            } catch (IllegalAccessException ignored) {

            }
        } catch (NullPointerException e) {
            try {
                Field field = getField(object, fieldName);
                if (field != null) {
                    field.setAccessible(true);
                    value = field.get(object);
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        return value;
    }


    private static boolean fieldIsNotContainedIn(Field field, Collection<String> excludeFields) {
        return !fieldIsContainedIn(field, excludeFields);
    }

    private static boolean fieldIsContainedIn(Field field, Collection<String> excludeFields) {
        return IterableUtils.find(excludeFields, fieldName -> fieldNameEqualsToPredicate(field, fieldName)) != null;
    }

    private static boolean fieldNameEqualsToPredicate(Field field, String fieldName) {
        return !StringUtils.isBlank(fieldName) && field.getName().equals(fieldName);
    }

    public static Field getField(Object object, final String fieldName) {
        return getField(object.getClass(), fieldName);
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        return IterableUtils.find(getAllFieldsIncludingParents(clazz),
                field -> fieldNameEqualsToPredicate(field, fieldName));
    }

    public static Collection<Method> getGetterMethods(Class<?> clazz) {
        return CollectionUtils.select(getAllMethodsIncludingParents(clazz), METHOD_IS_GETTER_PREDICATE);
    }

    public static Collection<Method> getAllMethodsIncludingParents(Class<?> clazz) {
        return getAllMethodsIncludingParents(clazz, null, Modifier.VOLATILE | Modifier.NATIVE | Modifier.TRANSIENT);
    }

    public static Collection<Method> getAllMethodsIncludingParents(Class<?> clazz,
                                                                   final Integer modifiersInclusion,
                                                                   final Integer modifiersExclusion) {
        final Collection<Method> methods = new ArrayList<>();
        MethodFilter fieldFilterModifierInclusion = method -> modifiersInclusion == null || (method.getModifiers() &
                modifiersInclusion) != 0;
        MethodFilter fieldFilterModifierExclusion = method -> modifiersExclusion == null || (method.getModifiers() &
                modifiersExclusion) == 0;
        doWithMethods(clazz,
                methods::add,
                modifiersInclusion == null ? modifiersExclusion == null ? null : fieldFilterModifierExclusion :
                        fieldFilterModifierInclusion);
        return methods;
    }

    public static void doWithFields(Class<?> clazz, FieldCallback fc, FieldFilter ff) {
        // Keep backing up the inheritance hierarchy.
        Class<?> targetClass = clazz;
        do {
            Field[] fields = getDeclaredFields(targetClass);
            for (Field field : fields) {
                if (ff != null && !ff.matches(field)) {
                    continue;
                }
                try {
                    fc.doWith(field);
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);
    }

    public static void doWithMethods(Class<?> clazz, MethodCallback mc, MethodFilter mf) {
        // Keep backing up the inheritance hierarchy.
        Method[] methods = getDeclaredMethods(clazz, false);
        for (Method method : methods) {
            if (mf != null && !mf.matches(method)) {
                continue;
            }
            try {
                mc.doWith(method);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
            }
        }
        if (clazz.getSuperclass() != null && (mf != USER_DECLARED_METHODS || clazz.getSuperclass() != Object.class)) {
            doWithMethods(clazz.getSuperclass(), mc, mf);
        } else if (clazz.isInterface()) {
            for (Class<?> superIfc : clazz.getInterfaces()) {
                doWithMethods(superIfc, mc, mf);
            }
        }
    }

    public static Collection<Field> getDeclaredFields(Object object) {
        Field[] declaredFields = getDeclaredFields(object.getClass());
        return Arrays.asList(declaredFields);
    }

    private static Field[] getDeclaredFields(Class<?> clazz) {
        Field[] result = declaredFieldsCache.get(clazz);
        if (result == null) {
            try {
                result = clazz.getDeclaredFields();
                declaredFieldsCache.put(clazz, (result.length == 0 ? EMPTY_FIELD_ARRAY : result));
            } catch (Throwable ex) {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() +
                        "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }
        return result;
    }

    private static Method[] getDeclaredMethods(Class<?> clazz, boolean defensive) {
        Method[] result = declaredMethodsCache.get(clazz);
        if (result == null) {
            try {
                Method[] declaredMethods = clazz.getDeclaredMethods();
                List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
                if (defaultMethods != null) {
                    result = new Method[declaredMethods.length + defaultMethods.size()];
                    System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
                    int index = declaredMethods.length;
                    for (Method defaultMethod : defaultMethods) {
                        result[index] = defaultMethod;
                        index++;
                    }
                } else {
                    result = declaredMethods;
                }
                declaredMethodsCache.put(clazz, (result.length == 0 ? EMPTY_METHOD_ARRAY : result));
            } catch (Throwable ex) {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() +
                        "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }
        return (result.length == 0 || !defensive) ? result : result.clone();
    }

    private static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
        List<Method> result = null;
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method ifcMethod : ifc.getMethods()) {
                if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(ifcMethod);
                }
            }
        }
        return result;
    }

    @FunctionalInterface
    public interface FieldFilter {

        /**
         * Determine whether the given field matches.
         *
         * @param field the field to check
         */
        boolean matches(Field field);
    }

    @FunctionalInterface
    public interface MethodFilter {

        /**
         * Determine whether the given method matches.
         *
         * @param method the method to check
         */
        boolean matches(Method method);
    }

    @FunctionalInterface
    public interface FieldCallback {

        /**
         * Perform an operation using the given field.
         *
         * @param field the field to operate on
         */
        void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
    }

    @FunctionalInterface
    public interface MethodCallback {

        /**
         * Perform an operation using the given method.
         *
         * @param method the method to operate on
         */
        void doWith(Method method) throws IllegalArgumentException, IllegalAccessException;
    }
}