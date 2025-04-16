package com.esprit.wonderwise.Controller.BackOffice;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class NavBarBackController {
    @FXML
    private Button logoutButton;
    
    @FXML
    public void initialize() {
        logoutButton.setOnAction(event -> handleLogout());
    }
    
    private void handleLogout() {
        try {
            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/Login/Login.fxml"));
            if (loader.getLocation() == null) {
                throw new IOException("Failed to load interface. Please check if all required files are present.");
            }
            
            Scene scene = new Scene(loader.load());
            
            // Create a new stage for the login view
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            
            // Close the current window
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();
            
            // Show the login window
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Show error alert with specific message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load interface");
            alert.setContentText("Please check if all required files are present.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            // Show error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while logging out: " + e.getMessage());
            alert.showAndWait();
        }
    }
}