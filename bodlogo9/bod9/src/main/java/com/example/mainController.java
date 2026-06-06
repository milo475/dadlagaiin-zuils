package com.example;

import java.util.HashSet;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;

public class mainController {

    @FXML private Spinner<Integer> spinnerN;
    @FXML private TextArea txtBroken;
    @FXML private Label lblOutput;

    @FXML
    void shalgah(ActionEvent event) {
        try {
            int n = spinnerN.getValue();
            Set<Integer> broken = new HashSet<>();
            String raw = txtBroken.getText().trim();
            if (!raw.isEmpty()) {
                for (String s : raw.split("\\s+")) {
                    broken.add(Integer.parseInt(s));
                }
            }

            long[] dp = new long[n + 1];
            dp[0] = 1; // эхлэх цэг

            for (int i = 1; i <= n; i++) {
                if (broken.contains(i)) continue;
                if (i >= 1) dp[i] += dp[i - 1];
                if (i >= 2) dp[i] += dp[i - 2];
                if (i >= 3) dp[i] += dp[i - 3];
            }

            lblOutput.setText(String.valueOf(dp[n]));
        } catch (Exception e) {
            lblOutput.setText("Буруу оролт!");
        }
    }
}
