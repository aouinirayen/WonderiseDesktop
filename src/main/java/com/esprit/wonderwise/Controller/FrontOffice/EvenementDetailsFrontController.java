package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.Commentaire;
import com.esprit.wonderwise.Model.Evenement;
import com.esprit.wonderwise.Service.CommentaireService;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class EvenementDetailsFrontController {

    @FXML private Label nomLabel, dateLabel, heureLabel, lieuLabel, placeMaxLabel, prixLabel, categorieLabel, likesCountLabel;
    @FXML private Text descriptionLabel;
    @FXML private ImageView photoView;
    @FXML private TextField commentField;
    @FXML private Button addCommentButton;
    @FXML private FlowPane commentsFlowPane;
    private Evenement selectedEvenement;
    private CommentaireService commentaireService = new CommentaireService();
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    public void setDetailsData(Evenement evenement) {
        selectedEvenement = evenement;
        nomLabel.setText(evenement.getNom());
        dateLabel.setText(evenement.getDate().toString());
        heureLabel.setText(evenement.getHeure().toString());
        lieuLabel.setText(evenement.getLieu());
        descriptionLabel.setText(evenement.getDescription());
        placeMaxLabel.setText(String.valueOf(evenement.getPlaceMax()));
        prixLabel.setText(String.valueOf(evenement.getPrix()));
        categorieLabel.setText(evenement.getCategorie());
        likesCountLabel.setText(String.valueOf(evenement.getLikesCount()));

        File imageFile = new File(IMAGE_DESTINATION_DIR + evenement.getPhoto());
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString(), 800, 250, true, true);
            photoView.setImage(image);
        } else {
            Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 800, 250, true, true);
            photoView.setImage(fallback);
        }

        // Add rounded corners to the photo
        photoView.setFitWidth(450);
        photoView.setFitHeight(300);
        photoView.setPreserveRatio(false);
        Rectangle clip = new Rectangle(450, 300);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        photoView.setClip(clip);

        loadComments();
    }

    private void loadComments() {
        commentsFlowPane.getChildren().clear();
        List<Commentaire> commentaires = commentaireService.readByEvenementId(selectedEvenement.getId());
        for (Commentaire commentaire : commentaires) {
            VBox commentCard = new VBox(10);
            commentCard.setPrefWidth(320);
            commentCard.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-padding: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 15; -fx-border-width: 1;");
            commentCard.setOnMouseEntered(e -> commentCard.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 15; -fx-padding: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 15; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);"));
            commentCard.setOnMouseExited(e -> commentCard.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-padding: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 15; -fx-border-width: 1;"));

            Text commentText = new Text(commentaire.getCommentaire());
            commentText.setWrappingWidth(280);
            commentText.setStyle("-fx-font-size: 14px; -fx-fill: #1A202C; -fx-font-family: 'Inter', 'Arial', sans-serif;");

            Label dateLabel = new Label(commentaire.getDate().toString().replace("T", " "));
            dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096; -fx-font-family: 'Inter', 'Arial', sans-serif;");

            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            Button editButton = new Button("Edit");
            editButton.setStyle("-fx-background-color: #63B3ED; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif;");
            editButton.setOnMouseEntered(e -> editButton.setStyle("-fx-background-color: #4299E1; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif;"));
            editButton.setOnMouseExited(e -> editButton.setStyle("-fx-background-color: #63B3ED; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif;"));
            editButton.setOnAction(e -> editComment(commentaire));

            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #F687B3; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif;");
            deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #F56565; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif;"));
            deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #F687B3; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif;"));
            deleteButton.setOnAction(e -> deleteComment(commentaire.getId()));

            buttonBox.getChildren().addAll(editButton, deleteButton);
            commentCard.getChildren().addAll(commentText, dateLabel, buttonBox);
            commentsFlowPane.getChildren().add(commentCard);
        }
    }

    @FXML
    public void addComment() {
        String commentText = commentField.getText().trim();
        if (!commentText.isEmpty()) {
            Commentaire commentaire = new Commentaire(selectedEvenement.getId(), commentText, LocalDateTime.now());
            commentaireService.create(commentaire);
            commentField.clear();
            loadComments();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty Comment");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a comment before posting.");
            alert.showAndWait();
        }
    }

    private void editComment(Commentaire commentaire) {
        // Create a custom dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Comment");
        dialog.setHeaderText("Update Your Comment");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Create a VBox for the content
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5);");

        // Add a styled label
        Label promptLabel = new Label("Edit your comment below:");
        promptLabel.setStyle("-fx-font-family: 'Inter', 'Arial', sans-serif; -fx-font-size: 16px; -fx-text-fill: #1A202C; -fx-font-weight: bold;");

        // Add a beautiful TextArea
        TextArea commentTextArea = new TextArea(commentaire.getCommentaire());
        commentTextArea.setWrapText(true);
        commentTextArea.setPrefRowCount(4);
        commentTextArea.setPrefColumnCount(30);
        commentTextArea.setStyle("-fx-font-family: 'Inter', 'Arial', sans-serif; " +
                "-fx-font-size: 14px; " +
                "-fx-background-color: #F7FAFC; " +
                "-fx-text-fill: #2D3748; " +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10; " +
                "-fx-border-color: #CBD5E0; " +
                "-fx-padding: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2); " +
                "-fx-control-inner-background: #F7FAFC;");

        // Add components to VBox
        vbox.getChildren().addAll(promptLabel, commentTextArea);

        // Set the content of the dialog
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().setStyle("-fx-font-family: 'Inter', 'Arial', sans-serif; -fx-background-color: #F7FAFC; -fx-background-radius: 15; -fx-border-radius: 15;");

        // Style the Save button
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setStyle("-fx-background-color: #5A67D8; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        saveButton.setOnMouseEntered(e -> saveButton.setStyle("-fx-background-color: #434190; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"));
        saveButton.setOnMouseExited(e -> saveButton.setStyle("-fx-background-color: #5A67D8; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"));

        // Style the Cancel button with color
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.setStyle("-fx-background-color: #F687B3; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #F56565; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: #F687B3; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"));

        // Enable/Disable Save button based on text input
        saveButton.disableProperty().bind(commentTextArea.textProperty().isEmpty());

        // Set the result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return commentTextArea.getText();
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newText -> {
            commentaire.setCommentaire(newText);
            commentaire.setDate(LocalDateTime.now());
            commentaireService.update(commentaire);
            loadComments();
        });
    }

    private void deleteComment(int commentId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Comment");
        alert.setHeaderText("Are you sure you want to delete this comment?");
        alert.setContentText("This action cannot be undone.");
        alert.getDialogPane().setStyle("-fx-font-family: 'Inter', 'Arial', sans-serif;");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            commentaireService.delete(commentId);
            loadComments();
        }
    }

    @FXML
    public void goBack() throws IOException {
        GuideDetailsFrontController parentController = (GuideDetailsFrontController) photoView.getScene().getRoot().getUserData();
        Stage stage = (Stage) nomLabel.getScene().getWindow();

        if (parentController != null) {
            // Coming from Guide details page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Guide/GuideDetailsFront.fxml"));
            Parent root = loader.load();
            GuideDetailsFrontController controller = loader.getController();
            controller.setDetailsData(parentController.selectedGuide);
            stage.setScene(new Scene(root));
        } else {
            // Coming from Event list page
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Evenement/EvenementListFront.fxml"));
            stage.setScene(new Scene(root));
        }
    }
}