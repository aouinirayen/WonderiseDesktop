package com.esprit.wonderwise.Controller.FrontOffice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import com.esprit.wonderwise.Utils.UserSession;
import com.esprit.wonderwise.Model.User;

import java.io.IOException;

public class NavBarFrontController {

    @FXML
    private Button homeButton;
    @FXML
    private MenuButton profileMenu;
    @FXML
    private ImageView profileImageView;
    @FXML
    private MenuItem logoutMenuItem;
    @FXML
    private MenuItem nameMenuItem;
    @FXML
    private MenuItem settingsMenuItem;

    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    @FXML
    public void initialize() {
        User user = UserSession.getUser();
        if (user != null) {
            // Set user name
            if (nameMenuItem != null) nameMenuItem.setText(user.getUsername());
            // Set user image if available, else set default
            if (profileImageView != null) {
                Image image = null;
                boolean loaded = false;
                if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                    String filename = user.getProfilePicture();
                    String localPath = IMAGE_DESTINATION_DIR + filename;
                    java.io.File file = new java.io.File(localPath);
                    try {
                        if (file.exists()) {
                            image = new Image(file.toURI().toString(), true);
                            if (image.isError() || image.getException() != null) throw new Exception();
                            loaded = true;
                        } else {
                            loaded = false;
                        }
                    } catch (Exception e) {
                        loaded = false;
                    }
                }
                if (!loaded) {
                    // Load default image from resources
                    try {
                        image = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"));
                    } catch (Exception ex) {
                        // If even default image fails, leave empty
                        image = null;
                    }
                }
                profileImageView.setImage(image);
                // Clip image to circle
                double radius = Math.min(profileImageView.getFitWidth(), profileImageView.getFitHeight()) / 2;
                javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(radius, radius, radius);
                profileImageView.setClip(clip);
            }
        }
        if (logoutMenuItem != null) {
            logoutMenuItem.setOnAction(e -> {
                UserSession.clear();
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/auth/Login.fxml"));
                    Stage stage = (Stage) homeButton.getScene().getWindow();
                    stage.setScene(new Scene(root));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
        if (settingsMenuItem != null) {
            settingsMenuItem.setOnAction(e -> {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Users/UserSettings.fxml"));
                    Stage stage = (Stage) homeButton.getScene().getWindow();
                    stage.setScene(new Scene(root));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    @FXML
    private void handleHomeButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/FrontOffice/FrontOffice.fxml"));
            Stage stage = (Stage) homeButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}