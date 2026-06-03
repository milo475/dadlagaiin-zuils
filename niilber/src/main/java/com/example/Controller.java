package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private TextField txtFirst;

    @FXML
    private TextField txtSecond;

    @FXML
    private TextField txtResult;

    @FXML
    void btnCalculate(ActionEvent event) {
        
        try {
            double num1 = Double.parseDouble(txtFirst.getText().trim());
            double num2 = Double.parseDouble(txtSecond.getText().trim());
            
            double sum = num1 + num2;
            
            txtResult.setText(String.valueOf(sum));
        } catch (NumberFormatException e) {
            txtResult.setText("too!");
        } catch (Exception e) {
            txtResult.setText("error!");
        }
    }
}