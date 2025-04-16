package com.esprit.wonderwise;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFx extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainFx.class.getResource("/com/esprit/wonderwise/Login/Login.fxml")
            );
            if (fxmlLoader.getLocation() == null) {
                throw new IOException("Could not find FXML file");
            }
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}