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
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        // Set explicit width and height - increased width from default 1070 to 1300
        stage.setWidth(1300);
        stage.setHeight(740);
        stage.setTitle("WonderWise");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

/////lahdha khli nrakah el mail mara wahda khter andi mnaaml