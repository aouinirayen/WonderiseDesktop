package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.Reclamation;
import com.esprit.wonderwise.Model.Status;
import com.esprit.wonderwise.Service.ReclamationService;
import com.esprit.wonderwise.Utils.NotificationUtil;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.List;

public class ListeReclamation {
    private static final int TAILLE_PAGE = 2;
    private int pageActuelle = 0;
    private List<Reclamation> reclamationsUtilisateur;

    @FXML
    private Label labelMesReclamations;

    @FXML
    private VBox listeReclamationsVBox;

    private final ReclamationService service = new ReclamationService();

    @FXML
    public void initialize() {
        labelMesReclamations.setText("🗂️ Mes Réclamations");
        afficherReclamationsUtilisateur(1);

        // Attendre que la Scene soit prête pour appeler cette méthode
        Platform.runLater(() -> checkReclamationsAnciennes(1));
    }

    private void afficherReclamationsUtilisateur(int userId) {
        List<Reclamation> toutes = service.find();

        reclamationsUtilisateur = toutes.stream()
                .filter(r -> r.getUserId() == userId)
                .sorted((r1, r2) -> r2.getDate().compareTo(r1.getDate()))
                .toList();

        afficherPage(pageActuelle);
    }

    @FXML
    private void afficherPage(int page) {
        listeReclamationsVBox.getChildren().clear();

        int debut = page * TAILLE_PAGE;
        int fin = Math.min(debut + TAILLE_PAGE, reclamationsUtilisateur.size());
        List<Reclamation> pageReclamations = reclamationsUtilisateur.subList(debut, fin);

        for (Reclamation r : pageReclamations) {
            VBox card = new VBox(8);
            card.setPadding(new Insets(12));
            card.setMaxWidth(500);
            card.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-background-color: #E8F5E9; -fx-background-radius: 10; -fx-border-radius: 10;");

            boolean isToday = r.getDate().equals(LocalDate.now());
            if (isToday) {
                Label nouveauLabel = new Label("NOUVEAU");
                nouveauLabel.setStyle("-fx-background-color: #43A047; -fx-text-fill: white; -fx-padding: 3 8; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 5;");
                card.getChildren().add(nouveauLabel);
            }

            Label objetLabel = new Label("Objet: " + r.getObjet());
            objetLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2C3E50; ");

            Label descriptionLabel = new Label("📋 " + r.getDescription());
            descriptionLabel.setWrapText(true);
            descriptionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495E;");

            Label dateLabel = new Label("📅 " + r.getDate().toString());
            dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #34495E;");

            Label statusLabel = new Label("📌 Statut: " + r.getStatus().name());
            String statusColor = switch (r.getStatus()) {
                case ENVOYEE -> "#4CAF50";
                case EN_COURS -> "#FFC107";
                case TRAITEE -> "#F44336";
                default -> "#607D8B";
            };
            statusLabel.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 6;");

            long joursDepuisCreation = java.time.temporal.ChronoUnit.DAYS.between(r.getDate(), LocalDate.now());
            boolean estEnAttente = r.getStatus() == Status.ENVOYEE || r.getStatus() == Status.EN_COURS;
            int SEUIL_ALERTE = 7;

            if (estEnAttente && joursDepuisCreation >= SEUIL_ALERTE) {
                Label alerteLabel = new Label("⚠️ En attente depuis " + joursDepuisCreation + " jours");
                alerteLabel.setStyle("-fx-text-fill: #D84315; -fx-font-weight: bold; -fx-padding: 5 0; -fx-font-size: 13px;");
                card.getChildren().add(alerteLabel);

                Tooltip alerteTooltip = new Tooltip("Cette réclamation est en attente depuis " + joursDepuisCreation + " jours.");
                Tooltip.install(card, alerteTooltip);
            }

            Tooltip tooltip = new Tooltip("Créée le : " + r.getDate().toString());
            Tooltip.install(card, tooltip);

            card.getChildren().addAll(objetLabel, descriptionLabel, dateLabel, statusLabel);

            card.setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.millis(400), card);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

            listeReclamationsVBox.getChildren().add(card);
        }

        ajouterBoutonsPagination();
    }

    private void ajouterBoutonsPagination() {
        int totalPages = (int) Math.ceil((double) reclamationsUtilisateur.size() / TAILLE_PAGE);

        HBox paginationBox = new HBox(10);
        paginationBox.setStyle("-fx-alignment: center; -fx-padding: 20;");

        for (int i = 0; i < totalPages; i++) {
            int pageIndex = i;
            Button pageBtn = new Button(String.valueOf(i + 1));
            pageBtn.setStyle("-fx-background-radius: 6; -fx-background-color: #CFD8DC;");
            if (i == pageActuelle) {
                pageBtn.setStyle("-fx-background-radius: 6; -fx-background-color: #00796B; -fx-text-fill: white;");
            }
            pageBtn.setOnAction(e -> {
                pageActuelle = pageIndex;
                afficherPage(pageActuelle);
            });
            paginationBox.getChildren().add(pageBtn);
        }

        listeReclamationsVBox.getChildren().add(paginationBox);
    }

    private void checkReclamationsAnciennes(int userId) {
        List<Reclamation> toutes = service.find();

        List<Reclamation> reclamationsAnciennes = toutes.stream()
                .filter(r -> r.getUserId() == userId)
                .filter(r -> r.getStatus() == Status.ENVOYEE || r.getStatus() == Status.EN_COURS)
                .filter(r -> java.time.temporal.ChronoUnit.DAYS.between(r.getDate(), LocalDate.now()) >= 7)
                .toList();

        if (!reclamationsAnciennes.isEmpty()) {
            StringBuilder message = new StringBuilder("⚠️ Réclamations en attente > 7 jours :\n");

            for (Reclamation r : reclamationsAnciennes) {
                long jours = java.time.temporal.ChronoUnit.DAYS.between(r.getDate(), LocalDate.now());
                message.append("• ").append(r.getObjet()).append(" (").append(jours).append(" jours)\n");
            }

            Stage currentStage = (Stage) listeReclamationsVBox.getScene().getWindow();
            NotificationUtil.showNotification(currentStage, message.toString());
        }
    }
}
