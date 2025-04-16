package com.esprit.wonderwise.Controller.Login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import com.esprit.wonderwise.Utils.DatabaseConnection;
import java.net.URL;
import javafx.scene.Parent;

public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Hyperlink forgotPasswordLink;
    
    @FXML
    private Hyperlink createAccountLink;
    
    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        forgotPasswordLink.setOnAction(event -> handleForgotPassword());
        createAccountLink.setOnAction(event -> handleCreateAccount());
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Login Error", "Please enter both username and password.");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String role = rs.getString("role");
                        String fxmlPath;
                        
                        if (role.equalsIgnoreCase("admin")) {
                            fxmlPath = "/com/esprit/wonderwise/BackOffice/BackOffice.fxml";
                        } else if (role.equalsIgnoreCase("client")) {
                            fxmlPath = "/com/esprit/wonderwise/FrontOffice/FrontOffice.fxml";
                        } else {
                            showAlert(AlertType.ERROR, "Login Error", "Invalid user role.");
                            return;
                        }
                        
                        try {
                            URL resourceUrl = getClass().getResource(fxmlPath);
                            if (resourceUrl == null) {
                                throw new IOException("Could not find FXML file: " + fxmlPath);
                            }
                            
                            FXMLLoader loader = new FXMLLoader(resourceUrl);
                            Parent root = loader.load();
                            Scene scene = new Scene(root);
                            Stage stage = new Stage();
                            stage.setScene(scene);
                            stage.setTitle("WonderWise - " + (role.equalsIgnoreCase("admin") ? "Admin Panel" : "Client Portal"));
                            
                            // Close current window before showing new one
                            Stage currentStage = (Stage) loginButton.getScene().getWindow();
                            currentStage.close();
                            
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            showAlert(AlertType.ERROR, "Error", "Failed to load interface: " + e.getMessage());
                        }
                    } else {
                        showAlert(AlertType.ERROR, "Login Error", "Invalid username or password.");
                    }
                }
            }
        } catch (SQLException e) {
            String errorMessage = "Database connection error: " + e.getMessage();
            showAlert(AlertType.ERROR, "Database Error", errorMessage);
            e.printStackTrace();
        }
    }
    
    private void handleForgotPassword() {
        // TODO: Implement password reset functionality
        showAlert(AlertType.INFORMATION, "Reset Password", "Password reset functionality will be implemented soon.");
    }
    
    private void handleCreateAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/Login/SignUp.fxml"));
            Scene signUpScene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(signUpScene);
            
            // Close the current window
            createAccountLink.getScene().getWindow().hide();
            
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Could not load sign-up page.");
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