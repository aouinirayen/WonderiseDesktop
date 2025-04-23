package com.esprit.wonderwise.Controller.Auth;

import com.esprit.wonderwise.Service.UserService;
import com.esprit.wonderwise.Model.User;
import com.esprit.wonderwise.Util.UserSession;
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import java.io.IOException;

public class LoginController {
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

        // Show/hide password logic
        passwordTextField.setManaged(false);
        passwordTextField.setVisible(false);
        passwordTextField.setText("");
        passwordTextField.setPromptText(passwordField.getPromptText());

        // Set eye icon to closed by default
        setEyeIcon(false);

        // Toggle on eye click
        togglePasswordView.setOnMouseClicked(event -> togglePasswordVisibility());

        // Keep both fields in sync
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!passwordTextField.isVisible()) passwordTextField.setText(newVal);
        });
        passwordTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (passwordTextField.isVisible()) passwordField.setText(newVal);
        });
    }

    private void togglePasswordVisibility() {
        boolean showing = passwordTextField.isVisible();
        if (showing) {
            // Hide password
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            setEyeIcon(false);
        } else {
            // Show password
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
        togglePasswordView.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream(iconPath)));
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
        if (user.getStatus() != null && user.getStatus().equalsIgnoreCase("Inactive")) {
            showError("Votre compte est inactif. Veuillez contacter l'administrateur.");
            return;
        }
        // Save session for global access
        if (user.getRole().equalsIgnoreCase("admin")) {
            switchSceneFromControl(loginButton, "/com/esprit/wonderwise/backoffice/BackOffice.fxml");
        } else if (user.getRole().equalsIgnoreCase("client")) {
            com.esprit.wonderwise.Util.UserSession.setUser(user);
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
