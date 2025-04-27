package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.API.Geminiapi;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ChatBotController {

    @FXML private TextField userInputField;
    @FXML private TextArea chatArea;
    @FXML private Button sendButton;
    @FXML private ProgressIndicator loadingIndicator;

    private Stage stage;

    @FXML
    public void initialize() {
        // Initial welcome message
        appendToChat("WonderWise AI", "Hello! I'm your WonderWise AI assistant. How can I help you today?");

        // Set up auto-scrolling
        chatArea.textProperty().addListener((observable, oldValue, newValue) -> {
            chatArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    @FXML
    private void handleUserInput() {
        String userMessage = userInputField.getText().trim();
        if (userMessage.isEmpty()) return;

        appendToChat("You", userMessage);
        userInputField.clear();

        // Disable input while processing
        setInputDisabled(true);

        // Process in background thread
        new Thread(() -> {
            try {
                String aiResponse = Geminiapi.getAIResponse(userMessage);
                Platform.runLater(() -> {
                    appendToChat("WonderWise AI", aiResponse);
                    setInputDisabled(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    appendToChat("System", "⚠️ Sorry, I encountered an error. Please try again later.");
                    setInputDisabled(false);
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void appendToChat(String sender, String message) {
        String senderStyle = sender.equals("You") ?
                "-fx-fill: #19c37d; -fx-font-weight: bold;" :
                "-fx-fill: #5436da; -fx-font-weight: bold;";

        String formattedMessage = String.format(
                "%-15s %s\n",
                sender + ":",
                message
        );

        // This would be better with a proper TextFlow implementation
        chatArea.appendText(formattedMessage);
    }

    private void setInputDisabled(boolean disabled) {
        userInputField.setDisable(disabled);
        sendButton.setDisable(disabled);
        loadingIndicator.setVisible(disabled);
        if (!disabled) {
            userInputField.requestFocus();
        }
    }

    @FXML
    private void closeWindow() {
        if (stage != null) {
            stage.close();
        } else {
            ((Stage) userInputField.getScene().getWindow()).close();
        }
    }

    public void showChatBot() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/esprit/wonderwise/Controller/FrontOffice/Chatbot.fxml")
        );
        Parent root = loader.load();

        ChatBotController controller = loader.getController();
        controller.stage = new Stage();

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        controller.stage.setScene(scene);
        controller.stage.setTitle("WonderWise AI Assistant");
        controller.stage.initModality(Modality.APPLICATION_MODAL);
        controller.stage.initStyle(StageStyle.DECORATED);
        controller.stage.setResizable(false);
        controller.stage.show();
    }
}