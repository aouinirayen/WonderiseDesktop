package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.Evenement;
import com.esprit.wonderwise.Service.EvenementService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class EvenementFrontController {

    @FXML private FlowPane evenementCards;
    private EvenementService evenementService = new EvenementService();
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    @FXML
    public void initialize() {
        loadEvenements();
    }

    private void loadEvenements() {
        evenementCards.getChildren().clear();
        List<Evenement> evenements = evenementService.readAll();
        for (Evenement evenement : evenements) {
            // Create the card (VBox)
            VBox card = new VBox(10);
            card.setPrefWidth(250);
            card.setAlignment(Pos.TOP_CENTER);
            card.setPadding(new Insets(15));
            card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5); -fx-cursor: hand;");

            // Rectangular Image - Full width with rounded corners
            ImageView imageView = new ImageView();
            File imageFile = new File(IMAGE_DESTINATION_DIR + evenement.getPhoto());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString(), 220, 150, true, true); // Adjusted width to account for padding
                imageView.setImage(image);
            } else {
                Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 220, 150, true, true);
                imageView.setImage(fallback);
            }
            imageView.setFitWidth(220); // Full width minus padding
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(false);
            // Add rounded corners to match card
            Rectangle clip = new Rectangle(220, 150);
            clip.setArcWidth(15);
            clip.setArcHeight(15);
            imageView.setClip(clip);

            // Name Label
            Label name = new Label(evenement.getNom());
            name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-alignment: center;");

            // Date HBox with Icon
            HBox dateBox = new HBox(5);
            dateBox.setAlignment(Pos.CENTER_LEFT);
            ImageView dateIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/calendar.png"), 16, 16, true, true));
            Label date = new Label(evenement.getDate().toString());
            date.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
            dateBox.getChildren().addAll(dateIcon, date);

            // Time HBox with Icon
            HBox timeBox = new HBox(5);
            timeBox.setAlignment(Pos.CENTER_LEFT);
            ImageView timeIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/clock.png"), 16, 16, true, true));
            Label time = new Label(evenement.getHeure().toString());
            time.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
            timeBox.getChildren().addAll(timeIcon, time);

            // Location HBox with Icon
            HBox locationBox = new HBox(5);
            locationBox.setAlignment(Pos.CENTER_LEFT);
            ImageView locationIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/gps.png"), 16, 16, true, true));
            Label location = new Label(evenement.getLieu());
            location.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
            locationBox.getChildren().addAll(locationIcon, location);

            // Category and Country HBox
            HBox categoryCountryBox = new HBox(5);
            categoryCountryBox.setAlignment(Pos.CENTER_LEFT);
            Label category = new Label(evenement.getCategorie());
            category.setStyle("-fx-font-size: 12px; -fx-text-fill: #155724; -fx-background-color: #D4EDDA; -fx-padding: 2 5; -fx-background-radius: 5; -fx-cursor: hand;");
            Label country = new Label(evenement.getPays());
            country.setStyle("-fx-font-size: 12px; -fx-text-fill: #3498DB; -fx-background-color: #E6F0FA; -fx-padding: 2 5; -fx-background-radius: 5; -fx-cursor: hand;");
            categoryCountryBox.getChildren().addAll(category, country);

            // Price
            Label price = new Label(evenement.getPrix() + "€");
            price.setStyle("-fx-font-size: 12px; -fx-text-fill: #2C3E50; -fx-font-weight: bold;");

            // Available Places
            Label places = new Label("Places disponibles: " + evenement.getPlaceMax());
            places.setStyle("-fx-font-size: 12px; -fx-text-fill: #2C3E50;");

            // Status Label (bottom right)
            Label status = new Label("Actif");
            status.setStyle("-fx-font-size: 12px; -fx-text-fill: #28A745;");

            // Button HBox
            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER);

            Button detailsBtn = new Button("Voir plus");
            detailsBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-pref-width: 200;");
            detailsBtn.setOnAction(e -> {
                try {
                    showDetails(evenement);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            detailsBtn.setOnMouseEntered(e -> detailsBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-pref-width: 200; -fx-opacity: 0.8;"));
            detailsBtn.setOnMouseExited(e -> detailsBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-pref-width: 200;"));

            buttons.getChildren().add(detailsBtn);

            // Add all elements to the card
            card.getChildren().addAll(imageView, name, dateBox, timeBox, locationBox, categoryCountryBox, price, places, status, buttons);
            card.setOnMouseClicked(e -> {
                try {
                    showDetails(evenement);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            evenementCards.getChildren().add(card);
        }
    }

    private void showDetails(Evenement evenement) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Evenement/EvenementDetailsFront.fxml"));
        Parent root = loader.load();
        EvenementDetailsFrontController controller = loader.getController();
        controller.setDetailsData(evenement);
        Stage stage = (Stage) evenementCards.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}