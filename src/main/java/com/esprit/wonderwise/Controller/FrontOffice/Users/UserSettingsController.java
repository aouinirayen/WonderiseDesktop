package com.esprit.wonderwise.Controller.FrontOffice.Users;

import com.esprit.wonderwise.Model.User;
import com.esprit.wonderwise.Service.UserService;
import com.esprit.wonderwise.Util.UserSession;
import javafx.fxml.FXML;
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.scene.control.*;

import java.io.File;
import java.time.LocalDate;

public class UserSettingsController {
    private final UserService userService = new UserService();
    private User user;
    private static final String IMAGE_DESTINATION_DIR = "C:/xampp/htdocs/pidev3/";

    // FXML fields
    @FXML
    private javafx.scene.image.ImageView profileImageView;
    @FXML
    private Button changePhotoBtn;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private TextField addressField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private Button saveBtn;
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private TextField currentPasswordTextField;
    @FXML
    private javafx.scene.image.ImageView toggleCurrentPasswordView;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private TextField newPasswordTextField;
    @FXML
    private javafx.scene.image.ImageView toggleNewPasswordView;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField confirmPasswordTextField;
    @FXML
    private javafx.scene.image.ImageView toggleConfirmPasswordView;
    @FXML
    private Button changePasswordBtn;

    private String selectedProfilePicturePath = null;

    // Password visibility toggle helpers
    private static final String EYE_OPEN_ICON = "/com/esprit/wonderwise/icons/eye_open.png";
    private static final String EYE_CLOSED_ICON = "/com/esprit/wonderwise/icons/eye_closed.png";

