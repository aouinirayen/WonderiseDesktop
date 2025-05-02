package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.Commentaire;
import com.esprit.wonderwise.Model.Evenement;
import com.esprit.wonderwise.Service.CommentaireService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import javafx.embed.swing.SwingFXUtils;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import javax.swing.*;


public class EvenementDetailsFrontController {

    @FXML private Label nomLabel, dateLabel, heureLabel, lieuLabel, placeMaxLabel, prixLabel, categorieLabel, likesCountLabel, weatherLabel;
    @FXML private Text descriptionLabel;
    @FXML private ImageView photoView;
    @FXML private ImageView qrCodeView;
    @FXML private TextField commentField;
    @FXML private Button addCommentButton;
    @FXML private FlowPane commentsFlowPane;
    @FXML private Pane mapPane;
    private Evenement selectedEvenement;
    private CommentaireService commentaireService = new CommentaireService();
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    public void setDetailsData(Evenement evenement) {
        selectedEvenement = evenement;
        nomLabel.setText(evenement.getNom());
        dateLabel.setText(evenement.getDate().toString());
        heureLabel.setText(evenement.getHeure().toString());
        lieuLabel.setText(evenement.getLieu());
        fetchAndDisplayWeather(evenement.getLieu());
        descriptionLabel.setText(evenement.getDescription());
        placeMaxLabel.setText(String.valueOf(evenement.getPlaceMax()));
        prixLabel.setText(String.valueOf(evenement.getPrix()));
        categorieLabel.setText(evenement.getCategorie());
        likesCountLabel.setText(String.valueOf(evenement.getLikesCount()));

        File imageFile = new File(IMAGE_DESTINATION_DIR + evenement.getPhoto());
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString(), 800, 250, true, true);
            photoView.setImage(image);
        } else {
            Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 800, 250, true, true);
            photoView.setImage(fallback);
        }

        photoView.setFitWidth(450);
        photoView.setFitHeight(300);
        photoView.setPreserveRatio(false);
        Rectangle clip = new Rectangle(450, 300);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        photoView.setClip(clip);

        String wikiUrl = "https://en.wikipedia.org/wiki/" + evenement.getLieu().replace(" ", "_");
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(wikiUrl, BarcodeFormat.QR_CODE, 100, 100);
            java.awt.Color bgColor = new java.awt.Color(224, 242, 254);
            java.awt.Color fgColor = java.awt.Color.BLACK;
            BufferedImage qrImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 100; x++) {
                for (int y = 0; y < 100; y++) {
                    qrImage.setRGB(x, y, bitMatrix.get(x, y) ? fgColor.getRGB() : bgColor.getRGB());
                }
            }
            javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(qrImage, null);
            qrCodeView.setImage(fxImage);
            qrCodeView.setFitWidth(100);
            qrCodeView.setFitHeight(100);
            Rectangle qrClip = new Rectangle(100, 100);
            qrClip.setArcWidth(24);
            qrClip.setArcHeight(24);
            qrCodeView.setClip(qrClip);
        } catch (WriterException e) {
            e.printStackTrace();
            qrCodeView.setImage(null);
        }

        initializeMap(evenement.getLieu());
        loadComments();
    }

    private void initializeMap(String location) {
        // Create JXMapViewer
        JXMapViewer mapViewer = new JXMapViewer();
        mapViewer.setPreferredSize(new Dimension(450, 200)); // Set size for Swing component

        // Set tile factory (OpenStreetMap)
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Enable zoom and pan interactivity
        PanMouseInputListener panListener = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(panListener);
        mapViewer.addMouseMotionListener(panListener);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        // Create SwingNode to embed JXMapViewer in JavaFX
        SwingNode swingNode = new SwingNode();
        createSwingContent(swingNode, mapViewer);

        // Geocode the location to get coordinates
        new Thread(() -> {
            try {
                String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + java.net.URLEncoder.encode(location, "UTF-8");
                String geoResp = httpGet(geoUrl);
                if (!geoResp.contains("\"results\":") || !geoResp.contains("\"latitude\":") || !geoResp.contains("\"longitude\":")) {
                    Platform.runLater(() -> {
                        Label errorLabel = new Label("Map unavailable");
                        errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2D3748;");
                        mapPane.getChildren().add(errorLabel);
                    });
                    return;
                }
                String latStr = geoResp.split("\"latitude\":")[1].split(",")[0];
                String lonStr = geoResp.split("\"longitude\":")[1].split(",")[0];
                double latitude = Double.parseDouble(latStr);
                double longitude = Double.parseDouble(lonStr);

                // Set map center and zoom
                GeoPosition eventLocation = new GeoPosition(latitude, longitude);
                mapViewer.setAddressLocation(eventLocation);
                mapViewer.setZoom(4);

                // Add a waypoint (pin) for the location
                DefaultWaypoint waypoint = new DefaultWaypoint(eventLocation);
                WaypointPainter<DefaultWaypoint> waypointPainter = new WaypointPainter<>();
                waypointPainter.setWaypoints(Set.of(waypoint));

                // Combine painters
                List<Painter<JXMapViewer>> painters = List.of(waypointPainter);
                CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
                mapViewer.setOverlayPainter(painter);

                // Add SwingNode to mapPane
                Platform.runLater(() -> {
                    mapPane.getChildren().clear();
                    mapPane.getChildren().add(swingNode);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Label errorLabel = new Label("Map unavailable");
                    errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2D3748;");
                    mapPane.getChildren().add(errorLabel);
                });
            }
        }).start();
    }

    private void createSwingContent(SwingNode swingNode, JXMapViewer mapViewer) {
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(mapViewer);
        });
    }

    private void loadComments() {
        commentsFlowPane.getChildren().clear();
        List<Commentaire> commentaires = commentaireService.readByEvenementId(selectedEvenement.getId());
        for (Commentaire commentaire : commentaires) {
            VBox commentCard = new VBox(10);
            commentCard.setPrefWidth(320);
            commentCard.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-padding: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 15; -fx-border-width: 1;");
            commentCard.setOnMouseEntered(e -> commentCard.setStyle("-fx-background-color: #F7FAFC; -fx-background-radius: 15; -fx-padding: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 15; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);"));
            commentCard.setOnMouseExited(e -> commentCard.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-padding: 15; -fx-border-color: #E2E8F0; -fx-border-radius: 15; -fx-border-width: 1;"));

            Text commentText = new Text(commentaire.getCommentaire());
            commentText.setWrappingWidth(280);
            commentText.setStyle("-fx-font-size: 14px; -fx-fill: #1A202C; -fx-font-family: 'Inter', 'Arial', sans-serif;");

            Label dateLabel = new Label(commentaire.getDate().toString().replace("T", " "));
            dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096; -fx-font-family: 'Inter', 'Arial', sans-serif;");

            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            Button editButton = new Button("Edit");
            editButton.setStyle("-fx-background-color: #63B3ED; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif;");
            editButton.setOnMouseEntered(e -> editButton.setStyle("-fx-background-color: #4299E1; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif;"));
            editButton.setOnMouseExited(e -> editButton.setStyle("-fx-background-color: #63B3ED; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif;"));
            editButton.setOnAction(e -> editComment(commentaire));

            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #F687B3; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif;");
            deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #F56565; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif;"));
            deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #F687B3; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif;"));
            deleteButton.setOnAction(e -> deleteComment(commentaire.getId()));

            buttonBox.getChildren().addAll(editButton, deleteButton);
            commentCard.getChildren().addAll(commentText, dateLabel, buttonBox);
            commentsFlowPane.getChildren().add(commentCard);
        }
    }

    @FXML
    public void addComment() {
        String commentText = commentField.getText().trim();
        if (!commentText.isEmpty()) {
            Commentaire commentaire = new Commentaire(selectedEvenement.getId(), commentText, LocalDateTime.now());
            commentaireService.create(commentaire);
            commentField.clear();
            loadComments();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty Comment");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a comment before posting.");
            alert.showAndWait();
        }
    }

    private void editComment(Commentaire commentaire) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Comment");
        dialog.setHeaderText("Update Your Comment");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5);");

        Label promptLabel = new Label("Edit your comment below:");
        promptLabel.setStyle("-fx-font-family: 'Inter', 'Arial', sans-serif; -fx-font-size: 16px; -fx-text-fill: #1A202C; -fx-font-weight: bold;");

        TextArea commentTextArea = new TextArea(commentaire.getCommentaire());
        commentTextArea.setWrapText(true);
        commentTextArea.setPrefRowCount(4);
        commentTextArea.setPrefColumnCount(30);
        commentTextArea.setStyle("-fx-font-family: 'Inter', 'Arial', sans-serif; " +
                "-fx-font-size: 14px; " +
                "-fx-background-color: #F7FAFC; " +
                "-fx-text-fill: #2D3748; " +
                "-fx-background-radius: 10; " +
                "-fx-border-radius: 10; " +
                "-fx-border-color: #CBD5E0; " +
                "-fx-padding: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2); " +
                "-fx-control-inner-background: #F7FAFC;");

        vbox.getChildren().addAll(promptLabel, commentTextArea);

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().setStyle("-fx-font-family: 'Inter', 'Arial', sans-serif; -fx-background-color: #F7FAFC; -fx-background-radius: 15; -fx-border-radius: 15;");

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setStyle("-fx-background-color: #5A67D8; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        saveButton.setOnMouseEntered(e -> saveButton.setStyle("-fx-background-color: #434190; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"));
        saveButton.setOnMouseExited(e -> saveButton.setStyle("-fx-background-color: #5A67D8; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"));

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.setStyle("-fx-background-color: #F687B3; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #F56565; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: #F687B3; -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 10; -fx-font-family: 'Inter', 'Arial', sans-serif; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"));

        saveButton.disableProperty().bind(commentTextArea.textProperty().isEmpty());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return commentTextArea.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newText -> {
            commentaire.setCommentaire(newText);
            commentaire.setDate(LocalDateTime.now());
            commentaireService.update(commentaire);
            loadComments();
        });
    }

    private void deleteComment(int commentId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Comment");
        alert.setHeaderText("Are you sure you want to delete this comment?");
        alert.setContentText("This action cannot be undone.");
        alert.getDialogPane().setStyle("-fx-font-family: 'Inter', 'Arial', sans-serif;");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            commentaireService.delete(commentId);
            loadComments();
        }
    }

    @FXML
    public void goBack() throws IOException {
        GuideDetailsFrontController parentController = (GuideDetailsFrontController) photoView.getScene().getRoot().getUserData();
        Stage stage = (Stage) nomLabel.getScene().getWindow();

        if (parentController != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Guide/GuideDetailsFront.fxml"));
            Parent root = loader.load();
            GuideDetailsFrontController controller = loader.getController();
            controller.setDetailsData(parentController.selectedGuide);
            stage.setScene(new Scene(root));
        } else {
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Evenement/EvenementListFront.fxml"));
            stage.setScene(new Scene(root));
        }
    }

    private void fetchAndDisplayWeather(String location) {
        System.out.println("[Weather] Requested location: " + location);
        new Thread(() -> {
            try {
                String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + java.net.URLEncoder.encode(location, "UTF-8");
                System.out.println("[Weather] Geocoding URL: " + geoUrl);
                String geoResp = httpGet(geoUrl);
                System.out.println("[Weather] Geocoding response: " + geoResp);
                if (!geoResp.contains("\"results\":") || !geoResp.contains("\"latitude\":") || !geoResp.contains("\"longitude\":")) {
                    System.out.println("[Weather] No results or missing lat/lon in geocoding response");
                    Platform.runLater(() -> weatherLabel.setText("Weather unavailable"));
                    return;
                }
                String latStr = geoResp.split("\"latitude\":")[1].split(",")[0];
                String lonStr = geoResp.split("\"longitude\":")[1].split(",")[0];
                System.out.println("[Weather] Parsed latitude: " + latStr + ", longitude: " + lonStr);
                double latitude = Double.parseDouble(latStr);
                double longitude = Double.parseDouble(lonStr);
                String apiUrl = String.format(Locale.US, "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true", latitude, longitude);
                System.out.println("[Weather] Weather API URL: " + apiUrl);
                String response = httpGet(apiUrl);
                System.out.println("[Weather] Weather API response: " + response);
                String weatherSection = response.split("\"current_weather\":\\{")[1].split("\\}")[0];
                String tempStr = weatherSection.split("\"temperature\":")[1].split(",")[0].trim();
                String windStr = weatherSection.split("\"windspeed\":")[1].split(",")[0].trim();
                String weather = String.format("Temp: %s°C, Wind: %s km/h", tempStr, windStr);
                Platform.runLater(() -> weatherLabel.setText(weather));
            } catch (Exception e) {
                System.out.println("[Weather] Exception: " + e);
                e.printStackTrace();
                Platform.runLater(() -> weatherLabel.setText("Weather unavailable"));
            }
        }).start();
    }

    private String httpGet(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();
        return content.toString();
    }
}