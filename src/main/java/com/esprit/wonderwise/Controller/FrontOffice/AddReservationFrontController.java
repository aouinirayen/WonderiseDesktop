package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.reservation;
import com.esprit.wonderwise.Service.ReservationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AddReservationFrontController {

    @FXML
    private TextField nomField;
    @FXML
    private Label nomError;
    @FXML
    private TextField prenomField;
    @FXML
    private Label prenomError;
    @FXML
    private TextField emailField;
    @FXML
    private Label emailError;
    @FXML
    private TextField telephoneField;
    @FXML
    private Label telephoneError;
    @FXML
    private TextField villeField;
    @FXML
    private TextField nombrePersonneField;
    @FXML
    private Label nombrePersonneError;
    @FXML
    private ChoiceBox<String> modePaiementChoice; 
    @FXML
    private Label modePaiementError;
    @FXML
    private TextField regimeAlimentaireField;
    @FXML
    private TextArea commentaireField;
    @FXML
    private Button addButton;
    @FXML
    private Button cancelButton;
    @FXML
    private DatePicker dateDepartPicker;
    @FXML
    private TextField heureDepartField;
    @FXML
    private ChoiceBox<String> typeVoyageChoice;
    @FXML
    private ChoiceBox<String> statutChoice;
    @FXML
    private TextArea preferencesVoyageField;
    @FXML
    private TextField stripePaymentIdField;

    private ReservationService reservationService = new ReservationService();
    private int offreId;

    public void initData(int offreId) {
        this.offreId = offreId;
    }

    @FXML
    private void handleAddReservation() {
        // Réinitialiser les styles d'erreur
        resetErrorStyles();
        
        boolean hasError = false;

        // Validation du nom
        if (nomField.getText().trim().isEmpty()) {
            showFieldError(nomField, nomError, "Le nom est obligatoire");
            hasError = true;
        }

        // Validation du prénom
        if (prenomField.getText().trim().isEmpty()) {
            showFieldError(prenomField, prenomError, "Le prénom est obligatoire");
            hasError = true;
        }

        // Validation de l'email
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showFieldError(emailField, emailError, "L'email est obligatoire");
            hasError = true;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showFieldError(emailField, emailError, "Format d'email invalide");
            hasError = true;
        }

        // Validation du téléphone
        String telephone = telephoneField.getText().trim();
        if (telephone.isEmpty()) {
            showFieldError(telephoneField, telephoneError, "Le téléphone est obligatoire");
            hasError = true;
        } else if (!telephone.matches("^[0-9]{8}$")) {
            showFieldError(telephoneField, telephoneError, "Le numéro doit contenir 8 chiffres");
            hasError = true;
        }

        // Validation du nombre de personnes
        String nombrePersonnes = nombrePersonneField.getText().trim();
        if (nombrePersonnes.isEmpty()) {
            showFieldError(nombrePersonneField, nombrePersonneError, "Le nombre de personnes est obligatoire");
            hasError = true;
        } else {
            try {
                int nombre = Integer.parseInt(nombrePersonnes);
                if (nombre <= 0) {
                    showFieldError(nombrePersonneField, nombrePersonneError, "Le nombre doit être supérieur à 0");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                showFieldError(nombrePersonneField, nombrePersonneError, "Veuillez entrer un nombre valide");
                hasError = true;
            }
        }

        // Validation du mode de paiement
        if (modePaiementChoice.getValue() == null) {
            showFieldError(modePaiementChoice, modePaiementError, "Le mode de paiement est obligatoire");
            hasError = true;
        }

        // S'il y a des erreurs, arrêter
        if (hasError) {
            return;
        }

        // Si tout est valide, procéder à la réservation
        try {
            reservation reservation = new reservation();
            reservation.setOffreId(offreId);
            reservation.setNom(nomField.getText().trim());
            reservation.setPrenom(prenomField.getText().trim());
            reservation.setEmail(emailField.getText().trim());
            reservation.setTelephone(telephoneField.getText().trim());
            reservation.setNombrePersonne(Integer.parseInt(nombrePersonneField.getText().trim()));
            reservation.setModePaiement(modePaiementChoice.getValue());
            reservation.setCommentaire(commentaireField.getText().trim().isEmpty() ? null : commentaireField.getText().trim());
            
            // Définir des valeurs par défaut pour les champs non affichés
            reservation.setVille("N/A");
            reservation.setRegimeAlimentaire("N/A");
            reservation.setDateDepart(null);
            reservation.setHeureDepart(null);
            reservation.setTypeVoyage("N/A");
            reservation.setPreferencesVoyage(null);
            reservation.setStripePaymentId(null);
            
            // Définir la date de réservation et le statut
            reservation.setDateReservation(LocalDateTime.now());
            reservation.setStatut("En attente");

            reservationService.create(reservation);
            showAlert("Succès", "Réservation effectuée avec succès !");
            closeWindow();

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la réservation : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private void resetErrorStyles() {
        // Réinitialiser les styles des champs
        nomField.getStyleClass().remove("error");
        prenomField.getStyleClass().remove("error");
        emailField.getStyleClass().remove("error");
        telephoneField.getStyleClass().remove("error");
        nombrePersonneField.getStyleClass().remove("error");
        modePaiementChoice.getStyleClass().remove("error");

        // Cacher tous les messages d'erreur
        nomError.setVisible(false);
        nomError.setManaged(false);
        prenomError.setVisible(false);
        prenomError.setManaged(false);
        emailError.setVisible(false);
        emailError.setManaged(false);
        telephoneError.setVisible(false);
        telephoneError.setManaged(false);
        nombrePersonneError.setVisible(false);
        nombrePersonneError.setManaged(false);
        modePaiementError.setVisible(false);
        modePaiementError.setManaged(false);
    }

    private void showFieldError(Control field, Label errorLabel, String message) {
        field.getStyleClass().add("error");
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}