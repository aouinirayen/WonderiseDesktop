package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.reservation;
import com.esprit.wonderwise.Service.ReservationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReservationListController implements Initializable {

    @FXML
    private FlowPane reservationContainer;
    
    @FXML
    private VBox emptyState;

    private ReservationService reservationService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reservationService = new ReservationService();
        loadReservations();
    }
    
    @FXML
    public void openAddReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/AddReservationFront.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/esprit/wonderwise/FrontOffice/AddReservationFront.css").toExternalForm());
            
            Stage stage = new Stage();
            stage.setTitle("Nouvelle Réservation");
            stage.setScene(scene);
            stage.showAndWait();
            
            // Refresh the list after adding a new reservation
            loadReservations();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire: " + e.getMessage());
        }
    }

    public void loadReservations() {
        reservationContainer.getChildren().clear();
        List<reservation> reservations = getReservations();

        if (reservations != null) {
            if (reservations.isEmpty()) {
                emptyState.setVisible(true);
                emptyState.setManaged(true);
                reservationContainer.setVisible(false);
                reservationContainer.setManaged(false);
            } else {
                emptyState.setVisible(false);
                emptyState.setManaged(false);
                reservationContainer.setVisible(true);
                reservationContainer.setManaged(true);
                
                for (reservation res : reservations) {
                    VBox card = createReservationCard(res);
                    reservationContainer.getChildren().add(card);
                }
            }
        }
    }

    private VBox createReservationCard(reservation res) {
        VBox card = new VBox(10);
        card.getStyleClass().add("reservation-item");
        
        // En-tête de la carte
        HBox header = new HBox(10);
        header.getStyleClass().add("card-header");
        
        // Nom complet
        Label nameLabel = new Label(res.getNom() + " " + res.getPrenom());
        nameLabel.getStyleClass().add("client-name");
        
        // Badge de statut
        Label statusBadge = new Label(res.getStatut());
        statusBadge.getStyleClass().addAll("status-badge", "status-" + res.getStatut().toLowerCase().replace(" ", "-"));
        
        header.getChildren().addAll(nameLabel, statusBadge);
        
        // Container des informations
        VBox infoContainer = new VBox(8);
        infoContainer.getStyleClass().add("info-container");
        
        // Email
        HBox emailBox = new HBox(5);
        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("info-label");
        Label emailValue = new Label(res.getEmail());
        emailValue.getStyleClass().add("info-value");
        emailBox.getChildren().addAll(emailLabel, emailValue);
        
        // Téléphone
        HBox phoneBox = new HBox(5);
        Label phoneLabel = new Label("Téléphone:");
        phoneLabel.getStyleClass().add("info-label");
        Label phoneValue = new Label(res.getTelephone());
        phoneValue.getStyleClass().add("info-value");
        phoneBox.getChildren().addAll(phoneLabel, phoneValue);
        
        // Nombre de personnes
        HBox personsBox = new HBox(5);
        Label personsLabel = new Label("Nombre de personnes:");
        personsLabel.getStyleClass().add("info-label");
        Label personsValue = new Label(String.valueOf(res.getNombrePersonne()));
        personsValue.getStyleClass().add("info-value");
        personsBox.getChildren().addAll(personsLabel, personsValue);
        
        infoContainer.getChildren().addAll(emailBox, phoneBox, personsBox);
        
        // Boutons d'action
        HBox actions = new HBox(10);
        actions.getStyleClass().add("actions-container");
        
        Button editButton = new Button("Modifier");
        editButton.getStyleClass().addAll("action-button", "edit-button");
        editButton.setOnAction(e -> modifyReservation(res));
        
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().addAll("action-button", "delete-button");
        deleteButton.setOnAction(e -> deleteReservation(res));
        
        actions.getChildren().addAll(editButton, deleteButton);
        
        // Assembler la carte
        card.getChildren().addAll(header, infoContainer, actions);
        
        return card;
    }

    private void modifyReservation(reservation res) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/AddReservationFront.fxml"));
            Parent root = loader.load();

            AddReservationFrontController controller = loader.getController();
            controller.setReservationForModification(res);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/esprit/wonderwise/FrontOffice/AddReservationFront.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Modifier Réservation");
            stage.setScene(scene);
            stage.showAndWait();

            // Refresh the list after modification
            loadReservations();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
        }
    }

    private void deleteReservation(reservation res) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Supprimer la réservation");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cette réservation ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservationService.delete(res.getId());
                reservationContainer.getChildren().removeIf(node ->
                        node instanceof VBox && node.getUserData() == res);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation supprimée avec succès!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    private List<reservation> getReservations() {
        try {
            return reservationService.readAll();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des réservations: " + e.getMessage());
            return null;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
