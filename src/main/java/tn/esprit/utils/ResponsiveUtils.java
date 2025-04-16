package tn.esprit.utils;

import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Classe utilitaire pour gérer les dimensions et le comportement responsive des interfaces
 */
public class ResponsiveUtils {

    // Dimensions standard pour toutes les interfaces
    public static final double BASE_WIDTH = 800;
    public static final double BASE_HEIGHT = 600;
    public static final double MIN_WIDTH = 600;
    public static final double MIN_HEIGHT = 400;
    public static final double MAX_WIDTH = 1200;
    public static final double MAX_HEIGHT = 900;

    /**
     * Configure une scène avec les dimensions standard et les contraintes responsives
     * @param root Le nœud racine de la scène
     * @return La scène configurée
     */
    public static Scene createResponsiveScene(Parent root) {
        Scene scene = new Scene(root, BASE_WIDTH, BASE_HEIGHT);
        
        // Ajouter les feuilles de style communes
        scene.getStylesheets().add("/styles/responsive.css");
        
        return scene;
    }

    /**
     * Configure un Stage avec les dimensions standard et les contraintes responsives
     * @param stage Le Stage à configurer
     * @param title Le titre de la fenêtre
     * @param root Le nœud racine de la scène
     */
    public static void setupResponsiveStage(Stage stage, String title, Parent root) {
        Scene scene = createResponsiveScene(root);
        
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMaxWidth(MAX_WIDTH);
        stage.setMaxHeight(MAX_HEIGHT);
        
        // Adapter la taille de la fenêtre à l'écran
        adaptToScreenSize(stage);
    }
    
    /**
     * Adapte la taille de la fenêtre en fonction de la taille de l'écran
     * @param stage Le Stage à adapter
     */
    public static void adaptToScreenSize(Stage stage) {
        // Obtenir les dimensions de l'écran
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        
        // Ajuster la taille de la fenêtre en fonction de l'écran
        if (screenWidth < 1024 || screenHeight < 768) {
            // Petit écran
            stage.getScene().getRoot().getStyleClass().add("small-screen");
            stage.setWidth(MIN_WIDTH);
            stage.setHeight(MIN_HEIGHT);
        } else if (screenWidth >= 1920 || screenHeight >= 1080) {
            // Grand écran
            stage.getScene().getRoot().getStyleClass().add("large-screen");
            stage.setWidth(BASE_WIDTH * 1.2);
            stage.setHeight(BASE_HEIGHT * 1.2);
        } else {
            // Écran moyen (taille par défaut)
            stage.setWidth(BASE_WIDTH);
            stage.setHeight(BASE_HEIGHT);
        }
        
        // Centrer la fenêtre sur l'écran
        stage.centerOnScreen();
    }
    
    /**
     * Calcule une dimension proportionnelle en fonction de la taille de base
     * @param baseSize Taille de base
     * @param percentage Pourcentage de la taille de base
     * @return La dimension calculée
     */
    public static double calculateProportionalSize(double baseSize, double percentage) {
        return baseSize * (percentage / 100.0);
    }
}
