package com.esprit.wonderwise.Controller.FrontOffice;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class FrontOfficeController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private NavBarFrontController navBarFrontController; // Injecte le contrôleur de NavBarFront

    @FXML
    public void initialize() {
        if (navBarFrontController != null) {
            navBarFrontController.setFrontOfficeController(this);
        } else {
            System.err.println("navBarFrontController n'est pas injecté.");
        }
    }

    public void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane newContent = loader.load();
            mainPane.setCenter(newContent);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}