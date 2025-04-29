package com.esprit.wonderwise.Controller.Auth;

import com.esprit.wonderwise.Service.UserService;
import com.esprit.wonderwise.Model.User;
import com.esprit.wonderwise.Util.UserSession;
import com.esprit.wonderwise.Utils.DialogUtils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LoginController {

    @FXML private AnchorPane backgroundPane;
    @FXML private ImageView worldMap; // Added for world map
    @FXML private ImageView pin1; // Pins for locations
    @FXML private ImageView pin2;
    @FXML private ImageView pin3;
    @FXML private ImageView pin4;
    @FXML private ImageView pin5;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private ImageView togglePasswordView;
    @FXML private Button loginButton;
    @FXML private Hyperlink forgotPasswordLink;
    @FXML private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        if (errorLabel != null) errorLabel.setVisible(false);

        passwordTextField.setManaged(false);
        passwordTextField.setVisible(false);
        passwordTextField.setPromptText(passwordField.getPromptText());

        setEyeIcon(false);

        togglePasswordView.setOnMouseClicked(event -> togglePasswordVisibility());

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!passwordTextField.isVisible()) passwordTextField.setText(newVal);
        });

        passwordTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (passwordTextField.isVisible()) passwordField.setText(newVal);
        });

        animatePins();
    }

    private void animatePins() {
        // List of pins to animate
        List<ImageView> pins = Arrays.asList(pin1, pin2, pin3, pin4, pin5);

        for (ImageView pin : pins) {
            // Create a pulsing animation (scale up and down)
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(pin.scaleXProperty(), 1.0)),
                    new KeyFrame(Duration.ZERO, new KeyValue(pin.scaleYProperty(), 1.0)),
                    new KeyFrame(Duration.seconds(1), new KeyValue(pin.scaleXProperty(), 1.3)),
                    new KeyFrame(Duration.seconds(1), new KeyValue(pin.scaleYProperty(), 1.3)),
                    new KeyFrame(Duration.seconds(2), new KeyValue(pin.scaleXProperty(), 1.0)),
                    new KeyFrame(Duration.seconds(2), new KeyValue(pin.scaleYProperty(), 1.0))
            );
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }

    private void togglePasswordVisibility() {
        boolean showing = passwordTextField.isVisible();
        if (showing) {
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            setEyeIcon(false);
        } else {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            setEyeIcon(true);
        }
    }

    private void setEyeIcon(boolean open) {
        String iconPath = open ? "/com/esprit/wonderwise/icons/eye_open.png" : "/com/esprit/wonderwise/icons/eye_closed.png";
        togglePasswordView.setImage(new Image(getClass().getResourceAsStream(iconPath)));
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        User user = userService.login(email, password);
        if (user == null) {
            showError("Email ou mot de passe incorrect.");
            return;
        }

        if ("Inactive".equalsIgnoreCase(user.getStatus())) {
            showError("Votre compte est inactif. Veuillez contacter l'administrateur.");
            return;
        }

        if ("admin".equalsIgnoreCase(user.getRole())) {
            switchSceneFromControl(loginButton, "/com/esprit/wonderwise/backoffice/BackOffice.fxml");
        } else if ("client".equalsIgnoreCase(user.getRole())) {
            UserSession.setUser(user);
            switchSceneFromControl(loginButton, "/com/esprit/wonderwise/frontoffice/FrontOffice.fxml");
        } else {
            showError("Rôle utilisateur inconnu : " + user.getRole());
        }
    }

    @FXML
    private void handleForgotPassword() {
        switchSceneFromControl(forgotPasswordLink, "/com/esprit/wonderwise/auth/ForgotPassword.fxml");
    }

    @FXML
    private void handleCreateAccount() {
        switchSceneFromControl(loginButton, "/com/esprit/wonderwise/auth/SignUpPage1.fxml");
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        } else {
            DialogUtils.showCustomDialog("Erreur", message, false);
        }
    }

    private void switchSceneFromControl(Control control, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) control.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            DialogUtils.showCustomDialog("Erreur", "Erreur lors du chargement de la scène", false);
            System.err.println("Error loading scene: " + e.getMessage());
        }
    }
}