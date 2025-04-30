package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.offre;
import com.esprit.wonderwise.Service.OffreService;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PackagesController {

    @FXML
    private FlowPane offersPane;

    @FXML
    private VBox detailsPane;
    
    @FXML
    private VBox emptyState;
    
    @FXML
    private StackPane loadingPane;

    @FXML
    private ScrollPane detailsScrollPane;

    @FXML
    private OfferDetailsController offerDetailsController;

    private OffreService offreService = new OffreService();
    private List<offre> allOffres;

    @FXML
    public void initialize() {
        // Start with the loading indicator visible
        loadingPane.setVisible(true);
        emptyState.setVisible(false);
        offersPane.getChildren().clear();
        
        // Load offers asynchronously
        CompletableFuture.runAsync(this::loadOffersAsync);
    }
    
    private void loadOffersAsync() {
        try {
            // Add a small delay for visual feedback
            Thread.sleep(800);
            
            // Load the offers
            allOffres = offreService.readAll();
            
            // Update UI on JavaFX thread
            Platform.runLater(() -> {
                loadingPane.setVisible(false);
                
                if (allOffres.isEmpty()) {
                    emptyState.setVisible(true);
                    emptyState.setManaged(true);
                } else {
                    displayOffers();
                }
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                loadingPane.setVisible(false);
                showAlert("Erreur", "Impossible de charger les offres : " + e.getMessage());
            });
        }
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
        
        // Animation delay between cards for staggered effect
        int delayIndex = 0;
        
        for (offre offre : allOffres) {
            VBox card = createOfferCard(offre);
            
            // Add staggered animation
            int delay = delayIndex * 100; // 100ms delay between each card
            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), card);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setDelay(Duration.millis(delay));
            
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), card);
            slideIn.setFromY(20);
            slideIn.setToY(0);
            slideIn.setDelay(Duration.millis(delay));
            
            // Add card to container
            offersPane.getChildren().add(card);
            
            // Start animations
            fadeIn.play();
            slideIn.play();
            
            delayIndex++;
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
        imageView.setFitWidth(280);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("card-image-view");

        // Contenu
        VBox content = new VBox(8);
        content.setAlignment(javafx.geometry.Pos.CENTER);

        Label titleLabel = new Label(offre.getTitre());
        titleLabel.getStyleClass().add("card-title");

        Label priceLabel = new Label(String.format("%.2f TND", offre.getPrix()));
        priceLabel.getStyleClass().add("card-price");

        VBox dateContainer = new VBox(5);
        dateContainer.getStyleClass().add("date-container");

        javafx.scene.layout.HBox startDateBox = new javafx.scene.layout.HBox(5);
        Label startDateLabel = new Label("Du:");
        startDateLabel.getStyleClass().add("date-label");
        Label startDate = new Label(offre.getDateDebut().toLocalDate().toString());
        startDate.getStyleClass().add("date-info");
        startDateBox.getChildren().addAll(startDateLabel, startDate);

        javafx.scene.layout.HBox endDateBox = new javafx.scene.layout.HBox(5);
        Label endDateLabel = new Label("Au:");
        endDateLabel.getStyleClass().add("date-label");
        Label endDate = new Label(offre.getDateFin().toLocalDate().toString());
        endDate.getStyleClass().add("date-info");
        endDateBox.getChildren().addAll(endDateLabel, endDate);

        dateContainer.getChildren().addAll(startDateBox, endDateBox);

        // Places disponibles avec indication visuelle si peu de places
        Label placesLabel = new Label(offre.getPlacesDisponibles() + " places disponibles");
        placesLabel.getStyleClass().add("available-places");
        if (offre.getPlacesDisponibles() < 5) {
            placesLabel.getStyleClass().add("low-places");
        }

        // Conteneur pour les boutons
        HBox buttonContainer = new HBox(10);
        buttonContainer.getStyleClass().add("button-container");
        buttonContainer.setAlignment(javafx.geometry.Pos.CENTER);

        // Bouton Détails
        Button detailsButton = new Button("Détails");
        detailsButton.getStyleClass().add("details-button");
        detailsButton.setOnAction(e -> showOfferDetails(offre));

        // Bouton Réserver
        Button reserveButton = new Button("Réserver");
        reserveButton.getStyleClass().add("reserve-button");
        
        // Ajouter une icône de réservation
        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/calendar-check.png")));
            icon.setFitWidth(16);
            icon.setFitHeight(16);
            reserveButton.setGraphic(icon);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'icône : " + e.getMessage());
        }
        
        reserveButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/AddReservationFront.fxml"));
                Parent reservationForm = loader.load();
                AddReservationFrontController controller = loader.getController();
                controller.initData(offre.getId());
                
                // Animate transition to reservation form
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), offersPane);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(event -> {
                    offersPane.setVisible(false);
                    detailsScrollPane.setVisible(true);
                    detailsScrollPane.setContent(reservationForm);
                    
                    // Fade in the reservation form
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(300), detailsScrollPane);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                });
                fadeOut.play();
            } catch (IOException ex) {
                showAlert("Erreur", "Impossible d'ouvrir le formulaire de réservation : " + ex.getMessage());
            }
        });

        buttonContainer.getChildren().addAll(detailsButton, reserveButton);
        content.getChildren().addAll(titleLabel, priceLabel, dateContainer, placesLabel, buttonContainer);
        card.getChildren().addAll(imageView, content);

        return card;
    }

    private void showOfferDetails(offre offre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/OfferDetails.fxml"));
            Parent detailsView = loader.load();
            OfferDetailsController controller = loader.getController();
            controller.initData(offre);
            
            // Animate transition to details
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), offersPane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                offersPane.setVisible(false);
                detailsScrollPane.setVisible(true);
                detailsScrollPane.setContent(detailsView);
                
                // Fade in the details
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), detailsScrollPane);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'afficher les détails de l'offre : " + e.getMessage());
        }
    }

    @FXML
    private void hideDetails() {
        // Animate transition back to offers list
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), detailsScrollPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            detailsScrollPane.setVisible(false);
            offersPane.setVisible(true);
            
            // Fade in the offers list
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), offersPane);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }
    
    // Refresh the offers list - can be called from other controllers
    public void refreshOffers() {
        loadingPane.setVisible(true);
        emptyState.setVisible(false);
        CompletableFuture.runAsync(this::loadOffersAsync);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}