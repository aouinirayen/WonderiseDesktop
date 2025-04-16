package com.esprit.wonderwise.Controller.FrontOffice;

import javafx.fxml.FXML;

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
}