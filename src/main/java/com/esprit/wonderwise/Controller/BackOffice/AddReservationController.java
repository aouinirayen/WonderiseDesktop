package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.offre;
import com.esprit.wonderwise.Model.reservation;
import com.esprit.wonderwise.Service.OffreService;
import com.esprit.wonderwise.Service.ReservationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AddReservationController {

    @FXML
    private Label formTitle;
    
    @FXML
    private ChoiceBox<offre> offreChoice;
    @FXML
    private Label offreIdError;
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
    private Label villeError;
    @FXML
    private TextField nombrePersonneField;
    @FXML
    private Label nombrePersonneError;
    @FXML
    private DatePicker dateDepartPicker;
    @FXML
    private Label dateDepartError;
    @FXML
    private ChoiceBox<String> typeVoyageChoice;
    @FXML
    private Label typeVoyageError;
    @FXML
    private ChoiceBox<String> modePaiementChoice;
    @FXML
    private Label modePaiementError;
    @FXML
    private TextField regimeAlimentaireField;
    @FXML
    private Label regimeAlimentaireError;
    @FXML
    private TextArea commentaireField;
    @FXML
    private ChoiceBox<String> statutChoice;
    @FXML
    private Label statutError;
    @FXML
    private Button addButton;

    private ReservationService reservationService = new ReservationService();
    private OffreService offreService = new OffreService();
    private reservation reservationToEdit = null;
    private BackOfficeController backOfficeController;

    public void setBackOfficeController(BackOfficeController controller) {
        this.backOfficeController = controller;
    }

    @FXML
    public void initialize() {
        typeVoyageChoice.setItems(FXCollections.observableArrayList("Individuel", "Groupe", "Famille"));
        modePaiementChoice.setItems(FXCollections.observableArrayList("Carte bancaire", "Espèces", "Virement", "Chèque"));
        statutChoice.setItems(FXCollections.observableArrayList("En attente", "Confirmée", "Annulée"));
        
        // Set default values
        typeVoyageChoice.setValue("Individuel");
        modePaiementChoice.setValue("Carte bancaire");
        statutChoice.setValue("En attente");
        regimeAlimentaireField.setText("Aucun");
        
        // Load available offers
        loadOffres();
        
        // Setup converter for offreChoice
        offreChoice.setConverter(new StringConverter<offre>() {
            @Override
            public String toString(offre offre) {
                if (offre == null) return null;
                return offre.getTitre() + " - " + offre.getPays() + " (" + offre.getPrix() + " TND)";
            }

            @Override
            public offre fromString(String string) {
                return null; // Not needed for ChoiceBox
            }
        });
    }
    
    private void loadOffres() {
        try {
            List<offre> offres = offreService.readAll();
            offreChoice.setItems(FXCollections.observableArrayList(offres));
            if (!offres.isEmpty()) {
                offreChoice.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les offres : " + e.getMessage());
        }
    }

    public void initData(reservation res) {
        this.reservationToEdit = res;
        formTitle.setText("Modifier une réservation");
        
        // Select the correct offre
        for (offre o : offreChoice.getItems()) {
            if (o.getId() == res.getOffreId()) {
                offreChoice.getSelectionModel().select(o);
                break;
            }
        }
        
        nomField.setText(res.getNom());
        prenomField.setText(res.getPrenom());
        emailField.setText(res.getEmail());
        telephoneField.setText(res.getTelephone());
        villeField.setText(res.getVille());
        nombrePersonneField.setText(String.valueOf(res.getNombrePersonne()));
        dateDepartPicker.setValue(res.getDateDepart() != null ? res.getDateDepart() : null);
        typeVoyageChoice.setValue(res.getTypeVoyage());
        modePaiementChoice.setValue(res.getModePaiement());
        regimeAlimentaireField.setText(res.getRegimeAlimentaire() != null ? res.getRegimeAlimentaire() : "Aucun");
        commentaireField.setText(res.getCommentaire());
        statutChoice.setValue(res.getStatut());
        addButton.setText("Enregistrer");
    }

    @FXML
    private void handleAddReservation() {
        try {
            clearErrors();
            boolean hasError = false;
            LocalDate today = LocalDate.now();

            // Validate Offre selection
            offre selectedOffre = offreChoice.getValue();
            if (selectedOffre == null) {
                showError(offreIdError, offreChoice, "Veuillez sélectionner une destination");
                hasError = true;
            }

            // Validate Nom
            if (nomField.getText().trim().isEmpty()) {
                showError(nomError, nomField, "Le nom est obligatoire");
                hasError = true;
            } else if (nomField.getText().trim().length() < 2) {
                showError(nomError, nomField, "Le nom doit contenir au moins 2 caractères");
                hasError = true;
            }

            // Validate Prénom
            if (prenomField.getText().trim().isEmpty()) {
                showError(prenomError, prenomField, "Le prénom est obligatoire");
                hasError = true;
            } else if (prenomField.getText().trim().length() < 2) {
                showError(prenomError, prenomField, "Le prénom doit contenir au moins 2 caractères");
                hasError = true;
            }

            // Validate Email
            if (emailField.getText().trim().isEmpty()) {
                showError(emailError, emailField, "L'email est obligatoire");
                hasError = true;
            } else if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showError(emailError, emailField, "Veuillez entrer un email valide");
                hasError = true;
            }

            // Validate Téléphone (optional, but if provided, check format)
            if (!telephoneField.getText().trim().isEmpty()) {
                if (!telephoneField.getText().matches("\\+?[0-9]{8,15}")) {
                    showError(telephoneError, telephoneField, "Le téléphone doit être un numéro valide");
                    hasError = true;
                }
            }

            // Validate Ville (optional)
            if (!villeField.getText().trim().isEmpty() && villeField.getText().trim().length() < 2) {
                showError(villeError, villeField, "La ville doit contenir au moins 2 caractères");
                hasError = true;
            }

            // Validate Nombre de personnes
            try {
                int nombrePersonne = Integer.parseInt(nombrePersonneField.getText().trim());
                if (nombrePersonne <= 0) {
                    showError(nombrePersonneError, nombrePersonneField, "Le nombre de personnes doit être positif");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                showError(nombrePersonneError, nombrePersonneField, "Le nombre de personnes doit être un nombre valide");
                hasError = true;
            }

            // Validate Date de départ (optional in the new design)
            if (dateDepartPicker.getValue() != null && dateDepartPicker.getValue().isBefore(today)) {
                showError(dateDepartError, dateDepartPicker, "La date de départ doit être aujourd'hui ou dans le futur");
                hasError = true;
            }

            // Validate Type de voyage
            if (typeVoyageChoice.getValue() == null) {
                showError(typeVoyageError, typeVoyageChoice, "Le type de voyage est obligatoire");
                hasError = true;
            }

            // Validate Mode de paiement
            if (modePaiementChoice.getValue() == null) {
                showError(modePaiementError, modePaiementChoice, "Le mode de paiement est obligatoire");
                hasError = true;
            }
            
            // Validate Régime alimentaire
            if (regimeAlimentaireField.getText() == null || regimeAlimentaireField.getText().trim().isEmpty()) {
                showError(regimeAlimentaireError, regimeAlimentaireField, "Le régime alimentaire est obligatoire");
                hasError = true;
            }

            // Validate Statut
            if (statutChoice.getValue() == null) {
                showError(statutError, statutChoice, "Le statut est obligatoire");
                hasError = true;
            }

            if (hasError) {
                return;
            }

            reservation res = reservationToEdit != null ? reservationToEdit : new reservation();
            res.setOffreId(selectedOffre.getId());
            res.setNom(nomField.getText().trim());
            res.setPrenom(prenomField.getText().trim());
            res.setEmail(emailField.getText().trim());
            res.setTelephone(telephoneField.getText().trim().isEmpty() ? null : telephoneField.getText().trim());
            res.setVille(villeField.getText().trim().isEmpty() ? null : villeField.getText().trim());
            res.setNombrePersonne(Integer.parseInt(nombrePersonneField.getText().trim()));
            res.setDateDepart(dateDepartPicker.getValue());
            res.setTypeVoyage(typeVoyageChoice.getValue());
            res.setModePaiement(modePaiementChoice.getValue());
            res.setRegimeAlimentaire(regimeAlimentaireField.getText().trim());
            res.setCommentaire(commentaireField.getText().trim().isEmpty() ? null : commentaireField.getText().trim());
            res.setDateReservation(LocalDateTime.now());
            res.setStatut(statutChoice.getValue());
            res.setDatePaiement(null);

            if (reservationToEdit != null) {
                reservationService.update(res);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation modifiée avec succès !");
            } else {
                reservationService.create(res);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation ajoutée avec succès !");
            }

            if (backOfficeController != null) {
                backOfficeController.loadContent("/com/esprit/wonderwise/BackOffice/ManageReservations.fxml");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'opération : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        if (backOfficeController != null) {
            backOfficeController.loadContent("/com/esprit/wonderwise/BackOffice/ManageReservations.fxml");
        }
    }

    private void clearErrors() {
        offreIdError.setVisible(false);
        nomError.setVisible(false);
        prenomError.setVisible(false);
        emailError.setVisible(false);
        telephoneError.setVisible(false);
        villeError.setVisible(false);
        nombrePersonneError.setVisible(false);
        dateDepartError.setVisible(false);
        typeVoyageError.setVisible(false);
        modePaiementError.setVisible(false);
        regimeAlimentaireError.setVisible(false);
        statutError.setVisible(false);

        offreChoice.setStyle(null);
        nomField.setStyle(null);
        prenomField.setStyle(null);
        emailField.setStyle(null);
        telephoneField.setStyle(null);
        villeField.setStyle(null);
        nombrePersonneField.setStyle(null);
        dateDepartPicker.setStyle(null);
        typeVoyageChoice.setStyle(null);
        modePaiementChoice.setStyle(null);
        regimeAlimentaireField.setStyle(null);
        statutChoice.setStyle(null);
    }

    private void showError(Label errorLabel, Control field, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        field.setStyle("-fx-border-color: #FF5252;");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}