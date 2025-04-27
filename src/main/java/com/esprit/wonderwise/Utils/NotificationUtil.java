package com.esprit.wonderwise.Utils;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NotificationUtil {

    public enum NotificationType {
        INFO("#1976D2"),       // bleu
        SUCCESS("#388E3C"),    // vert
        WARNING("#FBC02D"),    // jaune
        ERROR("#D32F2F");      // rouge

        private final String color;

        NotificationType(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

    public static void showNotification(Stage stage, String message) {
        showNotification(stage, message, NotificationType.INFO, Duration.seconds(5));
    }

    public static void showNotification(Stage stage, String message, NotificationType type, Duration duration) {
        Popup popup = new Popup();

        Label label = new Label(message);
        label.setStyle("-fx-background-color: " + type.getColor() + "; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 14 20; " +
                "-fx-font-size: 14px; " +
                "-fx-background-radius: 10; " +
                "-fx-font-weight: bold;");
        label.setWrapText(true);
        label.setMaxWidth(400);
        label.setEffect(new DropShadow(12, Color.rgb(0, 0, 0, 0.25)));
        label.setOpacity(0);
        label.setTranslateY(50); // pour slide-in

        StackPane container = new StackPane(label);
        container.setPadding(new Insets(10));
        container.setAlignment(Pos.BOTTOM_CENTER);
        container.setStyle("-fx-background-color: transparent;");

        popup.getContent().add(container);
        popup.setAutoFix(true);
        popup.setAutoHide(true);

        Scene scene = stage.getScene();
        double x = stage.getX() + (scene.getWidth() / 2) - 200;
        double y = stage.getY() + scene.getHeight() - 100;

        popup.show(stage, x, y);

        // Animations : Fade + Slide
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(label.opacityProperty(), 0),
                        new KeyValue(label.translateYProperty(), 50)),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(label.opacityProperty(), 1),
                        new KeyValue(label.translateYProperty(), 0))
        );

        Timeline autoClose = new Timeline(
                new KeyFrame(duration, e -> {
                    Timeline fadeOut = new Timeline(
                            new KeyFrame(Duration.ZERO,
                                    new KeyValue(label.opacityProperty(), 1),
                                    new KeyValue(label.translateYProperty(), 0)),
                            new KeyFrame(Duration.millis(300),
                                    new KeyValue(label.opacityProperty(), 0),
                                    new KeyValue(label.translateYProperty(), 50))
                    );
                    fadeOut.setOnFinished(ev -> popup.hide());
                    fadeOut.play();
                })
        );

        fadeIn.play();
        autoClose.play();
    }
}
