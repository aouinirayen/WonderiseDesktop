package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.TraditionalFood;
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
import java.awt.image.BufferedImage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import javafx.embed.swing.SwingFXUtils;

public class TraditionalFoodFrontController {
    @FXML private ImageView detailImageView;
    @FXML private ImageView qrCodeImageView;
    @FXML private Label nameLabel;
    @FXML private Text descLabel, recipeLabel;
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    public void setDetailsData(TraditionalFood food) {
        nameLabel.setText(food.getName());
        descLabel.setText(food.getDescription());
        recipeLabel.setText("Recipe: " + food.getRecipe());

        File imageFile = new File(IMAGE_DESTINATION_DIR + food.getImg());
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString(), 450, 350, true, true);
            detailImageView.setImage(image);
        } else {
            Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 450, 350, true, true);
            detailImageView.setImage(fallback);
        }

        // Generate QR code for Google search
        String searchQuery = food.getName() + " recette";
        String url = "https://www.google.com/search?q=" + URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
        int width = 120;
        int height = 120;
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
            qrCodeImageView.setImage(fxImage);
        } catch (Exception e) {
            e.printStackTrace();
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