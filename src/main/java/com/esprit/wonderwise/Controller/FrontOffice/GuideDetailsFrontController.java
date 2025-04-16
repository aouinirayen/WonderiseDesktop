package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.Evenement;
import com.esprit.wonderwise.Model.Guide;
import com.esprit.wonderwise.Service.EvenementService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class GuideDetailsFrontController {

    @FXML private Label nomLabel, emailLabel, numTelephoneLabel, nombreAvisLabel;
    @FXML private Text descriptionLabel;
    @FXML private ImageView photoView;
    @FXML private FlowPane evenementsFlowPane;
    Guide selectedGuide;
    private EvenementService evenementService = new EvenementService();
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    public void setDetailsData(Guide guide) {
        selectedGuide = guide;
        nomLabel.setText(guide.getNom() + " " + guide.getPrenom());
        emailLabel.setText(guide.getEmail());
        numTelephoneLabel.setText(guide.getNumTelephone());
        descriptionLabel.setText(guide.getDescription());
        nombreAvisLabel.setText(String.valueOf(guide.getNombreAvis()));

        File imageFile = new File(IMAGE_DESTINATION_DIR + guide.getPhoto());
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString(), 100, 100, true, true);
            photoView.setImage(image);
        } else {
            Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 100, 100, true, true);
            photoView.setImage(fallback);
        }

        loadEvenements();
    }

    private void loadEvenements() {
        evenementsFlowPane.getChildren().clear();
        List<Evenement> evenements = evenementService.readByGuideId(selectedGuide.getId());
        for (Evenement evenement : evenements) {
            VBox eventCard = new VBox(10);
            eventCard.setPrefWidth(200);
            eventCard.setAlignment(Pos.CENTER);
            eventCard.setStyle("-fx-cursor: hand;");

            ImageView eventImage = new ImageView();
            File eventImageFile = new File(IMAGE_DESTINATION_DIR + evenement.getPhoto());
            if (eventImageFile.exists()) {
                Image image = new Image(eventImageFile.toURI().toString(), 150, 100, true, true);
                eventImage.setImage(image);
            } else {
                Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 150, 100, true, true);
                eventImage.setImage(fallback);
            }
            eventImage.getStyleClass().add("rounded-image");

            Label eventName = new Label(evenement.getNom());
            eventName.setStyle("-fx-font-size: 14px; -fx-text-fill: #2C3E50; -fx-font-weight: bold;");

            Label eventDate = new Label(evenement.getDate().toString() + " " + evenement.getHeure().toString());
            eventDate.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");

            Label eventLieu = new Label(evenement.getLieu());
            eventLieu.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");

            eventCard.getChildren().addAll(eventImage, eventName, eventDate, eventLieu);
            eventCard.setOnMouseClicked(e -> {
                try {
                    showEvenementDetails(evenement);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            evenementsFlowPane.getChildren().add(eventCard);
        }
    }

    private void showEvenementDetails(Evenement evenement) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Evenement/EvenementDetailsFront.fxml"));
        Parent root = loader.load();
        EvenementDetailsFrontController controller = loader.getController();
        controller.setDetailsData(evenement);
        root.setUserData(this); // Store this controller instance
        Stage stage = (Stage) nomLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    public void goBack() throws IOException {
        // Navigate back to the previous view (e.g., a list of guides)
        Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Guide/GuideListFront.fxml"));
        Stage stage = (Stage) nomLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    public void openFacebook() {
        if (selectedGuide != null && selectedGuide.getFacebook() != null && !selectedGuide.getFacebook().isEmpty()) {
            try {
                java.awt.Desktop.getDesktop().browse(new URI(selectedGuide.getFacebook()));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void openInstagram() {
        if (selectedGuide != null && selectedGuide.getInstagram() != null && !selectedGuide.getInstagram().isEmpty()) {
            try {
                java.awt.Desktop.getDesktop().browse(new URI(selectedGuide.getInstagram()));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}