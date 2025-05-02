package com.esprit.wonderwise.Controller.Auth;

import com.esprit.wonderwise.Service.UserService;
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import java.io.IOException;

public class ChangePasswordController {

    

    @FXML
    private void initialize() {
        // --- New Password Field ---
        newPasswordTextField.setManaged(false);
        newPasswordTextField.setVisible(false);
        newPasswordTextField.setText("");
        newPasswordTextField.setPromptText(newPasswordField.getPromptText());
        setEyeIcon(toggleNewPasswordView, false);
        toggleNewPasswordView.setOnMouseClicked(event -> togglePasswordVisibility(newPasswordField, newPasswordTextField, toggleNewPasswordView));
        newPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newPasswordTextField.isVisible()) newPasswordTextField.setText(newVal);
        });
        newPasswordTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newPasswordTextField.isVisible()) newPasswordField.setText(newVal);
        });
        // --- Confirm Password Field ---
        confirmPasswordTextField.setManaged(false);
        confirmPasswordTextField.setVisible(false);
        confirmPasswordTextField.setText("");
        confirmPasswordTextField.setPromptText(confirmPasswordField.getPromptText());
        setEyeIcon(toggleConfirmPasswordView, false);
        toggleConfirmPasswordView.setOnMouseClicked(event -> togglePasswordVisibility(confirmPasswordField, confirmPasswordTextField, toggleConfirmPasswordView));
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!confirmPasswordTextField.isVisible()) confirmPasswordTextField.setText(newVal);
        });
        confirmPasswordTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (confirmPasswordTextField.isVisible()) confirmPasswordField.setText(newVal);
        });
    }

    private void togglePasswordVisibility(PasswordField pf, TextField tf, ImageView eye) {
        boolean showing = tf.isVisible();
        if (showing) {
            tf.setVisible(false);
            tf.setManaged(false);
            pf.setVisible(true);
            pf.setManaged(true);
            setEyeIcon(eye, false);
        } else {
            tf.setText(pf.getText());
            tf.setVisible(true);
            tf.setManaged(true);
            pf.setVisible(false);
            pf.setManaged(false);
            setEyeIcon(eye, true);
        }
    }

    private void setEyeIcon(ImageView eye, boolean open) {
        String iconPath = open ? EYE_OPEN_ICON : EYE_CLOSED_ICON;
        eye.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream(iconPath)));
    }

    @FXML
    private void handleChangePassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            DialogUtils.showCustomDialog("Erreur", "Veuillez remplir tous les champs.", false);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            DialogUtils.showCustomDialog("Erreur", "Les mots de passe ne correspondent pas.", false);
            return;
        }
        if (newPassword.length() < 6) {
            DialogUtils.showCustomDialog("Erreur", "Le mot de passe doit contenir au moins 6 caractères.", false);
            return;
        }
        boolean changed = userService.updatePassword(resetEmail, newPassword);
        if (changed) {
            DialogUtils.showCustomDialog("Succès", "Mot de passe changé avec succès. Connectez-vous.", true);
            switchSceneFromControl(changePasswordButton, "/com/esprit/wonderwise/auth/Login.fxml");
        } else {
            DialogUtils.showCustomDialog("Erreur", "Erreur lors du changement du mot de passe. Veuillez réessayer.", false);
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
