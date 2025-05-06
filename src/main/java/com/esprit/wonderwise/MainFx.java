package com.esprit.wonderwise;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFx extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                MainFx.class.getResource("/com/esprit/wonderwise/FrontOffice/FrontOffice.fxml")
        );
        //FrontOffice/FrontOffice.fxml
        //BackOffice/BackOffice.fxml
        Scene scene = new Scene(fxmlLoader.load());2
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}