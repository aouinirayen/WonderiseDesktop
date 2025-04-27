package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.Reponse;
import com.esprit.wonderwise.Service.ReponseService;
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class ReponseEditController {

    @FXML
    private TextArea responseTextArea;

    @FXML
    private Button saveButton;

    private ReponseService reponseService;
    private Reponse reponse;

    // Méthode pour initialiser l'objet 'reponse'
    public void setReponse(Reponse reponse) {
        this.reponse = reponse;
        this.reponseService = new ReponseService();
        responseTextArea.setText(reponse.getReponse());
    }

    @FXML
    private void handleSave() {
        String updatedResponse = responseTextArea.getText();

        // Contrôle de saisie : la réponse doit avoir plus de 20 caractères
        if (updatedResponse.isEmpty()) {
            DialogUtils.showCustomDialog("Champs vide", "La réponse ne peut pas être vide.", false, getCurrentStage());
            return;
        }

        if (updatedResponse.length() < 20) {
            DialogUtils.showCustomDialog("Longueur insuffisante", "La réponse doit comporter au moins 20 caractères.", false, getCurrentStage());
            return;
        }

        // Mettre à jour la réponse
        reponse.setReponse(updatedResponse);
        reponseService.update(reponse);

        // Afficher une notification de succès
        DialogUtils.showCustomDialog("Succès", "La réponse a été mise à jour avec succès.", true, getCurrentStage());

        // Redirection vers ReponseListe.fxml
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/ReponseListe.fxml"));
            Parent reclamationRoot = loader.load();

            Stage currentStage = getCurrentStage();
            currentStage.setScene(new Scene(reclamationRoot));
            currentStage.setTitle("Réponses");
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.showCustomDialog("Erreur", "Impossible de charger la liste des réponses.", false, getCurrentStage());
        }
    }


    @FXML
    public void handleCancel(ActionEvent actionEvent) {


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/ReponseListe.fxml"));
            Parent reclamationRoot = loader.load();

            Stage currentStage = (Stage) saveButton.getScene().getWindow();
            currentStage.setScene(new Scene(reclamationRoot));
            currentStage.setTitle("Reponses");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private Stage getCurrentStage() {
        return (Stage) saveButton.getScene().getWindow();
    }
}
