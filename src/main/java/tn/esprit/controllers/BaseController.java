package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.esprit.utils.ResponsiveUtils;

import java.io.IOException;

/**
 * Contrôleur de base pour toutes les interfaces
 * Assure la cohérence des dimensions et du comportement responsive
 */
public abstract class BaseController {

    @FXML
    protected Label headerTitle;
    
    @FXML
    protected Button backButton;
    
    /**
     * Initialise les éléments communs à toutes les interfaces
     */
    @FXML
    protected void initialize() {
        // Configuration du bouton de retour si présent
        if (backButton != null) {
            backButton.setOnAction(event -> handleBack());
        }
        
        // Initialisation spécifique à chaque contrôleur
        initializeSpecific();
    }
    
    /**
     * Méthode à implémenter dans chaque contrôleur spécifique
     * pour l'initialisation propre à chaque interface
     */
    protected abstract void initializeSpecific();
    
    /**
     * Définit le titre de l'interface
     * @param title Le titre à afficher
     */
    protected void setHeaderTitle(String title) {
        if (headerTitle != null) {
            headerTitle.setText(title);
        }
    }
    
    /**
     * Gère le retour à l'interface précédente
     */
    @FXML
    protected void handleBack() {
        // Par défaut, retourne à la liste des expériences
        navigateTo("/fxml/ListExperience.fxml", "Liste des Expériences");
    }
    
    /**
     * Navigue vers une autre interface
     * @param fxmlPath Chemin du fichier FXML
     * @param title Titre de la nouvelle interface
     */
    protected void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            ResponsiveUtils.setupResponsiveStage(stage, title, root);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Récupère la scène actuelle
     * @return La scène actuelle
     */
    protected Scene getCurrentScene() {
        return backButton != null ? backButton.getScene() : null;
    }
    
    /**
     * Récupère le stage actuel
     * @return Le stage actuel
     */
    protected Stage getCurrentStage() {
        return backButton != null ? (Stage) backButton.getScene().getWindow() : null;
    }
    
    /**
     * Adapte l'interface à la taille de l'écran
     */
    protected void adaptToScreenSize() {
        if (getCurrentStage() != null) {
            ResponsiveUtils.adaptToScreenSize(getCurrentStage());
        }
    }
}
