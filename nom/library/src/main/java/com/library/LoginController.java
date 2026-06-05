package com.library;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    @FXML
    private void handleLogin() {
        String user = txtUsername.getText().trim();
        String pass = txtPassword.getText();

        if (user.equals("admin") && pass.equals("admin123")) {
            App.openAdmin();
        } else if (user.equals("user") && pass.equals("user123")) {
            App.openUser();
        } else {
            lblError.setText("Нэвтрэх нэр эсвэл нууц үг буруу.");
        }
    }
}
