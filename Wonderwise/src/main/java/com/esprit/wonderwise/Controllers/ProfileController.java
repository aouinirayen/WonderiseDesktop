package com.esprit.wonderwise.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDate;
import javafx.collections.FXCollections;

public class ProfileController implements Initializable {
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField phoneField;
    @FXML private TextArea addressArea;
    @FXML private ComboBox<String> preferredDestinationsCombo;
    @FXML private CheckBox adventureCheck;
    @FXML private CheckBox cultureCheck;
    @FXML private CheckBox relaxCheck;
    @FXML private CheckBox gastronomyCheck;
    @FXML private Slider budgetSlider;
    @FXML private TableView<TravelHistory> travelHistoryTable;
    @FXML private TableColumn<TravelHistory, LocalDate> dateColumn;
    @FXML private TableColumn<TravelHistory, String> destinationColumn;
    @FXML private TableColumn<TravelHistory, String> typeColumn;
    @FXML private TableColumn<TravelHistory, String> statusColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser les destinations
        preferredDestinationsCombo.setItems(FXCollections.observableArrayList(
            "Paris, France",
            "Rome, Italie",
            "Tokyo, Japon",
            "New York, USA",
            "Dubai, UAE",
            "Londres, UK"
        ));

        // Configuration du slider de budget
        budgetSlider.setShowTickLabels(true);
        budgetSlider.setShowTickMarks(true);
        budgetSlider.setMajorTickUnit(2000);
        budgetSlider.setBlockIncrement(500);

        // Charger les données de l'utilisateur
        loadUserData();
    }

    private void loadUserData() {
        // TODO: Charger les données depuis la base de données
        userNameLabel.setText("John Doe");
        userEmailLabel.setText("john.doe@example.com");
        firstNameField.setText("John");
        lastNameField.setText("Doe");
        birthDatePicker.setValue(LocalDate.of(1990, 1, 1));
        phoneField.setText("+1234567890");
        addressArea.setText("123 Main Street\nNew York, NY 10001");

        // Préférences
        preferredDestinationsCombo.setValue("Paris, France");
        adventureCheck.setSelected(true);
        cultureCheck.setSelected(true);
        budgetSlider.setValue(5000);

        // TODO: Charger l'historique des voyages
    }

    @FXML
    private void handleSave() {
        // TODO: Sauvegarder les modifications dans la base de données
        showAlert("Succès", "Les modifications ont été enregistrées avec succès.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleReset() {
        loadUserData();
        showAlert("Réinitialisation", "Les données ont été réinitialisées.", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Classe interne pour l'historique des voyages
    public static class TravelHistory {
        private LocalDate date;
        private String destination;
        private String type;
        private String status;

        public TravelHistory(LocalDate date, String destination, String type, String status) {
            this.date = date;
            this.destination = destination;
            this.type = type;
            this.status = status;
        }

        // Getters
        public LocalDate getDate() { return date; }
        public String getDestination() { return destination; }
        public String getType() { return type; }
        public String getStatus() { return status; }
    }
}
