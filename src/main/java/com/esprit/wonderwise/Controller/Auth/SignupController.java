package com.esprit.wonderwise.Controller.Auth;

import com.esprit.wonderwise.Model.User;
import com.esprit.wonderwise.Service.UserService;
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.time.LocalDate;
import java.io.File;
import java.io.IOException;

import javafx.stage.FileChooser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SignupController {
    // Static fields to persist data between scenes
    private static String step1Username = "";
    private static String step1Email = "";
    private static String step1Password = "";
    private static String step2Phone = "";
    private static String step2Address = "";
    private static String step2Gender = "";
    private static LocalDate step2Dob = null;
    private static String step2ProfilePicPath = null;
    private static boolean comingBackToStep1 = false;
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    // Step 1 fields
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private ImageView togglePasswordView;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField confirmPasswordTextField;
    @FXML
    private ImageView toggleConfirmPasswordView;
    @FXML
    private Button nextButton;
    @FXML
    private Hyperlink loginLink;

    // Step 2 fields
    @FXML
    private TextField phoneField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private TextField addressField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private Button backButton;
    @FXML
    private Button signupButton;
    @FXML
    private Button ChoosePictureButton;

    // State
    private String selectedProfilePicturePath = null;
    private final UserService userService = new UserService();

    @FXML
    private static final String EYE_OPEN_ICON = "/com/esprit/wonderwise/icons/eye_open.png";
    private static final String EYE_CLOSED_ICON = "/com/esprit/wonderwise/icons/eye_closed.png";

    public void initialize() {
        if (genderComboBox != null) {
            genderComboBox.getItems().clear();
            genderComboBox.getItems().addAll("Male", "Female");
            if (!step2Gender.isEmpty()) genderComboBox.setValue(step2Gender);
        }
        // Repopulate step 1 fields
        if (usernameField != null) usernameField.setText(step1Username);
        if (emailField != null) emailField.setText(step1Email);
        if (passwordField != null) passwordField.setText(step1Password);
        if (confirmPasswordField != null) confirmPasswordField.setText(step1Password);

        // --- Password Field Show/Hide ---
        if (passwordTextField != null && passwordField != null && togglePasswordView != null) {
            passwordTextField.setManaged(false);
            passwordTextField.setVisible(false);
            passwordTextField.setText("");
            passwordTextField.setPromptText(passwordField.getPromptText());
            setEyeIcon(togglePasswordView, false);
            togglePasswordView.setOnMouseClicked(event -> togglePasswordVisibility(passwordField, passwordTextField, togglePasswordView));
            passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!passwordTextField.isVisible()) passwordTextField.setText(newVal);
            });
            passwordTextField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (passwordTextField.isVisible()) passwordField.setText(newVal);
            });
        }
        // --- Confirm Password Field Show/Hide ---
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
        // Repopulate step 2 fields
        if (phoneField != null) phoneField.setText(step2Phone);
        //if (addressField != null) addressField.setText(step2Address);
        if (dobPicker != null && step2Dob != null) dobPicker.setValue(step2Dob);
        // If coming back to step 1, reset flag
        if (comingBackToStep1) comingBackToStep1 = false;
    }

    private void togglePasswordVisibility(PasswordField pf, TextField tf, ImageView eye) {
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

    private void setEyeIcon(ImageView eye, boolean open) {
        String iconPath = open ? EYE_OPEN_ICON : EYE_CLOSED_ICON;
        eye.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream(iconPath)));
    }

    @FXML
    private void handleNext() {
        // Validate step 1
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            DialogUtils.showCustomDialog("Erreur", "Veuillez remplir tous les champs obligatoires.", false);
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            DialogUtils.showCustomDialog("Erreur", "Email invalide.", false);
            return;
        }
        if (!password.equals(confirmPassword)) {
            DialogUtils.showCustomDialog("Erreur", "Les mots de passe ne correspondent pas.", false);
            return;
        }
        if (password.length() < 6) {
            DialogUtils.showCustomDialog("Erreur", "Le mot de passe doit contenir au moins 6 caractères.", false);
            return;
        }
        if (!userService.isEmailUnique(email)) {
            DialogUtils.showCustomDialog("Erreur", "Cet email est déjà utilisé.", false);
            return;
        }
        // Save to static fields
        step1Username = username;
        step1Email = email;
        step1Password = password;
        // Switch to step 2
        switchSceneFromControl(nextButton, "/com/esprit/wonderwise/auth/SignUpPage2.fxml");
    }

    @FXML
    private void handleBack() {
        // Save step 2 data before going back
        step2Phone = phoneField != null ? phoneField.getText() : "";
        step2Address = addressField != null ? addressField.getText() : "";
        step2Gender = genderComboBox != null && genderComboBox.getValue() != null ? genderComboBox.getValue() : "";
        step2Dob = dobPicker != null ? dobPicker.getValue() : null;
        comingBackToStep1 = true;
        switchSceneFromControl(backButton, "/com/esprit/wonderwise/auth/SignUpPage1.fxml");
    }

    @FXML
    private void handleSignup() {
        // Validate step 2
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String gender = genderComboBox.getValue();
        LocalDate dob = dobPicker.getValue();
        String profilePicFileName = null;
        if (gender == null || gender.isEmpty()) {
            DialogUtils.showCustomDialog("Erreur", "Veuillez sélectionner le genre.", false);
            return;
        }
        if (dob == null) {
            DialogUtils.showCustomDialog("Erreur", "Veuillez sélectionner la date de naissance.", false);
            return;
        }
        if (step1Username.isEmpty() || step1Email.isEmpty() || step1Password.isEmpty()) {
            DialogUtils.showCustomDialog("Erreur", "Veuillez remplir d'abord la première étape.", false);
            return;
        }
        if (selectedProfilePicturePath != null && !selectedProfilePicturePath.isEmpty()) {
            try {
                profilePicFileName = copyImageToDestination(selectedProfilePicturePath);
            } catch (IOException e) {
                DialogUtils.showCustomDialog("Erreur", "Erreur lors de la copie de l'image: " + e.getMessage(), false);
                return;
            }
        }
        // Save to static fields
        step2Phone = phone;
        step2Address = address;
        step2Gender = gender;
        step2Dob = dob;
        step2ProfilePicPath = profilePicFileName;
        // Register user
        User user = new User();
        user.setUsername(step1Username);
        user.setEmail(step1Email);
        user.setPassword(step1Password);
        user.setRole("client");
        user.setStatus("Active");
        user.setPhone(phone);
        user.setAddress(address);
        user.setGender(gender);
        user.setDateOfBirth(dob);
        user.setProfilePicture(profilePicFileName);
        boolean success = userService.registerUser(user);
        if (success) {
            DialogUtils.showCustomDialog("Succès", "Inscription réussie! Veuillez vérifier votre email.", true);
            clearSignupData();
            switchSceneFromControl(signupButton, "/com/esprit/wonderwise/auth/Login.fxml");
        } else {
            DialogUtils.showCustomDialog("Erreur", "Erreur lors de l'inscription. Veuillez réessayer.", false);
        }
    }

    private void clearSignupData() {
        step1Username = "";
        step1Email = "";
        step1Password = "";
        step2Phone = "";
        step2Address = "";
        step2Gender = "";
        step2Dob = null;
        step2ProfilePicPath = null;
        selectedProfilePicturePath = null;
    }

    @FXML
    private void handleReturnToLogin() {
        clearSignupData();
        switchSceneFromControl(loginLink != null ? loginLink : backButton, "/com/esprit/wonderwise/auth/Login.fxml");
    }

    @FXML
    private void handleChoosePicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedProfilePicturePath = file.getAbsolutePath();
            if (ChoosePictureButton != null) {
                ChoosePictureButton.setText(file.getName());
            }
        }
    }

    private String copyImageToDestination(String sourcePath) throws IOException {
        if (sourcePath == null || sourcePath.isEmpty()) return "";
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) return "";
        String fileName = sourceFile.getName();
        File destFile = new File(IMAGE_DESTINATION_DIR + fileName);
        java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private void switchSceneFromControl(Control control, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) control.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            DialogUtils.showCustomDialog("Erreur", "Erreur lors du chargement de la scène", false);
            System.err.println("Error loading scene: " + e.getMessage());
        }
    }

}