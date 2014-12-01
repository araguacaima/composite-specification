package org.commons.util;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @noinspection UnusedDeclaration
 */
public class StringUtil {

    public static final String EMPTY_STRING = "";
    public static final String LESS_THAN_SYMBOL = "<";
    public static final String GREATER_THAN_SYMBOL = ">";
    public static final String BLANK_SPACE = " ";
    public static final String EQUAL_SYMBOL = "=";
    public static final String COMMA_SYMBOL = ",";
    public static final String COLON_SYMBOL = ":";
    public static final String SEMICOLON_SYMBOL = ";";
    public static final char NEW_LINE = '\n';
    public static final char EMPTY_CHAR = ' ';
    public static final char TAB = '\t';
    public static final String DOUBLE_QUOTE = "\"";
    public static final String AMPERSAND = "&";
    public static final String SINGLE_QUOTE = "\'";
    public static final String CDATA_START = "<![CDATA[";
    public static final String CDATA_END = "]]>";
    public static final String SLASH = "/";
    public static final String BACKSLASH = "\\";
    public static final String DOUBLEBACKSLASH = BACKSLASH + BACKSLASH;
    public static final String HTML_BLANK_SPACE = "&nbsp;";
    public static final Map
/*<String, EncodingMatrix>*/
            SYMBOL_ENCODING_MATRIX_MAP = new HashMap/*<String, EncodingMatrix>*/();
    public static final Map
/*<String, EncodingMatrix>*/
            HTML_ENTITY_NAMED_ENCODING_MATRIX_MAP = new HashMap/*<String, EncodingMatrix>*/();
    public static final Map
/*<String, EncodingMatrix>*/
            UNICODE_CHARACTER_ENCODING_MATRIX_MAP = new HashMap/*<String, EncodingMatrix>*/();
    public static final Map
/*<String, EncodingMatrix>*/
            URL_ENCODING_ENCODING_MATRIX_MAP = new HashMap/*<String, EncodingMatrix>*/();

    public static final List/*<String>*/ HTML_COMPLETED_TAG_FIELDS = Arrays.asList(new String[]{"font"});
    public static final List/*<String>*/ HTML_UNCOMPLETED_TAG_FIELDS = Arrays.asList(new String[]{"img"});

