package com.araguacaima.specification.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class EvaluateExpression {

    public static Expression buildExpression(final String str, Map<String, Double> variables) {
        return new Object() {
            int pos = -1, ch;

            //if check pos+1 is smaller than string length ch is char at new pos
            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            //skips 'spaces' and if current char is what was searched, if true move to next char return true
            //else return false
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            Expression parse() {
                nextChar();
                Expression x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            Expression parseExpression() {
                Expression x = parseTerm();
                for (; ; ) {
                    if (eat('+')) { // addition
                        Expression a = x, b = parseTerm();
                        x = (() -> a.eval() + b.eval());
                    } else if (eat('-')) { // subtraction
                        Expression a = x, b = parseTerm();
                        x = (() -> a.eval() - b.eval());
                    } else {
                        return x;
                    }
                }
            }

            Expression parseTerm() {
                Expression x = parseFactor();
                for (; ; ) {
                    if (eat('*')) {
                        Expression a = x, b = parseFactor(); // multiplication
                        x = (() -> a.eval() * b.eval());
                    } else if (eat('/')) {
                        Expression a = x, b = parseFactor(); // division
                        x = (() -> a.eval() / b.eval());
                    } else return x;
                }
            }

            Expression parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) {
                    Expression b = parseFactor(); // unary minus
                    return (() -> -1 * b.eval());
                }

                Expression x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') {
                        nextChar();
                    }
                    double xx = Double.parseDouble(str.substring(startPos, this.pos));
                    x = () -> xx;
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while ((ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z')) nextChar();
                    String func = str.substring(startPos, this.pos);

                    if (variables.containsKey(func)) {
                        x = () -> variables.get(func);
                    } else {
                        if (func.equals("min")) {
                            Collection<Double> arguments = extractArguments();
                            x = () -> Collections.min(arguments);
                        } else if (func.equals("max")) {
                            Collection<Double> arguments = extractArguments();
                            x = () -> Collections.max(arguments);
                        } else {
                            double xx = parseFactor().eval();
                            switch (func) {
                                case "sqrt":
                                    x = () -> Math.sqrt(xx);
                                    break;
                                case "sin":
                                    x = () -> Math.sin(Math.toRadians(xx));
                                    break;
                                case "cos":
                                    x = () -> Math.cos(Math.toRadians(xx));
                                    break;
                                case "tan":
                                    x = () -> Math.tan(Math.toRadians(xx));
                                    break;
                                default:
                                    throw new RuntimeException("Unknown function: " + func);
                            }
                        }
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                if (eat('^')) {
                    x = () -> {
                        double d = parseFactor().eval();
                        return Math.pow(d, d); // exponentiation
                    };
                }
                return x;
            }

            Collection<Double> extractArguments() {
                Collection<Double> result = new ArrayList<>();
                if (eat('(')) { // parentheses
                    while (!eat(')')) {
                        result.add(parseExpression().eval());
                        eat(',');
                    }
                }
                return result;
            }
        }.parse();
    }
}