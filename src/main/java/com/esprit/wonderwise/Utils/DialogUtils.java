package com.esprit.wonderwise.Utils;

import com.esprit.wonderwise.Controller.CustomDialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class DialogUtils {

    public static void showCustomDialog(String title, String message, boolean isSuccess, Stage mainStage) {
        try {
            // Apply blur effect to the main scene
            GaussianBlur blur = new GaussianBlur(10); // Adjust radius for blur intensity
            mainStage.getScene().getRoot().setEffect(blur);


            / --- Search Helper ---
            private void applySearchFilter() {
                String search = (searchField != null) ? searchField.getText() : null;
                loadGuides(search);
            }
            dialogStage.setResizable(false);
            dialogStage.initStyle(StageStyle.TRANSPARENT); // Use TRANSPARENT instead of UNDECORATED

            // Remove blur effect when dialog closes
            dialogStage.setOnHidden(event -> mainStage.getScene().getRoot().setEffect(null));

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}