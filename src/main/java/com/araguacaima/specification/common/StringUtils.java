package com.araguacaima.specification.common;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static String firstToken(String input, Collection<String> tokens) {
        for (String token : tokens) {
            if (input.startsWith(token)) {
                return token;
            }
        }
        return EMPTY;
    }

    public static int firstIndexOf(String input, Collection tokens) {
        int firstIndexOf = input.length() + 1;
        for (Object token : tokens) {
            int newIndexOf = input.indexOf(String.valueOf(token));
            if (newIndexOf != -1 & newIndexOf <= firstIndexOf) {
                firstIndexOf = newIndexOf;
            }
        }
        return firstIndexOf > input.length() ? -1 : firstIndexOf;
    }

    public static Collection<String> splitBySeparators(String input, Collection separators) {
        if (isNotBlank(input)) {
            String separatorMask = String.valueOf(System.currentTimeMillis());
            for (Object separator : separators) {
                input = input.replaceAll(encloseEachCharacterOnlyPrefix(String.valueOf(separator), "\\"), separatorMask);
            }
            Collection<String> result = Arrays.asList(input.split(separatorMask));
            CollectionUtils.transform(result, String::trim);
            return CollectionUtils.select(result, StringUtils::isNotBlank);
        } else {
            return new ArrayList<>();
        }
    }

    public static String encloseEachCharacterOnlyPrefix(String input, String enclosePrefix) {
        char[] chars = input.toCharArray();
        StringBuilder result = new StringBuilder();

        for (char aChar : chars) {
            result.append(enclosePrefix).append(aChar);
        }
        return result.toString();
    }
}
