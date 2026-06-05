package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class mainController {

    @FXML private TextField txtInput;
    @FXML private TextField txtOutput;
    @FXML private Button txtShalgah;

    @FXML
    void shalgah(ActionEvent event) {
        try {
            long n = Long.parseLong(txtInput.getText().trim());
            txtOutput.setText(String.valueOf(n * (n + 1) / 2));
        } catch (Exception e) {
            txtOutput.setText("adlaaa: natural too oruulanaudafuds");
        }
    }
}
