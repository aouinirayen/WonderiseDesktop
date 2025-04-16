package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Service.ReponseService;
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ReponseController {
    @FXML
    private Button cancelButton;

    @FXML
    private TextArea responseTextArea;

    @FXML
    private Button addResponseButton;

    @FXML
    private VBox formContainer; // Ajoute un fx:id="formContainer" dans le VBox du FXML

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

        ReponseService reponseService = new ReponseService();
        reponseService.addReponseToReclamation(responseText, reclamationId);

        DialogUtils.showCustomDialog(
                "Succès",
                "Réponse ajoutée avec succès à la réclamation.",
                true,
                stage
        );

        responseTextArea.clear();
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

            // Utiliser l'événement pour obtenir la scène via le source (ici un MenuItem)
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
            currentStage.setScene(new Scene(reclamationRoot));
            currentStage.setTitle(" Réclamations");


    }
}
