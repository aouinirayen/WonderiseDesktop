package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.esprit.models.Experience;
import tn.esprit.services.ExperienceService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class ExperienceFormController {
    @FXML private Label formTitle;
    @FXML private TextField titleField;
    @FXML private TextField locationField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker datePicker;
    @FXML private TextField imageUrlField;
    @FXML private ImageView imagePreview;
    @FXML private ComboBox<String> categoryField;
    @FXML private Button submitButton;

    private Experience experience;
    private Consumer<Void> afterSaveCallback;
    private final ExperienceService experienceService = new ExperienceService();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        // Configure date picker format
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() 
                    ? LocalDate.parse(string, dateFormatter) 
                    : null;
            }
        });

        // Initialize categories
        categoryField.getItems().addAll(
            "Voyage", "Aventure"
        );
        
        // Set default date to today
        datePicker.setValue(LocalDate.now());

        // Add listener to imageUrlField to automatically preview image when URL changes
        imageUrlField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                handlePreviewImage();
            }
        });
    }
    
    public void setExperience(Experience experience) {
        this.experience = experience;
        formTitle.setText(experience != null ? "Edit Experience" : "Create New Experience");
        
        if (experience != null) {
            titleField.setText(experience.getTitre());
            locationField.setText(experience.getLieu());
            descriptionField.setText(experience.getDescription());
            datePicker.setValue(experience.getDate());
            imageUrlField.setText(experience.getImage());
            categoryField.setValue(experience.getCategorie());
        }
    }
    
    public void setAfterSaveCallback(Consumer<Void> callback) {
        this.afterSaveCallback = callback;
    }
    
    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        
        try {
            Experience updatedExperience = new Experience(
                experience != null ? experience.getId() : 0,
                titleField.getText(),
                descriptionField.getText(),
                locationField.getText(),
                datePicker.getValue(),
                imageUrlField.getText(),
                categoryField.getValue()
            );
            
            if (experience == null) {
                experienceService.add(updatedExperience);
            } else {
                experienceService.update(updatedExperience);
            }
            
            if (afterSaveCallback != null) {
                afterSaveCallback.accept(null);
            }
        } catch (SQLException e) {
            showError("Error saving experience: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel() {
        if (afterSaveCallback != null) {
            afterSaveCallback.accept(null);
        }
    }
    
    @FXML
    private void handleBack() {
        handleCancel();
    }
    
    private boolean validateForm() {
        String errorMessage = "";
        
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            errorMessage += "Title is required\n";
        }
        
        if (locationField.getText() == null || locationField.getText().trim().isEmpty()) {
            errorMessage += "Location is required\n";
        }
        
        if (descriptionField.getText() == null || descriptionField.getText().trim().isEmpty()) {
            errorMessage += "Description is required\n";
        }
        
        if (datePicker.getValue() == null) {
            errorMessage += "Date is required\n";
        }
        
        if (categoryField.getValue() == null) {
            errorMessage += "Category is required\n";
        }
        
        if (!errorMessage.isEmpty()) {
            showError(errorMessage);
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handlePreviewImage() {
        String imageUrl = imageUrlField.getText().trim();
        if (!imageUrl.isEmpty()) {
            try {
                Image image = new Image(imageUrl, true); // true enables background loading
                image.errorProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        showError("Invalid image URL or image could not be loaded");
                        imagePreview.setImage(getDefaultImage());
                    }
                });
                imagePreview.setImage(image);
            } catch (Exception e) {
                showError("Error loading image: " + e.getMessage());
                imagePreview.setImage(getDefaultImage());
            }
        } else {
            imagePreview.setImage(getDefaultImage());
        }
    }

    private Image getDefaultImage() {
        return new Image(getClass().getResourceAsStream("/images/placeholder.jpg"));
    }
}
