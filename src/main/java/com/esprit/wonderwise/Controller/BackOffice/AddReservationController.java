package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.reservation;
import com.esprit.wonderwise.Service.ReservationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddReservationController {

    @FXML
    private TextField offreIdField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField villeField;

    @FXML
    private TextField nombrePersonneField;

    @FXML
    private DatePicker dateDepartPicker;

    @FXML
    private TextField heureDepartField;

    @FXML
    private ChoiceBox<String> typeVoyageChoice;

    @FXML
    private ChoiceBox<String> modePaiementChoice;

    @FXML
    private TextArea preferencesVoyageField;

    @FXML
    private TextArea commentaireField;

    @FXML
    private ChoiceBox<String> statutChoice;

    @FXML
    private TextField regimeAlimentaireField;

    @FXML
    private TextField stripePaymentIdField;

    @FXML
    private Button addButton;

    private ReservationService reservationService = new ReservationService();
    private reservation reservationToEdit = null;

    @FXML
    public void initialize() {
        // Initialiser les ChoiceBox
        typeVoyageChoice.setItems(FXCollections.observableArrayList("Solo", "Groupe", "Famille"));
        modePaiementChoice.setItems(FXCollections.observableArrayList("Carte", "Espèces", "Virement"));
        statutChoice.setItems(FXCollections.observableArrayList("En attente", "Confirmée", "Annulée"));
    }

    public void initData(reservation res) {
        this.reservationToEdit = res;
        offreIdField.setText(String.valueOf(res.getOffreId()));
        nomField.setText(res.getNom());
        prenomField.setText(res.getPrenom());
        emailField.setText(res.getEmail());
        telephoneField.setText(res.getTelephone());
        villeField.setText(res.getVille());
        nombrePersonneField.setText(String.valueOf(res.getNombrePersonne()));
        dateDepartPicker.setValue(res.getDateDepart());
        heureDepartField.setText(res.getHeureDepart() != null ? res.getHeureDepart().format(DateTimeFormatter.ofPattern("HH:mm")) : "");
        typeVoyageChoice.setValue(res.getTypeVoyage());
        modePaiementChoice.setValue(res.getModePaiement());
        preferencesVoyageField.setText(res.getPreferencesVoyage());
        commentaireField.setText(res.getCommentaire());
        statutChoice.setValue(res.getStatut());
        regimeAlimentaireField.setText(res.getRegimeAlimentaire());
        stripePaymentIdField.setText(res.getStripePaymentId());
        addButton.setText("Modifier");
    }

    @FXML
    private void handleAddReservation() {
        try {
            // Validation des champs obligatoires
            if (offreIdField.getText().isEmpty() || nomField.getText().isEmpty() ||
                    prenomField.getText().isEmpty() || emailField.getText().isEmpty() ||
                    nombrePersonneField.getText().isEmpty() || dateDepartPicker.getValue() == null) {
                showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            reservation res = reservationToEdit != null ? reservationToEdit : new reservation();
            res.setOffreId(Integer.parseInt(offreIdField.getText()));
            res.setNom(nomField.getText());
            res.setPrenom(prenomField.getText());
            res.setEmail(emailField.getText());
            res.setTelephone(telephoneField.getText().isEmpty() ? null : telephoneField.getText());
            res.setVille(villeField.getText().isEmpty() ? null : villeField.getText());
            res.setNombrePersonne(Integer.parseInt(nombrePersonneField.getText()));
            res.setDateDepart(dateDepartPicker.getValue());

            // Valider et parser l'heure de départ
            if (!heureDepartField.getText().isEmpty()) {
                try {
                    res.setHeureDepart(LocalTime.parse(heureDepartField.getText(), DateTimeFormatter.ofPattern("HH:mm")));
                } catch (DateTimeParseException e) {
                    showAlert("Erreur", "Format de l'heure invalide (utilisez HH:MM).");
                    return;
                }
            } else {
                res.setHeureDepart(null);
            }

            res.setTypeVoyage(typeVoyageChoice.getValue());
            res.setModePaiement(modePaiementChoice.getValue());
            res.setPreferencesVoyage(preferencesVoyageField.getText().isEmpty() ? null : preferencesVoyageField.getText());
            res.setCommentaire(commentaireField.getText().isEmpty() ? null : commentaireField.getText());
            res.setDateReservation(LocalDateTime.now()); // Toujours à jour
            res.setStatut(statutChoice.getValue());
            res.setDatePaiement(null); // Non modifiable dans ce formulaire
            res.setStripePaymentId(stripePaymentIdField.getText().isEmpty() ? null : stripePaymentIdField.getText());
            res.setRegimeAlimentaire(regimeAlimentaireField.getText().isEmpty() ? null : regimeAlimentaireField.getText());

            if (reservationToEdit != null) {
                reservationService.update(res);
                showAlert("Succès", "Réservation modifiée avec succès !");
            } else {
                reservationService.create(res);
                showAlert("Succès", "Réservation ajoutée avec succès !");
            }

            closeWindow();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des valeurs numériques valides pour Offre ID et Nombre de personnes.");
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'opération : " + e.getMessage());
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
}