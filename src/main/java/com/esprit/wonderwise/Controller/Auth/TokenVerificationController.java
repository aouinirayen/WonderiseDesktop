package com.esprit.wonderwise.Controller.Auth;

import com.esprit.wonderwise.Service.UserService;
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import java.io.IOException;

public class TokenVerificationController {
    @FXML private TextField tokenField;
    @FXML private Button verifyButton;
    @FXML private Label errorLabel;
    private final UserService userService = new UserService();
    private static String resetEmail;

    public static void setResetEmail(String email) {
        resetEmail = email;
    }

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleVerify() {
        String token = tokenField.getText().trim();
        if (token.isEmpty()) {
            errorLabel.setText("Veuillez entrer le code de vérification.");
            errorLabel.setVisible(true);
            return;
        }
        if (!userService.verifyResetToken(resetEmail, token)) {
            errorLabel.setText("Code de vérification incorrect ou expiré.");
            errorLabel.setVisible(true);
            return;
        }
        // Token valid, proceed to change password
        ChangePasswordController.setResetEmail(resetEmail);
        switchSceneFromControl(verifyButton, "/com/esprit/wonderwise/auth/ChangePassword.fxml");
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
