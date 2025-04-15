package com.esprit.wonderwise.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class CustomDialogController {
    @FXML private ImageView iconView;
    @FXML private Label titleLabel;
    @FXML private Label messageLabel;
    @FXML private Button closeButton;

    public void setDialogData(String title, String message, boolean isSuccess) {
        titleLabel.setText(title);
        messageLabel.setText(message);

        // Set icon based on success or error
        String iconPath = isSuccess ?
                "/com/esprit/wonderwise/images/success.png" :
                "/com/esprit/wonderwise/images/error.png";
        Image icon = new Image(getClass().getResourceAsStream(iconPath), 50, 50, true, true);
        iconView.setImage(icon);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}