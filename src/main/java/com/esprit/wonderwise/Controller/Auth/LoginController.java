package com.esprit.wonderwise.Controller.Auth;

import com.esprit.wonderwise.Service.UserService;
import com.esprit.wonderwise.Model.User;
import com.esprit.wonderwise.Utils.UserSession;
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

public class LoginController {
    private static final com.google.api.client.json.JsonFactory JSON_FACTORY =
        com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance();
    @FXML
    private Button googleLoginButton;

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private ImageView togglePasswordView;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink forgotPasswordLink;
    @FXML
    private Text errorLabel;
    @FXML
    private StackPane rootPane;

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
        // Show loading overlay as Node in rootPane
        Node loadingOverlay = showLoadingOverlayNode();
        // Simulate delay (e.g. 2 seconds), then do login logic
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
        pause.setOnFinished(event -> {
            User user = userService.login(email, password);
            // Remove loading overlay
            rootPane.getChildren().remove(loadingOverlay);
            if (user == null) {
                showError("Email ou mot de passe incorrect.");
                return;
            }
            if (user.getStatus() != null && user.getStatus().equalsIgnoreCase("Inactive")) {
                showError("Votre compte est inactif. Veuillez contacter l'administrateur.");
                return;
            }
            if (user.getStatus() != null && user.getStatus().equalsIgnoreCase("Pending")) {
                showError("Votre compte est en attente. Veuillez contacter l'administrateur.");
                return;
            }
            // Save session for global access
            if (user.getRole().equalsIgnoreCase("admin")) {
                switchSceneFromControl(loginButton, "/com/esprit/wonderwise/backoffice/BackOffice.fxml");
            } else if (user.getRole().equalsIgnoreCase("client")) {
                UserSession.setUser(user);
                switchSceneFromControl(loginButton, "/com/esprit/wonderwise/frontoffice/FrontOffice.fxml");
            } else {
                showError("Rôle utilisateur inconnu : " + user.getRole());
            }
        });
        pause.play();
    }

    // Helper to show loading overlay as a Node
    private Node showLoadingOverlayNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/loading/Loading.fxml"));
            Node loadingNode = loader.load();
            rootPane.getChildren().add(loadingNode);
            return loadingNode;
        } catch (IOException e) {
            System.err.println("Failed to load loading overlay: " + e.getMessage());
            return null;
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

    @FXML
    private void handleGoogleLogin () {
        try {
            // Load client secrets
            InputStream in = getClass().getResourceAsStream("/client_secrets.json");
            if (in == null) {
                showError("client_secrets.json introuvable dans resources. Veuillez le placer à la racine de src/main/resources.");
                return;
            }
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                    JSON_FACTORY, new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    clientSecrets,
                    Collections.singletonList("profile email openid")
            ).setAccessType("offline").build();

            // Authorize
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            // Get user info
            Oauth2 oauth2 = new Oauth2.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY, credential)
                    .setApplicationName("Wonderwise").build();
            Userinfo userInfo = oauth2.userinfo().get().execute();

            User user = userService.loginWithGoogle(userInfo.getEmail(), userInfo.getId());
            if (user == null) {
                // Optionally, create a new user or show error
                showError("Aucun compte associé à cet email Google.");
                return;
            }
            if (user.getStatus() != null && user.getStatus().equalsIgnoreCase("Inactive")) {
                showError("Votre compte est inactif. Veuillez contacter l'administrateur.");
                return;
            }
            if (user.getRole().equalsIgnoreCase("admin")) {
                switchSceneFromControl(googleLoginButton, "/com/esprit/wonderwise/backoffice/BackOffice.fxml");
            } else if (user.getRole().equalsIgnoreCase("client")) {
                UserSession.setUser(user);
                switchSceneFromControl(googleLoginButton, "/com/esprit/wonderwise/frontoffice/FrontOffice.fxml");
            } else {
                showError("Rôle utilisateur inconnu : " + user.getRole());
            }
        } catch (Exception e) {
            showError("Erreur de connexion Google: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

