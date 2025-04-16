package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import tn.esprit.models.Experience;
import tn.esprit.services.ExperienceService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UpdateExperienceController extends BaseController {
    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField imageField;
    @FXML private TextField lieuField;
    @FXML private ComboBox<String> categorieField;
    @FXML private TextField idClientField;
    @FXML private DatePicker datePicker;
    @FXML private ImageView imagePreview;
    @FXML private Button previewButton;
    
    private Map<Control, Label> errorLabels = new HashMap<>();
    private ExperienceService experienceService;
    private Experience experience;
    
    @Override
    protected void initializeSpecific() {
        experienceService = new ExperienceService();
        
        // Définir le titre de l'interface
        setHeaderTitle("Modifier l'Expérience");
        
        // Initialize category ComboBox
        categorieField.getItems().addAll(
            "Voyage", "Culture", "Sport", "Gastronomie", "Aventure"
        );
        
        // Add listener to image field for preview
        imageField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                previewImage();
            }
        });
        
        // Create error labels for each field
        createErrorLabel(titreField, "Le titre est requis");
        createErrorLabel(descriptionArea, "La description est requise");
        createErrorLabel(imageField, "L'URL de l'image est requise");
        createErrorLabel(lieuField, "Le lieu est requis");
        createErrorLabel(categorieField, "La catégorie est requise");
        createErrorLabel(idClientField, "L'ID client est requis et doit être un nombre");
        createErrorLabel(datePicker, "La date est requise");
        
        // Adapter l'interface à la taille de l'écran
        adaptToScreenSize();
    }
    
    private void createErrorLabel(Control field, String defaultMessage) {
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setText(defaultMessage);
        
        // Add error label after the field in the parent container
        if (field.getParent() instanceof VBox) {
            VBox parent = (VBox) field.getParent();
            int index = parent.getChildren().indexOf(field);
            if (index >= 0 && index < parent.getChildren().size()) {
                parent.getChildren().add(index + 1, errorLabel);
            }
        }
        
        errorLabels.put(field, errorLabel);
    }
    
    private void showFieldError(Control field, boolean show) {
        Label errorLabel = errorLabels.get(field);
        if (errorLabel != null) {
            errorLabel.setVisible(show);
            errorLabel.setManaged(show);
            
            if (show) {
                field.getStyleClass().add("error-field");
            } else {
                field.getStyleClass().remove("error-field");
            }
        }
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
        populateFields();
    }

    private void populateFields() {
        if (experience != null) {
            titreField.setText(experience.getTitre());
            descriptionArea.setText(experience.getDescription());
            imageField.setText(experience.getImage());
            lieuField.setText(experience.getLieu());
            categorieField.setValue(experience.getCategorie());
            idClientField.setText(String.valueOf(experience.getIdClient()));
            datePicker.setValue(experience.getDate());
            
            // Load image preview
            previewImage();
        }
    }
    
    @FXML
    private void previewImage() {
        try {
            String imageUrl = imageField.getText().trim();
            if (!imageUrl.isEmpty()) {
                Image image = new Image(imageUrl, true);
                imagePreview.setImage(image);
                
                // Handle loading errors
                image.errorProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        showDefaultImage();
                    }
                });
            } else {
                showDefaultImage();
            }
        } catch (Exception e) {
            showDefaultImage();
        }
    }
    
    private void showDefaultImage() {
        try {
            Image placeholder = new Image(getClass().getResourceAsStream("/images/placeholder.jpg"));
            imagePreview.setImage(placeholder);
        } catch (Exception e) {
            // If placeholder can't be loaded, just clear the image
            imagePreview.setImage(null);
        }
    }

    @FXML
    private void handleUpdate() {
        // Reset all error states
        errorLabels.keySet().forEach(field -> showFieldError(field, false));
        
        if (validateInputs()) {
            try {
                experience.setTitre(titreField.getText().trim());
                experience.setDescription(descriptionArea.getText().trim());
                experience.setImage(imageField.getText().trim());
                experience.setLieu(lieuField.getText().trim());
                experience.setCategorie(categorieField.getValue());
                experience.setIdClient(Integer.parseInt(idClientField.getText().trim()));
                experience.setDate(datePicker.getValue());

                experienceService.update(experience);
                showSuccess("Expérience mise à jour avec succès !");
                navigateTo("/fxml/ListExperience.fxml", "Liste des Expériences");
            } catch (NumberFormatException e) {
                showFieldError(idClientField, true);
            } catch (SQLException e) {
                showError("Erreur lors de la modification: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        navigateTo("/fxml/ListExperience.fxml", "Liste des Expériences");
    }

    private boolean validateInputs() {
        boolean isValid = true;
        
        // Check title
        if (titreField.getText().trim().isEmpty()) {
            showFieldError(titreField, true);
            isValid = false;
        }
        
        // Check description
        if (descriptionArea.getText().trim().isEmpty()) {
            showFieldError(descriptionArea, true);
            isValid = false;
        }
        
        // Check image URL
        if (imageField.getText().trim().isEmpty()) {
            showFieldError(imageField, true);
            isValid = false;
        }
        
        // Check location
        if (lieuField.getText().trim().isEmpty()) {
            showFieldError(lieuField, true);
            isValid = false;
        }
        
        // Check category
        if (categorieField.getValue() == null || categorieField.getValue().trim().isEmpty()) {
            showFieldError(categorieField, true);
            isValid = false;
        }
        
        // Check client ID
        try {
            if (idClientField.getText().trim().isEmpty()) {
                showFieldError(idClientField, true);
                isValid = false;
            } else {
                Integer.parseInt(idClientField.getText().trim());
            }
        } catch (NumberFormatException e) {
            showFieldError(idClientField, true);
            isValid = false;
        }
        
        // Check date
        if (datePicker.getValue() == null) {
            showFieldError(datePicker, true);
            isValid = false;
        }
        
        return isValid;
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
