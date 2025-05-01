package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.offre;
import com.esprit.wonderwise.Service.OffreService;
import com.esprit.wonderwise.Service.WeatherService;
import com.esprit.wonderwise.Service.WeatherService.WeatherInfo;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

    @FXML
    private TextField searchField;

    private OffreService offreService = new OffreService();
    private WeatherService weatherService = new WeatherService();
    private List<offre> allOffres = new java.util.ArrayList<>();

    @FXML
    public void initialize() {
        emptyState.setVisible(false);
        offersPane.getChildren().clear();

        // Configure search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterOffers(newValue);
        });

        // Load offers asynchronously
        CompletableFuture.runAsync(this::loadOffersAsync);
    }

    private void filterOffers(String searchText) {
        if (allOffres == null) return;

        if (searchText == null || searchText.trim().isEmpty()) {
            displayOffers(allOffres);
            return;
        }

        String searchLower = searchText.toLowerCase();
        List<offre> filteredOffers = allOffres.stream()
                .filter(offer -> {
                    String title = offer.getTitre().toLowerCase();
                    String desc = offer.getDescription().toLowerCase();
                    return title.contains(searchLower) || desc.contains(searchLower);
                })
                .collect(Collectors.toList());

        displayOffers(filteredOffers);
        
        // Show/hide empty state
        emptyState.setVisible(filteredOffers.isEmpty());
        emptyState.setManaged(filteredOffers.isEmpty());
    }

    private void loadOffersAsync() {
        try {
            Platform.runLater(() -> {
                loadingPane.setVisible(true);
                emptyState.setVisible(false);
            });

            allOffres = offreService.readAll();

            Platform.runLater(() -> {
                loadingPane.setVisible(false);
                if (allOffres.isEmpty()) {
                    emptyState.setVisible(true);
                    emptyState.setManaged(true);
                } else {
                    displayOffers(allOffres);
                }
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                loadingPane.setVisible(false);
                emptyState.setVisible(true);
                emptyState.setManaged(true);
                System.err.println("Erreur lors du chargement des offres: " + e.getMessage());
            });
        }
    }

    private void loadOffers() {
        try {
            allOffres = offreService.readAll();
            displayOffers(allOffres);
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

    private void displayOffers(List<offre> offers) {
        offersPane.getChildren().clear();

        // Animation delay between cards for staggered effect
        int delayIndex = 0;

        for (offre offre : offers) {
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

        // Image container with weather overlay
        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("image-container");

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

        // Weather overlay
        VBox weatherOverlay = new VBox(5);
        weatherOverlay.getStyleClass().add("weather-card");
        weatherOverlay.setMaxWidth(100);
        weatherOverlay.setMaxHeight(100);
        StackPane.setAlignment(weatherOverlay, javafx.geometry.Pos.TOP_RIGHT);
        StackPane.setMargin(weatherOverlay, new javafx.geometry.Insets(10, 10, 0, 0));

        String title = offre.getTitre();
        String city = extractCityFromTitle(title);

        if (city != null) {
            CompletableFuture.supplyAsync(() -> weatherService.getWeatherForCity(city))
                    .thenAcceptAsync(weather -> {
                        if (weather != null) {
                            Platform.runLater(() -> {
                                try {
                                    ImageView weatherIcon = new ImageView(new Image(weather.getIconUrl()));
                                    weatherIcon.setFitWidth(32);
                                    weatherIcon.setFitHeight(32);
                                    weatherIcon.getStyleClass().add("weather-icon");

                                    Label tempLabel = new Label(String.format("%.1f°C", weather.getTemperature()));
                                    tempLabel.getStyleClass().add("weather-temp");

                                    VBox weatherDetails = new VBox(2);
                                    weatherDetails.setAlignment(javafx.geometry.Pos.CENTER);

                                    Label descLabel = new Label(weather.getDescription());
                                    descLabel.getStyleClass().add("weather-desc");
                                    descLabel.setWrapText(true);

                                    Label humidityLabel = new Label(String.format("Humidité: %.0f%%", weather.getHumidity()));
                                    humidityLabel.getStyleClass().add("weather-detail");

                                    Label windLabel = new Label(String.format("Vent: %.1f km/h", weather.getWindSpeed() * 3.6));
                                    windLabel.getStyleClass().add("weather-detail");

                                    weatherDetails.getChildren().addAll(descLabel, humidityLabel, windLabel);

                                    weatherOverlay.getChildren().clear();
                                    weatherOverlay.getChildren().addAll(weatherIcon, tempLabel, weatherDetails);
                                    weatherOverlay.setAlignment(javafx.geometry.Pos.CENTER);

                                    // Simple fade animation
                                    FadeTransition fadeIn = new FadeTransition(Duration.millis(500), weatherOverlay);
                                    fadeIn.setFromValue(0);
                                    fadeIn.setToValue(1);
                                    fadeIn.play();
                                } catch (Exception e) {
                                    System.err.println("Error updating weather UI for " + city + ": " + e.getMessage());
                                }
                            });
                        }
                    });
        }

        imageContainer.getChildren().addAll(imageView, weatherOverlay);

        // Contenu
        VBox content = new VBox(8);
        content.setAlignment(javafx.geometry.Pos.CENTER);

        Label titleLabel = new Label(offre.getTitre());
        titleLabel.getStyleClass().add("card-title");

        Label priceLabel = new Label(String.format("%.2f TND", offre.getPrix()));
        priceLabel.getStyleClass().add("card-price");

        // Système de notation
        HBox ratingContainer = new HBox(5);
        ratingContainer.getStyleClass().add("rating-container");
        ratingContainer.setAlignment(javafx.geometry.Pos.CENTER);

        // Création du label de rating
        double currentRating = offre.getRating() != null ? offre.getRating() : 0.0;
        int voteCount = offre.getRatingCount() != null ? offre.getRatingCount() : 0;
        Label ratingLabel = new Label(String.format("%.1f/5 (%d votes)", currentRating, voteCount));
        ratingLabel.getStyleClass().add("rating-label");

        // Création des étoiles
        HBox stars = new HBox(2);
        stars.getStyleClass().add("rating-stars");

        for (int i = 1; i <= 5; i++) {
            final int starValue = i;
            SVGPath star = new SVGPath();
            star.setContent("M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z");
            star.getStyleClass().add("star");

            // Colorer l'étoile en fonction du rating actuel
            if (i <= currentRating) {
                star.getStyleClass().add("filled");
            }

            star.setOnMouseClicked(event -> {
                offre.setRating((double) starValue);
                if (offre.getRatingCount() == null) {
                    offre.setRatingCount(1);
                } else {
                    offre.setRatingCount(offre.getRatingCount() + 1);
                }

                updateStarRatings(stars, starValue);
                ratingLabel.setText(String.format("%.1f/5 (%d votes)",
                        offre.getRating(),
                        offre.getRatingCount()));

                try {
                    offreService.updateRating(offre.getId(), offre.getRating(), offre.getRatingCount());
                } catch (SQLException e) {
                    showAlert("Erreur", "Impossible de sauvegarder la note : " + e.getMessage());
                }
            });

            stars.getChildren().add(star);
        }

        ratingContainer.getChildren().addAll(stars, ratingLabel);

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

        Button detailsButton = new Button("Détails");
        detailsButton.getStyleClass().addAll("button", "details-button");

        Button reserveButton = new Button("Réserver");
        reserveButton.getStyleClass().addAll("button", "reserve-button");

        detailsButton.setOnAction(e -> showOfferDetails(offre));

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

                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), offersPane);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(event -> {
                    offersPane.setVisible(false);
                    detailsScrollPane.setVisible(true);
                    detailsScrollPane.setContent(reservationForm);

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

        // Ajout de tous les éléments dans l'ordre correct
        content.getChildren().addAll(
                titleLabel,
                priceLabel,
                ratingContainer,
                dateContainer,
                placesLabel,
                buttonContainer
        );

        // Modification de l'assemblage final
        card.getChildren().addAll(imageContainer, content);
        return card;
    }

    private void updateStarRatings(HBox stars, int rating) {
        for (int i = 0; i < stars.getChildren().size(); i++) {
            SVGPath star = (SVGPath) stars.getChildren().get(i);
            if (i < rating) {
                star.getStyleClass().remove("empty");
                star.getStyleClass().add("filled");
            } else {
                star.getStyleClass().remove("filled");
                star.getStyleClass().add("empty");
            }
        }
    }

    private void showOfferDetails(offre offre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/OfferDetails.fxml"));
            Parent detailsView = loader.load();
            OfferDetailsController controller = loader.getController();
            controller.initData(offre);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), offersPane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                offersPane.setVisible(false);
                detailsScrollPane.setVisible(true);
                detailsScrollPane.setContent(detailsView);

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

    private String extractCityFromTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return null;
        }

        // Map of common city names and their API-friendly versions
        Map<String, String> cityMap = Map.of(
                "dubai", "Dubai",
                "londres", "London",
                "rome", "Rome",
                "paris", "Paris",
                "tokyo", "Tokyo",
                "new york", "New York",
                "istanbul", "Istanbul",
                "barcelone", "Barcelona",
                "madrid", "Madrid",
                "berlin", "Berlin"
        );

        // Convert title to lowercase for matching
        String lowerTitle = title.toLowerCase();

        // Check for exact matches in our map
        for (Map.Entry<String, String> entry : cityMap.entrySet()) {
            if (lowerTitle.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Try to extract city name from patterns like "Séjour à [City]" or "Trip to [City]"
        String[] patterns = {" à ", " in ", " to "};
        for (String pattern : patterns) {
            if (title.contains(pattern)) {
                String[] parts = title.split(pattern);
                if (parts.length > 1) {
                    String potentialCity = parts[1].split("[^a-zA-Z]")[0].trim();
                    if (!potentialCity.isEmpty()) {
                        return potentialCity;
                    }
                }
            }
        }

        return null;
    }
}