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

            // Load and set up the custom dialog
            FXMLLoader loader = new FXMLLoader(DialogUtils.class.getResource("/com/esprit/wonderwise/CustomDialog.fxml"));
            Parent dialogRoot = loader.load();

            CustomDialogController controller = loader.getController();
            controller.setDialogData(title, message, isSuccess);

            // Create the scene with transparent fill
            Scene dialogScene = new Scene(dialogRoot);
            dialogScene.setFill(Color.TRANSPARENT); // Make the scene background transparent

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Makes dialog modal
            dialogStage.initOwner(mainStage); // Ties dialog to main window
            dialogStage.setScene(dialogScene);
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