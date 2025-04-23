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
    @FXML private Button dashboardBtn, countryBtn, artBtn, monumentBtn, foodBtn, celebrityBtn;

    @FXML
    private void handleDashboardButton(ActionEvent event) throws IOException {
        loadScene("BackOffice.fxml", event);
    }

    @FXML
    private void handleUsersButton(ActionEvent event) throws IOException {
        loadScene("Users/UsersBack.fxml", event);
    }

    private void loadScene(String fxml, ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/BackOffice/" + fxml));
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    public void handleLogoutButton(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/auth/Login.fxml"));
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}