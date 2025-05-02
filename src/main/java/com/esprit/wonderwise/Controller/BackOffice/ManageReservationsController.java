package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.offre;
import com.esprit.wonderwise.Model.reservation;
import com.esprit.wonderwise.Service.OffreService;
import com.esprit.wonderwise.Service.ReservationService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ManageReservationsController {

    @FXML
    private VBox reservationsContainer;

    @FXML
    private VBox emptyState;

    @FXML
    private Label totalReservationsLabel;

    @FXML
    private Label totalPersonnesLabel;

    @FXML
    private StackPane reservationsChartContainer;

    @FXML
    private StackPane personnesChartContainer;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private DatePicker dateFilter;

    private ReservationService reservationService = new ReservationService();
    private OffreService offreService = new OffreService();
    private ObservableList<reservation> reservationsList = FXCollections.observableArrayList();
    private Map<Integer, offre> offresMap = new HashMap<>();
    private BackOfficeController backOfficeController;
    private PieChart reservationsChart;
    private PieChart personnesChart;

    private FilteredList<reservation> filteredReservations;
    private SortedList<reservation> sortedReservations;

    public void setBackOfficeController(BackOfficeController controller) {
        this.backOfficeController = controller;
    }

    @FXML
    public void initialize() {
        // Initialize pie charts
        reservationsChart = new PieChart();
        reservationsChart.setTitle("Réservations par offre");
        reservationsChart.setLabelsVisible(true);
        reservationsChart.setLegendVisible(true);
        reservationsChartContainer.getChildren().add(reservationsChart);

        personnesChart = new PieChart();
        personnesChart.setTitle("Personnes par offre");
        personnesChart.setLabelsVisible(true);
        personnesChart.setLegendVisible(true);
        personnesChartContainer.getChildren().add(personnesChart);

        // Initialize filtered and sorted lists
        filteredReservations = new FilteredList<>(reservationsList, p -> true);
        sortedReservations = new SortedList<>(filteredReservations);

        // Load all offres first
        try {
            List<offre> offres = offreService.readAll();
            offres.forEach(offre -> offresMap.put(offre.getId(), offre));

            filterComboBox.getItems().add("Toutes les offres");
            offres.forEach(offre -> filterComboBox.getItems().add(offre.getTitre()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Setup search listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredReservations.setPredicate(reservation -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                offre offreAssociee = offresMap.get(reservation.getOffreId());
                return (offreAssociee != null && offreAssociee.getTitre().toLowerCase().contains(lowerCaseFilter)) ||
                        reservation.getNom().toLowerCase().contains(lowerCaseFilter);
            });
            updateDisplay();
        });

        // Setup filter listeners
        filterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filteredReservations.setPredicate(reservation -> {
                if (newValue == null || newValue.equals("Toutes les offres")) {
                    return true;
                }
                offre offreAssociee = offresMap.get(reservation.getOffreId());
                return offreAssociee != null && offreAssociee.getTitre().equals(newValue);
            });
            updateDisplay();
        });

        dateFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredReservations.setPredicate(reservation -> {
                if (newValue == null) {
                    return true;
                }
                return reservation.getDateDepart().equals(newValue);
            });
            updateDisplay();
        });

        // Setup sort listener
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                switch (newValue) {
                    case "Date (plus récent)":
                        sortedReservations.setComparator((r1, r2) -> r2.getDateDepart().compareTo(r1.getDateDepart()));
                        break;
                    case "Date (plus ancien)":
                        sortedReservations.setComparator((r1, r2) -> r1.getDateDepart().compareTo(r2.getDateDepart()));
                        break;
                    case "Nombre de personnes":
                        sortedReservations.setComparator((r1, r2) -> Integer.compare(r2.getNombrePersonne(), r1.getNombrePersonne()));
                        break;
                    case "Offre":
                        sortedReservations.setComparator((r1, r2) -> {
                            offre o1 = offresMap.get(r1.getOffreId());
                            offre o2 = offresMap.get(r2.getOffreId());
                            if (o1 == null || o2 == null) return 0;
                            return o1.getTitre().compareTo(o2.getTitre());
                        });
                        break;
                }
                updateDisplay();
            }
        });

        // Load reservations
        loadReservations();
    }

    private void updateDisplay() {
        reservationsContainer.getChildren().clear();
        sortedReservations.forEach(reservation -> reservationsContainer.getChildren().add(createReservationCard(reservation)));
        updateStatistics();
    }

    private void updateStatistics() {
        // Mise à jour du nombre total de réservations
        totalReservationsLabel.setText(String.valueOf(sortedReservations.size()));

        // Calcul du nombre total de personnes
        int totalPersonnes = sortedReservations.stream()
                .mapToInt(reservation::getNombrePersonne)
                .sum();
        totalPersonnesLabel.setText(String.valueOf(totalPersonnes));

        // Nombre de réservations par offre
        Map<Integer, Long> reservationsParOffre = sortedReservations.stream()
                .collect(Collectors.groupingBy(
                        reservation::getOffreId,
                        Collectors.counting()
                ));

        ObservableList<PieChart.Data> reservationsData = FXCollections.observableArrayList();
        reservationsParOffre.forEach((offreId, count) -> {
            offre offre = offresMap.get(offreId);
            String offreName = offre != null ? offre.getTitre() : "Offre " + offreId;
            String label = String.format("%s (%d rés.)", offreName, count);
            PieChart.Data data = new PieChart.Data(label, count);
            reservationsData.add(data);
        });
        reservationsChart.setData(reservationsData);

        // Nombre de personnes par offre
        Map<Integer, Integer> personnesParOffre = sortedReservations.stream()
                .collect(Collectors.groupingBy(
                        reservation::getOffreId,
                        Collectors.summingInt(reservation::getNombrePersonne)
                ));

        ObservableList<PieChart.Data> personnesData = FXCollections.observableArrayList();
        personnesParOffre.forEach((offreId, nbPersonnes) -> {
            offre offre = offresMap.get(offreId);
            String offreName = offre != null ? offre.getTitre() : "Offre " + offreId;
            String label = String.format("%s (%d pers.)", offreName, nbPersonnes);
            PieChart.Data data = new PieChart.Data(label, nbPersonnes);
            personnesData.add(data);
        });
        personnesChart.setData(personnesData);

        // Appliquer les couleurs spécifiques
        String[] colors = {"#22c55e", "#f97316", "#ef4444", "#3b82f6", "#8b5cf6"};
        int colorIndex = 0;

        for (PieChart.Data data : reservationsData) {
            String color = colors[colorIndex % colors.length];
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
            colorIndex++;
        }

        colorIndex = 0;
        for (PieChart.Data data : personnesData) {
            String color = colors[colorIndex % colors.length];
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
            colorIndex++;
        }
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
                updateDisplay();

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