package com.example.healthjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            // Chargement du fichier FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            // Chargement et application du fichier CSS
            scene.getStylesheets().add(getClass().getResource("/com/example/healthjavafx/styles.css").toExternalForm());

            // Configuration de la scène et du titre de la fenêtre
            stage.setTitle("Suivi de santé");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}

