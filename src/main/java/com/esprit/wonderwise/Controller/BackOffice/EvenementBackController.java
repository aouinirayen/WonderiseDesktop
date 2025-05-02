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
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class EvenementBackController {

    @FXML private FlowPane evenementCards;
    @FXML private TextField nomField, lieuField, descriptionField, placeMaxField, prixField, paysField, categorieField, heureField, photoField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Guide> guideComboBox;
    @FXML private ImageView photoView;
    @FXML private Label nomLabel, dateLabel, heureLabel, lieuLabel, descriptionLabel, placeMaxLabel, prixLabel, statusLabel, paysLabel, categorieLabel, guideLabel, likesCountLabel, latitudeLabel, longitudeLabel, isFavoriteLabel, isAnnuleLabel, isInterestedLabel, isLikedLabel;
    @FXML private javafx.scene.control.TextField searchField;
    @FXML private javafx.scene.control.ComboBox<String> filterCombo;
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
            // Populate filterCombo with categories
            List<Evenement> allEvents = evenementService.readAll();
            java.util.Set<String> categories = new java.util.HashSet<>();
            for (Evenement evt : allEvents) {
                if (evt.getCategorie() != null && !evt.getCategorie().isEmpty()) {
                    categories.add(evt.getCategorie());
                }
            }
            if (filterCombo != null) {
                filterCombo.getItems().clear();
                filterCombo.getItems().add("Toutes les catégories");
                filterCombo.getItems().addAll(categories);
                filterCombo.getSelectionModel().selectFirst();
                filterCombo.valueProperty().addListener((obs, oldVal, newVal) -> applySearchFilter());
            }
            if (searchField != null) {
                searchField.textProperty().addListener((obs, oldVal, newVal) -> applySearchFilter());
            }
            loadEvenements(null, null);
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

    private void loadEvenements(String search, String category) {
        evenementCards.getChildren().clear();
        List<Evenement> evenements = evenementService.readAll();
        // Filter by search and category
        List<Evenement> filtered = new java.util.ArrayList<>();
        for (Evenement evt : evenements) {
            boolean matchesSearch = (search == null || search.isEmpty()) ||
                evt.getNom().toLowerCase().contains(search != null ? search.toLowerCase() : "") ||
                (evt.getLieu() != null && evt.getLieu().toLowerCase().contains(search != null ? search.toLowerCase() : ""));
            boolean matchesCategory = (category == null || category.equals("Toutes les catégories")) ||
                (evt.getCategorie() != null && evt.getCategorie().equals(category));
            if (matchesSearch && matchesCategory) {
                filtered.add(evt);
            }
        }
        for (Evenement evenement : filtered) {
            VBox card = new VBox(8);
            card.setPrefWidth(240);
            card.setAlignment(Pos.TOP_CENTER);
            card.setPadding(new Insets(12));
            card.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);");

            // Image with Rounded Corners
            ImageView imageView = new ImageView();
            File imageFile = new File(IMAGE_DESTINATION_DIR + evenement.getPhoto());
            Image image;
            if (imageFile.exists()) {
                image = new Image(imageFile.toURI().toString(), 200, 120, true, true);
            } else {
                image = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 200, 120, true, true);
            }
            imageView.setImage(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(120);
            Rectangle clip = new Rectangle(200, 120);
            clip.setArcWidth(16);
            clip.setArcHeight(16);
            imageView.setClip(clip);

            // Name
            Label name = new Label(evenement.getNom());
            name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1F2A44; -fx-alignment: center; -fx-wrap-text: true; -fx-max-width: 200;");
            name.setWrapText(true);

            // Category, Country, and Status
            HBox categoryCountryStatusBox = new HBox(6);
            categoryCountryStatusBox.setAlignment(Pos.CENTER);
            Label categoryLabel = new Label(evenement.getCategorie());
            categoryLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #146C43; -fx-background-color: #D1E7DD; -fx-padding: 3 6; -fx-background-radius: 4; -fx-font-weight: bold;");
            Label country = new Label(evenement.getPays());
            country.setStyle("-fx-font-size: 11px; -fx-text-fill: #1E6091; -fx-background-color: #D7E9F7; -fx-padding: 3 6; -fx-background-radius: 4; -fx-font-weight: bold;");
            Label status = new Label(evenement.getStatus().equals("actif") ? "Actif" : "Inactif");
            status.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; " +
                    "-fx-text-fill: " + (evenement.getStatus().equals("actif") ? "#22C55E" : "#EF4444") + "; " +
                    "-fx-border-color: " + (evenement.getStatus().equals("actif") ? "#22C55E" : "#EF4444") + "; " +
                    "-fx-border-width: 1; -fx-border-radius: 4; -fx-background-color: transparent; " +
                    "-fx-padding: 2 6; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);");
            categoryCountryStatusBox.getChildren().addAll(categoryLabel, country, status);

            // Description (Centered)
            String descriptionText = evenement.getDescription();
            if (descriptionText.length() > 50) {
                descriptionText = descriptionText.substring(0, 47) + "...";
            }
            Label description = new Label(descriptionText);
            description.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280; -fx-wrap-text: true; -fx-max-width: 200; -fx-alignment: center;");
            description.setWrapText(true);

            // Date and Time
            Label dateTime = new Label(evenement.getDate().toString() + " | " + evenement.getHeure().toString());
            dateTime.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");

            // Price and Places
            HBox pricePlacesBox = new HBox(10);
            pricePlacesBox.setAlignment(Pos.CENTER);
            Label price = new Label(evenement.getPrix() + "€");
            price.setStyle("-fx-font-size: 12px; -fx-text-fill: #1F2A44; -fx-font-weight: bold;");
            Label places = new Label("Places: " + evenement.getPlaceMax());
            places.setStyle("-fx-font-size: 12px; -fx-text-fill: #1F2A44; -fx-font-weight: bold;");
            pricePlacesBox.getChildren().addAll(price, places);

            // Buttons
            HBox buttons = new HBox(8);
            buttons.setAlignment(Pos.CENTER);

            Button detailsBtn = new Button("Details");
            detailsBtn.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;");
            detailsBtn.setOnAction(e -> showEvenementDetails(evenement));
            detailsBtn.setOnMouseEntered(e -> detailsBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"));
            detailsBtn.setOnMouseExited(e -> detailsBtn.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"));

            Button editBtn = new Button("Edit");
            editBtn.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;");
            editBtn.setOnAction(e -> showEditEvenement(evenement));
            editBtn.setOnMouseEntered(e -> editBtn.setStyle("-fx-background-color: #D97706; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"));
            editBtn.setOnMouseExited(e -> editBtn.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"));

            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;");
            deleteBtn.setOnAction(e -> deleteEvenement(evenement));
            deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"));
            deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"));

            buttons.getChildren().addAll(detailsBtn, editBtn, deleteBtn);

            card.getChildren().addAll(imageView, name, categoryCountryStatusBox, description, dateTime, pricePlacesBox, buttons);
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
        evenement.setStatus("inactif");
        evenement.setPays(paysField.getText().trim());
        evenement.setCategorie(categorieField.getText().trim());
        evenement.setLikesCount(0);
        evenement.setLatitude(0.0);
        evenement.setLongitude(0.0);
        evenement.setFavorite(false);
        evenement.setAnnule(false);
        //evenement.setInterested();
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
        selectedEvenement.setStatus("inactif");
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
            loadEvenements(null, null);
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
        if (datePicker.getValue().isBefore(LocalDate.now())) {
            DialogUtils.showCustomDialog("Validation Error", "The event date cannot be in the past.", false, getCurrentStage());
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
            LocalTime.parse(heureField.getText().trim());
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

    // --- Search and Filter Helper ---
    private void applySearchFilter() {
        String search = (searchField != null) ? searchField.getText() : null;
        String category = (filterCombo != null && filterCombo.getValue() != null) ? filterCombo.getValue() : null;
        loadEvenements(search, category);
    }
}