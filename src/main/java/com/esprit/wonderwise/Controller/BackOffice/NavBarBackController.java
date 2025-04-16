package com.esprit.wonderwise.Controller.BackOffice;

import javafx.fxml.FXML;

public class NavBarBackController {

    private BackOfficeController backOfficeController;

    public void setBackOfficeController(BackOfficeController controller) {
        this.backOfficeController = controller;
    }

    @FXML
    private void openAddOffreForm() {
        if (backOfficeController != null) {
            backOfficeController.loadContent("/com/esprit/wonderwise/BackOffice/ManageOffres.fxml");
        } else {
            System.err.println("BackOfficeController n'est pas défini.");
        }
    }

    @FXML
    private void openManageReservations() {
        if (backOfficeController != null) {
            backOfficeController.loadContent("/com/esprit/wonderwise/BackOffice/ManageReservations.fxml");
        } else {
            System.err.println("BackOfficeController n'est pas défini.");
        }
    }
}