package com.esprit.wonderwise.Controller.Login;

import com.esprit.wonderwise.Model.User;
import com.esprit.wonderwise.Repository.UserRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class SignUpController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Button signUpButton;
    
    @FXML
    private Hyperlink loginLink;
    
    @FXML
    public void initialize() {
        signUpButton.setOnAction(event -> handleSignUp());
        loginLink.setOnAction(event -> handleLoginLink());
    }
    
    private void handleSignUp() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(AlertType.ERROR, "Sign Up Error", "Please fill in all fields.");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showAlert(AlertType.ERROR, "Sign Up Error", "Passwords do not match.");
            return;
        }
        
        try {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setRole("client"); // Set role as client for new registrations
            
            UserRepository userRepository = new UserRepository();
            boolean success = userRepository.save(newUser);
            
            if (success) {
                showAlert(AlertType.INFORMATION, "Success", "Account created successfully!");
                handleLoginLink(); // Redirect to login page after successful registration
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to create account. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "An error occurred during registration.");
        }
    }
    
    private void handleLoginLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/Login/Login.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(loginScene);
            
            // Close the current window
            loginLink.getScene().getWindow().hide();
            
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Could not load login page.");
        }
    }
    
    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}