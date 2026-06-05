package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class mainController {

    @FXML private TextArea txtInput;
    @FXML private TextField txtUserInput;
    @FXML private TextField txtOutput;
    @FXML private Button txtShalgah;

    @FXML
    void shalgah(ActionEvent event) {
        String input = txtUserInput.getText().trim();
        if (input.length() == 1 && Character.isLowerCase(input.charAt(0))) {
            txtOutput.setText(input.toUpperCase());
        } else {
            txtOutput.setText("alda: 1 jijig useg oruulna aaaadaaaaaaaaa");
        }
    }
}
