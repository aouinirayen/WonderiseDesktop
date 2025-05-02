package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.offre;
import com.esprit.wonderwise.Service.OffreService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class ManageOffresController {

    @FXML
    private VBox offresContainer;

    @FXML
    private VBox emptyState;

    @FXML
    private Label totalOffresLabel;

    @FXML
    private Label prixMoyenLabel;

    @FXML
    private StackPane pieChartContainer;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox;

    private OffreService offreService = new OffreService();
    private ObservableList<offre> offresList = FXCollections.observableArrayList();
    private BackOfficeController backOfficeController; // Reference to BackOfficeController
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private PieChart typesPieChart;

    private FilteredList<offre> filteredOffres;
    private SortedList<offre> sortedOffres;

    // Method to set the BackOfficeController
    public void setBackOfficeController(BackOfficeController controller) {
        this.backOfficeController = controller;
    }

    @FXML
    public void initialize() {
        typesPieChart = new PieChart();
        typesPieChart.setTitle("Répartition par type");
        typesPieChart.setLabelsVisible(true);
        typesPieChart.setLegendVisible(true);
        pieChartContainer.getChildren().add(typesPieChart);

        // Initialize filtered and sorted lists
        filteredOffres = new FilteredList<>(offresList, p -> true);
        sortedOffres = new SortedList<>(filteredOffres);

        // Setup search listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredOffres.setPredicate(offre -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return offre.getTitre().toLowerCase().contains(lowerCaseFilter);
            });
            updateDisplay();
        });

        // Setup filter listener
        filterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filteredOffres.setPredicate(offre -> {
                if (newValue == null || newValue.equals("Toutes les offres")) {
                    return true;
                }
                switch (newValue) {
                    case "Complètement disponible":
                        return offre.getPlacesDisponibles() == offre.getNombrePlaces();
                    case "Partiellement disponible":
                        return offre.getPlacesDisponibles() > 0 && offre.getPlacesDisponibles() < offre.getNombrePlaces();
                    case "Complet":
                        return offre.getPlacesDisponibles() == 0;
                    default:
                        return true;
                }
            });
            updateDisplay();
        });

        // Setup sort listener
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                switch (newValue) {
                    case "Prix (croissant)":
                        sortedOffres.setComparator(Comparator.comparing(offre::getPrix));
                        break;
                    case "Prix (décroissant)":
                        sortedOffres.setComparator(Comparator.comparing(offre::getPrix).reversed());
                        break;
                    case "Places disponibles":
                        sortedOffres.setComparator(Comparator.comparing(offre::getPlacesDisponibles).reversed());
                        break;
                    case "Date (plus récent)":
                        sortedOffres.setComparator(Comparator.comparing(offre::getDateDebut).reversed());
                        break;
                    case "Date (plus ancien)":
                        sortedOffres.setComparator(Comparator.comparing(offre::getDateDebut));
                        break;
                }
                updateDisplay();
            }
        });

        // Load offers
        loadOffres();
    }

    private void updateDisplay() {
        offresContainer.getChildren().clear();
        sortedOffres.forEach(offre -> offresContainer.getChildren().add(createOfferCard(offre)));
        updateStatistics();
    }

    private void updateStatistics() {
        // Mise à jour du nombre total d'offres
        totalOffresLabel.setText(String.valueOf(sortedOffres.size()));

        // Calcul et mise à jour du prix moyen
        double prixMoyen = sortedOffres.stream()
                .mapToDouble(offre::getPrix)
                .average()
                .orElse(0.0);
        prixMoyenLabel.setText(String.format("%.2f DT", prixMoyen));

        // Mise à jour du graphique circulaire des disponibilités
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        long completementDisponibles = sortedOffres.stream()
                .filter(o -> o.getPlacesDisponibles() == o.getNombrePlaces())
                .count();

        long partialDisponibles = sortedOffres.stream()
                .filter(o -> o.getPlacesDisponibles() > 0 && o.getPlacesDisponibles() < o.getNombrePlaces())
                .count();

        long complet = sortedOffres.stream()
                .filter(o -> o.getPlacesDisponibles() == 0)
                .count();

        // Création des données avec des couleurs spécifiques
        PieChart.Data completData = new PieChart.Data("Complètement disponible", completementDisponibles);
        PieChart.Data partialData = new PieChart.Data("Partiellement disponible", partialDisponibles);
        PieChart.Data reservedData = new PieChart.Data("Complet", complet);

        if (completementDisponibles > 0) pieChartData.add(completData);
        if (partialDisponibles > 0) pieChartData.add(partialData);
        if (complet > 0) pieChartData.add(reservedData);

        typesPieChart.setData(pieChartData);

        // Application des couleurs et ajout des tooltips
        pieChartData.forEach(data -> {
            String tooltipText = String.format("%s: %.0f offres", data.getName(), data.getPieValue());
            Tooltip tooltip = new Tooltip(tooltipText);

            if (data.getName().equals("Complètement disponible")) {
                data.getNode().setStyle("-fx-pie-color: #22c55e;"); // Vert
            } else if (data.getName().equals("Partiellement disponible")) {
                data.getNode().setStyle("-fx-pie-color: #f97316;"); // Orange
            } else if (data.getName().equals("Complet")) {
                data.getNode().setStyle("-fx-pie-color: #ef4444;"); // Rouge
            }

            Tooltip.install(data.getNode(), tooltip);
        });
    }

    private void loadOffres() {
        try {
            offresList.setAll(offreService.readAll());

            // Show empty state if no offers
            if (offresList.isEmpty()) {
                emptyState.setVisible(true);
                emptyState.setManaged(true);
            } else {
                emptyState.setVisible(false);
                emptyState.setManaged(false);

                // Clear existing cards from container
                offresContainer.getChildren().clear();

                // Create cards for each offer
                sortedOffres.forEach(offre -> offresContainer.getChildren().add(createOfferCard(offre)));
            }

            // Mise à jour des statistiques après le chargement des offres
            updateStatistics();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les offres : " + e.getMessage());
        }
    }

    private HBox createOfferCard(offre offre) {
        // Create the main card container
        HBox card = new HBox();
        card.getStyleClass().add("offer-card");
        card.setSpacing(15);

        // Left side: Image
        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("card-image-container");
        HBox.setMargin(imageContainer, new Insets(0, 0, 0, 0));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("card-image");

        // Set image if available
        if (offre.getImage() != null && !offre.getImage().isEmpty()) {
            File file = new File(offre.getImage());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            } else {
                // Default image
                try {
                    imageView.setImage(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/default-offer.png")));
                } catch (Exception e) {
                    // If default image can't be loaded, just leave it empty
                }
            }
        } else {
            // Default image
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/default-offer.png")));
            } catch (Exception e) {
                // If default image can't be loaded, just leave it empty
            }
        }

        imageContainer.getChildren().add(imageView);

        // Middle: Offer Details
        VBox contentBox = new VBox();
        contentBox.setSpacing(10);
        contentBox.getStyleClass().add("card-content");
        HBox.setHgrow(contentBox, javafx.scene.layout.Priority.ALWAYS);

        // Title and country row
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(offre.getTitre());
        titleLabel.getStyleClass().add("card-title");
        titleRow.getChildren().add(titleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        titleRow.getChildren().add(spacer);

        Label paysLabel = new Label(offre.getPays());
        paysLabel.getStyleClass().add("card-pays");
        titleRow.getChildren().add(paysLabel);

        // Description
        Text descriptionText = new Text(offre.getDescription());
        descriptionText.getStyleClass().add("card-description");
        descriptionText.setWrappingWidth(500);

        // Info row
        HBox infoRow = new HBox();
        infoRow.setSpacing(15);
        infoRow.setAlignment(Pos.CENTER_LEFT);

        // Price
        VBox priceBox = new VBox();
        priceBox.getStyleClass().add("price-container");
        Label priceLabel = new Label("Prix:");
        priceLabel.getStyleClass().add("price-label");
        Label priceValue = new Label(String.format("%.2f TND", offre.getPrix()));
        priceValue.getStyleClass().add("price-value");
        priceBox.getChildren().addAll(priceLabel, priceValue);

        // Places
        VBox placesBox = new VBox();
        placesBox.getStyleClass().add("places-container");
        Label placesLabel = new Label("Places:");
        placesLabel.getStyleClass().add("places-label");
        Label placesValue = new Label(offre.getPlacesDisponibles() + "/" + offre.getNombrePlaces() + " places");
        placesValue.getStyleClass().add("places-value");
        placesBox.getChildren().addAll(placesLabel, placesValue);

        // Dates
        VBox datesBox = new VBox();
        datesBox.getStyleClass().add("dates-container");
        Label datesLabel = new Label("Période:");
        datesLabel.getStyleClass().add("dates-label");

        // Format dates if they exist
        String dateRange = "";
        if (offre.getDateDebut() != null && offre.getDateFin() != null) {
            dateRange = offre.getDateDebut().format(dateFormatter) + " - " + offre.getDateFin().format(dateFormatter);
        } else {
            dateRange = "Dates non définies";
        }

        Label datesValue = new Label(dateRange);
        datesValue.getStyleClass().add("dates-value");
        datesBox.getChildren().addAll(datesLabel, datesValue);

        infoRow.getChildren().addAll(priceBox, placesBox, datesBox);

        // Add all to content box
        contentBox.getChildren().addAll(titleRow, descriptionText, infoRow);

        // Right side: Actions
        VBox actionsBox = new VBox();
        actionsBox.setSpacing(10);
        actionsBox.setAlignment(Pos.CENTER);
        actionsBox.getStyleClass().add("card-actions");

        // Edit button
        Button editButton = new Button("Modifier");
        editButton.getStyleClass().addAll("button-action", "button-edit");

        try {
            ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/edit.png")));
            editIcon.setFitHeight(12);
            editIcon.setFitWidth(12);
            editButton.setGraphic(editIcon);
        } catch (Exception e) {
            // If icon can't be loaded, just leave button without graphic
        }

        editButton.setOnAction(event -> handleEditOffre(offre));

        // Delete button
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().addAll("button-action", "button-delete");

        try {
            ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/delete.png")));
            deleteIcon.setFitHeight(12);
            deleteIcon.setFitWidth(12);
            deleteButton.setGraphic(deleteIcon);
        } catch (Exception e) {
            // If icon can't be loaded, just leave button without graphic
        }

        deleteButton.setOnAction(event -> handleDeleteOffre(offre));

        actionsBox.getChildren().addAll(editButton, deleteButton);

        // Add everything to the card
        card.getChildren().addAll(imageContainer, contentBox, actionsBox);

        return card;
    }

    @FXML
    private void openAddOffreForm() {
        if (backOfficeController != null) {
            try {
                // Load AddOffre.fxml into the contentPane
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/AddOffre.fxml"));
                backOfficeController.loadContent(loader); // Use a custom method to load content
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "BackOfficeController n'est pas défini.");
        }
    }

    private void handleEditOffre(offre offre) {
        if (backOfficeController != null) {
            try {
                // Load AddOffre.fxml into the contentPane
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/AddOffre.fxml"));
                backOfficeController.loadContent(loader); // Load content first
                AddOffreController controller = loader.getController();
                controller.initData(offre); // Pass offer data for editing
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "BackOfficeController n'est pas défini.");
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
                loadOffres(); // Reload the cards
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre supprimée avec succès.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer l'offre : " + e.getMessage());
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