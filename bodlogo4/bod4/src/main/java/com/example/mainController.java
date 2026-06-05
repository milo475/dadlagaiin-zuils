package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class mainController {

    @FXML private Button btnShalgah;
    @FXML private TextArea txtInput;
    @FXML private TextField txtUserInput;
    @FXML private TextField txtOutput;

    @FXML
    void shalgah(ActionEvent event) {
        try {
            String[] parts = txtUserInput.getText().trim().split("\\s+");
            double a = Double.parseDouble(parts[0]);
            double b = Double.parseDouble(parts[1]);
            double c = Double.parseDouble(parts[2]);
            double s = (a + b + c) / 2;
            double area = Math.sqrt(s * (s - a) * (s - b) * (s - c));
            txtOutput.setText(String.format("%.2f", area));
        } catch (Exception e) {
            txtOutput.setText("Алдаа");
        }
    }
}
