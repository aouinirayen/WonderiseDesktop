package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.Reclamation;
import com.esprit.wonderwise.Model.Status;
import com.esprit.wonderwise.Service.ReclamationService;
import com.esprit.wonderwise.Service.SpeechToTextService;
import com.esprit.wonderwise.Utils.BadWordsFilter;
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;

public class ReclamationController {

    /* ---------- FXML ---------- */
    @FXML private Button btnConsulterReclamations;
    @FXML private Button btnEnvoyer;
    @FXML private TextField tfObjet;
    @FXML private TextArea taDescription;

    /*  Boutons dictée  */
    @FXML private Button btnParler;
    @FXML private Button btnStopParler;

    /* ---------- Services ---------- */
    private final ReclamationService reclamationService = new ReclamationService();
    private SpeechToTextService sttService;

    /* ========== INITIALISATION ========== */
    @FXML
    private void initialize() {
        /*  Décale l'init pour être sûr que la scène est attachée */
        Platform.runLater(this::initSpeechService);
    }

    /** Charge le modèle Vosk et prépare les callbacks UI */
    private void initSpeechService() {
        try {
            /* 1. Modèle embarqué dans resources */
            String rel = "/com/esprit/wonderwise/models/vosk-model-small-fr-0.22";
            URL url = getClass().getResource(rel);
            if (url == null)
                throw new IOException("Modèle Vosk introuvable à " + rel);

            Path modelPath = Paths.get(url.toURI());

            /* 2. Callbacks UI */
            sttService = new SpeechToTextService(
                    modelPath.toString(),

                    /* partial → placeholder */
                    partial -> Platform.runLater(() -> taDescription.setPromptText(partial)),

                    /* final → injecte dans TextArea */
                    text -> Platform.runLater(() -> {
                        if (!text.isBlank()) {
                            if (taDescription.getText().isBlank())
                                taDescription.setText(text);
                            else
                                taDescription.appendText(" " + text);
                        }
                        taDescription.setPromptText("");
                    })
            );

        } catch (Exception e) {
            System.err.println("STT désactivé : " + e.getMessage());
            disableDictationButtons();
            DialogUtils.showCustomDialog(
                    "Reconnaissance vocale indisponible",
                    "Chargement du modèle Vosk impossible.\n" + e.getMessage(),
                    false, getCurrentStage());
        }
    }

    private void disableDictationButtons() {
        btnParler.setDisable(true);
        btnStopParler.setDisable(true);
    }

    /* ========== ACTIONS VOICE-TO-TEXT ========== */

    @FXML
    private void demarrerDictée(ActionEvent evt) {
        if (sttService == null) return;
        btnParler.setDisable(true);
        btnStopParler.setDisable(false);
        sttService.start();
    }

    @FXML
    private void arreterDictée(ActionEvent evt) {
        if (sttService != null) sttService.stop();
        btnParler.setDisable(false);
        btnStopParler.setDisable(true);
        taDescription.setPromptText("");
    }

    /* ========== ENREGISTREMENT RÉCLAMATION ========== */

    @FXML
    public void ajoutReclamation(ActionEvent actionEvent) {
        if (!validateInputs()) return;

        Task<Boolean> badWordsTask = new Task<>() {
            @Override protected Boolean call() {
                return BadWordsFilter.containsBadWords(tfObjet.getText())
                        || BadWordsFilter.containsBadWords(taDescription.getText());
            }
        };

        badWordsTask.setOnSucceeded(evt -> {
            if (badWordsTask.getValue()) {
                DialogUtils.showCustomDialog("Contenu inapproprié",
                        "Votre réclamation contient des mots interdits.",
                        false, getCurrentStage());
                return;
            }

            Reclamation r = new Reclamation();
            r.setObjet(tfObjet.getText().trim());
            r.setDescription(taDescription.getText().trim());
            r.setDate(LocalDate.now());
            r.setStatus(Status.ENVOYEE);
            r.setUserId(1);

            reclamationService.add(r);

            DialogUtils.showCustomDialog("Succès",
                    "Réclamation envoyée avec succès.",
                    true, getCurrentStage());

            tfObjet.clear();
            taDescription.clear();
        });

        badWordsTask.setOnFailed(evt ->
                DialogUtils.showCustomDialog("Erreur",
                        "Erreur lors de la vérification du contenu.",
                        false, getCurrentStage()));

        new Thread(badWordsTask, "BadWordsTask").start();
    }

    /* ========== NAVIGATION ========== */

    @FXML
    public void consulterMesReclamations(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(getClass()
                            .getResource("/com/esprit/wonderwise/FrontOffice/ListeReclamation.fxml")));
            getCurrentStage().setScene(new Scene(root));
            getCurrentStage().setTitle("Mes réclamations");
        } catch (IOException e) {
            DialogUtils.showCustomDialog("Erreur",
                    "Impossible de charger la liste des réclamations.",
                    false, getCurrentStage());
        }
    }

    /* ========== VALIDATION LOCALE ========== */

    private boolean validateInputs() {
        String objet = tfObjet.getText().trim();
        String description = taDescription.getText().trim();

        if (objet.isEmpty()) {
            DialogUtils.showCustomDialog("Champ manquant",
                    "Veuillez saisir l'objet de la réclamation.",
                    false, getCurrentStage());
            return false;
        }
        if (description.isEmpty()) {
            DialogUtils.showCustomDialog("Champ manquant",
                    "Veuillez saisir la description de la réclamation.",
                    false, getCurrentStage());
            return false;
        }
        if (objet.length() < 5) {
            DialogUtils.showCustomDialog("Objet trop court",
                    "L'objet doit contenir au moins 5 caractères.",
                    false, getCurrentStage());
            return false;
        }
        if (description.length() < 10) {
            DialogUtils.showCustomDialog("Description trop courte",
                    "La description doit contenir au moins 10 caractères.",
                    false, getCurrentStage());
            return false;
        }
        return true;
    }



    private Stage getCurrentStage() {
        Scene s = tfObjet.getScene();
        return (s != null) ? (Stage) s.getWindow() : null;
    }
}
