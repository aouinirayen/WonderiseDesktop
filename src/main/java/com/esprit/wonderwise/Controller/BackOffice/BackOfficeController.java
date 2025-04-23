package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class BackOfficeController {

    @FXML
    private Label totalUsers;
    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        int userCount = userService.getUserCount();
        totalUsers.setText(String.valueOf(userCount));
    }

    public void handleAddUser(ActionEvent actionEvent) throws IOException {
        loadScene("Users/UserAdd.fxml", actionEvent);
    }
    private void loadScene(String fxml, ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/BackOffice/" + fxml));
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}