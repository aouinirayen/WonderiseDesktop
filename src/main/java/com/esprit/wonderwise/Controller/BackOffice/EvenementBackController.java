package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.Evenement;
import com.esprit.wonderwise.Model.Guide;
import com.esprit.wonderwise.Service.EvenementService;
import com.esprit.wonderwise.Service.GuideService;
import com.esprit.wonderwise.Utils.DialogUtils; // Import DialogUtils
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class EvenementBackController {

    @FXML private FlowPane evenementCards;
    @FXML private TextField nomField, lieuField, descriptionField, placeMaxField, prixField, paysField, categorieField, heureField, photoField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Guide> guideComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ImageView photoView;
    @FXML private Label nomLabel, dateLabel, heureLabel, lieuLabel, descriptionLabel, placeMaxLabel, prixLabel, statusLabel, paysLabel, categorieLabel, guideLabel, likesCountLabel, latitudeLabel, longitudeLabel, isFavoriteLabel, isAnnuleLabel, isInterestedLabel, isLikedLabel;
    private EvenementService evenementService = new EvenementService();
    private GuideService guideService = new GuideService();
    private Evenement selectedEvenement;
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$"); // HH:MM:SS format
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+$"); // Only positive integers
    private static final Pattern PRICE_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$"); // Positive number with up to 2 decimals

    @FXML
    public void initialize() {
        if (guideComboBox != null) {
            loadGuides();
        }
        if (evenementCards != null) {
            loadEvenements();
        }
        if (statusComboBox != null) {
            statusComboBox.setItems(FXCollections.observableArrayList("actif", "inactif"));
        }
        // Real-time validation for numeric fields
        if (placeMaxField != null) {
            placeMaxField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    placeMaxField.setText(oldVal); // Only allow digits
                }
            });
        }
        if (prixField != null) {
            prixField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*\\.?\\d{0,2}")) {
                    prixField.setText(oldVal); // Allow digits and up to 2 decimals
                }
            });
        }
    }

    private void loadGuides() {
        List<Guide> guides = guideService.readAll();
        guideComboBox.getItems().addAll(guides);
    }

    private void loadEvenements() {
        evenementCards.getChildren().clear();
        List<Evenement> evenements = evenementService.readAll();
        for (Evenement evenement : evenements) {
            VBox card = new VBox(10);
            card.setPrefWidth(250);
            card.setAlignment(Pos.TOP_CENTER);
            card.setPadding(new Insets(15));
            card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

            ImageView imageView = new ImageView();
            File imageFile = new File(IMAGE_DESTINATION_DIR + evenement.getPhoto());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString(), 200, 150, true, true);
                imageView.setImage(image);
            } else {
                Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 200, 150, true, true);
                imageView.setImage(fallback);
            }
            imageView.setFitWidth(200);
            imageView.setFitHeight(150);

            Label name = new Label(evenement.getNom());
            name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-alignment: center;");

            HBox categoryCountryBox = new HBox(5);
            categoryCountryBox.setAlignment(Pos.CENTER_LEFT);
            Label category = new Label(evenement.getCategorie());
            category.setStyle("-fx-font-size: 12px; -fx-text-fill: #155724; -fx-background-color: #D4EDDA; -fx-padding: 2 5; -fx-background-radius: 5; -fx-cursor: hand;");
            Label country = new Label(evenement.getPays());
            country.setStyle("-fx-font-size: 12px; -fx-text-fill: #3498DB; -fx-background-color: #E6F0FA; -fx-padding: 2 5; -fx-background-radius: 5; -fx-cursor: hand;");
            categoryCountryBox.getChildren().addAll(category, country);

            String descriptionText = evenement.getDescription();
            if (descriptionText.length() > 20) {
                descriptionText = descriptionText.substring(0, 17) + "...";
            }
            Label description = new Label(descriptionText);
            description.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");

            Label dateTime = new Label(evenement.getDate().toString() + " à " + evenement.getHeure().toString());
            dateTime.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");

            Label price = new Label("PRIX: " + evenement.getPrix() + "€");
            price.setStyle("-fx-font-size: 12px; -fx-text-fill: #2C3E50; -fx-font-weight: bold;");

            Label places = new Label("Places disponibles: " + evenement.getPlaceMax());
            places.setStyle("-fx-font-size: 12px; -fx-text-fill: #2C3E50;");

            Label status = new Label(evenement.getStatus().equals("actif") ? "Actif" : "Inactif");
            status.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (evenement.getStatus().equals("actif") ? "#28A745" : "#6C757D") + ";");

            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER);

            Button detailsBtn = new Button("Voir");
            detailsBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;");
            detailsBtn.setOnAction(e -> showEvenementDetails(evenement));
            detailsBtn.setOnMouseEntered(e -> detailsBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-opacity: 0.8;"));
            detailsBtn.setOnMouseExited(e -> detailsBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;"));

            Button editBtn = new Button("Modifier");
            editBtn.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;");
            editBtn.setOnAction(e -> showEditEvenement(evenement));
            editBtn.setOnMouseEntered(e -> editBtn.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-opacity: 0.8;"));
            editBtn.setOnMouseExited(e -> editBtn.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;"));

            Button deleteBtn = new Button("Supprimer");
            deleteBtn.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;");
            deleteBtn.setOnAction(e -> deleteEvenement(evenement));
            deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-opacity: 0.8;"));
            deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;"));

            buttons.getChildren().addAll(detailsBtn, editBtn, deleteBtn);

            card.getChildren().addAll(imageView, name, categoryCountryBox, description, dateTime, price, places, status, buttons);
            evenementCards.getChildren().add(card);
        }
    }

    @FXML
    public void showAddEvenement() throws IOException {
        loadScene("/com/esprit/wonderwise/BackOffice/Evenement/EvenementAdd.fxml");
    }

    @FXML
    public void showEvenementList() throws IOException {
        loadScene("/com/esprit/wonderwise/BackOffice/Evenement/Evenement.fxml");
    }

    private void showEvenementDetails(Evenement evenement) {
        try {
            selectedEvenement = evenement;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Evenement/EvenementDetails.fxml"));
            Parent root = loader.load();
            EvenementBackController controller = loader.getController();
            controller.setEvenementDetails(evenement);
            Stage stage = (Stage) (evenementCards != null ? evenementCards.getScene().getWindow() : nomLabel.getScene().getWindow());
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showEditEvenement(Evenement evenement) {
        try {
            selectedEvenement = evenement;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Evenement/EvenementEdit.fxml"));
            Parent root = loader.load();
            EvenementBackController controller = loader.getController();
            controller.setEvenementDataForEdit(evenement);
            Stage stage = (Stage) evenementCards.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setEvenementDetails(Evenement evenement) {
        nomLabel.setText(evenement.getNom());
        dateLabel.setText("Date: " + evenement.getDate().toString());
        heureLabel.setText("Heure: " + evenement.getHeure().toString());
        lieuLabel.setText("Lieu: " + evenement.getLieu());
        descriptionLabel.setText("Description: " + evenement.getDescription());
        placeMaxLabel.setText("Places Disponibles: " + evenement.getPlaceMax());
        prixLabel.setText("Prix: " + evenement.getPrix());
        statusLabel.setText("Actif: " + evenement.getStatus());
        paysLabel.setText("Pays: " + evenement.getPays());
        categorieLabel.setText("Catégorie: " + evenement.getCategorie());
        guideLabel.setText("Guide: " + guideService.readById(evenement.getGuideId()).toString());

        File imageFile = new File(IMAGE_DESTINATION_DIR + evenement.getPhoto());
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString(), 200, 200, true, true);
            photoView.setImage(image);
        } else {
            Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 200, 200, true, true);
            photoView.setImage(fallback);
        }
    }

    private void setEvenementDataForEdit(Evenement evenement) {
        selectedEvenement = evenement;
        nomField.setText(evenement.getNom());
        guideComboBox.getSelectionModel().select(guideService.readById(evenement.getGuideId()));
        descriptionField.setText(evenement.getDescription());
        datePicker.setValue(evenement.getDate().toLocalDate());
        heureField.setText(evenement.getHeure().toString());
        lieuField.setText(evenement.getLieu());
        placeMaxField.setText(String.valueOf(evenement.getPlaceMax()));
        prixField.setText(String.valueOf(evenement.getPrix()));
        statusComboBox.getSelectionModel().select(evenement.getStatus());
        paysField.setText(evenement.getPays());
        categorieField.setText(evenement.getCategorie());
        photoField.setText(evenement.getPhoto());
    }

    @FXML
    public void handlePhotoBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            photoField.setText(file.getAbsolutePath());
        }
    }

    private String copyImageToDestination(String sourcePath) throws IOException {
        if (sourcePath == null || sourcePath.isEmpty()) return "";
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) return "";
        String fileName = System.currentTimeMillis() + "-" + sourceFile.getName();
        File destFile = new File(IMAGE_DESTINATION_DIR + fileName);
        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    @FXML
    public void addEvenement() throws SQLException, IOException {
        if (!validateInputs()) return;

        Evenement evenement = new Evenement();
        evenement.setNom(nomField.getText().trim());
        evenement.setGuideId(guideComboBox.getSelectionModel().getSelectedItem().getId());
        evenement.setDescription(descriptionField.getText().trim());
        evenement.setDate(Date.valueOf(datePicker.getValue()));
        evenement.setHeure(Time.valueOf(heureField.getText().trim()));
        evenement.setLieu(lieuField.getText().trim());
        evenement.setPlaceMax(Integer.parseInt(placeMaxField.getText().trim()));
        evenement.setPrix(Double.parseDouble(prixField.getText().trim()));
        evenement.setStatus(statusComboBox.getSelectionModel().getSelectedItem());
        evenement.setPays(paysField.getText().trim());
        evenement.setCategorie(categorieField.getText().trim());
        evenement.setLikesCount(0);
        evenement.setLatitude(0.0);
        evenement.setLongitude(0.0);
        evenement.setFavorite(false);
        evenement.setAnnule(false);
        evenement.setInterested(false);
        evenement.setLiked(false);

        String imageFileName = copyImageToDestination(photoField.getText());
        evenement.setPhoto(imageFileName);

        evenementService.add(evenement);
        DialogUtils.showCustomDialog("Success", "Event added successfully!", true, getCurrentStage());
        showEvenementList();
    }

    @FXML
    public void updateEvenement() throws SQLException, IOException {
        if (selectedEvenement == null) {
            DialogUtils.showCustomDialog("Error", "No event selected for update.", false, getCurrentStage());
            return;
        }
        if (!validateInputs()) return;

        selectedEvenement.setNom(nomField.getText().trim());
        selectedEvenement.setGuideId(guideComboBox.getSelectionModel().getSelectedItem().getId());
        selectedEvenement.setDescription(descriptionField.getText().trim());
        selectedEvenement.setDate(Date.valueOf(datePicker.getValue()));
        selectedEvenement.setHeure(Time.valueOf(heureField.getText().trim()));
        selectedEvenement.setLieu(lieuField.getText().trim());
        selectedEvenement.setPlaceMax(Integer.parseInt(placeMaxField.getText().trim()));
        selectedEvenement.setPrix(Double.parseDouble(prixField.getText().trim()));
        selectedEvenement.setStatus(statusComboBox.getSelectionModel().getSelectedItem());
        selectedEvenement.setPays(paysField.getText().trim());
        selectedEvenement.setCategorie(categorieField.getText().trim());

        String imageFileName = copyImageToDestination(photoField.getText());
        if (!imageFileName.isEmpty()) {
            selectedEvenement.setPhoto(imageFileName);
        }

        evenementService.update(selectedEvenement);
        DialogUtils.showCustomDialog("Success", "Event updated successfully!", true, getCurrentStage());
        showEvenementList();
    }

    private void deleteEvenement(Evenement evenement) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this event?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            evenementService.delete(evenement.getId());
            loadEvenements();
            DialogUtils.showCustomDialog("Success", "Event deleted successfully!", true, getCurrentStage());
        }
    }

    private void loadScene(String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) (nomField != null ? nomField.getScene().getWindow() :
                (nomLabel != null ? nomLabel.getScene().getWindow() :
                        evenementCards.getScene().getWindow()));
        stage.setScene(new Scene(root));
    }

    // Validation method
    private boolean validateInputs() {
        if (nomField.getText().trim().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Event name cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (guideComboBox.getSelectionModel().getSelectedItem() == null) {
            DialogUtils.showCustomDialog("Validation Error", "Please select a guide.", false, getCurrentStage());
            return false;
        }
        if (descriptionField.getText().trim().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Description cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (datePicker.getValue() == null) {
            DialogUtils.showCustomDialog("Validation Error", "Please select a date.", false, getCurrentStage());
            return false;
        }
        if (heureField.getText().trim().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Time cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (!TIME_PATTERN.matcher(heureField.getText().trim()).matches()) {
            DialogUtils.showCustomDialog("Validation Error", "Time must be in HH:MM:SS format (e.g., 14:30:00).", false, getCurrentStage());
            return false;
        }
        try {
            LocalTime.parse(heureField.getText().trim()); // Validate time is parseable
        } catch (Exception e) {
            DialogUtils.showCustomDialog("Validation Error", "Invalid time format.", false, getCurrentStage());
            return false;
        }
        if (lieuField.getText().trim().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Location cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (placeMaxField.getText().trim().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Maximum places cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (!NUMBER_PATTERN.matcher(placeMaxField.getText().trim()).matches() || Integer.parseInt(placeMaxField.getText().trim()) <= 0) {
            DialogUtils.showCustomDialog("Validation Error", "Maximum places must be a positive integer.", false, getCurrentStage());
            return false;
        }
        if (prixField.getText().trim().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Price cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (!PRICE_PATTERN.matcher(prixField.getText().trim()).matches() || Double.parseDouble(prixField.getText().trim()) < 0) {
            DialogUtils.showCustomDialog("Validation Error", "Price must be a non-negative number with up to 2 decimal places.", false, getCurrentStage());
            return false;
        }
        if (statusComboBox.getSelectionModel().getSelectedItem() == null) {
            DialogUtils.showCustomDialog("Validation Error", "Please select a status.", false, getCurrentStage());
            return false;
        }
        if (paysField.getText().trim().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Country cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (categorieField.getText().trim().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Category cannot be empty.", false, getCurrentStage());
            return false;
        }
        return true;
    }

    // Utility method to get the current stage
    private Stage getCurrentStage() {
        return (Stage) (evenementCards != null ? evenementCards.getScene().getWindow() :
                nomField != null ? nomField.getScene().getWindow() :
                        nomLabel.getScene().getWindow());
    }
}