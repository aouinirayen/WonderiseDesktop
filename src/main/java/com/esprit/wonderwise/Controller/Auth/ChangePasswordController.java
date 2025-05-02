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

  
}
