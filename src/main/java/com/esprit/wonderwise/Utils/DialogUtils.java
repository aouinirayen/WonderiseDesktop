package com.esprit.wonderwise.Utils;

import com.esprit.wonderwise.Controller.CustomDialogController;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;

public class DialogUtils {

    public static void showCustomDialog(String title, String message, boolean isSuccess) {
        Stage ownerStage = null;
        final Node[] ownerRootRef = new Node[1];
        try {
            FXMLLoader loader = new FXMLLoader(DialogUtils.class.getResource("/com/esprit/wonderwise/CustomDialog.fxml"));
            Parent dialogRoot = loader.load();
            CustomDialogController controller = loader.getController();
            controller.setDialogData(title, message, isSuccess);
            Scene dialogScene = new Scene(dialogRoot);
            dialogScene.setFill(Color.TRANSPARENT);
            Stage dialogStage = new Stage();
            dialogStage.setScene(dialogScene);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.TRANSPARENT);

            // Blur background
            ownerStage = getOwnerWindow();
            if (ownerStage != null && ownerStage.getScene() != null) {
                ownerRootRef[0] = ownerStage.getScene().getRoot();
                if (ownerRootRef[0] != null) {
                    GaussianBlur blur = new GaussianBlur(16);
                    ownerRootRef[0].setEffect(blur);
                    // Always remove blur when dialog closes
                    dialogStage.setOnHidden(e -> ownerRootRef[0].setEffect(null));
                }
                dialogStage.initOwner(ownerStage);
            }

            dialogStage.setResizable(false);
            dialogStage.setTitle(title);
            // Fade in
            dialogRoot.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(250), dialogRoot);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Utility to get the currently focused stage (owner window)
    private static Stage getOwnerWindow() {
        for (Window window : Window.getWindows()) {
            if (window.isFocused() && window instanceof Stage) {
                return (Stage) window;
            }
        }
        return null;
    }

}