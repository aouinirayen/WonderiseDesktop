package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tn.esprit.models.Experience;
import tn.esprit.services.ExperienceService;
import tn.esprit.utils.ResponsiveUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListExperienceController extends BaseController {
    @FXML
    private TextField searchField;
    
    @FXML
    private FlowPane experiencesContainer;

    private final ExperienceService experienceService = new ExperienceService();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void initializeSpecific() {
        // Définir le titre de l'interface
        setHeaderTitle("Liste des Expériences");
        
        setupSearch();
        loadExperiences();
        
        // Adapter l'interface à la taille de l'écran
        adaptToScreenSize();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                loadExperiences();
            } else {
                filterExperiences(newValue);
            }
        });
    }
    public static class ImageUtils {
        public static Image loadImageSafe(String resourcePath) {
            try {
                InputStream stream = ImageUtils.class.getResourceAsStream(resourcePath);
                if (stream == null) {
                    System.err.println("Image not found: " + resourcePath);
                    return createPlaceholderImage();
                }
                return new Image(stream);
            } catch (Exception e) {
                System.err.println("Error loading image: " + resourcePath);
                e.printStackTrace();
                return createPlaceholderImage();
            }
        }

        private static Image createPlaceholderImage() {
            int width = 100;
            int height = 100;
            WritableImage img = new WritableImage(width, height);
            PixelWriter writer = img.getPixelWriter();

            // Create checkerboard pattern
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color color = ((x / 20 + y / 20) % 2 == 0) ? Color.LIGHTGRAY : Color.GRAY;
                    writer.setColor(x, y, color);
                }
            }
            return img;
        }
    }
    private void filterExperiences(String searchText) {
        try {
            experiencesContainer.getChildren().clear();
            List<Experience> experiences = experienceService.readAll();
            for (Experience experience : experiences) {
                if (matchesSearch(experience, searchText.toLowerCase())) {
                    addExperienceCard(experience);
                }
            }
        } catch (SQLException e) {
            showError("Erreur lors de la recherche : " + e.getMessage());
        }
    }

    private boolean matchesSearch(Experience experience, String searchText) {
        return experience.getTitre().toLowerCase().contains(searchText) ||
               experience.getDescription().toLowerCase().contains(searchText) ||
               experience.getLieu().toLowerCase().contains(searchText) ||
               experience.getCategorie().toLowerCase().contains(searchText);
    }

    @FXML
    private void loadExperiences() {
        try {
            experiencesContainer.getChildren().clear();
            List<Experience> experiences = experienceService.readAll();
            for (Experience experience : experiences) {
                addExperienceCard(experience);
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement des expériences : " + e.getMessage());
        }
    }

    private void addExperienceCard(Experience experience) {
        VBox card = new VBox();
        card.getStyleClass().add("experience-card");

        // Image
        ImageView imageView = new ImageView();
        try {
            // Essayer de charger l'image depuis le chemin stocké dans l'expérience
            Image experienceImage;
            if (experience.getImage() != null && !experience.getImage().isEmpty()) {
                experienceImage = new Image(getClass().getResourceAsStream(experience.getImage()));
                if (experienceImage.isError()) {
                    throw new Exception("Image not found: " + experience.getImage());
                }
            } else {
                throw new Exception("No image path specified");
            }
            imageView.setImage(experienceImage);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            // Utiliser une image par défaut
            Image fallbackImage = ImageUtils.loadImageSafe("/images/placeholder.jpg");
            imageView.setImage(fallbackImage);
        }
        imageView.getStyleClass().add("experience-image");
        imageView.setFitWidth(300);
        imageView.setFitHeight(200);

        // Content container
        VBox content = new VBox();
        content.getStyleClass().add("experience-content");

        Label title = new Label(experience.getTitre());
        title.getStyleClass().add("experience-title");

        HBox categoryBox = new HBox();
        categoryBox.setSpacing(5);
        Label category = new Label(experience.getCategorie());
        category.getStyleClass().add("experience-category");
        categoryBox.getChildren().add(category);

        Label location = new Label(experience.getLieu());
        location.getStyleClass().add("experience-location");

        Label date = new Label("Publié le " + experience.getDateCreation().format(dateFormatter));
        date.getStyleClass().add("experience-date");

        Label description = new Label(experience.getDescription());
        description.getStyleClass().add("experience-description");
        description.setWrapText(true);
        description.setMaxWidth(280);

        // Button container
        HBox buttonContainer = new HBox();
        buttonContainer.getStyleClass().add("button-container");
        buttonContainer.setSpacing(10);

        Button readMoreBtn = new Button("Détails");
        readMoreBtn.getStyleClass().add("read-more-button");
        readMoreBtn.setOnAction(e -> showExperienceDetails(experience));

        Button editBtn = new Button("Modifier");
        editBtn.getStyleClass().add("edit-button");
        editBtn.setOnAction(e -> showUpdateExperience(experience));

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.getStyleClass().add("delete-button");
        deleteBtn.setOnAction(e -> handleDeleteExperience(experience));

        buttonContainer.getChildren().addAll(readMoreBtn, editBtn, deleteBtn);

        content.getChildren().addAll(title, categoryBox, location, date, description, buttonContainer);
        card.getChildren().addAll(imageView, content);

        experiencesContainer.getChildren().add(card);
    }

    @FXML
    private void handleAddExperience() {
        navigateTo("/fxml/AddExperience.fxml", "Ajouter une Expérience");
    }

    private void showExperienceDetails(Experience experience) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExperienceDetails.fxml"));
            Parent root = loader.load();
            
            ExperienceDetailsController controller = loader.getController();
            controller.setExperience(experience);
            controller.setOnBackAction(() -> loadExperiences());
            
            Stage currentStage = getCurrentStage();
            if (currentStage != null) {
                ResponsiveUtils.setupResponsiveStage(currentStage, "Détails de l'Expérience", root);
            }
        } catch (IOException e) {
            showError("Erreur lors de l'affichage des détails : " + e.getMessage());
        }
    }

    private void showUpdateExperience(Experience experience) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UpdateExperience.fxml"));
            Parent root = loader.load();
            
            UpdateExperienceController controller = loader.getController();
            controller.setExperience(experience);
            
            Stage currentStage = getCurrentStage();
            if (currentStage != null) {
                ResponsiveUtils.setupResponsiveStage(currentStage, "Modifier l'Expérience", root);
            }
        } catch (IOException e) {
            showError("Erreur lors de l'ouverture du formulaire de modification : " + e.getMessage());
        }
    }

    private void handleDeleteExperience(Experience experience) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette expérience ?");
        alert.setContentText("Cette action est irréversible.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                experienceService.delete(experience.getId());
                loadExperiences();
                showSuccess("Expérience supprimée avec succès !");
            } catch (SQLException e) {
                showError("Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
