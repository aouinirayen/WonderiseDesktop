package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Service.ReclamationService;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ReclamationStatsController {

    @FXML private BarChart<String, Number> barChart;
    @FXML private Label totalLabel, frequentLabel, updatedLabel;
    @FXML private StackPane rootPane;

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
        int total = 0;
        String frequentType = "";
        int maxCount = 0;

        for (Map.Entry<String, Integer> entry : currentStats.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            series.getData().add(data);
            total += entry.getValue();
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                frequentType = entry.getKey();
            }
        }

        barChart.getData().add(series);
        animateChart();
        updateMetrics(total, frequentType);
    }

    private void animateChart() {
        for (XYChart.Series<String, Number> series : barChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                data.getNode().setScaleY(0);
                ScaleTransition st = new ScaleTransition(Duration.millis(600), data.getNode());

                st.setToY(1);
                st.play();
            }
        }
    }

    private void updateMetrics(int total, String frequentType) {
        totalLabel.setText("Total: " + total);
        frequentLabel.setText("Fréquent: " + (frequentType.isEmpty() ? "Aucune" : frequentType));
        updatedLabel.setText("Mis à jour: Maintenant");
    }

    @FXML
    public void handleDeleteMostFrequent() {
        if (currentStats == null || currentStats.isEmpty()) {
            showSnackbar("Aucune réclamation à supprimer.");
            return;
        }

        Map.Entry<String, Integer> mostFrequent = currentStats.entrySet().stream()
                .max(Map.Entry.comparingByValue()).orElse(null);

        if (mostFrequent != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Supprimer la réclamation la plus fréquente");
            confirm.setContentText("Supprimer toutes les '" + mostFrequent.getKey() + "' (" + mostFrequent.getValue() + " fois) ?");
            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                reclamationService.deleteReclamationsByType(mostFrequent.getKey());
                loadChartData();
                showSnackbar("Réclamations supprimées.");
            }
        }
    }

    private void showSnackbar(String message) {
        Label snackbar = new Label(message);
        snackbar.getStyleClass().add("snackbar");
        rootPane.getChildren().add(snackbar);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), snackbar);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);
        fadeIn.setOnFinished(e -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(ev -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), snackbar);
                fadeOut.setFromValue(1); fadeOut.setToValue(0);
                fadeOut.setOnFinished(ev2 -> rootPane.getChildren().remove(snackbar));
                fadeOut.play();
            });
            pause.play();
        });
        fadeIn.play();
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
