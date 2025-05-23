package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.Art;
import com.esprit.wonderwise.Model.Country;
import com.esprit.wonderwise.Service.ArtService;
import com.esprit.wonderwise.Service.CountryService;
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.util.List;
import java.util.Optional;

public class ArtController {
    @FXML private FlowPane artCards;
    @FXML private TextField nameField, imgPathField, descField, dateField, typeField;
    @FXML private ComboBox<Country> countryComboBox;
    @FXML private Label nameLabel, descLabel, dateLabel, typeLabel;
    @FXML private ImageView detailImageView;
    // Advanced search controls
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<String> countryCombo;

    private List<Art> allArts = null;
    private List<Art> filteredArts = null;
    private List<Country> allCountries = null;

    private ArtService artService = new ArtService();
    private CountryService countryService = new CountryService();
    private Art selectedArt;
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    @FXML
    public void initialize() {
        System.out.println("Initializing ArtController...");
        if (artCards != null) {
            // Load all arts and countries only once
            allArts = artService.readAll();
            filteredArts = allArts;
            allCountries = countryService.readAll();

            // Populate typeCombo with 'All' + distinct types
            List<String> typeOptions = new java.util.ArrayList<>();
            typeOptions.add("All");
            typeOptions.addAll(allArts.stream().map(Art::getType).filter(s -> s != null && !s.isEmpty()).distinct().sorted().toList());
            if (typeCombo != null) {
                typeCombo.getItems().setAll(typeOptions);
                typeCombo.getSelectionModel().selectFirst();
            }

            // Populate countryCombo with 'All' + country names
            List<String> countryOptions = new java.util.ArrayList<>();
            countryOptions.add("All");
            countryOptions.addAll(allCountries.stream().map(Country::getName).distinct().sorted().toList());
            if (countryCombo != null) {
                countryCombo.getItems().setAll(countryOptions);
                countryCombo.getSelectionModel().selectFirst();
            }

            // Add listeners for live search/filter
            if (searchField != null)
                searchField.textProperty().addListener((obs, oldVal, newVal) -> applyAdvancedSearch());
            if (typeCombo != null)
                typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyAdvancedSearch());
            if (countryCombo != null)
                countryCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyAdvancedSearch());

