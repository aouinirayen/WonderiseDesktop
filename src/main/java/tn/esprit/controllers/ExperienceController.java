package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.BorderPane;
import tn.esprit.models.Experience;
import tn.esprit.services.ExperienceService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ExperienceController {
    @FXML private TextField searchField;
    @FXML private VBox experiencesContainer;
    @FXML private BorderPane mainContainer;
    
    private final ExperienceService experienceService = new ExperienceService();

    @FXML
    public void initialize() {
        loadExperiences();
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterExperiences(newValue);
        });
    }

    private void loadExperiences() {
        try {
            List<Experience> experiences = experienceService.getAll();
            displayExperiences(experiences);
        } catch (SQLException e) {
            showError("Error loading experiences: " + e.getMessage());
        }
    }

    private void filterExperiences(String searchText) {
        try {
            List<Experience> allExperiences = experienceService.getAll();
            List<Experience> filteredExperiences = allExperiences.stream()
                .filter(exp -> exp.getTitre().toLowerCase().contains(searchText.toLowerCase()) ||
                             exp.getDescription().toLowerCase().contains(searchText.toLowerCase()) ||
                             exp.getLieu().toLowerCase().contains(searchText.toLowerCase()))
                .toList();
            displayExperiences(filteredExperiences);
        } catch (SQLException e) {
            showError("Error filtering experiences: " + e.getMessage());
        }
    }

    private void displayExperiences(List<Experience> experiences) {
        experiencesContainer.getChildren().clear();
        
        if (experiences.isEmpty()) {
            Label noExperiences = new Label("No experiences found");
            noExperiences.getStyleClass().add("no-experiences");
            experiencesContainer.getChildren().add(noExperiences);
            return;
        }

        for (Experience experience : experiences) {
            experiencesContainer.getChildren().add(createExperienceCard(experience));
        }
    }

    private VBox createExperienceCard(Experience experience) {
        VBox card = new VBox(10);
        card.getStyleClass().add("experience-card");

        // Header with title and menu button
        HBox header = new HBox(10);
        header.getStyleClass().add("card-header");
        
        Label title = new Label(experience.getTitre());
        title.getStyleClass().add("experience-title");
        
        MenuButton menuButton = new MenuButton("•••");
        menuButton.getStyleClass().add("menu-button");
        
        MenuItem editItem = new MenuItem("Modifier");
        editItem.setOnAction(e -> handleEditExperience(experience));
        
        MenuItem deleteItem = new MenuItem("Supprimer");
        deleteItem.setOnAction(e -> handleDeleteExperience(experience));
        
        menuButton.getItems().addAll(editItem, deleteItem);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, menuButton);

        Label date = new Label("Published on " + experience.getDate());
        date.getStyleClass().add("experience-date");

        Label description = new Label(experience.getDescription());
        description.getStyleClass().add("experience-description");

        Button readMore = new Button("Voir détails");
        readMore.getStyleClass().add("read-more-button");
        readMore.setOnAction(e -> handleViewExperience(experience));

        card.getChildren().addAll(header, date, description, readMore);
        return card;
    }

    @FXML
    private void handleAddExperience() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExperienceForm.fxml"));
            Parent root = loader.load();
            
            ExperienceFormController controller = loader.getController();
            controller.setAfterSaveCallback(v -> {
                loadExperiences();
                mainContainer.setCenter(experiencesContainer);
            });
            
            mainContainer.setCenter(root);
        } catch (IOException e) {
            showError("Error loading form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleViewExperience(Experience experience) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExperienceDetails.fxml"));
            Parent root = loader.load();
            
            ExperienceDetailsController controller = loader.getController();
            controller.setExperience(experience);
            controller.setOnBackAction(() -> mainContainer.setCenter(experiencesContainer));
            
            mainContainer.setCenter(root);
        } catch (IOException e) {
            showError("Error viewing experience: " + e.getMessage());
        }
    }

    private void handleEditExperience(Experience experience) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExperienceForm.fxml"));
            Parent root = loader.load();
            
            ExperienceFormController controller = loader.getController();
            controller.setExperience(experience);
            controller.setAfterSaveCallback(v -> {
                loadExperiences();
                mainContainer.setCenter(experiencesContainer);
            });
            
            mainContainer.setCenter(root);
        } catch (IOException e) {
            showError("Error loading form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteExperience(Experience experience) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Experience");
        alert.setContentText("Are you sure you want to delete this experience?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                experienceService.delete(experience.getId());
                loadExperiences();
            } catch (SQLException e) {
                showError("Error deleting experience: " + e.getMessage());
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
