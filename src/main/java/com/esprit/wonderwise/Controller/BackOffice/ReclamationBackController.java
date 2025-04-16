package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.Reclamation;
import com.esprit.wonderwise.Service.ReclamationService;
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class ReclamationBackController {

    @FXML
    private VBox reclamationContainer;

    @FXML
    private Button viewResponsesButton;
    private final ReclamationService reclamationService = new ReclamationService();
    @FXML
    private Label pageInfoLabel;

    private int currentPage = 1;
    private static final int ITEMS_PER_PAGE = 3;

    @FXML
    public void initialize() {
        loadPage(currentPage);
    }

    private void loadPage(int page) {
        reclamationContainer.getChildren().clear();
        List<Reclamation> reclamations = reclamationService.findPage(page, ITEMS_PER_PAGE);

        for (Reclamation r : reclamations) {
            reclamationContainer.getChildren().add(createReclamationCard(r));
        }

        int totalPages = reclamationService.getTotalPages(ITEMS_PER_PAGE);
        pageInfoLabel.setText("Page " + page + "/" + totalPages);
    }


    private Node createReclamationCard(Reclamation r) {
        VBox card = new VBox();
        card.setSpacing(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: #BDC3C7; -fx-border-radius: 10; -fx-background-color: #F9F9F9; -fx-background-radius: 10;");

        // Animation d'opacité pour une apparence dynamique
        FadeTransition fade = new FadeTransition(Duration.millis(500), card);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setCycleCount(1);
        fade.setAutoReverse(false);
        fade.play();

        Label objet = new Label("Objet: " + r.getObjet());
        objet.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2C3E50; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 2, 0.2, 0, 2);");

        Label desc = new Label("Description: " + r.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");


        Label date = new Label("Date: " + r.getDate().toString());
        date.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495E;");

        Label status = new Label("Statut: " + r.getStatus());
        status.setStyle("-fx-font-size: 10px; -fx-text-fill: #e80f0f;");


        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.setStyle("-fx-alignment: center;");

        Button deleteButton = createStyledButton("delete-icon.png", "Supprimer", "#e74c3c");
        Button responseButton = createStyledButton("response-icon.png", "Répondre", "#2ecc71");

        deleteButton.setOnAction(event -> {
            Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation de suppression");
            confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette réclamation ?");
            confirmationAlert.setContentText("Cette action est irréversible.");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response.getText().equals("OK")) {
                    reclamationService.delete(r);
                    reclamationContainer.getChildren().remove(card);

                    Stage stage = (Stage) reclamationContainer.getScene().getWindow();

                    DialogUtils.showCustomDialog(
                            "Succès",
                            "Réclamation supprimée avec succès.",
                            true,
                            stage
                    );

                }
            });
        });


        responseButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Reponse.fxml"));
                Parent root = loader.load();
                ReponseController reponseController = loader.getController();
                reponseController.setReclamationId(r.getId());

                Scene scene = new Scene(root);
                Stage stage = (Stage) responseButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        buttonBox.getChildren().addAll(deleteButton, responseButton);

        buttonBox.setStyle("-fx-alignment: center;");

        card.getChildren().addAll(objet, desc, date, status, buttonBox);

        return card;
    }

    private Button createStyledButton(String iconPath, String text, String backgroundColor) {
        Button button = new Button(text);
        Image icon = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/" + iconPath));
        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);

        button.setGraphic(imageView);
        button.setStyle(
                "-fx-background-color: " + backgroundColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 6 15;" +
                        "-fx-graphic-text-gap: 8;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: derive(" + backgroundColor + ", 20%);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 6 15;" +
                        "-fx-graphic-text-gap: 8;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + backgroundColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 6 15;" +
                        "-fx-graphic-text-gap: 8;"
        ));

        return button;
    }



    public void handleViewResponses(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/ReponseListe.fxml"));
            Parent reclamationRoot = loader.load();

            Stage currentStage = (Stage) viewResponsesButton.getScene().getWindow();
            currentStage.setScene(new Scene(reclamationRoot));
            currentStage.setTitle("Reponses");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page de réclamation !");
        }

    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handlePreviousPage(ActionEvent actionEvent) {
        if (currentPage > 1) {
            currentPage--;
            loadPage(currentPage);
        }
    }

    public void handleNextPage(ActionEvent actionEvent) {
        int totalPages = reclamationService.getTotalPages(ITEMS_PER_PAGE);
        if (currentPage < totalPages) {
            currentPage++;
            loadPage(currentPage);
        }
    }

    public void handleSearch(ActionEvent actionEvent) {

    }



}
