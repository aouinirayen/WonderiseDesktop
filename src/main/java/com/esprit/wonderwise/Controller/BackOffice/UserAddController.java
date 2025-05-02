package com.esprit.wonderwise.Controller.BackOffice;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import com.esprit.wonderwise.Model.User;
import com.esprit.wonderwise.Service.UserService;
import com.esprit.wonderwise.Utils.DialogUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public class UserAddController {
    @FXML private ImageView profileImageView;
    @FXML private Button changePhotoBtn;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField phoneField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private String profileImagePath = null;
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        roleComboBox.getItems().addAll("Client", "Admin");
        changePhotoBtn.setOnAction(e -> handleChangePhoto());
        saveBtn.setOnAction(e -> handleSave());
        cancelBtn.setOnAction(e -> handleCancel());
    }

    private void handleChangePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (file != null) {
            try {
                String destDir = "C:/xampp/htdocs/pidev3/";
                File destFile = new File(destDir + file.getName());
                Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                profileImagePath = destFile.getName();
                profileImageView.setImage(new javafx.scene.image.Image(destFile.toURI().toString()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleSave() {
        // Input validation
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String phone = phoneField.getText().trim();
        LocalDate dob = dobPicker.getValue();
        String address = addressField.getText().trim();
        String gender = genderComboBox.getValue();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty() || dob == null || address.isEmpty() || gender == null || role == null) {
            DialogUtils.showCustomDialog("Erreur", "Please fill in all fields.", false);
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            DialogUtils.showCustomDialog("Erreur", "Invalid email address.", false);
            return;
        }
        if (!password.equals(confirmPassword)) {
            DialogUtils.showCustomDialog("Erreur", "Passwords do not match.", false);
            return;
        }
        if (password.length() < 6) {
            DialogUtils.showCustomDialog("Erreur", "Password must be at least 6 characters.", false);
            return;
        }
        if (!phone.matches("^[0-9]{8,15}$")) {
            DialogUtils.showCustomDialog("Erreur", "Invalid phone number.", false);
            return;
        }
        // Add user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password); // Hash in production!
        user.setPhone(phone);
        user.setProfilePicture(profileImagePath);
        user.setDateOfBirth(dob);
        user.setAddress(address);
        user.setGender(gender);
        user.setRole(role);
        user.setStatus("Active");
        boolean added = userService.registerUser(user);
        if (added) {
            DialogUtils.showCustomDialog("Succès", "User added successfully.", true);
            // Redirect to user management page
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/BackOffice/Users/UsersBack.fxml"));
                Stage stage = (Stage) saveBtn.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DialogUtils.showCustomDialog("Erreur", "Failed to add user. Please try again.", false);
        }
    }

    private void handleCancel() {
        // Return to users list (UsersBack.fxml)
        try {
            Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/BackOffice/Users/UsersBack.fxml"));
            Stage stage = (Stage) saveBtn.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
