package com.esprit.wonderwise.Controller.FrontOffice;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FrontOfficeController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}