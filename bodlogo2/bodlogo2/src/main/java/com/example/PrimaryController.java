package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PrimaryController {

    @FXML
    private TextField txtMinute;

    @FXML
    private TextField txtSecond;
    

    @FXML
    private TextField txtSecondInput;

    @FXML
    void btnConvert(ActionEvent event) {
        int totalSeconds = Integer.parseInt(txtSecondInput.getText());
        txtMinute.setText(String.valueOf(totalSeconds / 60));
        txtSecond.setText(String.valueOf(totalSeconds % 60));
    }
}
