package com.esprit.wonderwise.Controller.FrontOffice;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.io.IOException;

public class NavBarFrontController {

    private FrontOfficeController frontOfficeController;

    public void setFrontOfficeController(FrontOfficeController controller) {
        this.frontOfficeController = controller;
    }

    @FXML
    private void openPackages() {
        if (frontOfficeController != null) {
            frontOfficeController.loadContent("/com/esprit/wonderwise/FrontOffice/Packages.fxml");
        } else {
            System.err.println("FrontOfficeController n'est pas défini.");
        }
    }
    
    @FXML
    private void openReservationList() {
        if (frontOfficeController != null) {
            frontOfficeController.loadContent("/com/esprit/wonderwise/FrontOffice/ReservationList.fxml");
        } else {
            System.err.println("FrontOfficeController n'est pas défini.");
        }
    }
    
    @FXML
    private void openAddReservationFront() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/AddReservationFront.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter une réservation");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du formulaire de réservation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}