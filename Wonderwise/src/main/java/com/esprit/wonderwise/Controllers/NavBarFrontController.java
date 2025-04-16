package com.esprit.wonderwise.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.io.IOException;

public class NavBarFrontController {

    @FXML
    private void handleHome(ActionEvent event) {
        // TODO: Implement home navigation
    }

    @FXML
    private void handleAbout(ActionEvent event) {
        // TODO: Implement about navigation
    }

    @FXML
    private void handleServices(ActionEvent event) {
        // TODO: Implement services navigation
    }

    @FXML
    private void handlePackages(ActionEvent event) {
        // TODO: Implement packages navigation
    }

    @FXML
    private void handleBlog(ActionEvent event) {
        // TODO: Implement blog navigation
    }

    @FXML
    private void handleCountry1(ActionEvent event) {
        // TODO: Implement country 1 selection
    }

    @FXML
    private void handleCountry2(ActionEvent event) {
        // TODO: Implement country 2 selection
    }

    @FXML
    private void handleContact(ActionEvent event) {
        // TODO: Implement contact navigation
    }

    @FXML
    private void handleProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Profile.fxml"));
            Parent root = loader.load();
            
            Stage profileStage = new Stage();
            profileStage.initModality(Modality.APPLICATION_MODAL);
            profileStage.setTitle("Mon Profil");
            
            Scene scene = new Scene(root);
            profileStage.setScene(scene);
            profileStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du profil : " + e.getMessage());
        }
    }

    @FXML
    private void handleNotifications(ActionEvent event) {
        // TODO: Implement notifications view
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/Login/Login.fxml"));
            Parent root = loader.load();
            
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la déconnexion : " + e.getMessage());
        }
    }
}
