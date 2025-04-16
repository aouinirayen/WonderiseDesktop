package com.esprit.wonderwise.Controller.FrontOffice;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.io.IOException;
import java.util.Optional;

public class NavBarFrontController {

    @FXML
    private MenuItem logoutMenuItem;

    @FXML
    public void initialize() {
        logoutMenuItem.setOnAction(event -> handleLogout());
    }

    private void handleLogout() {
        // Afficher une boîte de dialogue de confirmation
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Déconnexion");
        confirmation.setContentText("Voulez-vous vraiment vous déconnecter ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Charger la vue Login
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/Login/Login.fxml"));
                Scene scene = new Scene(loader.load());

                Stage loginStage = new Stage();
                loginStage.setScene(scene);
                loginStage.setTitle("WonderWise - Login");
                loginStage.show();

                // Fermer la fenêtre actuelle (celle qui a lancé le MenuItem)
                Stage currentStage = (Stage) logoutMenuItem.getParentPopup().getOwnerWindow();
                currentStage.close();

            } catch (IOException e) {
                showError("Erreur de chargement", "Impossible de charger la page de login.");
                e.printStackTrace();
            }
        }
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
