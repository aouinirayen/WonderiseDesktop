package com.esprit.wonderwise;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFx extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Birthday check at startup and every 24 hours
        java.util.concurrent.Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
            com.esprit.wonderwise.Utils.BirthdayUtils::checkAndSendBirthdayEmails,
            0, 24, java.util.concurrent.TimeUnit.HOURS
        );

        FXMLLoader fxmlLoader = new FXMLLoader(
                MainFx.class.getResource("/com/esprit/wonderwise/auth/Login.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}