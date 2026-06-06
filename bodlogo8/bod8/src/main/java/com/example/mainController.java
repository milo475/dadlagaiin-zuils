package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class mainController {

    @FXML
    private TextField txtInput;

    @FXML
    private TextField txtOutput;

    @FXML
    private Button txtShalgah;

    @FXML
    void shalgah(ActionEvent event) {
        try {
            int n = Integer.parseInt(txtInput.getText().trim());
            int[] trips = {60, 20, 10, 5, 1};
            int[] counts = new int[5];
            int rem = n;
            for (int i = 0; i < trips.length; i++) {
                counts[i] = rem / trips[i];
                rem %= trips[i];
            }
            txtOutput.setText(counts[4] + " " + counts[3] + " " + counts[2] + " " + counts[1] + " " + counts[0]);
        } catch (NumberFormatException e) {
            txtOutput.setText("Буруу оролт!");
        }
    }

}
