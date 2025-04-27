package com.esprit.wonderwise.Controller.FrontOffice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;

public class NavBarFrontController {


    @FXML
    private MenuItem mesReclamations;

    @FXML
    private MenuItem ajouterReclamation;

    @FXML
    private Button home;

    @FXML
    public void Mesreclamation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/ListeReclamation.fxml"));
            Parent reclamationRoot = loader.load();

            // Utiliser l'événement pour obtenir la scène via le source (ici un MenuItem)
            Stage currentStage = (Stage) ((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow();
            currentStage.setScene(new Scene(reclamationRoot));
            currentStage.setTitle("Mes Réclamations");

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


    public void home(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/FrontOffice.fxml"));
            Parent reclamationRoot = loader.load();

            Stage currentStage = (Stage) home.getScene().getWindow();
            currentStage.setScene(new Scene(reclamationRoot));
            currentStage.setTitle("Home");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page de réclamation !");
        }
    }

    public void reclamation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/reclamation.fxml"));
            Parent reclamationRoot = loader.load();

            Stage currentStage = (Stage) ((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow();
            currentStage.setScene(new Scene(reclamationRoot));
            currentStage.setTitle(" Ajouter Réclamation");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page de réclamation !");
        }
    }

    public void ch(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Chatbot.fxml"));
            Parent reclamationRoot = loader.load();

            Stage currentStage = (Stage) ((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow();
            currentStage.setScene(new Scene(reclamationRoot));
            currentStage.setTitle(" Support");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page de réclamation !");
        }
    }
}
