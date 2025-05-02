package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.Guide;
import com.esprit.wonderwise.Service.GuideService;
import com.esprit.wonderwise.Utils.DialogUtils; // Import DialogUtils
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class GuideBackController {


    @FXML
    public void initialize() {
        if (guideCards != null) {
            if (searchField != null) {
                searchField.textProperty().addListener((obs, oldVal, newVal) -> applySearchFilter());
            }
            loadGuides(null);
        }
        // Add listeners for real-time validation
        if (numTelephoneField != null) {
            numTelephoneField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    numTelephoneField.setText(oldVal); // Only allow digits
                }
            });
        }
    }

    private void loadGuides(String search) {
        guideCards.getChildren().clear();
        List<Guide> guides = guideService.readAll();
        // Filter guides by search
        if (search != null && !search.trim().isEmpty()) {
            String lower = search.toLowerCase();
            guides.removeIf(g -> !(g.getNom().toLowerCase().contains(lower)
                    || g.getPrenom().toLowerCase().contains(lower)
                    || (g.getNumTelephone() != null && g.getNumTelephone().contains(lower))
                    || (g.getEmail() != null && g.getEmail().toLowerCase().contains(lower))));
        }
        for (Guide guide : guides) {
            // Create the card (VBox)
            VBox card = new VBox(10);
            card.setPrefWidth(250);
            card.setAlignment(Pos.TOP_CENTER);
            card.setPadding(new Insets(15));
            // Inline style for the card: white background, rounded corners, shadow
            card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

            // Circular Image
            ImageView imageView = new ImageView();
            File imageFile = new File(IMAGE_DESTINATION_DIR + guide.getPhoto());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString(), 100, 100, true, true);
                imageView.setImage(image);
            } else {
                Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 100, 100, true, true);
                imageView.setImage(fallback);
            }
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            // Inline style for circular image
            imageView.setStyle("-fx-background-radius: 50%; -fx-border-radius: 50%;");

            // Name Label
            Label name = new Label(guide.getNom() + " " + guide.getPrenom());
            // Inline style for name label
            name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-alignment: center;");

            // Email HBox with Icon
            HBox emailBox = new HBox(5);
            emailBox.setAlignment(Pos.CENTER_LEFT);
            ImageView emailIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/email.png"), 16, 16, true, true));
            Label email = new Label(guide.getEmail());
            // Inline style for email label
            email.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
            emailBox.getChildren().addAll(emailIcon, email);

            // Phone HBox with Icon
            HBox phoneBox = new HBox(5);
            phoneBox.setAlignment(Pos.CENTER_LEFT);
            ImageView phoneIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/phone.png"), 16, 16, true, true));
            Label phone = new Label(guide.getNumTelephone());
            // Inline style for phone label
            phone.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
            phoneBox.getChildren().addAll(phoneIcon, phone);

            // Social Media HBox with Icons
            HBox socialBox = new HBox(10);
            socialBox.setAlignment(Pos.CENTER);
            if (guide.getFacebook() != null && !guide.getFacebook().isEmpty()) {
                ImageView facebookIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/facebook.png"), 20, 20, true, true));
                facebookIcon.setOnMouseClicked(e -> {
                    try {
                        java.awt.Desktop.getDesktop().browse(new java.net.URI(guide.getFacebook()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                // Inline style for cursor
                facebookIcon.setStyle("-fx-cursor: hand;");
                socialBox.getChildren().add(facebookIcon);
            }
            if (guide.getInstagram() != null && !guide.getInstagram().isEmpty()) {
                ImageView instagramIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/instagram.png"), 20, 20, true, true));
                instagramIcon.setOnMouseClicked(e -> {
                    try {
                        java.awt.Desktop.getDesktop().browse(new java.net.URI(guide.getInstagram()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                // Inline style for cursor
                instagramIcon.setStyle("-fx-cursor: hand;");
                socialBox.getChildren().add(instagramIcon);
            }

            // Buttons HBox
            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER);

            Button detailsBtn = new Button("Voir");
            // Inline style for details button
            detailsBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;");
            detailsBtn.setOnAction(e -> showGuideDetails(guide));
            // Hover effect
            detailsBtn.setOnMouseEntered(e -> detailsBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-opacity: 0.8;"));
            detailsBtn.setOnMouseExited(e -> detailsBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;"));

            Button editBtn = new Button("Modifier");
            // Inline style for edit button
            editBtn.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;");
            editBtn.setOnAction(e -> showEditGuide(guide));
            // Hover effect
            editBtn.setOnMouseEntered(e -> editBtn.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-opacity: 0.8;"));
            editBtn.setOnMouseExited(e -> editBtn.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;"));

            Button deleteBtn = new Button("Supprimer");
            // Inline style for delete button
            deleteBtn.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;");
            deleteBtn.setOnAction(e -> {
                deleteGuide(guide);
            });
            // Hover effect
            deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-opacity: 0.8;"));
            deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 5;"));

            buttons.getChildren().addAll(detailsBtn, editBtn, deleteBtn);

            // Add all elements to the card
            card.getChildren().addAll(imageView, name, emailBox, phoneBox, socialBox, buttons);
            guideCards.getChildren().add(card);
        }
    }

    @FXML
    public void showAddGuide() throws IOException {
        loadScene("/com/esprit/wonderwise/BackOffice/Guide/GuideAdd.fxml");
    }

    @FXML
    public void showGuideList() throws IOException {
        loadScene("/com/esprit/wonderwise/BackOffice/Guide/Guide.fxml");
    }

    private void showGuideDetails(Guide guide) {
        try {
            selectedGuide = guide;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Guide/GuideDetails.fxml"));
            Parent root = loader.load();
            GuideBackController controller = loader.getController();
            controller.setGuideDetails(guide);
            Stage stage = (Stage) (guideCards != null ? guideCards.getScene().getWindow() : nomLabel.getScene().getWindow());
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showEditGuide(Guide guide) {
        try {
            selectedGuide = guide;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Guide/GuideEdit.fxml"));
            Parent root = loader.load();
            GuideBackController controller = loader.getController();
            controller.setGuideDataForEdit(guide);
            Stage stage = (Stage) guideCards.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setGuideDetails(Guide guide) {
        nomLabel.setText(guide.getNom());
        prenomLabel.setText("Prénom: " + guide.getPrenom());
        emailLabel.setText("Email: " + guide.getEmail());
        numTelephoneLabel.setText("Numéro de téléphone: " + guide.getNumTelephone());
        descriptionLabel.setText("Description: " + guide.getDescription());
        facebookLabel.setText("Facebook: " + guide.getFacebook());
        instagramLabel.setText("Instagram: " + guide.getInstagram());
        nombreAvisLabel.setText("Nombre d'avis: " + guide.getNombreAvis());

        File imageFile = new File(IMAGE_DESTINATION_DIR + guide.getPhoto());
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString(), 300, 180, true, true);
            photoView.setImage(image);
        } else {
            Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonder wise/images/notfound.png"), 300, 180, true, true);
            photoView.setImage(fallback);
        }
    }

    private void setGuideDataForEdit(Guide guide) {
        selectedGuide = guide;
        nomField.setText(guide.getNom());
        prenomField.setText(guide.getPrenom());
        emailField.setText(guide.getEmail());
        numTelephoneField.setText(guide.getNumTelephone());
        descriptionArea.setText(guide.getDescription());
        facebookField.setText(guide.getFacebook());
        instagramField.setText(guide.getInstagram());
        photoField.setText(guide.getPhoto());
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
    public void addGuide() throws SQLException, IOException {
        if (!validateInputs()) return;

        Guide guide = new Guide();
        guide.setNom(nomField.getText().trim());
        guide.setPrenom(prenomField.getText().trim());
        guide.setEmail(emailField.getText().trim());
        guide.setNumTelephone(numTelephoneField.getText().trim());
        guide.setDescription(descriptionArea.getText().trim());
        guide.setFacebook(facebookField.getText().trim());
        guide.setInstagram(instagramField.getText().trim());
        guide.setNombreAvis(0);

        String imageFileName = copyImageToDestination(photoField.getText());
        guide.setPhoto(imageFileName);

        guideService.create(guide);
        DialogUtils.showCustomDialog("Success", "Guide added successfully!", true, getCurrentStage());
        showGuideList();
    }

    @FXML
    public void updateGuide() throws SQLException, IOException {
        if (selectedGuide == null) {
            DialogUtils.showCustomDialog("Error", "No guide selected for update.", false, getCurrentStage());
            return;
        }
        if (!validateInputs()) return;

        selectedGuide.setNom(nomField.getText().trim());
        selectedGuide.setPrenom(prenomField.getText().trim());
        selectedGuide.setEmail(emailField.getText().trim());
        selectedGuide.setNumTelephone(numTelephoneField.getText().trim());
        selectedGuide.setDescription(descriptionArea.getText().trim());
        selectedGuide.setFacebook(facebookField.getText().trim());
        selectedGuide.setInstagram(instagramField.getText().trim());

        String imageFileName = copyImageToDestination(photoField.getText());
        if (!imageFileName.isEmpty()) {
            selectedGuide.setPhoto(imageFileName);
        }

        guideService.update(selectedGuide);
        DialogUtils.showCustomDialog("Success", "Guide updated successfully!", true, getCurrentStage());
        showGuideList();
    }

    private void deleteGuide(Guide guide) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this guide?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            guideService.delete(guide.getId());
            loadGuides(null);
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Success");
            success.setHeaderText(null);
            success.setContentText("Guide deleted successfully!");
            success.showAndWait();
        }
    }

    private void loadScene(String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) (nomField != null ? nomField.getScene().getWindow() :
                (nomLabel != null ? nomLabel.getScene().getWindow() :
                        guideCards.getScene().getWindow()));
        stage.setScene(new Scene(root));
    }

    // Validation method
    private boolean validateInputs() {
        if (nomField.getText().trim().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Name cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (prenomField.getText().trim().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "First name cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Email cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (!EMAIL_PATTERN.matcher(emailField.getText().trim()).matches()) {
            DialogUtils.showCustomDialog("Validation Error", "Invalid email format.", false, getCurrentStage());
            return false;
        }
        if (numTelephoneField.getText().trim().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Phone number cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (!PHONE_PATTERN.matcher(numTelephoneField.getText().trim()).matches()) {
            DialogUtils.showCustomDialog("Validation Error", "Phone number must be exactly 8 digits.", false, getCurrentStage());
            return false;
        }
        if (descriptionArea.getText().trim().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Description cannot be empty.", false, getCurrentStage());
            return false;
        }
        return true;
    }

    // --- Search Helper ---
    private void applySearchFilter() {
        String search = (searchField != null) ? searchField.getText() : null;
        loadGuides(search);
    }

    // Utility method to get the current stage
    private Stage getCurrentStage() {
        return (Stage) (guideCards != null ? guideCards.getScene().getWindow() :
                nomField != null ? nomField.getScene().getWindow() :
                        nomLabel.getScene().getWindow());
    }
}