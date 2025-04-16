package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.Reclamation;
import com.esprit.wonderwise.Service.ReclamationService;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.awt.event.MouseEvent;
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
    }
    private void afficherReclamationsUtilisateur(int userId) {
        ReclamationService service = new ReclamationService();
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

            // Ligne "NOUVEAU"
            boolean isToday = r.getDate().equals(LocalDate.now());
            if (isToday) {
                Label nouveauLabel = new Label("NOUVEAU");
                nouveauLabel.setStyle("-fx-background-color: #43A047; -fx-text-fill: white; -fx-padding: 3 8; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 5;");
                card.getChildren().add(nouveauLabel);
            }

            // Objet
            Label objetLabel = new Label("Objet: " + r.getObjet());
            objetLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2C3E50; ");

            // Description
            Label descriptionLabel = new Label("📋 " + r.getDescription());
            descriptionLabel.setWrapText(true);
            descriptionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495E;");

            // Date
            Label dateLabel = new Label("📅 " + r.getDate().toString());
            dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #34495E;");

            // Statut avec couleur dynamique
            Label statusLabel = new Label("📌 Statut: " + r.getStatus().name());
            String statusColor = switch (r.getStatus()) {
                case ENVOYEE -> "#4CAF50";
                case EN_COURS -> "#FFC107";
                case TRAITEE -> "#F44336";
                default -> "#607D8B";
            };
            statusLabel.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 6;");

            // Tooltip
            Tooltip tooltip = new Tooltip("Créée le : " + r.getDate().toString());
            Tooltip.install(card, tooltip);

            // Ajout des éléments à la carte
            card.getChildren().addAll(objetLabel, descriptionLabel, dateLabel, statusLabel);

            // Animation
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



}
