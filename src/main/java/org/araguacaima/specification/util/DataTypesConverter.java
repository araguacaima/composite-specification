package com.bbva.templates.validation.util;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alejandro on 05/03/2015.
 */
public class DataTypesConverter {

    public static final Map<Class, DataTypeView> DATA_TYPES_CONVERTER = new HashMap<Class, DataTypeView>();
    private static final Map<String, Class> PRIMITIVE_CLASS_MAP = new HashMap<String, Class>();

    static {
        DATA_TYPES_CONVERTER.put(Integer.TYPE, new DataTypeView("integer", "int32", "Integer", false));
        DATA_TYPES_CONVERTER.put(Long.TYPE, new DataTypeView("integer", "int64", "Integer", false));
        DATA_TYPES_CONVERTER.put(Float.TYPE, new DataTypeView("number", "float", "Number", false));
        DATA_TYPES_CONVERTER.put(Double.TYPE, new DataTypeView("number", "double", "Number", false));
        DATA_TYPES_CONVERTER.put(String.class, new DataTypeView("string", null, "String", false));
        DATA_TYPES_CONVERTER.put(Byte.TYPE, new DataTypeView("string", "byte", "String", false));
        DATA_TYPES_CONVERTER.put(Boolean.TYPE, new DataTypeView("boolean", null, "Boolean", false));
        DATA_TYPES_CONVERTER.put(Date.class, new DataTypeView("string", "date", "Date", false));
        DATA_TYPES_CONVERTER.put(DateTime.class, new DataTypeView("string", "datetime", "DateTime", false));
        DATA_TYPES_CONVERTER.put(Period.class, new DataTypeView("string", "period", "Period", false));
        DATA_TYPES_CONVERTER.put(java.sql.Date.class, new DataTypeView("string", "date", "Date", false));
        DATA_TYPES_CONVERTER.put(Integer.class, new DataTypeView("integer", "int32", "Integer", false));
        DATA_TYPES_CONVERTER.put(Long.class, new DataTypeView("integer", "int64", "Integer", false));
        DATA_TYPES_CONVERTER.put(Float.class, new DataTypeView("number", "float", "Number", false));
        DATA_TYPES_CONVERTER.put(Double.class, new DataTypeView("number", "double", "Number", false));
        DATA_TYPES_CONVERTER.put(Byte.class, new DataTypeView("string", "byte", "String", false));
        DATA_TYPES_CONVERTER.put(Boolean.class, new DataTypeView("boolean", null, "Boolean", false));
        DATA_TYPES_CONVERTER.put(Enum.class, new DataTypeView("string", null, "String", false));
        DATA_TYPES_CONVERTER.put(Character.class, new DataTypeView("char", null, "Character", false));
        DATA_TYPES_CONVERTER.put(null, new DataTypeView("unknown", null, null, false));
    }

    static {
        PRIMITIVE_CLASS_MAP.put("integer", Integer.class);
        PRIMITIVE_CLASS_MAP.put("int", Integer.class);
        PRIMITIVE_CLASS_MAP.put("long", Long.class);
        PRIMITIVE_CLASS_MAP.put("double", Double.class);
        PRIMITIVE_CLASS_MAP.put("float", Float.class);
        PRIMITIVE_CLASS_MAP.put("boolean", Boolean.class);
        PRIMITIVE_CLASS_MAP.put("char", Character.class);
        PRIMITIVE_CLASS_MAP.put("byte", Byte.class);
        PRIMITIVE_CLASS_MAP.put("string", String.class);
        PRIMITIVE_CLASS_MAP.put("short", Short.class);
    }

    public static DataTypeView getDataTypeView(String type) {
        if (type == null) {
            return null;
        }
        if (type.startsWith("List<")) {
            String temp = type.split("List<")[1];
            temp = temp.substring(0, temp.length() - 1);
            return new DataTypeView("list", null, temp, true);
        }
        if (type.startsWith("Map<")) {
            String temp = type.split("Map<")[1];
            temp = temp.substring(0, temp.length() - 1);
            return new DataTypeView("map", null, temp, true);
        }
        if (type.equalsIgnoreCase("Date")) {
            return new DataTypeView("date", null, "Date", false);
        }
        if (type.equalsIgnoreCase("DateTime") || type.equalsIgnoreCase("Date-Time")
                || type.equalsIgnoreCase("Date-Only") || type.equalsIgnoreCase("Time-Only")) {
            return new DataTypeView("datetime", null, "DateTime", false);
        }
        if (type.equalsIgnoreCase("Period")) {
            return new DataTypeView("period", null, "Period", false);
        }
        Class clazz = PRIMITIVE_CLASS_MAP.get(type.toLowerCase());
        if (clazz != null) {
            return DATA_TYPES_CONVERTER.get(clazz);
        } else {
            return new DataTypeView("complex", null, type, true);
        }
    }

    public static class DataTypeView {
        private String dataType;
        private String format;
        private String transformedDataType;
        private boolean complexDataType;

        public DataTypeView(String dataType, String format, String transformedDataType, boolean complexDataType) {

            this.dataType = dataType;
            this.format = format;
            this.transformedDataType = transformedDataType;
            this.complexDataType = complexDataType;
        }


        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getTransformedDataType() {
            return transformedDataType;
        }

        public void setTransformedDataType(String transformedDataType) {
            this.transformedDataType = transformedDataType;
        }

        public boolean isComplexDataType() {
            return complexDataType;
        }

        public void setComplexDataType(boolean isComplexDataType) {
            this.complexDataType = isComplexDataType;
        }

        public boolean isListOrMap() {
            return "list".equals(dataType) || "map".equals(dataType);
        }

        public String extractDataTypeFromListOrMap() {
            if (isListOrMap()) {
                return transformedDataType;
            } else {
                return null;
            }
        }
    }
}
