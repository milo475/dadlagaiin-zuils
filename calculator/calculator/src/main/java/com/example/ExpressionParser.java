package com.example;

public class ExpressionParser {
    private final String expr;
    private int pos = 0;

    public ExpressionParser(String expr) {
        this.expr = expr.replaceAll("\\s+", "");
    }

    public double parse() {
        double result = parseAddSub();
        if (pos < expr.length()) throw new RuntimeException("Unexpected: " + expr.charAt(pos));
        return result;
    }

    private double parseAddSub() {
        double left = parseMulDiv();
        while (pos < expr.length() && (expr.charAt(pos) == '+' || expr.charAt(pos) == '-')) {
            char op = expr.charAt(pos++);
            double right = parseMulDiv();
            left = op == '+' ? left + right : left - right;
        }
        return left;
    }

    private double parseMulDiv() {
        double left = parsePower();
        while (pos < expr.length() && (expr.charAt(pos) == '*' || expr.charAt(pos) == '/' || expr.charAt(pos) == '%')) {
            char op = expr.charAt(pos++);
            double right = parsePower();
            if (op == '/' && right == 0) throw new ArithmeticException("Division by zero");
            if (op == '%' && right == 0) throw new ArithmeticException("Division by zero");
            left = op == '*' ? left * right : op == '/' ? left / right : left % right;
        }
        return left;
    }

    private double parsePower() {
        double base = parseUnary();
        if (pos < expr.length() && expr.charAt(pos) == '^') {
            pos++;
            double exp = parsePower();
            return Math.pow(base, exp);
        }
        return base;
    }

    private double parseUnary() {
        if (pos < expr.length() && expr.charAt(pos) == '-') { pos++; return -parseUnary(); }
        if (pos < expr.length() && expr.charAt(pos) == '+') { pos++; return parseUnary(); }
        return parsePrimary();
    }

    private double parsePrimary() {
        if (pos >= expr.length()) throw new RuntimeException("Unexpected end of expression");

        // sqrt(...) функц
        if (expr.startsWith("sqrt(", pos)) {
            pos += 5;
            double val = parseAddSub();
            if (pos >= expr.length() || expr.charAt(pos) != ')')
                throw new RuntimeException("Missing closing parenthesis");
            pos++;
            if (val < 0) throw new ArithmeticException("sqrt of negative");
            return Math.sqrt(val);
        }

        if (expr.charAt(pos) == '(') {
            pos++;
            double val = parseAddSub();
            if (pos >= expr.length() || expr.charAt(pos) != ')')
                throw new RuntimeException("Missing closing parenthesis");
            pos++;
            return val;
        }

        int start = pos;
        while (pos < expr.length() && (Character.isDigit(expr.charAt(pos)) || expr.charAt(pos) == '.'))
            pos++;

        if (pos == start) throw new RuntimeException("Unexpected character: " + expr.charAt(pos));

        return Double.parseDouble(expr.substring(start, pos));
    }
}
