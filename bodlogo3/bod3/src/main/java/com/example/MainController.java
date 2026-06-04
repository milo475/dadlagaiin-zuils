package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MainController {

    @FXML
    private Button generateBtn;

    @FXML
    private TextField inputField;

    @FXML
    private TextField resultField;

    @FXML
    private TextArea taskArea;

    @FXML
    void handleGenerate(ActionEvent event) {
        try {
            String[] parts = inputField.getText().trim().split("\\s+");
            int m = Integer.parseInt(parts[0]);
            int n = Integer.parseInt(parts[1]);
            resultField.setText(String.valueOf((m * n) / 2));
        } catch (Exception e) {
            resultField.setText("Буруу оролт");
        }
    }

}
