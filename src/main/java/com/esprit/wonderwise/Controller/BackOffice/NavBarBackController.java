package com.esprit.wonderwise.Controller.BackOffice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class NavBarBackController {
    @FXML private Button dashboardBtn, countryBtn, artBtn, monumentBtn, foodBtn, celebrityBtn, ratingBtn;

    @FXML
    private void handleDashboardButton(ActionEvent event) throws IOException {
        loadScene("BackOffice.fxml", event);
    }

    @FXML
    private void handleCountryButton(ActionEvent event) throws IOException {
        loadScene("Country/Country.fxml", event);
    }

    @FXML
    private void handleArtButton(ActionEvent event) throws IOException {
        loadScene("Art/Art.fxml", event);
    }

    @FXML
    private void handleMonumentButton(ActionEvent event) throws IOException {
        loadScene("Monument/Monument.fxml", event);
    }

    @FXML
    public void handleFoodButton(ActionEvent event) throws IOException {
        loadScene("TraditionalFood/TraditionalFood.fxml", event);
    }

    @FXML
    private void handleCelebrityButton(ActionEvent event) throws IOException {
        loadScene("Celebrity/Celebrity.fxml", event);
    }

    @FXML
    private void handleRatingButton(ActionEvent event) throws IOException {
        loadScene("Rating/DashboardRating.fxml", event);
    }

    private void loadScene(String fxml, ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/BackOffice/" + fxml));
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}