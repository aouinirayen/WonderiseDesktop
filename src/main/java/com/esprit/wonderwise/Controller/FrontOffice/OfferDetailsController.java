package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.offre;
import com.esprit.wonderwise.Service.OffreService;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class OfferDetailsController {

    @FXML
    private Label offerTitleLabel;
    
    @FXML
    private Label paysLabel;
    
    @FXML
    private ImageView offerImageView;
    
    @FXML
    private Label ratingLabel;
    
    @FXML
    private Text descriptionText;
    
    @FXML
    private Label priceLabel;
    
    @FXML
    private Label startDateLabel;
    
    @FXML
    private Label endDateLabel;
    
    @FXML
    private Label placesLabel;
    
    @FXML
    private Button bookButton;
    
    @FXML
    private FlowPane featuresContainer;
    
    @FXML
    private VBox placesInfo;
    
    private offre currentOffer;
    private OffreService offreService = new OffreService();
    
    public void initData(offre offre) {
        this.currentOffer = offre;
        populateOfferDetails();
        
        // Add subtle animations for better user experience
        animateDetailElements();
    }
    
    private void populateOfferDetails() {
        // Set basic offer details
        offerTitleLabel.setText(currentOffer.getTitre());
        paysLabel.setText(currentOffer.getPays());
        
        // Format price with decimal format
        DecimalFormat df = new DecimalFormat("#,##0.00");
        priceLabel.setText(df.format(currentOffer.getPrix()) + " TND");
        
        // Set dates
        startDateLabel.setText(currentOffer.getDateDebut().toLocalDate().toString());
        endDateLabel.setText(currentOffer.getDateFin().toLocalDate().toString());
        
        // Set description
        descriptionText.setText(currentOffer.getDescription());
        
        // Set available places with visual indication if low
        placesLabel.setText(currentOffer.getPlacesDisponibles() + " places");
        
        // Style places info based on availability
        if (currentOffer.getPlacesDisponibles() < 5) {
            placesInfo.getStyleClass().add("places-low");
        } else {
            placesInfo.getStyleClass().removeAll("places-low");
        }
        
        // Set rating with 1 decimal place
        if (currentOffer.getRating() != null && currentOffer.getRating() > 0) {
            DecimalFormat ratingFormat = new DecimalFormat("0.0");
            ratingLabel.setText(ratingFormat.format(currentOffer.getRating()));
        } else {
            ratingLabel.setText("Nouveau");
        }
        
        // Load image
        loadOfferImage();
    }
    
    private void loadOfferImage() {
        try {
            String imagePath = currentOffer.getImage();
            if (imagePath != null && !imagePath.isEmpty()) {
                String fullPath = "src/main/resources" + imagePath;
                File imageFile = new File(fullPath);
                if (imageFile.exists()) {
                    offerImageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    offerImageView.setImage(new Image(getClass().getResource("/images/offres/placeholder.png").toExternalForm()));
                }
            } else {
                offerImageView.setImage(new Image(getClass().getResource("/images/offres/placeholder.png").toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
            offerImageView.setImage(new Image(getClass().getResource("/images/offres/placeholder.png").toExternalForm()));
        }
    }
    
    private void animateDetailElements() {
        // Subtle scale animation for the image
        ScaleTransition scaleImage = new ScaleTransition(Duration.millis(500), offerImageView);
        scaleImage.setFromX(0.95);
        scaleImage.setFromY(0.95);
        scaleImage.setToX(1.0);
        scaleImage.setToY(1.0);
        scaleImage.play();
        
        // Button hover animation effect
        bookButton.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), bookButton);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });
        
        bookButton.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), bookButton);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
    }
    
    @FXML
    private void handleBookNow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/AddReservationFront.fxml"));
            Parent root = loader.load();
            
            AddReservationFrontController reservationController = loader.getController();
            reservationController.initData(currentOffer.getId());
            
            Stage stage = new Stage();
            stage.setTitle("Réserver " + currentOffer.getTitre());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            
            // Add a listener to refresh the details when the reservation window is closed
            stage.setOnHidden(e -> refreshOfferDetails());
            
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de réservation: " + e.getMessage());
        }
    }
    
    private void refreshOfferDetails() {
        try {
            // Get all offers and find the current one by ID
            List<offre> allOffres = offreService.readAll();
            for (offre o : allOffres) {
                if (o.getId() == currentOffer.getId()) {
                    currentOffer = o;
                    populateOfferDetails();
                    break;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du rafraîchissement des détails de l'offre: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}