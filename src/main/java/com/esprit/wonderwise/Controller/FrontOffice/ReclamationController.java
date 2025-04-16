package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.Reclamation;
import com.esprit.wonderwise.Model.Status;
import com.esprit.wonderwise.Service.ReclamationService;
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class ReclamationController {

    @FXML
    private Button btnConsulterReclamations;

    @FXML
    private TextField tfObjet;

    @FXML
    private TextArea taDescription;

    private final ReclamationService service = new ReclamationService();

    @FXML
    public void ajoutReclamation(ActionEvent actionEvent) {
        if (!validateInputs()) return;

        Reclamation r = new Reclamation();
        r.setObjet(tfObjet.getText().trim());
        r.setDescription(taDescription.getText().trim());
        r.setDate(LocalDate.now());
        r.setStatus(Status.ENVOYEE);
        r.setUserId(1); // TODO : Remplacer par l'ID de l'utilisateur connecté

        service.add(r);

        DialogUtils.showCustomDialog("Succès", "Réclamation envoyée avec succès.", true, getCurrentStage());

        tfObjet.clear();
        taDescription.clear();
    }

    private boolean validateInputs() {
        String objet = tfObjet.getText().trim();
        String description = taDescription.getText().trim();

        if (objet.isEmpty()) {
            DialogUtils.showCustomDialog("Champ manquant", "Veuillez saisir l'objet de la réclamation.", false, getCurrentStage());
            return false;
        }

        if (description.isEmpty()) {
            DialogUtils.showCustomDialog("Champ manquant", "Veuillez saisir la description de la réclamation.", false, getCurrentStage());
            return false;
        }

        if (objet.length() < 5) {
            DialogUtils.showCustomDialog("Objet trop court", "L'objet doit contenir au moins 5 caractères.", false, getCurrentStage());
            return false;
        }

        if (description.length() < 10) {
            DialogUtils.showCustomDialog("Description trop courte", "La description doit contenir au moins 10 caractères.", false, getCurrentStage());
            return false;
        }

        return true;
    }

    @FXML
    public void consulterMesReclamations(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/ListeReclamation.fxml"));
            Parent reclamationRoot = loader.load();

            Stage currentStage = (Stage) btnConsulterReclamations.getScene().getWindow();
            currentStage.setScene(new Scene(reclamationRoot));
            currentStage.setTitle("Réclamation");

        } catch (IOException e) {
            e.printStackTrace();
            DialogUtils.showCustomDialog("Erreur", "Impossible de charger la page de réclamation !", false, getCurrentStage());
        }
    }

    private Stage getCurrentStage() {
        return (Stage) tfObjet.getScene().getWindow();
    }
}
