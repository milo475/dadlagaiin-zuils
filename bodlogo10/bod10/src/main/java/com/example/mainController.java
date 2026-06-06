package com.example;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class mainController implements Initializable {

    @FXML private ComboBox<Integer> comboN;
    @FXML private Label lblOutput;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        for (int i = 1; i <= 9; i++) comboN.getItems().add(i);
    }

    @FXML
    void shalgah(ActionEvent event) {
        if (comboN.getValue() == null) { lblOutput.setText("n сонгоно уу!"); return; }
        int n = comboN.getValue();
        long sum = 0;
        for (int i = 1; i <= n; i++) {
            long term = 1;
            for (int j = i; j <= 2 * i; j++) term *= j;
            sum += term;
        }
        lblOutput.setText(String.valueOf(sum));
    }
}
