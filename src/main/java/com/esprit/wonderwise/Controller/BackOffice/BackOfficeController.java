package com.esprit.wonderwise.Controller.BackOffice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class BackOfficeController {

    @FXML
    private Button addCountryBtn;

    @FXML
    private void initialize() {
        // Initialize dashboard data if needed
    }

    @FXML
    private void handleAddCountry(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/BackOffice/Country/CountryAdd.fxml"));
        Stage stage = (Stage) addCountryBtn.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}