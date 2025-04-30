package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.reservation;
import com.esprit.wonderwise.Service.ReservationService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManageReservationsController {

    @FXML
    private VBox reservationsContainer;
    
    @FXML
    private VBox emptyState;

    private ReservationService reservationService = new ReservationService();
    private ObservableList<reservation> reservationsList = FXCollections.observableArrayList();
    private BackOfficeController backOfficeController;

    public void setBackOfficeController(BackOfficeController controller) {
        this.backOfficeController = controller;
    }

    @FXML
    public void initialize() {
        loadReservations();
    }

    private void loadReservations() {
        try {
            // Clear previous data
            reservationsList.clear();
            reservationsContainer.getChildren().clear();
            
            // Load all reservations
            List<reservation> allReservations = reservationService.readAll();
            reservationsList.addAll(allReservations);
            
            // Check if we have reservations to display
            if (reservationsList.isEmpty()) {
                reservationsContainer.setVisible(false);
                reservationsContainer.setManaged(false);
                emptyState.setVisible(true);
                emptyState.setManaged(true);
                System.out.println("No reservations found to display.");
            } else {
                reservationsContainer.setVisible(true);
                reservationsContainer.setManaged(true);
                emptyState.setVisible(false);
                emptyState.setManaged(false);
                
                System.out.println("Loading " + reservationsList.size() + " reservations.");
                
                // Create cards for each reservation and add to container
                for (reservation res : reservationsList) {
                    HBox card = createReservationCard(res);
                    reservationsContainer.getChildren().add(card);
                }
                
                // Ensure proper layout
                reservationsContainer.setMinHeight(reservationsList.size() * 150);
                reservationsContainer.applyCss();
                reservationsContainer.layout();
                
                System.out.println("Loaded " + reservationsContainer.getChildren().size() + " reservation cards.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les réservations : " + e.getMessage());
        }
    }
    
    private HBox createReservationCard(reservation res) {
        // Main card container
        HBox card = new HBox();
        card.getStyleClass().add("reservation-card");
        card.setSpacing(15);
        
        // Client info section
        VBox clientInfo = new VBox();
        clientInfo.getStyleClass().add("card-client-info");
        
        Label nameLabel = new Label(res.getNom() + " " + res.getPrenom());
        nameLabel.getStyleClass().add("card-title");
        
        Label emailLabel = new Label(res.getEmail());
        emailLabel.getStyleClass().add("client-email");
        
        Label statusBadge = new Label(res.getStatut());
        statusBadge.getStyleClass().addAll("status-badge");
        // Add specific style based on status
        if ("En attente".equals(res.getStatut())) {
            statusBadge.getStyleClass().add("status-pending");
        } else if ("Confirmé".equals(res.getStatut())) {
            statusBadge.getStyleClass().add("status-confirmed");
        } else if ("Annulé".equals(res.getStatut())) {
            statusBadge.getStyleClass().add("status-cancelled");
        }
        
        clientInfo.getChildren().addAll(nameLabel, emailLabel, statusBadge);
        
        // Reservation details section
        VBox details = new VBox();
        details.getStyleClass().add("reservation-details");
        
        // Number of people
        VBox personnesBox = new VBox();
        Label personnesLabel = new Label("Nombre de personnes");
        personnesLabel.getStyleClass().add("detail-label");
        Label personnesValue = new Label(String.valueOf(res.getNombrePersonne()));
        personnesValue.getStyleClass().add("detail-value");
        personnesBox.getChildren().addAll(personnesLabel, personnesValue);
        
        // Departure date
        VBox dateBox = new VBox();
        Label dateLabel = new Label("Date de départ");
        dateLabel.getStyleClass().add("detail-label");
        
        String dateValue = "Non définie";
        if (res.getDateDepart() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dateValue = res.getDateDepart().format(formatter);
        }
        Label dateValueLabel = new Label(dateValue);
        dateValueLabel.getStyleClass().add("detail-value");
        dateBox.getChildren().addAll(dateLabel, dateValueLabel);
        
        details.getChildren().addAll(personnesBox, dateBox);
        
        // Spacer to push actions to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Actions section
        VBox actions = new VBox();
        actions.getStyleClass().add("card-actions");
        actions.setAlignment(Pos.CENTER);
        
        Button editButton = new Button("Modifier");
        editButton.getStyleClass().addAll("button-action", "button-edit");
        editButton.setOnAction(event -> handleEditReservation(res));
        
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().addAll("button-action", "button-delete");
        deleteButton.setOnAction(event -> handleDeleteReservation(res));
        
        actions.getChildren().addAll(editButton, deleteButton);
        
        // Add all sections to the card
        card.getChildren().addAll(clientInfo, details, spacer, actions);
        
        return card;
    }

    @FXML
    private void openAddReservationForm() {
        if (backOfficeController != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/AddReservation.fxml"));
                backOfficeController.loadContent(loader);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "BackOfficeController n'est pas défini.");
        }
    }

    private void handleEditReservation(reservation res) {
        if (backOfficeController != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/AddReservation.fxml"));
                backOfficeController.loadContent(loader);
                AddReservationController controller = loader.getController();
                controller.initData(res);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "BackOfficeController n'est pas défini.");
        }
    }

    private void handleDeleteReservation(reservation res) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la suppression");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous vraiment supprimer la réservation de " + res.getNom() + " " + res.getPrenom() + " ?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                reservationService.delete(res.getId());
                reservationsList.remove(res);
                loadReservations(); // Reload cards
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation supprimée avec succès.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer la réservation : " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}