package com.esprit.wonderwise.Controller.Auth;

import com.esprit.wonderwise.Service.UserService;
import com.esprit.wonderwise.Utils.DialogUtils;
import com.esprit.wonderwise.Utils.MailUtil;
import com.esprit.wonderwise.Model.User;
import jakarta.mail.MessagingException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import java.io.IOException;

public class ForgotPasswordController {

    @FXML private TextField emailField;
    @FXML private Button sendButton;
    @FXML private Hyperlink loginLink;
    private final UserService userService = new UserService();

    @FXML
    private void handleSend() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            DialogUtils.showCustomDialog("Erreur", "Veuillez entrer votre email.", false);
            return;
        }
        if (!userService.isEmailExist(email)) {
            DialogUtils.showCustomDialog("Erreur", "Aucun compte trouvé avec cet email.", false);
            return;
        }
        // Generate and store token
        String token = userService.generateAndStoreResetToken(email);
        if (token != null) {
            // Get user info for personalization
            User user = userService.getUserByEmail(email);
            String username = (user != null) ? user.getUsername() : email;
            String html = MailUtil.templateForgotPassword(username, email, token);
            try {
                MailUtil.sendHtmlMail(email, "Réinitialisation du mot de passe Wonderwise", html);
                DialogUtils.showCustomDialog("Succès", "Un code de vérification a été envoyé à votre email.", true);
                // Store email for next step
                TokenVerificationController.setResetEmail(email);
                switchSceneFromControl(sendButton, "/com/esprit/wonderwise/auth/TokenVerification.fxml");
            } catch (MessagingException e) {
                DialogUtils.showCustomDialog("Erreur", "Erreur lors de l'envoi de l'email. Veuillez réessayer.", false);
            }
        } else {
            DialogUtils.showCustomDialog("Erreur", "Erreur lors de la génération du code. Veuillez réessayer.", false);
        }
    }

    @FXML
    private void handleReturnToLogin() {
        switchSceneFromControl(loginLink, "/com/esprit/wonderwise/auth/Login.fxml");
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
