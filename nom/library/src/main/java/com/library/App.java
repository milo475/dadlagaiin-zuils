package com.library;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        stage.setTitle("Номын сан");
        stage.setScene(new Scene(load("login"), 340, 300));
        stage.show();
    }

    /** Admin: 3 табтай бүтэн view */
    public static void openAdmin() {
        try {
            primaryStage.setScene(new Scene(load("main"), 920, 640));
            primaryStage.setTitle("Номын сан — Админ");
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** User: зөвхөн уншигчийн хэсэг */
    public static void openUser() {
        try {
            primaryStage.setScene(new Scene(load("user"), 920, 640));
            primaryStage.setTitle("Номын сан — Уншигч");
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static Parent load(String fxml) throws IOException {
        return new FXMLLoader(App.class.getResource(fxml + ".fxml")).load();
    }

    public static void main(String[] args) {
        launch();
    }
}
