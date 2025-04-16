package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.Guide;
import com.esprit.wonderwise.Service.GuideService;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GuideFrontController {

    @FXML private FlowPane guideCards;
    private GuideService guideService = new GuideService();
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    @FXML
    public void initialize() {
        loadGuides();
    }

    private void loadGuides() {
        guideCards.getChildren().clear();
        List<Guide> guides = guideService.readAll();
        for (Guide guide : guides) {
            // Create the card (VBox)
            VBox card = new VBox(10);
            card.setPrefWidth(250);
            card.setAlignment(Pos.TOP_CENTER);
            card.setPadding(new Insets(15));
            // Inline style for the card: white background, rounded corners, shadow
            card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5); -fx-cursor: hand;");

            // Circular Image
            ImageView imageView = new ImageView();
            File imageFile = new File(IMAGE_DESTINATION_DIR + guide.getPhoto());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString(), 150, 150, true, true);
                imageView.setImage(image);
            } else {
                Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 150, 150, true, true);
                imageView.setImage(fallback);
            }
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            // Clip the ImageView to a circle
            Circle clip = new Circle(75, 75, 75); // CenterX = 75, CenterY = 75, Radius = 75
            imageView.setClip(clip);

            // Name Label
            Label name = new Label(guide.getNom() + " " + guide.getPrenom());
            // Inline style for name label
            name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-alignment: center;");

            // Email HBox with Icon
            HBox emailBox = new HBox(5);
            emailBox.setAlignment(Pos.CENTER_LEFT);
            ImageView emailIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/email.png"), 16, 16, true, true));
            Label email = new Label(guide.getEmail());
            email.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
            emailBox.getChildren().addAll(emailIcon, email);

            // Phone HBox with Icon
            HBox phoneBox = new HBox(5);
            phoneBox.setAlignment(Pos.CENTER_LEFT);
            ImageView phoneIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/phone.png"), 16, 16, true, true));
            Label phone = new Label(guide.getNumTelephone());
            phone.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
            phoneBox.getChildren().addAll(phoneIcon, phone);

            // Description (truncated if too long)
            String descriptionText = guide.getDescription();
            if (descriptionText != null && descriptionText.length() > 20) {
                descriptionText = descriptionText.substring(0, 17) + "...";
            }
            Label description = new Label(descriptionText != null ? descriptionText : "No description");
            description.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");

            // Social Media Icons HBox
            HBox socialMediaBox = new HBox(10);
            socialMediaBox.setAlignment(Pos.CENTER);

            ImageView facebookIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/facebook.png"), 20, 20, true, true));
            facebookIcon.setStyle("-fx-cursor: hand;");
            facebookIcon.setOnMouseClicked(e -> {
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(guide.getFacebook()));
                    e.consume(); // Stop event propagation
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            ImageView instagramIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/instagram.png"), 20, 20, true, true));
            instagramIcon.setStyle("-fx-cursor: hand;");
            instagramIcon.setOnMouseClicked(e -> {
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(guide.getInstagram()));
                    e.consume(); // Stop event propagation
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            socialMediaBox.getChildren().addAll(facebookIcon, instagramIcon);

            // Buttons HBox
            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER);

            Button detailsBtn = new Button("Voir");
            // Inline style for details button
            detailsBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;");
            detailsBtn.setOnAction(e -> {
                try {
                    showDetails(guide);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            // Hover effect
            detailsBtn.setOnMouseEntered(e -> detailsBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-opacity: 0.8;"));
            detailsBtn.setOnMouseExited(e -> detailsBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;"));

            Button likeBtn = new Button("J'aime 0"); // Hardcoded to 0 since there is no likes field in the model
            // Inline style for like button
            likeBtn.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;");
            // Placeholder for like functionality
            likeBtn.setOnAction(e -> {
                // Add like functionality here if needed
            });
            // Hover effect
            likeBtn.setOnMouseEntered(e -> likeBtn.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-opacity: 0.8;"));
            likeBtn.setOnMouseExited(e -> likeBtn.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;"));

            Button reviewBtn = new Button("Avis " + guide.getNombreAvis());
            // Inline style for review button
            reviewBtn.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;");
            // Placeholder for review functionality
            reviewBtn.setOnAction(e -> {
                // Add review functionality here if needed
            });
            // Hover effect
            reviewBtn.setOnMouseEntered(e -> reviewBtn.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-opacity: 0.8;"));
            reviewBtn.setOnMouseExited(e -> reviewBtn.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;"));

            buttons.getChildren().addAll(detailsBtn, likeBtn, reviewBtn);

            // Add all elements to the card
            card.getChildren().addAll(imageView, name, emailBox, phoneBox, description, socialMediaBox, buttons);
            card.setOnMouseClicked(e -> {
                try {
                    showDetails(guide);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
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