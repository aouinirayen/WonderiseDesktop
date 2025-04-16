package tn.esprit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainFx extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // First verify the resource exists
            URL fxmlUrl = getClass().getResource("/fxml/ListExperience.fxml");
            if (fxmlUrl == null) {
                System.err.println("FXML file not found in resources. Trying alternative paths...");
                
                // Try alternative paths
                fxmlUrl = getClass().getResource("fxml/ListExperience.fxml");
                if (fxmlUrl == null) {
                    fxmlUrl = getClass().getClassLoader().getResource("fxml/ListExperience.fxml");
                }
                
                if (fxmlUrl == null) {
                    throw new IOException("FXML file not found in resources. Tried multiple paths.");
                } else {
                    System.out.println("Found FXML at: " + fxmlUrl);
                }
            }

            // Then load it
            System.out.println("Loading FXML from: " + fxmlUrl);
            Parent root = FXMLLoader.load(fxmlUrl);
            
            // Utiliser une scène simple pour tester
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("Gestion des Expériences");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (IOException e) {
            System.err.println("FATAL: Failed to load FXML: " + e.getMessage());
            e.printStackTrace();

            // Show error to user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Application Error");
            alert.setHeaderText("Failed to load interface");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        // Afficher le classpath pour le débogage
        System.out.println("Classpath: " + System.getProperty("java.class.path"));
        
        // Afficher le chemin des ressources
        URL resourceUrl = MainFx.class.getResource("/");
        if (resourceUrl != null) {
            System.out.println("Resource root: " + resourceUrl);
        } else {
            System.out.println("Resource root not found");
        }
        
        launch(args);
    }
}