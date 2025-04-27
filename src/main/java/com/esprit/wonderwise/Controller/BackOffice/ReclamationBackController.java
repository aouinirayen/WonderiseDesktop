package com.esprit.wonderwise.Controller.BackOffice;

import com.esprit.wonderwise.Model.Reclamation;
import com.esprit.wonderwise.Model.Status;
import com.esprit.wonderwise.Service.ReclamationService;
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReclamationBackController {

    @FXML private VBox reclamationContainer;
    @FXML private Button viewResponsesButton;
    @FXML private Label pageInfoLabel;
    @FXML private ComboBox<Status> statusFilter;
    @FXML private TextField searchField;

    private final ReclamationService reclamationService = new ReclamationService();
    private int currentPage = 1;
    private static final int ITEMS_PER_PAGE = 3;
    private Status selectedStatus = null;
    private String searchQuery = "";

    // 🔥 Nouvelle map de transitions
    private static final Map<Status, List<Status>> transitions = Map.of(
            Status.ENVOYEE, List.of(Status.EN_COURS),
            Status.EN_COURS, List.of(Status.TRAITEE, Status.REJETEE)
    );

    @FXML
    public void initialize() {
        statusFilter.getItems().clear();
        statusFilter.getItems().add(null);
        statusFilter.getItems().addAll(Status.values());

        statusFilter.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Status item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? "Tous les statuts" : item.getLabel());
            }
        });

        statusFilter.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Status item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? "Tous les statuts" : item.getLabel());
            }
        });

        statusFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedStatus = newVal;
            currentPage = 1;
            loadPage(currentPage);
        });

        loadPage(currentPage);
    }

    private void loadPage(int page) {
        reclamationContainer.getChildren().clear();
        List<Reclamation> reclamations = reclamationService.findFilteredPage(
                page, ITEMS_PER_PAGE, selectedStatus, searchQuery);

        for (Reclamation r : reclamations) {
            reclamationContainer.getChildren().add(createReclamationCard(r));
        }

        int totalPages = reclamationService.getTotalFilteredPages(
                ITEMS_PER_PAGE, selectedStatus, searchQuery);
        pageInfoLabel.setText("Page " + page + "/" + totalPages);
    }

    private Node createReclamationCard(Reclamation r) {
        VBox card = new VBox();
        card.setSpacing(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: #BDC3C7; -fx-border-radius: 10; -fx-background-color: #F9F9F9; -fx-background-radius: 10;");

        FadeTransition fade = new FadeTransition(Duration.millis(500), card);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        Label objet = new Label("Objet: " + r.getObjet());
        objet.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2C3E50;");

        Label desc = new Label("Description: " + r.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");

        Label date = new Label("Date: " + r.getDate().toString());
        date.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");

        Label statusLabel = new Label("Statut: " + r.getStatus().getLabel());
        updateStatusLabelStyle(statusLabel, r.getStatus());

        // ➡️ Rendre le statut cliquable
        statusLabel.setOnMouseClicked(event -> handleStatusClick(r, statusLabel));
        statusLabel.setStyle(statusLabel.getStyle() + "-fx-cursor: hand;");

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.setStyle("-fx-alignment: center;");

        Button deleteButton = createStyledButton("Supprimer", "#e74c3c");
        Button responseButton = createStyledButton("Répondre", "#2ecc71");

        deleteButton.setOnAction(event -> handleDelete(r, card));
        responseButton.setOnAction(event -> handleResponse(r));

        buttonBox.getChildren().addAll(deleteButton, responseButton);
        card.getChildren().addAll(objet, desc, date, statusLabel, buttonBox);

        return card;
    }

    private void updateStatusLabelStyle(Label label, Status status) {
        String style = "-fx-font-weight: bold; -fx-font-size: 14px;";
        switch (status) {
            case TRAITEE -> style += "-fx-text-fill: #27AE60;";
            case EN_COURS -> style += "-fx-text-fill: #F39C12;";
            case REJETEE -> style += "-fx-text-fill: #E74C3C;";
            case ENVOYEE -> style += "-fx-text-fill: #3498DB;";
        }
        label.setStyle(style);
    }

    private Button createStyledButton(String text, String backgroundColor) {
        Button button = new Button(text);
        String baseStyle = "-fx-background-color: " + backgroundColor + ";" +
                "-fx-text-fill: white; -fx-font-size: 13px;" +
                "-fx-font-weight: bold; -fx-background-radius: 8;" +
                "-fx-padding: 6 15; -fx-graphic-text-gap: 8;";
        button.setStyle(baseStyle);

        button.setOnMouseEntered(e -> button.setStyle(baseStyle.replace(backgroundColor, "derive(" + backgroundColor + ", 20%)")));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    private void handleDelete(Reclamation r, VBox card) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette réclamation ?");

        if (confirmationAlert.showAndWait().get() == ButtonType.OK) {
            if (reclamationService.delete(r)) {
                reclamationContainer.getChildren().remove(card);
                DialogUtils.showCustomDialog("Succès", "Réclamation supprimée avec succès", true,
                        (Stage) reclamationContainer.getScene().getWindow());
                loadPage(currentPage);
            } else {
                DialogUtils.showCustomDialog("Erreur", "Échec de la suppression", false,
                        (Stage) reclamationContainer.getScene().getWindow());
            }
        }
    }

    private void handleResponse(Reclamation r) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Reponse.fxml"));
            Parent root = loader.load();
            ReponseController controller = loader.getController();
            controller.setReclamationId(r.getId());

            Stage stage = (Stage) viewResponsesButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            DialogUtils.showCustomDialog("Erreur", "Impossible d'ouvrir la page de réponse", false,
                    (Stage) reclamationContainer.getScene().getWindow());
        }
    }

    private void handleStatusClick(Reclamation r, Label statusLabel) {
        List<Status> availableStatuses = List.of(Status.EN_COURS, Status.TRAITEE, Status.REJETEE);

        ChoiceDialog<Status> dialog = new ChoiceDialog<>(availableStatuses.get(0), availableStatuses);
        dialog.setTitle("Changement de statut");

        dialog.setHeaderText("Sélectionnez un nouveau statut pour la réclamation");
        dialog.setContentText("Nouveau statut :");

        // ✨ Ajouter une petite icône (optionnel, à toi de choisir)
        Label graphicLabel = new Label("🛠️");
        graphicLabel.setStyle("-fx-font-size: 48px;");
        dialog.setGraphic(graphicLabel);

        // ✨ Customiser l'affichage de la liste
        ListView<Status> listView = (ListView<Status>) dialog.getDialogPane().lookup(".list-view");
        if (listView != null) {
            listView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Status item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getLabel()); // afficher joli label
                        setStyle("-fx-font-size: 14px; -fx-padding: 10 0 10 0;");
                    }
                }
            });
        }

        // ✨ Appliquer un style CSS doux
        DialogPane pane = dialog.getDialogPane();
        pane.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 10; -fx-background-radius: 10;");
        pane.lookupButton(ButtonType.OK).setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        pane.lookupButton(ButtonType.CANCEL).setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

        // ✨ Optionnel: Douce animation d'ouverture (fade-in)
        FadeTransition ft = new FadeTransition(Duration.millis(300), pane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        // ✨ Afficher le choix
        Optional<Status> result = dialog.showAndWait();
        result.ifPresent(newStatus -> updateStatusDirectly(r, newStatus, statusLabel));
    }


    private void updateStatusDirectly(Reclamation r, Status newStatus, Label statusLabel) {
        if (reclamationService.updateStatus(r.getId(), newStatus)) {
            r.setStatus(newStatus);
            statusLabel.setText("Statut: " + newStatus.getLabel());
            updateStatusLabelStyle(statusLabel, newStatus);

            // 🎨 Animation douce pour montrer le changement
            FadeTransition fade = new FadeTransition(Duration.millis(400), statusLabel);
            fade.setFromValue(0.5);
            fade.setToValue(1.0);
            fade.play();
        } else {
            DialogUtils.showCustomDialog("Erreur", "Impossible de mettre à jour le statut", false,
                    (Stage) reclamationContainer.getScene().getWindow());
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        searchQuery = searchField.getText().trim();
        currentPage = 1;
        loadPage(currentPage);
    }

    @FXML
    private void handlePreviousPage(ActionEvent event) {
        if (currentPage > 1) {
            loadPage(--currentPage);
        }
    }

    @FXML
    private void handleNextPage(ActionEvent event) {
        int totalPages = reclamationService.getTotalFilteredPages(ITEMS_PER_PAGE, selectedStatus, searchQuery);
        if (currentPage < totalPages) {
            loadPage(++currentPage);
        }
    }

    @FXML
    private void handleViewResponses(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/BackOffice/ReponseListe.fxml"));
            Stage stage = (Stage) viewResponsesButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Réponses");
        } catch (IOException e) {
            DialogUtils.showCustomDialog("Erreur", "Impossible de charger la page des réponses", false,
                    (Stage) reclamationContainer.getScene().getWindow());
        }
    }

    @FXML
    private void handleViewStats(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/BackOffice/ReclamationStats.fxml"));
            Stage stage = (Stage) reclamationContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Statistiques");
        } catch (IOException e) {
            DialogUtils.showCustomDialog("Erreur", "Impossible de charger la page des statistiques", false,
                    (Stage) reclamationContainer.getScene().getWindow());
        }
    }
}
