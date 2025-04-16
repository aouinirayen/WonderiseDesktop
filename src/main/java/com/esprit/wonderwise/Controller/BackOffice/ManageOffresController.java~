package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.offre;
import com.esprit.wonderwise.Service.OffreService;
import javafx.beans.property.SimpleDoubleProperty;
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

public class ManageOffresController {

    @FXML
    private TableView<offre> offresTable;

    @FXML
    private TableColumn<offre, String> titreColumn;

    @FXML
    private TableColumn<offre, String> descriptionColumn;

    @FXML
    private TableColumn<offre, Double> prixColumn;

    @FXML
    private TableColumn<offre, String> paysColumn;

    @FXML
    private TableColumn<offre, Void> actionsColumn;

    private OffreService offreService = new OffreService();
    private ObservableList<offre> offresList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurer les colonnes
        titreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitre()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        prixColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrix()).asObject());
        paysColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPays()));

        // Configurer la colonne Actions
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            {
                editButton.getStyleClass().addAll("button-action", "button-edit");
                deleteButton.getStyleClass().addAll("button-action", "button-delete");

                editButton.setOnAction(event -> {
                    offre offre = getTableView().getItems().get(getIndex());
                    handleEditOffre(offre);
                });

                deleteButton.setOnAction(event -> {
                    offre offre = getTableView().getItems().get(getIndex());
                    handleDeleteOffre(offre);
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

        // Charger les offres
        loadOffres();
    }

    private void loadOffres() {
        try {
            offresList.setAll(offreService.readAll());
            offresTable.setItems(offresList);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les offres : " + e.getMessage());
        }
    }

    @FXML
    private void openAddOffreForm() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/esprit/wonderwise/BackOffice/AddOffre.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Ajouter une offre");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            
            // Attendre que la fenêtre soit fermée pour rafraîchir la table
            stage.showAndWait();
            loadOffres();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
        }
    }

    private void handleEditOffre(offre offre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/AddOffre.fxml"));
            Scene scene = new Scene(loader.load());
            AddOffreController controller = loader.getController();
            controller.initData(offre); // Passer les données de l'offre
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Modifier une offre");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            // Recharger les offres après modification
            loadOffres();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
        }
    }

    private void handleDeleteOffre(offre offre) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la suppression");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous vraiment supprimer l'offre : " + offre.getTitre() + " ?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                offreService.delete(offre.getId());
                offresList.remove(offre);
                showAlert("Succès", "Offre supprimée avec succès.");
            } catch (SQLException e) {
                showAlert("Erreur", "Impossible de supprimer l'offre : " + e.getMessage());
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