    @FXML
    public void initialize() {
        user = UserSession.getUser();
        if (user == null) return;
        // Populate fields
        usernameField.setText(user.getUsername());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        addressField.setText(user.getAddress());
        dobPicker.setValue(user.getDateOfBirth());
        genderComboBox.getItems().setAll("Male", "Female");
        if (user.getGender() != null && !user.getGender().isEmpty()) genderComboBox.setValue(user.getGender());
        // Load profile image
        loadProfileImage(user.getProfilePicture());
        // Button actions
        changePhotoBtn.setOnAction(e -> handleChangePhoto());
        saveBtn.setOnAction(e -> handleSaveChanges());
        changePasswordBtn.setOnAction(e -> handleChangePassword());
        // --- Password Visibility Toggle Logic ---
        // Current Password
        if (currentPasswordTextField != null && currentPasswordField != null && toggleCurrentPasswordView != null) {
            currentPasswordTextField.setManaged(false);
            currentPasswordTextField.setVisible(false);
            currentPasswordTextField.setText("");
            currentPasswordTextField.setPromptText(currentPasswordField.getPromptText());
            setEyeIcon(toggleCurrentPasswordView, false);
            toggleCurrentPasswordView.setOnMouseClicked(event -> togglePasswordVisibility(currentPasswordField, currentPasswordTextField, toggleCurrentPasswordView));
            currentPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!currentPasswordTextField.isVisible()) currentPasswordTextField.setText(newVal);
            });
            currentPasswordTextField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (currentPasswordTextField.isVisible()) currentPasswordField.setText(newVal);
            });
        }
        // New Password
        if (newPasswordTextField != null && newPasswordField != null && toggleNewPasswordView != null) {
            newPasswordTextField.setManaged(false);
            newPasswordTextField.setVisible(false);
            newPasswordTextField.setText("");
            newPasswordTextField.setPromptText(newPasswordField.getPromptText());
            setEyeIcon(toggleNewPasswordView, false);
            toggleNewPasswordView.setOnMouseClicked(event -> togglePasswordVisibility(newPasswordField, newPasswordTextField, toggleNewPasswordView));
            newPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newPasswordTextField.isVisible()) newPasswordTextField.setText(newVal);
            });
            newPasswordTextField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newPasswordTextField.isVisible()) newPasswordField.setText(newVal);
            });
        }
        // Confirm Password
        if (confirmPasswordTextField != null && confirmPasswordField != null && toggleConfirmPasswordView != null) {
            confirmPasswordTextField.setManaged(false);
            confirmPasswordTextField.setVisible(false);
            confirmPasswordTextField.setText("");
            confirmPasswordTextField.setPromptText(confirmPasswordField.getPromptText());
            setEyeIcon(toggleConfirmPasswordView, false);
            toggleConfirmPasswordView.setOnMouseClicked(event -> togglePasswordVisibility(confirmPasswordField, confirmPasswordTextField, toggleConfirmPasswordView));
            confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!confirmPasswordTextField.isVisible()) confirmPasswordTextField.setText(newVal);
            });
            confirmPasswordTextField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (confirmPasswordTextField.isVisible()) confirmPasswordField.setText(newVal);
            });
        }
    }

    private void loadProfileImage(String filename) {
        javafx.scene.image.Image image = null;
        if (filename != null && !filename.isEmpty()) {
            File file = new File(IMAGE_DESTINATION_DIR + filename);
            if (file.exists()) {
                image = new javafx.scene.image.Image(file.toURI().toString());
            }
        }
        if (image == null) {
            image = new javafx.scene.image.Image(getClass().getResource("/com/esprit/wonderwise/images/notfound.png").toExternalForm());
        }
        profileImageView.setImage(image);
    }

    private void handleChangePhoto() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Choose Profile Photo");
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedProfilePicturePath = file.getAbsolutePath();
            profileImageView.setImage(new javafx.scene.image.Image(file.toURI().toString()));
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
            DialogUtils.showCustomDialog("Erreur", "Veuillez remplir tous les champs obligatoires.", false);
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            DialogUtils.showCustomDialog("Erreur", "Email invalide.", false);
            return;
        }
        if (!email.equals(user.getEmail()) && !userService.isEmailUnique(email)) {
            DialogUtils.showCustomDialog("Erreur", "Cet email est déjà utilisé.", false);
            return;
        }
        // Already declared and validated above, do not redeclare variables here.
        if (!phone.matches("^[0-9]{8,15}$")) {
            DialogUtils.showCustomDialog("Erreur", "Numéro de téléphone invalide (8-15 chiffres).", false);
            return;
        }
        // Copy profile picture if changed
        String profilePicFileName = user.getProfilePicture();
        if (selectedProfilePicturePath != null) {
            try {
                profilePicFileName = copyImageToDestination(selectedProfilePicturePath);
            } catch (Exception e) {
                DialogUtils.showCustomDialog("Erreur", "Erreur lors de la copie de l'image: " + e.getMessage(), false);
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
            DialogUtils.showCustomDialog("Succès", "Modifications enregistrées avec succès.", true);
            UserSession.setUser(user); // update session
            try {
                javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/FrontOffice/FrontOffice.fxml"));
                javafx.stage.Stage stage = (javafx.stage.Stage) saveBtn.getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(root));
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        } else {
            DialogUtils.showCustomDialog("Erreur", "Erreur lors de la mise à jour. Veuillez réessayer.", false);
        }
    }

    private void handleChangePassword() {
        String currentPass = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();
        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            DialogUtils.showCustomDialog("Erreur", "Veuillez remplir tous les champs de mot de passe.", false);
            return;
        }
        if (!user.getPassword().equals(currentPass)) {
            DialogUtils.showCustomDialog("Erreur", "Mot de passe actuel incorrect.", false);
            return;
        }
        if (!newPass.equals(confirmPass)) {
            DialogUtils.showCustomDialog("Erreur", "Les nouveaux mots de passe ne correspondent pas.", false);
            return;
        }
        if (newPass.length() < 6) {
            DialogUtils.showCustomDialog("Erreur", "Le nouveau mot de passe doit contenir au moins 6 caractères.", false);
            return;
        }
        user.setPassword(newPass);
        boolean success = userService.updateUser(user);
        if (success) {
            DialogUtils.showCustomDialog("Succès", "Mot de passe changé avec succès.", true);
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            // Reload main scene to update NavBar
            try {
                javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/FrontOffice/FrontOffice.fxml"));
                javafx.stage.Stage stage = (javafx.stage.Stage) changePasswordBtn.getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(root));
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        } else {
            DialogUtils.showCustomDialog("Erreur", "Erreur lors du changement de mot de passe.", false);
        }
    }

    private String copyImageToDestination(String sourcePath) throws java.io.IOException {
        if (sourcePath == null || sourcePath.isEmpty()) return user.getProfilePicture();
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) return user.getProfilePicture();
        String fileName = sourceFile.getName();
        File destFile = new File(IMAGE_DESTINATION_DIR + fileName);
        java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private void togglePasswordVisibility(PasswordField pf, TextField tf, javafx.scene.image.ImageView eye) {
        boolean showing = tf.isVisible();
        if (showing) {
            tf.setVisible(false);
            tf.setManaged(false);
            pf.setVisible(true);
            pf.setManaged(true);
            setEyeIcon(eye, false);
        } else {
            tf.setText(pf.getText());
            tf.setVisible(true);
            tf.setManaged(true);
            pf.setVisible(false);
            pf.setManaged(false);
            setEyeIcon(eye, true);
        }
    }
    private void setEyeIcon(javafx.scene.image.ImageView eye, boolean open) {
        String iconPath = open ? EYE_OPEN_ICON : EYE_CLOSED_ICON;
        eye.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream(iconPath)));
    }
}