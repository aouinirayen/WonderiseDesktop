package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.offre;
import com.esprit.wonderwise.Model.reservation;
import com.esprit.wonderwise.Service.EmailService;
import com.esprit.wonderwise.Service.OffreService;
import com.esprit.wonderwise.Service.PaymentService;
import com.esprit.wonderwise.Service.ReservationService;
import com.stripe.exception.StripeException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class AddReservationFrontController {

    @FXML
    private ChoiceBox<offre> offreChoice;
    @FXML
    private Label offreError;
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
    private Label regimeAlimentaireError;
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
    @FXML
    private VBox formContainer;
    @FXML
    private VBox listContainer;
    @FXML
    private FlowPane reservationContainer;
    @FXML
    private TableView<reservation> reservationsTable;
    @FXML
    private TableColumn<reservation, String> nomColumn;
    @FXML
    private TableColumn<reservation, String> prenomColumn;
    @FXML
    private TableColumn<reservation, String> emailColumn;
    @FXML
    private TableColumn<reservation, String> telephoneColumn;
    @FXML
    private TableColumn<reservation, Integer> nombrePersonneColumn;
    @FXML
    private TableColumn<reservation, String> villeColumn;
    @FXML
    private TableColumn<reservation, String> modePaiementColumn;
    @FXML
    private TableColumn<reservation, String> statutColumn;
    @FXML
    private TableColumn<reservation, LocalDateTime> dateReservationColumn;

    private ReservationService reservationService = new ReservationService();
    private OffreService offreService = new OffreService();
    private Map<String, offre> offreMap = new HashMap<>();
    private int offreId;
    private reservation currentReservation;

    @FXML
    public void initialize() {
        // Load CSS at scene level
        formContainer.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null && !newScene.getStylesheets().contains("@AddReservationFront.css")) {
                newScene.getStylesheets().add(getClass().getResource("/com/esprit/wonderwise/FrontOffice/AddReservationFront.css").toExternalForm());
            }
        });
        
        // Set default values
        modePaiementChoice.setValue("Carte bancaire");
        regimeAlimentaireField.setText("Standard");
        
        // Load available offers
        loadOffres();
        
        // Setup offer choice box
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
        
        // Add listener to update offreId when selection changes
        offreChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                offreId = newVal.getId();
            }
        });
    }
    
    private void loadOffres() {
        try {
            List<offre> offres = offreService.readAll();
            ObservableList<offre> offreItems = FXCollections.observableArrayList(offres);
            offreChoice.setItems(offreItems);
            
            // Pre-select first item if available
            if (!offres.isEmpty()) {
                offreChoice.getSelectionModel().selectFirst();
                offreId = offres.get(0).getId();
            }
            
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des offres : " + e.getMessage());
        }
    }

    public void initData(int offreId) {
        this.offreId = offreId;
        
        // Pre-select the offer if it exists in the list
        try {
            for (offre o : offreChoice.getItems()) {
                if (o.getId() == offreId) {
                    offreChoice.getSelectionModel().select(o);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Could not pre-select offer: " + e.getMessage());
        }
    }

    public void setReservationForModification(reservation reservation) {
        this.currentReservation = reservation;

        // Pre-fill the form fields with reservation data
        nomField.setText(reservation.getNom());
        prenomField.setText(reservation.getPrenom());
        emailField.setText(reservation.getEmail());
        telephoneField.setText(reservation.getTelephone());
        nombrePersonneField.setText(String.valueOf(reservation.getNombrePersonne()));
        villeField.setText(reservation.getVille());
        modePaiementChoice.setValue(reservation.getModePaiement());
        regimeAlimentaireField.setText(reservation.getRegimeAlimentaire() != null ? reservation.getRegimeAlimentaire() : "Standard");
        commentaireField.setText(reservation.getCommentaire());
        
        // Pre-select the offer if it exists in the list
        try {
            for (offre o : offreChoice.getItems()) {
                if (o.getId() == reservation.getOffreId()) {
                    offreChoice.getSelectionModel().select(o);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Could not pre-select offer: " + e.getMessage());
        }

        // Change button text to indicate modification
        addButton.setText("Modifier");
    }

    @FXML
    private void handleAddReservation() {
        // Get all field values first
        offre selectedOffre = offreChoice.getValue();
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String telephone = telephoneField.getText();
        String nombrePersonneText = nombrePersonneField.getText();
        String ville = villeField.getText();
        String modePaiement = modePaiementChoice.getValue();
        String regimeAlimentaire = regimeAlimentaireField.getText();
        String commentaire = commentaireField.getText();

        // Reset error styles
        resetErrorStyles();

        boolean hasError = false;

        // Validation of fields
        if (selectedOffre == null) {
            showFieldError(offreChoice, offreError, "Veuillez sélectionner une destination");
            hasError = true;
        }
        if (nom == null || nom.trim().isEmpty()) {
            showFieldError(nomField, nomError, "Le nom est obligatoire");
            hasError = true;
        }
        if (prenom == null || prenom.trim().isEmpty()) {
            showFieldError(prenomField, prenomError, "Le prénom est obligatoire");
            hasError = true;
        }
        if (email == null || email.trim().isEmpty()) {
            showFieldError(emailField, emailError, "L'email est obligatoire");
            hasError = true;
        } else if (!email.trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showFieldError(emailField, emailError, "Format d'email invalide");
            hasError = true;
        }
        if (telephone == null || telephone.trim().isEmpty()) {
            showFieldError(telephoneField, telephoneError, "Le téléphone est obligatoire");
            hasError = true;
        } else if (!telephone.trim().matches("^[0-9]{8}$")) {
            showFieldError(telephoneField, telephoneError, "Le numéro doit contenir 8 chiffres");
            hasError = true;
        }
        if (nombrePersonneText == null || nombrePersonneText.trim().isEmpty()) {
            showFieldError(nombrePersonneField, nombrePersonneError, "Le nombre de personnes est obligatoire");
            hasError = true;
        } else {
            try {
                int nombre = Integer.parseInt(nombrePersonneText.trim());
                if (nombre <= 0) {
                    showFieldError(nombrePersonneField, nombrePersonneError, "Le nombre doit être supérieur à 0");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                showFieldError(nombrePersonneField, nombrePersonneError, "Veuillez entrer un nombre valide");
                hasError = true;
            }
        }
        if (modePaiement == null) {
            showFieldError(modePaiementChoice, modePaiementError, "Le mode de paiement est obligatoire");
            hasError = true;
        }
        if (regimeAlimentaire == null || regimeAlimentaire.trim().isEmpty()) {
            showFieldError(regimeAlimentaireField, regimeAlimentaireError, "Le régime alimentaire est obligatoire");
            hasError = true;
        }

        // If there are errors, stop
        if (hasError) {
            return;
        }

        // Proceed with reservation creation or update
        try {
            reservation newReservation = currentReservation != null ? currentReservation : new reservation();
            if (currentReservation != null) {
                newReservation.setId(currentReservation.getId());
                newReservation.setOffreId(selectedOffre.getId());
            } else {
                newReservation.setOffreId(selectedOffre.getId());
            }

            newReservation.setNom(nom.trim());
            newReservation.setPrenom(prenom.trim());
            newReservation.setEmail(email.trim());
            newReservation.setTelephone(telephone.trim());
            newReservation.setNombrePersonne(Integer.parseInt(nombrePersonneText.trim()));
            newReservation.setVille(ville != null ? ville.trim() : null);
            newReservation.setModePaiement(modePaiement);
            newReservation.setRegimeAlimentaire(regimeAlimentaire.trim());
            newReservation.setCommentaire(commentaire != null && !commentaire.trim().isEmpty() ? commentaire.trim() : null);
            newReservation.setDateDepart(null);
            newReservation.setHeureDepart(null);
            newReservation.setTypeVoyage("Individuel");
            newReservation.setPreferencesVoyage(null);
            newReservation.setDateReservation(LocalDateTime.now());
            newReservation.setStatut("En attente");

            ReservationService reservationService = new ReservationService();
            PaymentService paymentService = new PaymentService();

            // Save the reservation first to get an ID
            if (currentReservation != null) {
                reservationService.update(newReservation);
            } else {
                reservationService.create(newReservation);
            }

            // Validate reservation ID
            if (newReservation.getId() <= 0) {
                throw new SQLException("Failed to obtain a valid reservation ID.");
            }

            if ("Carte bancaire".equals(modePaiement)) {
                // Create Stripe checkout session
                String checkoutUrl = paymentService.createCheckoutSession(newReservation, selectedOffre);

                // Send confirmation email
                EmailService.sendReservationConfirmation(
                        newReservation.getEmail(),
                        newReservation.getPrenom() + " " + newReservation.getNom(),
                        newReservation,
                        selectedOffre
                );

                try {
                    System.out.println("Opening Stripe checkout URL: " + checkoutUrl);
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(checkoutUrl));
                } catch (Exception e) {
                    showAlert("Erreur", "Impossible d'ouvrir la page de paiement : " + e.getMessage());
                    return;
                }

                showAlert("Réservation Initiée", "Vous allez être redirigé vers la page de paiement Stripe pour finaliser votre réservation.\n" +
                        "Un email de confirmation a été envoyé à " + newReservation.getEmail() + ".");
            } else {
                // Send confirmation email for non-Stripe payment
                EmailService.sendReservationConfirmation(
                        newReservation.getEmail(),
                        newReservation.getPrenom() + " " + newReservation.getNom(),
                        newReservation,
                        selectedOffre
                );

                showAlert("Succès", currentReservation != null ? "Réservation modifiée avec succès !" : "Réservation effectuée avec succès !");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/FrontOffice.fxml"));
                Parent root = loader.load();
                FrontOfficeController frontController = loader.getController();
                frontController.loadContent("/com/esprit/wonderwise/FrontOffice/ReservationList.fxml");

                Scene scene = new Scene(root);
                Stage stage = (Stage) nomField.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }

        } catch (SQLException e) {
            showAlert("Erreur", currentReservation != null ? "Erreur lors de la modification de la réservation : " + e.getMessage() : "Erreur lors de la réservation : " + e.getMessage());
        } catch (StripeException e) {
            showAlert("Erreur de paiement", "Erreur lors de la création de la session de paiement : " + e.getMessage());
        } catch (Exception e) {
            showAlert("Erreur", "Erreur inattendue : " + e.getMessage());
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
        offreChoice.setStyle(null);
        offreError.setVisible(false);
        offreError.setManaged(false);
        
        nomField.setStyle(null);
        nomError.setVisible(false);
        nomError.setManaged(false);
        
        prenomField.setStyle(null);
        prenomError.setVisible(false);
        prenomError.setManaged(false);
        
        emailField.setStyle(null);
        emailError.setVisible(false);
        emailError.setManaged(false);
        
        telephoneField.setStyle(null);
        telephoneError.setVisible(false);
        telephoneError.setManaged(false);
        
        nombrePersonneField.setStyle(null);
        nombrePersonneError.setVisible(false);
        nombrePersonneError.setManaged(false);
        
        modePaiementChoice.setStyle(null);
        modePaiementError.setVisible(false);
        modePaiementError.setManaged(false);
        
        regimeAlimentaireField.setStyle(null);
        regimeAlimentaireError.setVisible(false);
        regimeAlimentaireError.setManaged(false);
    }

    private void showFieldError(Control field, Label errorLabel, String message) {
        field.getStyleClass().add("error");
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    @FXML
    private void showReservationForm() {
        // Clear form fields
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        nombrePersonneField.clear();
        villeField.clear();
        modePaiementChoice.setValue(null);
        regimeAlimentaireField.clear();
        commentaireField.clear();

        // Reset error styles
        resetErrorStyles();

        // Show form and hide list
        formContainer.setVisible(true);
        listContainer.setVisible(false);

        // Reset currentReservation
        currentReservation = null;
        addButton.setText("Réserver");
    }

    private void loadReservations() {
        try {
            ReservationService reservationService = new ReservationService();
            List<reservation> reservations = reservationService.readAll();
            
            // Configure les colonnes de la table
            nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
            prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
            nombrePersonneColumn.setCellValueFactory(new PropertyValueFactory<>("nombrePersonne"));
            villeColumn.setCellValueFactory(new PropertyValueFactory<>("ville"));
            modePaiementColumn.setCellValueFactory(new PropertyValueFactory<>("modePaiement"));
            statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
            dateReservationColumn.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));

            // Formater la date de réservation
            dateReservationColumn.setCellFactory(column -> new TableCell<reservation, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                    }
                }
            });

            // Met à jour la table avec les nouvelles données
            reservationsTable.setItems(FXCollections.observableArrayList(reservations));
            
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des réservations : " + e.getMessage());
        }
    }

    private VBox createReservationCard(reservation res) {
        VBox card = new VBox(10);
        card.getStyleClass().add("reservation-card");
        card.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Header section with destination and status
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER);
        header.getStyleClass().add("reservation-card-header");
        
        // Try to find the offer name from the offreId
        String offreName = "Offre #" + res.getOffreId();
        for (offre o : offreChoice.getItems()) {
            if (o.getId() == res.getOffreId()) {
                offreName = o.getTitre();
                break;
            }
        }
        
        Label destinationLabel = new Label(offreName);
        destinationLabel.getStyleClass().add("reservation-title");
        
        Label statusBadge = new Label(res.getStatut());
        statusBadge.getStyleClass().add("status-badge");
        if ("confirmé".equalsIgnoreCase(res.getStatut())) {
            statusBadge.getStyleClass().add("status-confirmed");
        } else if ("en attente".equalsIgnoreCase(res.getStatut())) {
            statusBadge.getStyleClass().add("status-pending");
        } else if ("annulé".equalsIgnoreCase(res.getStatut())) {
            statusBadge.getStyleClass().add("status-cancelled");
        }
        
        header.getChildren().addAll(destinationLabel, statusBadge);
        
        // Customer info section
        VBox customerInfo = new VBox(5);
        customerInfo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        customerInfo.getStyleClass().add("customer-info");
        
        Label nameLabel = new Label(res.getNom() + " " + res.getPrenom());
        nameLabel.getStyleClass().add("customer-name");
        
        HBox contactInfo = new HBox(10);
        contactInfo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label emailLabel = new Label(res.getEmail());
        emailLabel.getStyleClass().add("customer-email");
        
        Label phoneLabel = new Label(res.getTelephone());
        phoneLabel.getStyleClass().add("customer-phone");
        
        contactInfo.getChildren().addAll(emailLabel, phoneLabel);
        
        customerInfo.getChildren().addAll(nameLabel, contactInfo);
        
        // Reservation details section
        VBox detailsBox = new VBox(5);
        detailsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        detailsBox.getStyleClass().add("reservation-details");
        
        Label personsLabel = new Label("Personnes: " + res.getNombrePersonne());
        personsLabel.getStyleClass().add("detail-item");
        
        Label paymentLabel = new Label("Paiement: " + res.getModePaiement());
        paymentLabel.getStyleClass().add("detail-item");
        
        // Format the date if it exists, or use a placeholder
        String dateStr = "Date non disponible";
        if (res.getDateReservation() != null) {
            dateStr = res.getDateReservation().toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        Label dateLabel = new Label("Date: " + dateStr);
        dateLabel.getStyleClass().add("detail-item");
        
        detailsBox.getChildren().addAll(personsLabel, paymentLabel, dateLabel);
        
        // Action buttons
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonsBox.getStyleClass().add("card-buttons");
        
        Button editButton = new Button("Modifier");
        editButton.getStyleClass().add("card-button");
        editButton.getStyleClass().add("edit-button");
        editButton.setOnAction(e -> modifyReservation(res));
        
        Button deleteButton = new Button("Annuler");
        deleteButton.getStyleClass().add("card-button");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> {
            try {
                reservationService.delete(res.getId());
                loadReservations();
            } catch (SQLException ex) {
                showAlert("Erreur", "Erreur lors de la suppression de la réservation: " + ex.getMessage());
            }
        });
        
        // Only add the buttons if the reservation status is not "annulé"
        if (!"annulé".equalsIgnoreCase(res.getStatut())) {
            buttonsBox.getChildren().addAll(editButton, deleteButton);
        } else {
            // Add a spacer to maintain the layout
            buttonsBox.setPrefHeight(40);
        }
        
        // Add all sections to the card
        card.getChildren().addAll(header, customerInfo, detailsBox, buttonsBox);
        
        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.getStyleClass().add("reservation-card-hover");
        });
        
        card.setOnMouseExited(e -> {
            card.getStyleClass().remove("reservation-card-hover");
        });
        
        return card;
    }

    private void modifyReservation(reservation res) {
        currentReservation = res;

        // Pre-fill form fields
        nomField.setText(res.getNom());
        prenomField.setText(res.getPrenom());
        emailField.setText(res.getEmail());
        telephoneField.setText(res.getTelephone());
        nombrePersonneField.setText(String.valueOf(res.getNombrePersonne()));
        villeField.setText(res.getVille());
        modePaiementChoice.setValue(res.getModePaiement());
        regimeAlimentaireField.setText(res.getRegimeAlimentaire() != null ? res.getRegimeAlimentaire() : "Standard");
        commentaireField.setText(res.getCommentaire());
        
        // Pre-select the offer if it exists in the list
        try {
            for (offre o : offreChoice.getItems()) {
                if (o.getId() == res.getOffreId()) {
                    offreChoice.getSelectionModel().select(o);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Could not pre-select offer: " + e.getMessage());
        }

        // Change button text
        addButton.setText("Modifier");

        // Show form
        formContainer.setVisible(true);
        listContainer.setVisible(false);
    }
}