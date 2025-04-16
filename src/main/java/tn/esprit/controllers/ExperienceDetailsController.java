package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.models.Commentaire;
import tn.esprit.models.Experience;
import tn.esprit.services.CommentaireService;
import tn.esprit.services.ExperienceService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class ExperienceDetailsController {
    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label locationLabel;
    @FXML private Label categoryLabel;
    @FXML private ImageView experienceImage;
    @FXML private Label descriptionLabel;
    @FXML private TextField nameField;
    @FXML private TextArea commentField;
    @FXML private VBox commentsContainer;
    @FXML private Button backButton;

    private Experience experience;
    private final ExperienceService experienceService = new ExperienceService();
    private final CommentaireService commentaireService = new CommentaireService();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private Runnable onBackAction;

    public void setExperience(Experience experience) {
        this.experience = experience;
        displayExperienceDetails();
        loadComments();
    }

    public void setOnBackAction(Runnable action) {
        this.onBackAction = action;
    }

    private void displayExperienceDetails() {
        titleLabel.setText(experience.getTitre());
        dateLabel.setText(experience.getDate().format(dateFormatter));
        locationLabel.setText(experience.getLieu());
        categoryLabel.setText(experience.getCategorie());
        descriptionLabel.setText(experience.getDescription());

        try {
            Image image = new Image(experience.getImage());
            experienceImage.setImage(image);
        } catch (Exception e) {
            // Use placeholder image if URL is invalid
            Image placeholder = new Image(getClass().getResourceAsStream("/images/placeholder.jpg"));
            experienceImage.setImage(placeholder);
        }
    }

    private void loadComments() {
        try {
            commentsContainer.getChildren().clear();
            var comments = commentaireService.getAll();
            if (comments.isEmpty()) {
                Label noComments = new Label("No comments yet. Be the first to comment!");
                noComments.getStyleClass().add("no-comments");
                commentsContainer.getChildren().add(noComments);
            } else {
                for (Commentaire comment : comments) {
                    addCommentToView(comment);
                }
            }
        } catch (SQLException e) {
            showError("Error loading comments: " + e.getMessage());
        }
    }

    private void addCommentToView(Commentaire comment) {
        VBox commentBox = new VBox(5);
        commentBox.getStyleClass().add("comment-box");

        Label authorLabel = new Label(comment.getAuteur());
        authorLabel.getStyleClass().add("comment-author");

        Label contentLabel = new Label(comment.getContenu());
        contentLabel.getStyleClass().add("comment-content");
        contentLabel.setWrapText(true);

        commentBox.getChildren().addAll(authorLabel, contentLabel);
        commentsContainer.getChildren().add(commentBox);
    }

    @FXML
    private void handleSubmitComment() {
        if (validateCommentForm()) {
            Commentaire comment = new Commentaire(
                commentField.getText(),
                nameField.getText()
            );

            try {
                commentaireService.add(comment);
                clearCommentForm();
                loadComments();
            } catch (SQLException e) {
                showError("Error adding comment: " + e.getMessage());
            }
        }
    }

    private boolean validateCommentForm() {
        if (nameField.getText().isEmpty()) {
            showError("Please enter your name");
            return false;
        }
        if (commentField.getText().isEmpty()) {
            showError("Please enter your comment");
            return false;
        }
        return true;
    }

    private void clearCommentForm() {
        nameField.clear();
        commentField.clear();
    }

    @FXML
    private void handleBackToList() {
        closeWindow();
    }

    @FXML
    private void handleBack() {
        if (onBackAction != null) {
            onBackAction.run();
        }
    }

    @FXML
    private void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UpdateExperience.fxml"));
            Parent root = loader.load();
            
            UpdateExperienceController controller = loader.getController();
            controller.setExperience(experience);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Experience");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh details after edit
            experience = experienceService.getById(experience.getId());
            displayExperienceDetails();
        } catch (IOException | SQLException e) {
            showError("Error opening edit form: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Experience");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                experienceService.delete(experience.getId());
                closeWindow();
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

    private void closeWindow() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}
