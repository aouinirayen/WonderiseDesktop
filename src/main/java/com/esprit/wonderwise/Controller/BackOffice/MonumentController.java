package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.Monument;
import com.esprit.wonderwise.Model.Country;
import com.esprit.wonderwise.Service.MonumentService;
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

public class MonumentController {
    @FXML private FlowPane monumentCards;
    @FXML private TextField nameField, imgPathField, descField;
    @FXML private ComboBox<Country> countryComboBox;
    @FXML private Label nameLabel, descLabel;
    @FXML private ImageView detailImageView;
    // Advanced search fields
    @FXML private TextField searchField;
    @FXML private ComboBox<String> countryCombo;

    private MonumentService monumentService = new MonumentService();
    private CountryService countryService = new CountryService();
    private Monument selectedMonument;
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    @FXML
    public void initialize() {
        if (monumentCards != null) {
            setupAdvancedSearch();
            loadMonuments(monumentService.readAll());
        }
        if (countryComboBox != null) {
            loadCountries();
        }
        if (descField != null) {
            descField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.length() > 1000) descField.setText(oldVal);
            });
        }
    }

    private void setupAdvancedSearch() {
        if (searchField == null || countryCombo == null) return;
        countryCombo.getItems().clear();
        countryCombo.getItems().add("All");
        for (Country c : countryService.readAll()) {
            countryCombo.getItems().add(c.getName());
        }
        countryCombo.getSelectionModel().selectFirst();
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyAdvancedSearch());
        countryCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyAdvancedSearch());
    }

    private void applyAdvancedSearch() {
        if (searchField == null || countryCombo == null) return;
        String keyword = searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";
        String selectedCountry = countryCombo.getValue();
        List<Monument> allMonuments = monumentService.readAll();
        List<Monument> filtered = allMonuments.stream().filter(monument -> {
            boolean matchesKeyword = keyword.isEmpty() ||
                    (monument.getName() != null && monument.getName().toLowerCase().contains(keyword)) ||
                    (monument.getDescription() != null && monument.getDescription().toLowerCase().contains(keyword));
            boolean matchesCountry = selectedCountry == null || selectedCountry.equals("All");
            if (!matchesCountry) {
                Country country = countryService.getById(monument.getCountryId());
                matchesCountry = country != null && selectedCountry.equals(country.getName());
            }
            return matchesKeyword && matchesCountry;
        }).toList();
        loadMonuments(filtered);
    }

    private void loadMonuments(List<Monument> monuments) {
        monumentCards.getChildren().clear();
        if (monuments == null || monuments.isEmpty()) {
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

            Label mainText = new Label("No monuments found");
            mainText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
            Label subText = new Label("Try adjusting your search or add a new monument.");
            subText.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

            indicator.getChildren().addAll(icon, mainText, subText);
            monumentCards.getChildren().add(indicator);
            return;
        }
        for (Monument monument : monuments) {
            VBox card = new VBox(15);
            card.getStyleClass().add("country-card");
            card.setPrefWidth(250);
            card.setPrefHeight(300);
            card.setAlignment(Pos.CENTER);

            ImageView imageView = new ImageView();
            File imageFile = new File(IMAGE_DESTINATION_DIR + monument.getImg());
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

            Label name = new Label(monument.getName());
            name.setStyle("-fx-font-size: 18px; -fx-text-fill: #2C3E50; -fx-font-weight: bold;");

            Country country = countryService.getById(monument.getCountryId());
            String countryName = (country != null) ? country.getName() : "Unknown Country";
            Label snippet = new Label("Country: " + countryName);
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
                    showDetails(monument);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            editBtn.setOnAction(e -> {
                try {
                    showEditMonument(monument);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            deleteBtn.setOnAction(e -> {
                try {
                    deleteMonument(monument.getId());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            buttons.getChildren().addAll(detailsBtn, editBtn, deleteBtn);
            card.getChildren().addAll(imageView, name, snippet, buttons);
            monumentCards.getChildren().add(card);
            System.out.println("Monument card loaded: " + monument.getName());
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
    public void showAddMonument() throws IOException {
        loadScene("MonumentAdd.fxml", null);
    }

    @FXML
    public void addMonument() throws IOException {
        if (!validateInputs()) return;

        String imageFileName = copyImageToDestination(imgPathField.getText());
        Monument monument = new Monument(
                0,
                countryComboBox.getValue().getId(),
                nameField.getText(),
                imageFileName,
                descField.getText()
        );
        monumentService.add(monument);
        DialogUtils.showCustomDialog("Success", "Monument added successfully!", true, getCurrentStage());
        showMonumentList();
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
    public void showEditMonument(Monument monument) throws IOException {
        selectedMonument = monument;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Monument/MonumentEdit.fxml"));
        Parent root = loader.load();
        MonumentController controller = loader.getController();
        controller.setMonumentData(monument);
        controller.selectedMonument = monument;
        Stage stage = (Stage) (monumentCards != null ? monumentCards.getScene().getWindow() : nameField.getScene().getWindow());
        stage.setScene(new Scene(root));
    }

    @FXML
    public void updateMonument() throws IOException {
        if (selectedMonument == null) {
            DialogUtils.showCustomDialog("Error", "No monument selected for update.", false, getCurrentStage());
            return;
        }
        if (!validateInputs()) return;

        String imageFileName = copyImageToDestination(imgPathField.getText());
        selectedMonument.setCountryId(countryComboBox.getValue().getId());
        selectedMonument.setName(nameField.getText());
        selectedMonument.setImg(imageFileName);
        selectedMonument.setDescription(descField.getText());
        monumentService.update(selectedMonument);
        DialogUtils.showCustomDialog("Success", "Monument updated successfully!", true, getCurrentStage());
        showMonumentList();
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

    public void setMonumentData(Monument monument) {
        nameField.setText(monument.getName());
        imgPathField.setText(IMAGE_DESTINATION_DIR + monument.getImg());
        descField.setText(monument.getDescription());
        Country country = countryService.getById(monument.getCountryId());
        if (country != null) {
            countryComboBox.setValue(country);
            System.out.println("Set country in ComboBox: " + country.getName());
        } else {
            System.out.println("Country not found for ID: " + monument.getCountryId());
        }
    }

    @FXML
    public void deleteMonument(int id) throws IOException {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this monument?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            monumentService.delete(id);
            DialogUtils.showCustomDialog("Success", "Monument deleted successfully!", true, getCurrentStage());
            showMonumentList();
        }
    }

    @FXML
    public void showDetails(Monument monument) throws IOException {
        selectedMonument = monument;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Monument/MonumentDetails.fxml"));
        Parent root = loader.load();
        MonumentController controller = loader.getController();
        controller.setDetailsData(monument);
        Stage stage = (Stage) (monumentCards != null ? monumentCards.getScene().getWindow() : nameLabel.getScene().getWindow());
        stage.setScene(new Scene(root));
    }

    public void setDetailsData(Monument monument) {
        nameLabel.setText(monument.getName());
        descLabel.setText(monument.getDescription());

        File imageFile = new File(IMAGE_DESTINATION_DIR + monument.getImg());
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString(), 300, 200, true, true);
            detailImageView.setImage(image);
        } else {
            Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 300, 200, true, true);
            detailImageView.setImage(fallback);
        }
    }

    @FXML
    public void showMonumentList() throws IOException {
        loadScene("Monument.fxml", null);
    }

    private void loadScene(String fxml, Button sourceButton) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/BackOffice/Monument/" + fxml));
        Stage stage;
        if (sourceButton != null) {
            stage = (Stage) sourceButton.getScene().getWindow();
        } else if (monumentCards != null) {
            stage = (Stage) monumentCards.getScene().getWindow();
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
        return true;
    }

    private Stage getCurrentStage() {
        return (Stage) (monumentCards != null ? monumentCards.getScene().getWindow() :
                nameField != null ? nameField.getScene().getWindow() :
                        nameLabel.getScene().getWindow());
    }
}