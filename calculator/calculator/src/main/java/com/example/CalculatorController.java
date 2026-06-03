package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class CalculatorController {

    @FXML private TextField display;

    private String expression = "";
    private double memory = 0;
    private double lastAnswer = 0;

    private void append(String s) {
        expression += s;
        display.setText(expression);
    }

    // Сүүлийн тэмдэгт тоо эсвэл ) бол автоматаар * нэмнэ
    private void appendWithImplicitMul(String s) {
        if (!expression.isEmpty()) {
            char last = expression.charAt(expression.length() - 1);
            if (Character.isDigit(last) || last == ')' || last == '.') append("*");
        }
        append(s);
    }

    // Тооцоол товчнуудад (sin, cos гэх мэт): expression-г evaluate хийж үр дүн дээр үйлдэл хийнэ
    private double currentValue() throws Exception {
        return new ExpressionParser(expression).parse();
    }

    @FXML void handleNumber(ActionEvent event) {
        String num = ((Button) event.getSource()).getText();
        if (num.equals("0")) {
            // Сүүлийн операнд "0" бол нэмэхгүй
            String[] parts = expression.split("[+\\-*/^%(]", -1);
            String last = parts[parts.length - 1];
            if (last.equals("0")) return;
            if (expression.isEmpty()) { expression = "0"; display.setText("0"); return; }
        }
        append(num);
    }

    @FXML void handleOperator(ActionEvent event) {
        if (expression.isEmpty()) return;
        String op = ((Button) event.getSource()).getText();
        if (op.equals("÷")) append("/");
        else if (op.equals("×")) append("*");
        else append(op);
    }

    @FXML void handleDecimal(ActionEvent event) {
        // Сүүлийн тооны хэсэгт . байвал нэмэхгүй
        String[] parts = expression.split("[+\\-*/^%()]");
        String lastPart = parts.length > 0 ? parts[parts.length - 1] : "";
        if (!lastPart.contains(".")) append(".");
    }

    @FXML void handleOpenParen(ActionEvent event) {
        // Тооны ард ( орвол автоматаар * нэмнэ: 5( → 5*(
        if (!expression.isEmpty()) {
            char last = expression.charAt(expression.length() - 1);
            if (Character.isDigit(last) || last == ')') append("*");
        }
        append("(");
    }

    @FXML void handleCloseParen(ActionEvent event) { append(")"); }

    @FXML void handlePi(ActionEvent event) { appendWithImplicitMul(String.valueOf(Math.PI)); }
    @FXML void handleE(ActionEvent event)  { appendWithImplicitMul(String.valueOf(Math.E)); }
    @FXML void handlePower(ActionEvent event) { append("^"); }

    // % товч: expression-г /100 болгоно
    @FXML void handlePercent(ActionEvent event) {
        try {
            double v = currentValue();
            expression = String.valueOf(v / 100);
            display.setText(expression);
        } catch (Exception ignored) {}
    }

    // mod товч: expression дотор % оператор нэмнэ
    @FXML void handleMod(ActionEvent event) { append("%"); }

    @FXML void handleAC(ActionEvent event)    { expression = ""; display.setText("0"); }
    @FXML void handleClear(ActionEvent event) { expression = ""; display.setText("0"); }

    @FXML void handleBackspace(ActionEvent event) {
        if (!expression.isEmpty()) {
            expression = expression.substring(0, expression.length() - 1);
            display.setText(expression.isEmpty() ? "0" : expression);
        }
    }

    // ± : сүүлийн тоог эсрэг тэмдэгт болгоно
    @FXML void handleSign(ActionEvent event) {
        if (expression.isEmpty()) return;
        // Сүүлийн операндыг хайна
        int i = expression.length() - 1;
        while (i > 0 && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) i--;
        char before = expression.charAt(i);
        if (Character.isDigit(before) || before == '.') {
            // Бүх expression нь нэг тоо
            expression = String.valueOf(-Double.parseDouble(expression));
        } else if (before == '+') {
            expression = expression.substring(0, i) + "-" + expression.substring(i + 1);
        } else if (before == '-') {
            expression = expression.substring(0, i) + "+" + expression.substring(i + 1);
        } else {
            // Оператороны дараа -( хэлбэрээр оруулна
            expression = expression + "*(-1)";
        }
        display.setText(expression);
    }

    @FXML void handleSqrt(ActionEvent event) {
        if (!expression.isEmpty()) {
            char last = expression.charAt(expression.length() - 1);
            // Тооны ард шууд sqrt дарвал тэр тоог wrapped хийнэ: 4 → sqrt(4)
            if (Character.isDigit(last) || last == '.') {
                // Сүүлийн операндыг олж sqrt()-д оруулна
                int i = expression.length() - 1;
                while (i > 0 && (Character.isDigit(expression.charAt(i - 1)) || expression.charAt(i - 1) == '.')) i--;
                expression = expression.substring(0, i) + "sqrt(" + expression.substring(i) + ")";
                display.setText(expression);
                return;
            }
        }
        append("sqrt(");
    }
    @FXML void handleReciprocal(ActionEvent event) {
        if (!expression.isEmpty()) {
            char last = expression.charAt(expression.length() - 1);
            if (Character.isDigit(last) || last == '.') {
                int i = expression.length() - 1;
                while (i > 0 && (Character.isDigit(expression.charAt(i - 1)) || expression.charAt(i - 1) == '.')) i--;
                expression = expression.substring(0, i) + "1/(" + expression.substring(i) + ")";
                display.setText(expression);
                return;
            }
        }
        append("1/(");
    }

    @FXML void handleSquare(ActionEvent event) { append("^2"); }
    @FXML void handleCube(ActionEvent event)   { append("^3"); }


    @FXML void handleAbs(ActionEvent event) {
        try { double v = currentValue(); expression = String.valueOf(Math.abs(v)); display.setText(expression); }
        catch (Exception ignored) {}
    }

    @FXML void handleSin(ActionEvent event) {
        try { double v = currentValue(); expression = String.valueOf(Math.sin(Math.toRadians(v))); display.setText(expression); }
        catch (Exception ignored) {}
    }

    @FXML void handleCos(ActionEvent event) {
        try { double v = currentValue(); expression = String.valueOf(Math.cos(Math.toRadians(v))); display.setText(expression); }
        catch (Exception ignored) {}
    }

    @FXML void handleTan(ActionEvent event) {
        try { double v = currentValue(); expression = String.valueOf(Math.tan(Math.toRadians(v))); display.setText(expression); }
        catch (Exception ignored) {}
    }

    @FXML void handleLog(ActionEvent event) {
        try { double v = currentValue(); expression = String.valueOf(Math.log10(v)); display.setText(expression); }
        catch (Exception ignored) {}
    }

    @FXML void handleLn(ActionEvent event) {
        try { double v = currentValue(); expression = String.valueOf(Math.log(v)); display.setText(expression); }
        catch (Exception ignored) {}
    }

    @FXML void handleFactorial(ActionEvent event) {
        try {
            int n = (int) currentValue();
            if (n < 0) return;
            long r = 1;
            for (int i = 2; i <= n; i++) r *= i;
            expression = String.valueOf(r);
            display.setText(expression);
        } catch (Exception ignored) {}
    }

    @FXML void handleAns(ActionEvent event) { appendWithImplicitMul(String.valueOf(lastAnswer)); }
    @FXML void handleMC(ActionEvent event)  { memory = 0; }
    @FXML void handleMR(ActionEvent event)  { appendWithImplicitMul(String.valueOf(memory)); }

    @FXML void handleEquals(ActionEvent event) {
        try {
            double result = new ExpressionParser(expression).parse();
            lastAnswer = result;
            // Бүхэл тоо бол .0 харуулахгүй
            if (result == Math.floor(result) && !Double.isInfinite(result)) {
                expression = String.valueOf((long) result);
            } else {
                expression = String.valueOf(result);
            }
            display.setText(expression);
        } catch (Exception e) {
            display.setText("Error");
            expression = "";
        }
    }
}