            loadArts();
        }
        // Load countries regardless of the view (list, add, or edit)
        if (countryComboBox != null) {
            loadCountries();
        } else {
            System.out.println("countryComboBox is null in this view.");
        }
        if (descField != null) {
            descField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.length() > 255) descField.setText(oldVal);
            });
        }
    }

    private void applyAdvancedSearch() {
        String keyword = (searchField != null && searchField.getText() != null) ? searchField.getText().trim().toLowerCase() : "";
        String type = (typeCombo != null) ? typeCombo.getValue() : null;
        String country = (countryCombo != null) ? countryCombo.getValue() : null;
        filteredArts = allArts.stream()
            .filter(a -> (keyword.isEmpty() || (a.getName() != null && a.getName().toLowerCase().contains(keyword)) || (a.getDescription() != null && a.getDescription().toLowerCase().contains(keyword))))
            .filter(a -> (type == null || type.equals("All") || type.isEmpty() || (a.getType() != null && a.getType().equals(type))))
            .filter(a -> (country == null || country.equals("All") || country.isEmpty() || (getCountryNameById(a.getCountryId()).equals(country))))
            .toList();
        loadArts();
    }

    private String getCountryNameById(int countryId) {
        if (allCountries == null) return "";
        return allCountries.stream().filter(c -> c.getId() == countryId).map(Country::getName).findFirst().orElse("");
    }

    private void loadArts() {
        artCards.getChildren().clear();
        List<Art> arts = (filteredArts != null) ? filteredArts : artService.readAll();
        System.out.println("Number of arts loaded: " + arts.size());

        if (arts == null || arts.isEmpty()) {
            VBox indicator = new VBox(16);
            indicator.setAlignment(Pos.CENTER);
            indicator.setStyle("-fx-padding: 40; -fx-background-color: #f8fafd; -fx-background-radius: 12;");

            ImageView icon = new ImageView();
            try {
                Image emptyIcon = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/empty-box.png"));
                icon.setImage(emptyIcon);
            } catch (Exception e) {
                // fallback: no icon
            }
            icon.setFitWidth(90);
            icon.setFitHeight(90);
            icon.setPreserveRatio(true);
            icon.setSmooth(true);

            Label mainText = new Label("No arts found");
            mainText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
            Label subText = new Label("Try adjusting your search or add a new art.");
            subText.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

            indicator.getChildren().addAll(icon, mainText, subText);
            artCards.getChildren().add(indicator);
            return;
        }

        for (Art art : arts) {
            VBox card = new VBox(15);
            card.getStyleClass().add("country-card");
            card.setPrefWidth(250);
            card.setPrefHeight(300);
            card.setAlignment(Pos.CENTER);

            ImageView imageView = new ImageView();
            File imageFile = new File(IMAGE_DESTINATION_DIR + art.getImg());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString(), 200, 150, true, true);
                imageView.setImage(image);
            } else {
                Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 200, 150, true, true);
                imageView.setImage(fallback);
            }
            imageView.setFitWidth(200);
            imageView.setFitHeight(150);
            imageView.getStyleClass().add("rounded-image");

            Label name = new Label(art.getName());
            name.setStyle("-fx-font-size: 18px; -fx-text-fill: #2C3E50; -fx-font-weight: bold;");

            Label snippet = new Label("Type: " + art.getType());
            snippet.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");

            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER);
            Button detailsBtn = new Button("Details");
            Button editBtn = new Button("Edit");
            Button deleteBtn = new Button("Delete");

            detailsBtn.getStyleClass().add("action-button");
            editBtn.getStyleClass().add("action-button");
            deleteBtn.getStyleClass().addAll("action-button", "delete-button");

            detailsBtn.setOnAction(e -> {
                try {
                    showDetails(art);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            editBtn.setOnAction(e -> {
                try {
                    showEditArt(art);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            deleteBtn.setOnAction(e -> {
                try {
                    deleteArt(art.getId());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            buttons.getChildren().addAll(detailsBtn, editBtn, deleteBtn);
            card.getChildren().addAll(imageView, name, snippet, buttons);
            artCards.getChildren().add(card);
            System.out.println("Art card loaded: " + art.getName());
        }
    }

    private void loadCountries() {
        if (countryComboBox == null) {
            System.out.println("countryComboBox is null, cannot load countries.");
            return;
        }
        countryComboBox.getItems().clear();
        List<Country> countries = countryService.readAll();
        System.out.println("Number of countries retrieved: " + countries.size());
        if (countries.isEmpty()) {
            System.out.println("No countries found in the database.");
        } else {
            countryComboBox.getItems().addAll(countries);
            System.out.println("Countries loaded into ComboBox: " + countryComboBox.getItems().size());
        }
    }

    @FXML
    public void showAddArt() throws IOException {
        loadScene("ArtAdd.fxml", null);
    }

    @FXML
    public void addArt() throws IOException {
        if (!validateInputs()) return;

        String imageFileName = copyImageToDestination(imgPathField.getText());
        Art art = new Art(
                0,
                countryComboBox.getValue().getId(),
                nameField.getText(),
                imageFileName,
                descField.getText(),
                dateField.getText(),
                typeField.getText()
        );
        artService.add(art);
        DialogUtils.showCustomDialog("Success", "Art added successfully!", true, getCurrentStage());
        showArtList();
    }

    @FXML
    public void handleImgBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imgPathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void showEditArt(Art art) throws IOException {
        selectedArt = art;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Art/ArtEdit.fxml"));
        Parent root = loader.load();
        ArtController controller = loader.getController();
        controller.setArtData(art);
        controller.selectedArt = art;
        Stage stage = (Stage) (artCards != null ? artCards.getScene().getWindow() : nameField.getScene().getWindow());
        stage.setScene(new Scene(root));
    }

    @FXML
    public void updateArt() throws IOException {
        if (selectedArt == null) {
            DialogUtils.showCustomDialog("Error", "No art selected for update.", false, getCurrentStage());
            return;
        }
        if (!validateInputs()) return;

        String imageFileName = copyImageToDestination(imgPathField.getText());
        selectedArt.setCountryId(countryComboBox.getValue().getId());
        selectedArt.setName(nameField.getText());
        selectedArt.setImg(imageFileName);
        selectedArt.setDescription(descField.getText());
        selectedArt.setDate(dateField.getText());
        selectedArt.setType(typeField.getText());
        artService.update(selectedArt);
        DialogUtils.showCustomDialog("Success", "Art updated successfully!", true, getCurrentStage());
        showArtList();
    }

    private String copyImageToDestination(String sourcePath) throws IOException {
        if (sourcePath == null || sourcePath.isEmpty()) return "";
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) return "";
        String fileName = sourceFile.getName();
        File destFile = new File(IMAGE_DESTINATION_DIR + fileName);
        try {
            // First copy the image
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            // Apply watermark
            com.esprit.wonderwise.Utils.WatermarkUtils.applyTextWatermark(destFile, destFile, "WonderWise");
        } catch (Exception e) {
            // If watermarking fails, fallback to normal copy
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.err.println("Failed to apply watermark: " + e.getMessage());
        }
        return fileName;
    }

    public void setArtData(Art art) {
        nameField.setText(art.getName());
        imgPathField.setText(IMAGE_DESTINATION_DIR + art.getImg());
        descField.setText(art.getDescription());
        dateField.setText(art.getDate());
        typeField.setText(art.getType());
        Country country = countryService.getById(art.getCountryId());
        if (country != null) {
            countryComboBox.setValue(country);
            System.out.println("Set country in ComboBox: " + country.getName());
        } else {
            System.out.println("Country not found for ID: " + art.getCountryId());
        }
    }

    @FXML
    public void deleteArt(int id) throws IOException {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this art?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            artService.delete(id);
            DialogUtils.showCustomDialog("Success", "Art deleted successfully!", true, getCurrentStage());
            showArtList();
        }
    }

    @FXML
    public void showDetails(Art art) throws IOException {
        selectedArt = art;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Art/ArtDetails.fxml"));
        Parent root = loader.load();
        ArtController controller = loader.getController();
        controller.setDetailsData(art);
        Stage stage = (Stage) (artCards != null ? artCards.getScene().getWindow() : nameLabel.getScene().getWindow());
        stage.setScene(new Scene(root));
    }

    public void setDetailsData(Art art) {
        nameLabel.setText(art.getName());
        descLabel.setText(art.getDescription());
        dateLabel.setText("Date: " + art.getDate());
        typeLabel.setText("Type: " + art.getType());

        File imageFile = new File(IMAGE_DESTINATION_DIR + art.getImg());
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString(), 300, 200, true, true);
            detailImageView.setImage(image);
        } else {
            Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 300, 200, true, true);
            detailImageView.setImage(fallback);
        }
    }

    @FXML
    public void showArtList() throws IOException {
        loadScene("Art.fxml", null);
    }

    private void loadScene(String fxml, Button sourceButton) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/BackOffice/Art/" + fxml));
        Stage stage;
        if (sourceButton != null) {
            stage = (Stage) sourceButton.getScene().getWindow();
        } else if (artCards != null) {
            stage = (Stage) artCards.getScene().getWindow();
        } else if (nameField != null) {
            stage = (Stage) nameField.getScene().getWindow();
        } else {
            stage = (Stage) nameLabel.getScene().getWindow();
        }
        stage.setScene(new Scene(root));
    }

    private boolean validateInputs() {
        if (nameField.getText().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Name cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (countryComboBox.getValue() == null) {
            DialogUtils.showCustomDialog("Validation Error", "Please select a country.", false, getCurrentStage());
            return false;
        }
        if (dateField.getText().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Date cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (typeField.getText().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Type cannot be empty.", false, getCurrentStage());
            return false;
        }
        return true;
    }

    private Stage getCurrentStage() {
        return (Stage) (artCards != null ? artCards.getScene().getWindow() :
                nameField != null ? nameField.getScene().getWindow() :
                        nameLabel.getScene().getWindow());
    }
}