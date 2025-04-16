package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.reservation;
import com.esprit.wonderwise.Service.ReservationService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class ManageReservationsController {

    @FXML
    private TableView<reservation> reservationsTable;

    @FXML
    private TableColumn<reservation, Integer> idColumn;

    @FXML
    private TableColumn<reservation, Integer> offreIdColumn;

    @FXML
    private TableColumn<reservation, String> nomColumn;

    @FXML
    private TableColumn<reservation, String> prenomColumn;

    @FXML
    private TableColumn<reservation, String> emailColumn;

    @FXML
    private TableColumn<reservation, Integer> nombrePersonneColumn;



    @FXML
    private TableColumn<reservation, String> statutColumn;

    @FXML
    private TableColumn<reservation, Void> actionsColumn;

    private ReservationService reservationService = new ReservationService();
    private ObservableList<reservation> reservationsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurer les colonnes
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        offreIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getOffreId()).asObject());
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        prenomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        nombrePersonneColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNombrePersonne()).asObject());

        statutColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatut()));

        // Configurer la colonne Actions
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            {
                editButton.getStyleClass().addAll("button-action", "button-edit");
                deleteButton.getStyleClass().addAll("button-action", "button-delete");

                editButton.setOnAction(event -> {
                    reservation res = getTableView().getItems().get(getIndex());
                    handleEditReservation(res);
                });

                deleteButton.setOnAction(event -> {
                    reservation res = getTableView().getItems().get(getIndex());
                    handleDeleteReservation(res);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(10, editButton, deleteButton));
                }
            }
        });

        // Charger les réservations
        loadReservations();
    }

    private void loadReservations() {
        try {
            reservationsList.setAll(reservationService.readAll());
            reservationsTable.setItems(reservationsList);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les réservations : " + e.getMessage());
        }
    }

    @FXML
    private void openAddReservationForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/AddReservation.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Ajouter une réservation");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            // Recharger les réservations après ajout
            loadReservations();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
        }
    }

    private void handleEditReservation(reservation res) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/AddReservation.fxml"));
            Scene scene = new Scene(loader.load(), 600, 500);
            AddReservationController controller = loader.getController();
            controller.initData(res);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Modifier une réservation");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            // Recharger les réservations après modification
            loadReservations();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
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
                showAlert("Succès", "Réservation supprimée avec succès.");
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible de supprimer la réservation : " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}