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
            int k = Integer.parseInt(txtInput.getText().trim());
            // Stars and bars: C(k+5, 5)
            long result = combination(k + 5, 5);
            txtOutput.setText(String.valueOf(result));
        } catch (Exception e) {
            txtOutput.setText("Алдаа: натурал тоо оруулна уу");
        }
    }

    private long combination(int n, int r) {
        long num = 1, den = 1;
        for (int i = 0; i < r; i++) {
            num *= (n - i);
            den *= (i + 1);
        }
        return num / den;
    }
}
