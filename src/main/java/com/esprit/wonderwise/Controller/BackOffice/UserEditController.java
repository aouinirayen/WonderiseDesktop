package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.User;
import com.esprit.wonderwise.Service.UserService;
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public class UserEditController {
    private final UserService userService = new UserService();
    private User user;
    private static final String IMAGE_DESTINATION_DIR = "C:/xampp/htdocs/pidev3/";
    private String selectedProfilePicturePath = null;

    @FXML private ImageView profileImageView;
    @FXML private Button changePhotoBtn;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    public void setUser(User user) {
        this.user = user;
        populateFields();
    }

    @FXML
    public void initialize() {
        genderComboBox.getItems().setAll("Male", "Female");
        changePhotoBtn.setOnAction(e -> handleChangePhoto());
        saveBtn.setOnAction(e -> handleSaveChanges());
        cancelBtn.setOnAction(e -> handleCancel());
    }

    private void populateFields() {
        if (user == null) return;
        usernameField.setText(user.getUsername());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        addressField.setText(user.getAddress());
        dobPicker.setValue(user.getDateOfBirth());
        if (user.getGender() != null && !user.getGender().isEmpty()) genderComboBox.setValue(user.getGender());
        loadProfileImage(user.getProfilePicture());
    }

    private void loadProfileImage(String filename) {
        Image image = null;
        if (filename != null && !filename.isEmpty()) {
            File file = new File(IMAGE_DESTINATION_DIR + filename);
            if (file.exists()) {
                image = new Image(file.toURI().toString());
            }
        }
        if (image == null) {
            image = new Image(getClass().getResource("/com/esprit/wonderwise/images/notfound.png").toExternalForm());
        }
        profileImageView.setImage(image);
    }

    private void handleChangePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedProfilePicturePath = file.getAbsolutePath();
            profileImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    private void handleSaveChanges() {
        // Validate fields
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String gender = genderComboBox.getValue();
        LocalDate dob = dobPicker.getValue();
        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || gender == null || dob == null) {
            DialogUtils.showCustomDialog("Erreur", "Please fill all fields.", false);
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            DialogUtils.showCustomDialog("Erreur", "Invalid email format.", false);
            return;
        }
        if (!phone.matches("^\\+?[0-9]{6,15}$")) {
            DialogUtils.showCustomDialog("Erreur", "Invalid phone number.", false);
            return;
        }
        // Save profile picture if changed
        String profilePicFileName = user.getProfilePicture();
        if (selectedProfilePicturePath != null) {
            try {
                profilePicFileName = copyImageToDestination(selectedProfilePicturePath);
            } catch (IOException e) {
                DialogUtils.showCustomDialog("Erreur", "Error saving profile image.", false);
                return;
            }
        }
        // Update user object
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        user.setGender(gender);
        user.setDateOfBirth(dob);
        user.setProfilePicture(profilePicFileName);
        boolean success = userService.updateUser(user);
        if (success) {
            DialogUtils.showCustomDialog("Succès", "User updated successfully.", true);
            handleCancel(); // Go back to users list
        } else {
            DialogUtils.showCustomDialog("Erreur", "Error updating user. Please try again.", false);
        }
    }

    private void handleCancel() {
        // Return to users list
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/BackOffice/Users/UsersBack.fxml"));
            Stage stage = (Stage) saveBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String copyImageToDestination(String sourcePath) throws IOException {
        if (sourcePath == null || sourcePath.isEmpty()) return user.getProfilePicture();
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) return user.getProfilePicture();
        String fileName = sourceFile.getName();
        File destFile = new File(IMAGE_DESTINATION_DIR + fileName);
        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

}
