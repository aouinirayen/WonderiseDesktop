package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.offre;
import com.esprit.wonderwise.Service.OffreService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AddOffreController {

    @FXML
    private TextField titreField;
    @FXML
    private Label titreError;
    @FXML
    private TextArea descriptionField;
    @FXML
    private Label descriptionError;
    @FXML
    private TextField prixField;
    @FXML
    private Label prixError;
    @FXML
    private TextField nombrePlacesField;
    @FXML
    private Label nombrePlacesError;
    @FXML
    private TextField placesDisponiblesField;
    @FXML
    private Label placesDisponiblesError;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private Label dateDebutError;
    @FXML
    private DatePicker dateFinPicker;
    @FXML
    private Label dateFinError;
    @FXML
    private TextField imageField;
    @FXML
    private Label imageError;
    @FXML
    private Button selectImageButton;
    @FXML
    private ImageView imagePreview;
    @FXML
    private TextField paysField;

    @FXML
    private Button addButton;

    private OffreService offreService = new OffreService();
    private offre offreToEdit = null;
    private static final String IMAGE_DIR = "src/main/resources/images/offres/";

    @FXML
    public void initialize() {
        // Create image directory if it doesn't exist
        File dir = new File(IMAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void initData(offre offre) {
        this.offreToEdit = offre;
        titreField.setText(offre.getTitre());
        descriptionField.setText(offre.getDescription());
        prixField.setText(String.valueOf(offre.getPrix()));
        nombrePlacesField.setText(String.valueOf(offre.getNombrePlaces()));
        placesDisponiblesField.setText(String.valueOf(offre.getPlacesDisponibles()));
        dateDebutPicker.setValue(offre.getDateDebut() != null ? offre.getDateDebut().toLocalDate() : null);
        dateFinPicker.setValue(offre.getDateFin() != null ? offre.getDateFin().toLocalDate() : null);
        imageField.setText(offre.getImage());
        paysField.setText(offre.getPays());
        addButton.setText("Modifier");

        // Show image preview if image is available
        if (offre.getImage() != null && !offre.getImage().isEmpty()) {
            try {
                imagePreview.setImage(new Image("file:" + IMAGE_DIR + offre.getImage().substring("/images/offres/".length())));
            } catch (Exception e) {
                imagePreview.setImage(null);
            }
        }
    }

    @FXML
    private void handleImageSelection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(selectImageButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Generate a unique filename based on current time
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path targetPath = Paths.get(IMAGE_DIR + fileName);

                // Copy file to the target image directory
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Update image field and preview
                imageField.setText("/images/offres/" + fileName);
                imagePreview.setImage(new Image(selectedFile.toURI().toString()));
                imagePreview.setFitWidth(200);
                imagePreview.setFitHeight(150);
                imagePreview.setPreserveRatio(true);

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de l'image: " + e.getMessage());
            }
        }
    }

    private void clearErrors() {
        titreError.setVisible(false);
        descriptionError.setVisible(false);
        prixError.setVisible(false);
        nombrePlacesError.setVisible(false);
        placesDisponiblesError.setVisible(false);
        dateDebutError.setVisible(false);
        dateFinError.setVisible(false);
        imageError.setVisible(false);

        titreField.getStyleClass().remove("field-error");
        descriptionField.getStyleClass().remove("field-error");
        prixField.getStyleClass().remove("field-error");
        nombrePlacesField.getStyleClass().remove("field-error");
        placesDisponiblesField.getStyleClass().remove("field-error");
        dateDebutPicker.getStyleClass().remove("field-error");
        dateFinPicker.getStyleClass().remove("field-error");
        imageField.getStyleClass().remove("field-error");
    }

    private void showError(Label errorLabel, Control field, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        field.getStyleClass().add("field-error");
    }

    @FXML
    private void handleAddOffre() {
        try {
            clearErrors();
            boolean hasError = false;
            LocalDate today = LocalDate.now();

            // Validate the title
            if (titreField.getText().trim().isEmpty()) {
                showError(titreError, titreField, "Le titre est obligatoire");
                hasError = true;
            } else if (titreField.getText().trim().length() < 3) {
                showError(titreError, titreField, "Le titre doit contenir au moins 3 caractères");
                hasError = true;
            }

            // Validate the description
            if (descriptionField.getText().trim().isEmpty()) {
                showError(descriptionError, descriptionField, "La description est obligatoire");
                hasError = true;
            } else if (descriptionField.getText().trim().length() < 10) {
                showError(descriptionError, descriptionField, "La description doit contenir au moins 10 caractères");
                hasError = true;
            }

            // Validate the price
            try {
                double prix = Double.parseDouble(prixField.getText().trim());
                if (prix <= 0) {
                    showError(prixError, prixField, "Le prix doit être strictement positif");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                showError(prixError, prixField, "Le prix doit être un nombre valide");
                hasError = true;
            }

            // Validate the number of places
            try {
                int nombrePlaces = Integer.parseInt(nombrePlacesField.getText().trim());
                if (nombrePlaces <= 0) {
                    showError(nombrePlacesError, nombrePlacesField, "Le nombre de places doit être strictement positif");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                showError(nombrePlacesError, nombrePlacesField, "Le nombre de places doit être un nombre entier valide");
                hasError = true;
            }

            // Validate available places
            try {
                int placesDisponibles = Integer.parseInt(placesDisponiblesField.getText().trim());
                int nombrePlaces = Integer.parseInt(nombrePlacesField.getText().trim());
                if (placesDisponibles < 0) {
                    showError(placesDisponiblesError, placesDisponiblesField, "Les places disponibles ne peuvent pas être négatives");
                    hasError = true;
                } else if (placesDisponibles > nombrePlaces) {
                    showError(placesDisponiblesError, placesDisponiblesField, "Les places disponibles ne peuvent pas dépasser le nombre total de places");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                showError(placesDisponiblesError, placesDisponiblesField, "Les places disponibles doivent être un nombre entier valide");
                hasError = true;
            }

            // Validate dates
            if (dateDebutPicker.getValue() == null) {
                showError(dateDebutError, dateDebutPicker, "La date de début est obligatoire");
                hasError = true;
            } else if (dateDebutPicker.getValue().isBefore(today)) {
                showError(dateDebutError, dateDebutPicker, "La date de début doit être supérieure ou égale à aujourd'hui");
                hasError = true;
            }

            if (dateFinPicker.getValue() == null) {
                showError(dateFinError, dateFinPicker, "La date de fin est obligatoire");
                hasError = true;
            } else if (dateFinPicker.getValue().isBefore(today)) {
                showError(dateFinError, dateFinPicker, "La date de fin doit être supérieure ou égale à aujourd'hui");
                hasError = true;
            }

            // Check that the end date is after the start date
            if (dateDebutPicker.getValue() != null && dateFinPicker.getValue() != null) {
                if (dateFinPicker.getValue().isBefore(dateDebutPicker.getValue())) {
                    showError(dateFinError, dateFinPicker, "La date de fin doit être après la date de début");
                    hasError = true;
                }
            }

            // Validate image field
            if (imageField.getText().trim().isEmpty()) {
                showError(imageError, imageField, "L'image est obligatoire");
                hasError = true;
            }

            if (hasError) {
                return;
            }

            // If no errors, create/update the offer
            offre offre = offreToEdit != null ? offreToEdit : new offre();
            offre.setTitre(titreField.getText().trim());
            offre.setDescription(descriptionField.getText().trim());
            offre.setPrix(Double.parseDouble(prixField.getText().trim()));
            offre.setNombrePlaces(Integer.parseInt(nombrePlacesField.getText().trim()));
            offre.setPlacesDisponibles(Integer.parseInt(placesDisponiblesField.getText().trim()));
            offre.setDateDebut(dateDebutPicker.getValue().atStartOfDay());
            offre.setDateFin(dateFinPicker.getValue().atStartOfDay());
            offre.setDateCreation(LocalDateTime.now());
            offre.setImage(imageField.getText());

            if (offreToEdit != null) {
                offreService.update(offre);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre modifiée avec succès !");
            } else {
                offreService.create(offre);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre ajoutée avec succès !");
            }

            closeWindow();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'opération : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) titreField.getScene().getWindow();
        stage.close();
    }
}
