package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.Reponse;
import com.esprit.wonderwise.Service.ReponseService;
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class ReponseListeController {

    @FXML
    private VBox reponsesContainer;

    private ReponseService reponseService;

    @FXML
    public void initialize() {
        reponseService = new ReponseService();

        List<Reponse> reponses = reponseService.find();
        for (Reponse r : reponses) {
            reponsesContainer.getChildren().add(createReponseCard(r));
        }
    }

    private Node createReponseCard(Reponse r) {
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

        Label objet = new Label("Reclamation: " + r.getReclamation().getObjet());
        objet.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2C3E50; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 2, 0.2, 0, 2);");

        Label desc = new Label("Reponse: " + r.getReponse());
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");

        Label date = new Label("Date: " + r.getDate().toString());
        date.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495E;");


        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.setStyle("-fx-alignment: center;");
        Button deleteButton = createStyledButton("delete-icon.png", "Supprimer", "#e74c3c");
        Button updateButton = createStyledButton("update-icon.png", "Répondre", "#2ecc71");

        deleteButton.setOnAction(event -> {
            // Demander la confirmation avant de supprimer
                    // Suppression de la réclamation
                    boolean isDeleted = reponseService.delete(r);

                    if (isDeleted) {
                        // Notification de succès
                        DialogUtils.showCustomDialog("Succès", "Réponse supprimée avec succès.", true, getCurrentStage());
                        reponsesContainer.getChildren().remove(card);
                        this.initialize();
                    } else {
                        // Notification d'échec
                        DialogUtils.showCustomDialog("Échec", "Une erreur est survenue lors de la suppression.", false, getCurrentStage());
                        this.initialize();
                    }


        });

        updateButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/ReponseEdit.fxml"));
                Parent reclamationRoot = loader.load();

                // Récupérer le contrôleur de ReponseEditController
                ReponseEditController controller = loader.getController();

                // Passer l'objet 'Reponse' au contrôleur ReponseEditController
                controller.setReponse(r);

                // Charger la nouvelle scène
                Stage currentStage = (Stage) updateButton.getScene().getWindow();
                currentStage.setScene(new Scene(reclamationRoot));
                currentStage.setTitle("Modifier la réponse");

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        buttonBox.getChildren().addAll(deleteButton, updateButton);
        buttonBox.setStyle("-fx-alignment: center;");

        card.getChildren().addAll(objet, desc, date, buttonBox);

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



    private Stage getCurrentStage() {
        return (Stage) reponsesContainer.getScene().getWindow();
    }
}
