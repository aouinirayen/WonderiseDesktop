package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.offre;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.format.DateTimeFormatter;

public class OfferDetailsController {

    @FXML
    private Label titleLabel;

    @FXML
    private ImageView imageView;

    @FXML
    private Label priceLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label placesLabel;

    @FXML
    private Text descriptionText;

    @FXML
    private Button bookButton;

    private offre offre;

    public void initData(offre offre) {
        this.offre = offre;
        titleLabel.setText(offre.getTitre());
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
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
            imageView.setImage(new Image(getClass().getResource("/images/offres/placeholder.png").toExternalForm()));
        }
        priceLabel.setText(String.format("%.2f TND", offre.getPrix()));
        dateLabel.setText(String.format("%s - %s",
                offre.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                offre.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        placesLabel.setText("Places disponibles : " + offre.getPlacesDisponibles());
        descriptionText.setText(offre.getDescription());
    }

    @FXML
    private void openReservationForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/AddReservationFront.fxml"));
            Scene scene = new Scene(loader.load(), 600, 500);
            AddReservationFrontController controller = loader.getController();
            controller.initData(offre.getId());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Réserver : " + offre.getTitre());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            // Fermer la fenêtre des détails après réservation
            Stage currentStage = (Stage) bookButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de réservation : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}