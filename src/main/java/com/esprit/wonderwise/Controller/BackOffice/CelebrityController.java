package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.Celebrity;
import com.esprit.wonderwise.Model.Country;
import com.esprit.wonderwise.Service.CelebrityService;
import com.esprit.wonderwise.Service.CountryService;
import com.esprit.wonderwise.Utils.DialogUtils;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javafx.embed.swing.SwingFXUtils;

public class CelebrityController {
    @FXML private FlowPane celebrityCards;
    @FXML private TextField nameField, workField, imgPathField, descField, jobField, nationalityField, notableWorksField, personalLifeField, netWorthField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<Country> countryComboBox;
    @FXML private Label nameLabel, workLabel, descLabel, jobLabel, dobLabel, nationalityLabel, notableWorksLabel, personalLifeLabel, netWorthLabel;
    @FXML private ImageView detailImageView;

    private CelebrityService celebrityService = new CelebrityService();
    private CountryService countryService = new CountryService();
    private Celebrity selectedCelebrity;
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    @FXML
    public void initialize() {
        if (celebrityCards != null) {
            loadCelebrities();
        }
        if (countryComboBox != null) {
            loadCountries();
        }
    }

    private void loadCelebrities() {
        celebrityCards.getChildren().clear();
        List<Celebrity> celebrities = celebrityService.readAll();
        for (Celebrity celebrity : celebrities) {
            VBox card = new VBox(15);
            card.getStyleClass().add("country-card");
            card.setPrefWidth(250);
            card.setPrefHeight(300);
            card.setAlignment(Pos.CENTER);

            ImageView imageView = new ImageView();
            File imageFile = new File(IMAGE_DESTINATION_DIR + celebrity.getImg());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString(), 200, 150, true, true);
                imageView.setImage(image);
            } else {
                Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 200, 150, true, true);
                imageView.setImage(fallback);
            }
            imageView.getStyleClass().add("rounded-image");

            Label name = new Label(celebrity.getName());
            name.setStyle("-fx-font-size: 18px; -fx-text-fill: #2C3E50; -fx-font-weight: bold;");

            Country country = countryService.getById(celebrity.getCountryId());
            String countryName = (country != null) ? country.getName() : "Unknown Country";
            Label snippet = new Label("From: " + countryName);
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
                    showDetails(celebrity);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            editBtn.setOnAction(e -> {
                try {
                    showEditCelebrity(celebrity);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            deleteBtn.setOnAction(e -> {
                try {
                    deleteCelebrity(celebrity.getId());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            buttons.getChildren().addAll(detailsBtn, editBtn, deleteBtn);
            card.getChildren().addAll(imageView, name, snippet, buttons);
            celebrityCards.getChildren().add(card);
        }
    }

    private void loadCountries() {
        countryComboBox.getItems().clear();
        List<Country> countries = countryService.readAll();
        countryComboBox.getItems().addAll(countries);
    }

    @FXML
    public void showAddCelebrity() throws IOException {
        loadScene("CelebrityAdd.fxml", null);
    }

    @FXML
    public void addCelebrity() throws IOException {
        if (!validateInputs()) return;

        String imageFileName = copyImageToDestination(imgPathField.getText());
        Celebrity celebrity = new Celebrity(
                0,
                countryComboBox.getValue().getId(),
                nameField.getText(),
                workField.getText(),
                imageFileName,
                descField.getText(),
                jobField.getText(),
                Date.valueOf(dobPicker.getValue()),
                nationalityField.getText(),
                notableWorksField.getText(),
                personalLifeField.getText(),
                Double.parseDouble(netWorthField.getText())
        );
        celebrityService.add(celebrity);
        DialogUtils.showCustomDialog("Success", "Celebrity added successfully!", true, getCurrentStage());
        showCelebrityList();
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
    public void showEditCelebrity(Celebrity celebrity) throws IOException {
        selectedCelebrity = celebrity;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Celebrity/CelebrityEdit.fxml"));
        Parent root = loader.load();
        CelebrityController controller = loader.getController();
        controller.setCelebrityData(celebrity);
        controller.selectedCelebrity = celebrity;
        Stage stage = (Stage) (celebrityCards != null ? celebrityCards.getScene().getWindow() : nameField.getScene().getWindow());
        stage.setScene(new Scene(root));
    }

    @FXML
    public void updateCelebrity() throws IOException {
        if (selectedCelebrity == null) {
            DialogUtils.showCustomDialog("Error", "No celebrity selected for update.", false, getCurrentStage());
            return;
        }
        if (!validateInputs()) return;

        String imageFileName = copyImageToDestination(imgPathField.getText());
        selectedCelebrity.setCountryId(countryComboBox.getValue().getId());
        selectedCelebrity.setName(nameField.getText());
        selectedCelebrity.setWork(workField.getText());
        selectedCelebrity.setImg(imageFileName);
        selectedCelebrity.setDescription(descField.getText());
        selectedCelebrity.setJob(jobField.getText());
        selectedCelebrity.setDateOfBirth(Date.valueOf(dobPicker.getValue()));
        selectedCelebrity.setNationality(nationalityField.getText());
        selectedCelebrity.setNotableWorks(notableWorksField.getText());
        selectedCelebrity.setPersonalLife(personalLifeField.getText());
        selectedCelebrity.setNetWorth(Double.parseDouble(netWorthField.getText()));
        celebrityService.update(selectedCelebrity);
        DialogUtils.showCustomDialog("Success", "Celebrity updated successfully!", true, getCurrentStage());
        showCelebrityList();
    }

    private String copyImageToDestination(String sourcePath) throws IOException {
        if (sourcePath == null || sourcePath.isEmpty()) return "";
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) return "";
        String fileName = sourceFile.getName();
        File destFile = new File(IMAGE_DESTINATION_DIR + fileName);
        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    public void setCelebrityData(Celebrity celebrity) {
        nameField.setText(celebrity.getName());
        workField.setText(celebrity.getWork());
        imgPathField.setText(IMAGE_DESTINATION_DIR + celebrity.getImg());
        descField.setText(celebrity.getDescription());
        jobField.setText(celebrity.getJob());
        dobPicker.setValue(celebrity.getDateOfBirth().toLocalDate());
        nationalityField.setText(celebrity.getNationality());
        notableWorksField.setText(celebrity.getNotableWorks());
        personalLifeField.setText(celebrity.getPersonalLife());
        netWorthField.setText(String.valueOf(celebrity.getNetWorth()));
        Country country = countryService.getById(celebrity.getCountryId());
        if (country != null) {
            countryComboBox.setValue(country);
        }
    }

    @FXML
    public void deleteCelebrity(int id) throws IOException {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this celebrity?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            celebrityService.delete(id);
            DialogUtils.showCustomDialog("Success", "Celebrity deleted successfully!", true, getCurrentStage());
            showCelebrityList();
        }
    }

    @FXML
    public void showDetails(Celebrity celebrity) throws IOException {
        selectedCelebrity = celebrity;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Celebrity/CelebrityDetails.fxml"));
        Parent root = loader.load();
        CelebrityController controller = loader.getController();
        controller.setDetailsData(celebrity);
        Stage stage = (Stage) (celebrityCards != null ? celebrityCards.getScene().getWindow() : nameLabel.getScene().getWindow());
        stage.setScene(new Scene(root));
    }

    public void setDetailsData(Celebrity celebrity) {
        nameLabel.setText(celebrity.getName());
        workLabel.setText("Work: " + celebrity.getWork());
        descLabel.setText("Description: " + celebrity.getDescription());
        jobLabel.setText("Job: " + celebrity.getJob());
        dobLabel.setText("Date of Birth: " + celebrity.getDateOfBirth().toString());
        nationalityLabel.setText("Nationality: " + celebrity.getNationality());
        notableWorksLabel.setText("Notable Works: " + celebrity.getNotableWorks());
        personalLifeLabel.setText("Personal Life: " + celebrity.getPersonalLife());
        netWorthLabel.setText("Net Worth: $" + celebrity.getNetWorth());

        File imageFile = new File(IMAGE_DESTINATION_DIR + celebrity.getImg());
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString(), 300, 200, true, true);
            detailImageView.setImage(image);
        } else {
            Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 300, 200, true, true);
            detailImageView.setImage(fallback);
        }
    }

    @FXML
    public void showCelebrityList() throws IOException {
        loadScene("Celebrity.fxml", null);
    }

    private void loadScene(String fxml, Button sourceButton) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/BackOffice/Celebrity/" + fxml));
        Stage stage;
        if (sourceButton != null) {
            stage = (Stage) sourceButton.getScene().getWindow();
        } else if (celebrityCards != null) {
            stage = (Stage) celebrityCards.getScene().getWindow();
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
        if (workField.getText().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Work cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (jobField.getText().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Job cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (nationalityField.getText().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Nationality cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (dobPicker.getValue() == null) {
            DialogUtils.showCustomDialog("Validation Error", "Date of Birth must be selected.", false, getCurrentStage());
            return false;
        }
        if (countryComboBox.getValue() == null) {
            DialogUtils.showCustomDialog("Validation Error", "Please select a country.", false, getCurrentStage());
            return false;
        }
        try {
            Double.parseDouble(netWorthField.getText());
        } catch (NumberFormatException e) {
            DialogUtils.showCustomDialog("Validation Error", "Net Worth must be a valid number.", false, getCurrentStage());
            return false;
        }
        return true;
    }

    private Stage getCurrentStage() {
        return (Stage) (celebrityCards != null ? celebrityCards.getScene().getWindow() :
                nameField != null ? nameField.getScene().getWindow() :
                        nameLabel.getScene().getWindow());
    }


    private Webcam webcam = null;
    @FXML private ImageView webcamPreview;

    @FXML
    public void handleWebcamCapture() {
        new Thread(() -> {
            try {
                if (webcam == null) {
                    webcam = Webcam.getDefault();
                    webcam.setViewSize(WebcamResolution.QVGA.getSize());
                }
                webcam.open();
                BufferedImage grabbedImage = webcam.getImage();
                if (grabbedImage != null) {
                    // Save captured image to disk
                    String filename = "celebrity_" + UUID.randomUUID() + ".png";
                    File outputFile = new File(IMAGE_DESTINATION_DIR + filename);
                    ImageIO.write(grabbedImage, "PNG", outputFile);

                    // Update imgPathField and preview in JavaFX thread
                    Platform.runLater(() -> {
                        imgPathField.setText(IMAGE_DESTINATION_DIR  + filename);
                        webcamPreview.setImage(SwingFXUtils.toFXImage(grabbedImage, null));
                        webcamPreview.setVisible(true);
                        webcamPreview.setManaged(true);
                    });
                }
                webcam.close();
            } catch (Exception e) {
                Platform.runLater(() -> DialogUtils.showCustomDialog("Webcam Error", "Failed to capture image: " + e.getMessage(), false, getCurrentStage()));
            }
        }).start();
    }

}