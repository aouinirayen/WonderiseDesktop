package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.Guide;
import com.esprit.wonderwise.Model.RatingGuide;
import com.esprit.wonderwise.Service.GuideService;
import com.esprit.wonderwise.Service.RatingGuideService;
import javafx.application.Platform;
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
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GuideFrontController {

    @FXML private FlowPane guideCards;
    private final GuideService guideService = new GuideService();
    private final RatingGuideService ratingGuideService = new RatingGuideService();
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";
    private final int currentUserId = 1; // Replace with session user if available

    @FXML
    public void initialize() {
        loadGuides();
    }

    private void loadGuides() {
        guideCards.getChildren().clear();
        List<Guide> guides = guideService.readAll();

        for (Guide guide : guides) {
            VBox card = new VBox(10);
            card.setPrefWidth(250);
            card.setAlignment(Pos.TOP_CENTER);
            card.setPadding(new Insets(15));
            card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5); -fx-cursor: hand;");

            // Image
            ImageView imageView = new ImageView();
            File imageFile = new File(IMAGE_DESTINATION_DIR + guide.getPhoto());
            Image image = imageFile.exists()
                    ? new Image(imageFile.toURI().toString(), 150, 150, true, true)
                    : new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 150, 150, true, true);
            imageView.setImage(image);
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            Circle clip = new Circle(75, 75, 75);
            imageView.setClip(clip);

            // Name
            Label name = new Label(guide.getNom() + " " + guide.getPrenom());
            name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-alignment: center;");

            // Rating using ControlsFX
            Rating ratingControl = new Rating(5);
            ratingControl.setPartialRating(false);
            double avgRating = ratingGuideService.getAverageRating(guide.getId());
            int userRating = ratingGuideService.getUserRatingForGuide(guide.getId(), currentUserId);
            ratingControl.setRating(userRating > 0 ? userRating : avgRating);
            ratingControl.setStyle("-fx-cursor: hand;");
            ratingControl.setOnMouseClicked(e -> {
                int selectedRating = (int) ratingControl.getRating();
                int existing = ratingGuideService.getUserRatingForGuide(guide.getId(), currentUserId);
                RatingGuide rating = new RatingGuide(0, guide.getId(), currentUserId, selectedRating);
                if (existing > 0) {
                    ratingGuideService.updateRating(rating);
                } else {
                    ratingGuideService.addRating(rating);
                }
                new Thread(() -> {
                    try {
                        Thread.sleep(300);
                        Platform.runLater(this::loadGuides);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            });

            // Email
            HBox emailBox = new HBox(5);
            emailBox.setAlignment(Pos.CENTER_LEFT);
            ImageView emailIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/email.png"), 16, 16, true, true));
            Label email = new Label(guide.getEmail());
            email.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
            emailBox.getChildren().addAll(emailIcon, email);

            // Phone
            HBox phoneBox = new HBox(5);
            phoneBox.setAlignment(Pos.CENTER_LEFT);
            ImageView phoneIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/phone.png"), 16, 16, true, true));
            Label phone = new Label(guide.getNumTelephone());
            phone.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
            phoneBox.getChildren().addAll(phoneIcon, phone);

            // Description
            String descriptionText = guide.getDescription();
            if (descriptionText != null && descriptionText.length() > 20) {
                descriptionText = descriptionText.substring(0, 17) + "...";
            }
            Label description = new Label(descriptionText != null ? descriptionText : "No description");
            description.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");

            // Social media
            HBox socialMediaBox = new HBox(10);
            socialMediaBox.setAlignment(Pos.CENTER);

            ImageView facebookIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/facebook.png")));
            facebookIcon.setStyle("-fx-cursor: hand;");
            facebookIcon.setFitWidth(20);
            facebookIcon.setFitHeight(20);
            facebookIcon.setOnMouseClicked(e -> {
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(guide.getFacebook()));
                    e.consume();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            ImageView instagramIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/instagram.png")));
            instagramIcon.setStyle("-fx-cursor: hand;");
            instagramIcon.setFitWidth(20);
            instagramIcon.setFitHeight(20);
            instagramIcon.setOnMouseClicked(e -> {
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(guide.getInstagram()));
                    e.consume();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            socialMediaBox.getChildren().addAll(facebookIcon, instagramIcon);

            // Buttons
            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER);

            Button detailsBtn = new Button("Voir");
            detailsBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;");
            detailsBtn.setOnAction(e -> {
                try {
                    showDetails(guide);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            Button likeBtn = new Button("J'aime 0");
            likeBtn.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;");

            Button reviewBtn = new Button("Avis " + guide.getNombreAvis());
            reviewBtn.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;");

            buttons.getChildren().addAll(detailsBtn, likeBtn, reviewBtn);

            // Final assembly
            card.getChildren().addAll(imageView, name, ratingControl, emailBox, phoneBox, description, socialMediaBox, buttons);
            guideCards.getChildren().add(card);
        }
    }

    private void showDetails(Guide guide) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Guide/GuideDetailsFront.fxml"));
        Parent root = loader.load();
        GuideDetailsFrontController controller = loader.getController();
        controller.setDetailsData(guide);
        Stage stage = (Stage) guideCards.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}