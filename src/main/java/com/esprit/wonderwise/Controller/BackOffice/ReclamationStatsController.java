package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Service.ReclamationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ReclamationStatsController {

    @FXML
    private BarChart<String, Number> barChart;

    private final ReclamationService reclamationService = new ReclamationService();
    private Map<String, Integer> currentStats;

    @FXML
    public void initialize() {
        loadChartData();
    }

    private void loadChartData() {
        barChart.getData().clear();
        currentStats = reclamationService.getReclamationsFrequentes();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : currentStats.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);
    }

    @FXML
    public void handleDeleteMostFrequent() {
        if (currentStats == null || currentStats.isEmpty()) {
            showAlert("Aucune donnée", "Il n'y a aucune réclamation à supprimer.");
            return;
        }

        // Trouver la réclamation la plus fréquente
        Map.Entry<String, Integer> mostFrequent = currentStats.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (mostFrequent != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Supprimer la réclamation la plus fréquente");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer toutes les réclamations de type: "
                    + mostFrequent.getKey() + " (" + mostFrequent.getValue() + " occurrences) ?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Supprimer les réclamations
                reclamationService.deleteReclamationsByType(mostFrequent.getKey());

                // Recharger les données
                loadChartData();

                showAlert("Succès", "Les réclamations de type " + mostFrequent.getKey()
                        + " ont été supprimées avec succès.");
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

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/ReclamationBack.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) barChart.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Réclamations");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}