    public static String decodeUTF8(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, "UTF-8");
    }

    public static byte[] encodeUTF8(String string) throws UnsupportedEncodingException {
        return string.getBytes("UTF-8");
    }


    public static boolean isNullOrEmpty(String incomingString) {
        return ((incomingString == null) || (incomingString.length() == 0));
    }

    public static String isNullOrEmptyDefaultValue(String incomingString) {
        return isNullOrEmptyDefaultValue(incomingString, EMPTY_STRING);
    }

    public static String isNullOrEmptyDefaultValue(String incomingString, String defaulValue) {
        return isNullOrEmpty(incomingString)
                ? defaulValue
                : incomingString;
    }

    public static String enclose(String input, String encloseIn, String encloseOut) {
        return (new StringBuffer().append(encloseIn).append(input).append(encloseOut)).toString();
    }

    public static String encloseOnlyPrefix(String input, String enclosePrefix) {
        return (new StringBuffer().append(enclosePrefix).append(input)).toString();
    }

    public static String encloseEachCharacterOnlyPrefix(String input, String enclosePrefix) {
        char[] chars = input.toCharArray();
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < chars.length; i++) {
            result.append(enclosePrefix).append(chars[i]);
        }
        return result.toString();
    }

    public static String encloseOnlySuffix(String input, String encloseSuffix) {
        return (new StringBuffer().append(input).append(encloseSuffix)).toString();
    }

    public static String capitalize(String s) {
        if (isNullOrEmpty(s)) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String toParragraph(String s) {
        if (isNullOrEmpty(s)) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static boolean representAnInteger(String value) {
        try {
            new Integer(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean representADouble(String value) {
        try {
            new Double(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean representAFloat(String value) {
        try {
            new Float(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String transformsCollectionIntoString(Collection/*<String>*/ tokens) {
        final StringBuffer result = new StringBuffer();
        CollectionUtils.forAllDo(tokens, new Closure() {
            public void execute(Object o) {
                result.append(o);
            }
        });
        return result.toString();
    }

    public static String transformsCollectionIntoStringWithSeperator(Collection/*<Object>*/ tokens, final String separator) {
        final StringBuffer result = new StringBuffer();
        CollectionUtils.forAllDo(tokens, new Closure() {
            public void execute(Object o) {
                result.append(o).append(separator);
            }
        });
        result.replace(result.length() - 1, result.length(), StringUtil.EMPTY_STRING);
        return result.toString();
    }

    public static String transformsCollectionIntoStringWithSeperatorDemarcatedBy(Collection/*<Object>*/ tokens, final String separator, final String demarcation) {
        final StringBuffer result = new StringBuffer();
        CollectionUtils.forAllDo(tokens, new Closure() {
            public void execute(Object o) {
                result.append(demarcation).append(o).append(demarcation).append(separator);
            }
        });
        result.replace(result.length() - 1, result.length(), StringUtil.EMPTY_STRING);
        return result.toString();
    }


    public static Collection splitBySeparators(String input, Collection separators) {
        if (!isNullOrEmpty(input)) {
            String separatorMask = String.valueOf(System.currentTimeMillis());
            for (Iterator separatorsIterator = separators.iterator(); separatorsIterator.hasNext(); ) {
                String separator = (String) separatorsIterator.next();
                input = input.replaceAll(encloseEachCharacterOnlyPrefix(separator, StringUtil.BACKSLASH),
                        separatorMask);
            }
            Collection result = Arrays.asList(input.split(separatorMask));
            CollectionUtils.transform(result, new Transformer() {
                public Object transform(Object o) {
                    return ((String) o).trim();
                }
            });
            return CollectionUtils.select(result, NotNullOrEmptyStringPredicate.getInstance());
        } else {
            return new ArrayList();
        }
    }

    public static boolean contains(String input, Collection tokens) {
        for (Iterator tokensIterator = tokens.iterator(); tokensIterator.hasNext(); ) {
            String token = (String) tokensIterator.next();
            if (input.equals(token)) {
                return true;
            }
        }
        return false;
    }

    public static String firstToken(String input, Collection tokens) {
        for (Iterator tokensIterator = tokens.iterator(); tokensIterator.hasNext(); ) {
            String token = (String) tokensIterator.next();
            if (input.startsWith(token)) {
                return token;
            }
        }
        return StringUtil.EMPTY_STRING;
    }

    public static int firstIndexOf(String input, Collection tokens) {
        int firstIndexOf = input.length() + 1;
        for (Iterator tokensIterator = tokens.iterator(); tokensIterator.hasNext(); ) {
            String token = (String) tokensIterator.next();
            int newIndexOf = input.indexOf(token);
            if (newIndexOf != -1 & newIndexOf <= firstIndexOf) {
                firstIndexOf = newIndexOf;
            }
        }
        return firstIndexOf > input.length()
                ? -1
                : firstIndexOf;
    }

    public static String deleteAll(String input, Collection tokens) {
        return replaceAll(input, tokens, StringUtil.EMPTY_STRING);
    }

    public static String replaceAll(String input, Collection tokens, String value) {
        for (Iterator tokensIterator = tokens.iterator(); tokensIterator.hasNext(); ) {
            String token = (String) tokensIterator.next();
            input = input.replaceAll(encloseEachCharacterOnlyPrefix(token, StringUtil.BACKSLASH), value);
        }
        return input;
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
    }

    public static String htmlText(String text) {
        StringBuffer sb = new StringBuffer();
        if (text == null) {
            return "";
        }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case 60: // '<'
                    sb.append("&lt;");
                    break;

                case 62: // '>'
                    sb.append("&gt;");
                    break;

                case 38: // '&'
                    sb.append("&amp;");
                    break;

                case 34: // '"'
                    sb.append("&quot;");
                    break;

                case 10: // '\n'
                    sb.append("<br>");
                    break;

                default:
                    sb.append(c);
                    break;
            }
        }

        return sb.toString();
    }

    public static String xmlText(String text) {
        StringBuffer sb = new StringBuffer();
        if (text == null) {
            return "";
        }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case 60: // '<'
                    sb.append("&lt;");
                    break;

                case 62: // '>'
                    sb.append("&gt;");
                    break;

                case 38: // '&'
                    sb.append("&amp;");
                    break;

                case 34: // '"'
                    sb.append("&quot;");
                    break;

                default:
                    sb.append(c);
                    break;
            }
        }

        return sb.toString();
    }

    public static String filterStack(String stack) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        StringReader sr = new StringReader(stack);
        BufferedReader br = new BufferedReader(sr);
        do {
            try {
                String line;
                if ((line = br.readLine()) == null) {
                    break;
                }
                if (stopLine(line)) {
                    pw.println(line);
                    break;
                }
                if (!filterLine(line)) {
                    pw.println(line);
                }
            } catch (Exception IOException) {
                return stack;
            }
        } while (true);
        return sw.toString();
    }

    private static boolean filterLine(String line) {
        for (int i = 0; i < DEFAULT_TRACE_FILTERS.length; i++) {
            if (line.indexOf(DEFAULT_TRACE_FILTERS[i]) > 0) {
                return true;
            }
        }

        return false;
    }

    private static boolean stopLine(String line) {
        for (int i = 0; i < DEFAULT_STOP_FILTERS.length; i++) {
            if (line.indexOf(DEFAULT_STOP_FILTERS[i]) > 0) {
                return true;
            }
        }

        return false;
    }

    private static final String DEFAULT_TRACE_FILTERS[] = {"junit.framework.TestCase",
            "junit.framework.TestResult",
            "junit.framework.TestSuite",
            "junit.framework.Assert.",
            "junit.swingui.TestRunner",
            "junit.awtui.TestRunner",
            "junit.textui.TestRunner",
            "java.lang.reflect.Method.invoke(",
            "org.apache.tools.ant."};
    private static final String DEFAULT_STOP_FILTERS[] = {"junit.framework.TestCase.runTest",
            "junit.framework.TestSuite.runTest"};

    public static String fullFillWithBlankSpace(int numberOfBlackSpaces) {
        return fullFillWithCharacter(numberOfBlackSpaces, StringUtil.BLANK_SPACE.toCharArray()[0]);
    }

    public static String fullFillWithBlankSpace(String textToFullFilling, int completeLengthToFullFilling) {
        return fullFillWithWithCharacter(textToFullFilling,
                completeLengthToFullFilling,
                StringUtil.BLANK_SPACE.toCharArray()[0]);
    }

    public static String fullFillWithCharacter(int numberOfCharacteres, char characterToFullFill) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < numberOfCharacteres; i++) {
            result.append(characterToFullFill);
        }
        return result.toString();
    }

    public static String fullFillWithWithCharacter(String textToFullFilling,
                                                   int completeLengthToFullFilling,
                                                   char characterToFullFill) {
        StringBuffer result = new StringBuffer();
        if (textToFullFilling.length() >= completeLengthToFullFilling) {
            return textToFullFilling;
        } else {
            return textToFullFilling.concat(fullFillWithBlankSpace(completeLengthToFullFilling
                    - textToFullFilling.length()));
        }
    }

    public static String centerTextFullFillingWithWithCharacter(String textToCenter,
                                                                int completeLengthToFullFilling,
                                                                char characterToFullFill) {
        StringBuffer result = new StringBuffer();
        if (textToCenter.length() >= completeLengthToFullFilling) {
            return textToCenter;
        } else {
            int lengthForFilling = completeLengthToFullFilling - textToCenter.length();
            int numbersOfCharacteresRight = lengthForFilling / 2;
            int numbersOfCharacteresLeft = lengthForFilling - numbersOfCharacteresRight;
            String charactersToAppendLeft = fullFillWithCharacter(numbersOfCharacteresLeft, characterToFullFill);
            String charactersToAppendRight = fullFillWithCharacter(numbersOfCharacteresRight, characterToFullFill);
            return charactersToAppendLeft.concat(textToCenter.concat(charactersToAppendRight));
        }
    }

    /**
     * Trim characters in prefix and suffix
     *
     * @param str String
     * @param ch  character which has to be removed
     * @return null, if str is null, otherwise string will be returned
     * without character prefixed/suffixed
     */
    public static String trim(String str, char ch) {
        if (str == null) {
            return null;
        }
        int count = str.length();
        int len = str.length();
        int st = 0;
        int off = 0;
        char[] val = str.toCharArray();

        while ((st < len) && (val[off + st] == ch)) {
            st++;
        }
        while ((st < len) && (val[off + len - 1] == ch)) {
            len--;
        }
        return ((st > 0) || (len < count))
                ? str.substring(st, len)
                : str;
    }

    /**
     * Trim characters in prefix
     *
     * @param str String
     * @param ch  character which has to be removed
     * @return null, if str is null, otherwise string will be returned
     * without character prefixed
     */
    public static String leftTrim(String str, char ch) {
        if (str == null) {
            return null;
        }
        int count = str.length();
        int len = str.length();
        int st = 0;
        int off = 0;
        char[] val = str.toCharArray();

        while ((st < len) && (val[off + st] == ch)) {
            st++;
        }

        return ((st > 0) || (len < count))
                ? str.substring(st, len)
                : str;
    }

    /**
     * Trim characters in suffix
     *
     * @param str String
     * @param ch  character which has to be removed
     * @return null, if str is null, otherwise string will be returned
     * without character suffixed
     */
    public static String rightTrim(String str, char ch) {
        if (str == null) {
            return null;
        }
        int count = str.length();
        int len = str.length();
        int st = 0;
        int off = 0;
        char[] val = str.toCharArray();

        while ((st < len) && (val[off + len - 1] == ch)) {
            len--;
        }
        return ((st > 0) || (len < count))
                ? str.substring(st, len)
                : str;
    }

}



