package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.Reclamation;
import com.esprit.wonderwise.Model.User;
import com.esprit.wonderwise.Service.*;
import com.esprit.wonderwise.Utils.DialogUtils;
import com.esprit.wonderwise.Utils.MailUtil;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ReponseController {

    @FXML private Button cancelButton;
    @FXML private TextArea responseTextArea;
    @FXML private Button addResponseButton;
    @FXML private VBox formContainer;

    private int reclamationId;

    public void setReclamationId(int reclamationId) {
        this.reclamationId = reclamationId;
    }

    public void handleAddResponse(ActionEvent actionEvent) {
        String responseText = responseTextArea.getText().trim();
        Stage stage = (Stage) formContainer.getScene().getWindow();

        if (responseText.length() < 20) {
            DialogUtils.showCustomDialog(
                    "Erreur de saisie",
                    "Votre réponse doit contenir au moins 20 caractères.",
                    false,
                    stage
            );
            return;
        }

        // Enregistrement de la réponse
        ReponseService reponseService = new ReponseService();
        reponseService.addReponseToReclamation(responseText, reclamationId);

        // Récupération des infos pour l'email
        Reclamation reclamation = new ReclamationService().findById(reclamationId);
        if (reclamation != null) {
            User user = new UserService().findById(reclamation.getUserId());
            if (user != null) {
                String email = user.getEmail();
                String prenom = user.getPrenom();
                String subject = "Réponse à votre réclamation";
                String content = "Bonjour " + prenom + ",\n\n" +
                        "Nous avons répondu à votre réclamation :\n\n" +
                        responseText + "\n\nMerci pour votre confiance.\n\nL'équipe WonderWise.";

                // Envoi asynchrone de l'email
                sendEmailAsync(email, subject, content, stage);
            }
        }

        responseTextArea.clear();
    }

    private void sendEmailAsync(String email, String subject, String content, Stage stage) {
        Task<Void> emailTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                MailUtil.sendEmail(email, subject, content);
                return null;
            }
        };

        emailTask.setOnSucceeded(e -> DialogUtils.showCustomDialog(
                "Succès",
                "Réponse ajoutée et email envoyé avec succès.",
                true,
                stage
        ));

        emailTask.setOnFailed(e -> DialogUtils.showCustomDialog(
                "Erreur",
                "Réponse enregistrée mais échec d'envoi de l'email.",
                false,
                stage
        ));

        new Thread(emailTask).start();
    }

    @FXML
    private void initialize() {
        addResponseButton.setDisable(true);
        responseTextArea.textProperty().addListener((obs, oldVal, newVal) -> {
            addResponseButton.setDisable(newVal.trim().isEmpty());
        });
    }

    public void cancelResponse(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/ReclamationBack.fxml"));
        Parent reclamationRoot = loader.load();
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.setScene(new Scene(reclamationRoot));
        currentStage.setTitle("Réclamations");
    }
}