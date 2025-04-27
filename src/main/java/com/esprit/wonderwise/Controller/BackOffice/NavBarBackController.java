package com.esprit.wonderwise.Controller.BackOffice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class NavBarBackController {

    @FXML
    private Button reclamation;

    public void reclamation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/ReclamationBack.fxml"));
            Parent reclamationRoot = loader.load();

            Stage currentStage = (Stage) reclamation.getScene().getWindow();
            currentStage.setScene(new Scene(reclamationRoot));
            currentStage.setTitle("Reclamation");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page de réclamation !");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}