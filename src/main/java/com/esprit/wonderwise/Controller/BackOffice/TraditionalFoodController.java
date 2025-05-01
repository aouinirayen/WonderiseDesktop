package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.TraditionalFood;
import com.esprit.wonderwise.Model.Country;
import com.esprit.wonderwise.Service.TraditionalFoodService;
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

public class TraditionalFoodController {
    @FXML private FlowPane foodCards;
    @FXML private TextField nameField, imgPathField, descField, recipeField;
    @FXML private ComboBox<Country> countryComboBox;
    @FXML private Label nameLabel, descLabel, recipeLabel;
    @FXML private ImageView detailImageView;
    // Advanced search fields
    @FXML private TextField searchField;
    @FXML private ComboBox<String> countryCombo;

    private TraditionalFoodService foodService = new TraditionalFoodService();
    private CountryService countryService = new CountryService();
    private TraditionalFood selectedFood;
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    @FXML
    public void initialize() {
        if (foodCards != null) {
            setupAdvancedSearch();
            loadFoods(foodService.readAll());
        }
        if (countryComboBox != null) {
            loadCountries();
        }
        if (descField != null) {
            descField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.length() > 1000) descField.setText(oldVal);
            });
        }
        if (recipeField != null) {
            recipeField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.length() > 2000) recipeField.setText(oldVal);
            });
        }
    }

    private void setupAdvancedSearch() {
        if (searchField == null || countryCombo == null) return;
        // Populate countryCombo
        countryCombo.getItems().clear();
        countryCombo.getItems().add("All");
        for (Country c : countryService.readAll()) {
            countryCombo.getItems().add(c.getName());
        }
        countryCombo.getSelectionModel().selectFirst();

        // Add listeners for live search
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyAdvancedSearch());
        countryCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyAdvancedSearch());
    }

    private void applyAdvancedSearch() {
        if (searchField == null || countryCombo == null) return;
        String keyword = searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";
        String selectedCountry = countryCombo.getValue();
        List<TraditionalFood> allFoods = foodService.readAll();
        List<TraditionalFood> filtered = allFoods.stream().filter(food -> {
            boolean matchesKeyword = keyword.isEmpty() ||
                    (food.getName() != null && food.getName().toLowerCase().contains(keyword)) ||
                    (food.getDescription() != null && food.getDescription().toLowerCase().contains(keyword));
            boolean matchesCountry = selectedCountry == null || selectedCountry.equals("All");
            if (!matchesCountry) {
                Country country = countryService.getById(food.getCountryId());
                matchesCountry = country != null && selectedCountry.equals(country.getName());
            }
            return matchesKeyword && matchesCountry;
        }).toList();
        loadFoods(filtered);
    }

    private void loadFoods(List<TraditionalFood> foods) {
        foodCards.getChildren().clear();
        if (foods == null || foods.isEmpty()) {
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

            Label mainText = new Label("No foods found");
            mainText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
            Label subText = new Label("Try adjusting your search or add a new food.");
            subText.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

            indicator.getChildren().addAll(icon, mainText, subText);
            foodCards.getChildren().add(indicator);
            return;
        }
        for (TraditionalFood food : foods) {
            VBox card = new VBox(15);
            card.getStyleClass().add("country-card");
            card.setPrefWidth(250);
            card.setPrefHeight(300);
            card.setAlignment(Pos.CENTER);

            ImageView imageView = new ImageView();
            File imageFile = new File(IMAGE_DESTINATION_DIR + food.getImg());
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

            Label name = new Label(food.getName());
            name.setStyle("-fx-font-size: 18px; -fx-text-fill: #2C3E50; -fx-font-weight: bold;");

            Country country = countryService.getById(food.getCountryId());
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
                    showDetails(food);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            editBtn.setOnAction(e -> {
                try {
                    showEditFood(food);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            deleteBtn.setOnAction(e -> {
                try {
                    deleteFood(food.getId());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            buttons.getChildren().addAll(detailsBtn, editBtn, deleteBtn);
            card.getChildren().addAll(imageView, name, snippet, buttons);
            foodCards.getChildren().add(card);
        }
    }

    private void loadFoods() {
        foodCards.getChildren().clear();
        List<TraditionalFood> foods = foodService.readAll();
        for (TraditionalFood food : foods) {
            VBox card = new VBox(15);
            card.getStyleClass().add("country-card");
            card.setPrefWidth(250);
            card.setPrefHeight(300);
            card.setAlignment(Pos.CENTER);

            ImageView imageView = new ImageView();
            File imageFile = new File(IMAGE_DESTINATION_DIR + food.getImg());
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

            Label name = new Label(food.getName());
            name.setStyle("-fx-font-size: 18px; -fx-text-fill: #2C3E50; -fx-font-weight: bold;");

            Country country = countryService.getById(food.getCountryId());
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
                    showDetails(food);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            editBtn.setOnAction(e -> {
                try {
                    showEditFood(food);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            deleteBtn.setOnAction(e -> {
                try {
                    deleteFood(food.getId());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            buttons.getChildren().addAll(detailsBtn, editBtn, deleteBtn);
            card.getChildren().addAll(imageView, name, snippet, buttons);
            foodCards.getChildren().add(card);
        }
    }

    private void loadCountries() {
        countryComboBox.getItems().clear();
        List<Country> countries = countryService.readAll();
        countryComboBox.getItems().addAll(countries);
    }

    @FXML
    public void showAddFood() throws IOException {
        loadScene("TraditionalFoodAdd.fxml", null);
    }

    @FXML
    public void addFood() throws IOException {
        if (!validateInputs()) return;

        String imageFileName = copyImageToDestination(imgPathField.getText());
        TraditionalFood food = new TraditionalFood(
                0,
                countryComboBox.getValue().getId(),
                nameField.getText(),
                imageFileName,
                descField.getText(),
                recipeField.getText()
        );
        foodService.add(food);
        DialogUtils.showCustomDialog("Success", "Traditional food added successfully!", true, getCurrentStage());
        showFoodList();
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
    public void showEditFood(TraditionalFood food) throws IOException {
        selectedFood = food;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/TraditionalFood/TraditionalFoodEdit.fxml"));
        Parent root = loader.load();
        TraditionalFoodController controller = loader.getController();
        controller.setFoodData(food);
        controller.selectedFood = food;
        Stage stage = (Stage) (foodCards != null ? foodCards.getScene().getWindow() : nameField.getScene().getWindow());
        stage.setScene(new Scene(root));
    }

    @FXML
    public void updateFood() throws IOException {
        if (selectedFood == null) {
            DialogUtils.showCustomDialog("Error", "No food selected for update.", false, getCurrentStage());
            return;
        }
        if (!validateInputs()) return;

        String imageFileName = copyImageToDestination(imgPathField.getText());
        selectedFood.setCountryId(countryComboBox.getValue().getId());
        selectedFood.setName(nameField.getText());
        selectedFood.setImg(imageFileName);
        selectedFood.setDescription(descField.getText());
        selectedFood.setRecipe(recipeField.getText());
        foodService.update(selectedFood);
        DialogUtils.showCustomDialog("Success", "Traditional food updated successfully!", true, getCurrentStage());
        showFoodList();
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

    public void setFoodData(TraditionalFood food) {
        nameField.setText(food.getName());
        imgPathField.setText(IMAGE_DESTINATION_DIR + food.getImg());
        descField.setText(food.getDescription());
        recipeField.setText(food.getRecipe());
        Country country = countryService.getById(food.getCountryId());
        if (country != null) {
            countryComboBox.setValue(country);
        }
    }

    @FXML
    public void deleteFood(int id) throws IOException {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this traditional food?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            foodService.delete(id);
            DialogUtils.showCustomDialog("Success", "Traditional food deleted successfully!", true, getCurrentStage());
            showFoodList();
        }
    }

    @FXML
    public void showDetails(TraditionalFood food) throws IOException {
        selectedFood = food;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/TraditionalFood/TraditionalFoodDetails.fxml"));
        Parent root = loader.load();
        TraditionalFoodController controller = loader.getController();
        controller.setDetailsData(food);
        Stage stage = (Stage) (foodCards != null ? foodCards.getScene().getWindow() : nameLabel.getScene().getWindow());
        stage.setScene(new Scene(root));
    }

    public void setDetailsData(TraditionalFood food) {
        nameLabel.setText(food.getName());
        descLabel.setText(food.getDescription());
        recipeLabel.setText(food.getRecipe());

        File imageFile = new File(IMAGE_DESTINATION_DIR + food.getImg());
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString(), 300, 200, true, true);
            detailImageView.setImage(image);
        } else {
            Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 300, 200, true, true);
            detailImageView.setImage(fallback);
        }
    }

    @FXML
    public void showFoodList() throws IOException {
        loadScene("TraditionalFood.fxml", null);
    }

    private void loadScene(String fxml, Button sourceButton) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/BackOffice/TraditionalFood/" + fxml));
        Stage stage;
        if (sourceButton != null) {
            stage = (Stage) sourceButton.getScene().getWindow();
        } else if (foodCards != null) {
            stage = (Stage) foodCards.getScene().getWindow();
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
        if (descField.getText().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Description cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (recipeField.getText().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Recipe cannot be empty.", false, getCurrentStage());
            return false;
        }
        return true;
    }

    private Stage getCurrentStage() {
        return (Stage) (foodCards != null ? foodCards.getScene().getWindow() :
                nameField != null ? nameField.getScene().getWindow() :
                        nameLabel.getScene().getWindow());
    }
}