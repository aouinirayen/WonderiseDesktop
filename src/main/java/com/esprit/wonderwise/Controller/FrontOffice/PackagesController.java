package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.offre;
import com.esprit.wonderwise.Service.OffreService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PackagesController {

    @FXML
    private FlowPane offersPane;

    private OffreService offreService = new OffreService();
    private List<offre> allOffres;

    @FXML
    public void initialize() {
        loadOffers();
    }

    private void loadOffers() {
        try {
            allOffres = offreService.readAll();
            displayOffers();
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les offres : " + e.getMessage());
        }
    }

    private void displayOffers() {
        offersPane.getChildren().clear();
        for (offre offre : allOffres) {
            VBox card = createOfferCard(offre);
            offersPane.getChildren().add(card);
        }
    }

    private VBox createOfferCard(offre offre) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setAlignment(javafx.geometry.Pos.CENTER);

        // Image
        ImageView imageView = new ImageView();
        try {
            String imagePath = offre.getImage();
            if (imagePath != null && !imagePath.isEmpty()) {
                String fullPath = "src/main/resources" + imagePath;
                File imageFile = new File(fullPath);
                if (imageFile.exists()) {
                    imageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    imageView.setImage(new Image(getClass().getResource("/images/offres/placeholder.png").toExternalForm()));
                }
            } else {
                imageView.setImage(new Image(getClass().getResource("/images/offres/placeholder.png").toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image pour l'offre " + offre.getTitre() + " : " + e.getMessage());
            imageView.setImage(new Image(getClass().getResource("/images/offres/placeholder.png").toExternalForm()));
        }
        imageView.setFitWidth(260);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("card-image");

        // Contenu
        VBox content = new VBox(8);
        content.setAlignment(javafx.geometry.Pos.CENTER);

        // Titre
        Label titleLabel = new Label(offre.getTitre());
        titleLabel.getStyleClass().add("card-title");

        // Prix
        Label priceLabel = new Label(String.format("%.2f TND", offre.getPrix()));
        priceLabel.getStyleClass().add("card-price");

        // Dates Container
        VBox dateContainer = new VBox(5);
        dateContainer.getStyleClass().add("date-container");

        // Date de début
        javafx.scene.layout.HBox startDateBox = new javafx.scene.layout.HBox(5);
        Label startDateLabel = new Label("Du:");
        startDateLabel.getStyleClass().add("date-label");
        Label startDate = new Label(offre.getDateDebut().toLocalDate().toString());
        startDate.getStyleClass().add("date-info");
        startDateBox.getChildren().addAll(startDateLabel, startDate);

        // Date de fin
        javafx.scene.layout.HBox endDateBox = new javafx.scene.layout.HBox(5);
        Label endDateLabel = new Label("Au:");
        endDateLabel.getStyleClass().add("date-label");
        Label endDate = new Label(offre.getDateFin().toLocalDate().toString());
        endDate.getStyleClass().add("date-info");
        endDateBox.getChildren().addAll(endDateLabel, endDate);

        dateContainer.getChildren().addAll(startDateBox, endDateBox);

        // Places disponibles
        Label placesLabel = new Label(offre.getPlacesDisponibles() + " places disponibles");
        placesLabel.getStyleClass().add("available-places");

        // Bouton Details
        Button detailsButton = new Button("Details");
        detailsButton.getStyleClass().add("details-button");
        detailsButton.setOnAction(e -> showOfferDetails(offre));

        content.getChildren().addAll(titleLabel, priceLabel, dateContainer, placesLabel, detailsButton);
        card.getChildren().addAll(imageView, content);

        return card;
    }

    private void showOfferDetails(offre offre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/OfferDetails.fxml"));
            Scene scene = new Scene(loader.load(), 700, 500);
            OfferDetailsController controller = loader.getController();
            controller.initData(offre);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'afficher les détails de l'offre : " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}