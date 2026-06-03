package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws java.io.IOException {
        App.setRoot("primary");
    }
}
