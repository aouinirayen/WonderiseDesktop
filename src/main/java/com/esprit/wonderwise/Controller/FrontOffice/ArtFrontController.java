package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.Art;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ArtFrontController {
    @FXML private ImageView detailImageView;
    @FXML private Label nameLabel, dateLabel, typeLabel;
    @FXML private Text descLabel;
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    public void setDetailsData(Art art) {
        nameLabel.setText(art.getName());
        descLabel.setText(art.getDescription());
        dateLabel.setText(art.getDate());
        typeLabel.setText(art.getType());

        File imageFile = new File(IMAGE_DESTINATION_DIR + art.getImg());
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString(), 450, 350, true, true);
            detailImageView.setImage(image);
        } else {
            Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 450, 350, true, true);
            detailImageView.setImage(fallback);
        }
    }

    @FXML
    public void goBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Country/CountryDetailsFront.fxml"));
        Parent root = loader.load();
        CountryFrontController controller = loader.getController();
        CountryFrontController parentController = (CountryFrontController) detailImageView.getScene().getRoot().getUserData();
        controller.setDetailsData(parentController.selectedCountry); // Use stored controller instance
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}