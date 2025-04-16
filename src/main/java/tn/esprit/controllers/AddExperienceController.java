package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.models.Experience;
import tn.esprit.services.ExperienceService;
import tn.esprit.utils.ResponsiveUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

public class AddExperienceController extends BaseController {
    
    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField imagePathField;
    @FXML private ImageView imagePreview;
    @FXML private TextField lieuField;
    @FXML private ComboBox<String> categorieField;
    @FXML private TextField idClientField;
    @FXML private DatePicker datePicker;
    @FXML private Button goToListButton;
    @FXML private Button goToDetailsButton;
    
    private final ExperienceService experienceService = new ExperienceService();
    private File selectedImageFile;
    private static final String UPLOAD_DIR = "src/main/resources/images/uploads/";
    
    @Override
    protected void initializeSpecific() {
        // Définir le titre de l'interface
        setHeaderTitle("Ajouter une Expérience");
        
        // Initialiser les catégories
        categorieField.getItems().addAll(
            "Voyage", "Culture", "Sport", "Gastronomie", "Aventure"
        );
        
        // Définir la date par défaut
        datePicker.setValue(LocalDate.now());
        
        // Adapter l'interface à la taille de l'écran
        adaptToScreenSize();
    }
    
    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        // Ouvrir le sélecteur de fichier
        File file = fileChooser.showOpenDialog(imagePathField.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            imagePathField.setText(file.getAbsolutePath());
            
            // Afficher l'aperçu de l'image
            try {
                Image image = new Image(file.toURI().toString());
                imagePreview.setImage(image);
            } catch (Exception e) {
                showError("Impossible de charger l'aperçu de l'image: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleAdd() {
        if (validateInputs()) {
            try {
                // Copier l'image sélectionnée dans le dossier uploads
                String imagePath = "";
                if (selectedImageFile != null) {
                    imagePath = copyImageToUploadsFolder(selectedImageFile);
                }
                
                // Créer une nouvelle expérience
                Experience newExperience = new Experience(
                    0, // ID sera généré par la base de données
                    titreField.getText(),
                    descriptionArea.getText(),
                    lieuField.getText(),
                    datePicker.getValue(),
                    imagePath,
                    categorieField.getValue()
                );
                
                newExperience.setDateCreation(LocalDate.now());
                newExperience.setIdClient(Integer.parseInt(idClientField.getText()));
                
                // Ajouter l'expérience à la base de données
                experienceService.add(newExperience);
                
                // Afficher un message de succès
                showSuccess("Expérience ajoutée avec succès !");
                
                // Retourner à la liste des expériences
                navigateTo("/fxml/ListExperience.fxml", "Liste des Expériences");
                
            } catch (SQLException e) {
                showError("Erreur lors de l'ajout : " + e.getMessage());
            } catch (NumberFormatException e) {
                showError("L'ID client doit être un nombre entier");
            } catch (IOException e) {
                showError("Erreur lors de la copie de l'image : " + e.getMessage());
            }
        }
    }
    
    private String copyImageToUploadsFolder(File sourceFile) throws IOException {
        // Créer le dossier uploads s'il n'existe pas
        File uploadsDir = new File(UPLOAD_DIR);
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs();
        }
        
        // Générer un nom de fichier unique pour éviter les collisions
        String fileName = UUID.randomUUID().toString() + getFileExtension(sourceFile.getName());
        Path destinationPath = Paths.get(UPLOAD_DIR + fileName);
        
        // Copier le fichier
        Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        
        // Retourner le chemin relatif pour le stockage en base de données
        return "/images/uploads/" + fileName;
    }
    
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }
    
    @FXML
    private void handleCancel() {
        navigateTo("/fxml/ListExperience.fxml", "Liste des Expériences");
    }
    
    @FXML
    private void goToExperienceList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListExperience.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) goToListButton.getScene().getWindow();
            ResponsiveUtils.setupResponsiveStage(stage, "Liste des Expériences", root);
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de la navigation : " + e.getMessage());
        }
    }
    
    @FXML
    private void goToExperienceDetails() {
        try {
            // Récupérer une expérience existante pour l'afficher
            List<Experience> experiences = experienceService.readAll();
            if (experiences.isEmpty()) {
                showError("Aucune expérience disponible. Veuillez d'abord ajouter une expérience.");
                return;
            }
            
            // Prendre la première expérience disponible
            Experience experience = experiences.get(0);
            
            // Charger l'interface des détails
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ExperienceDetails.fxml"));
            Parent root = loader.load();
            
            // Configurer le contrôleur
            ExperienceDetailsController controller = loader.getController();
            controller.setExperience(experience);
            controller.setOnBackAction(() -> {
                try {
                    // Revenir à l'interface d'ajout
                    FXMLLoader addLoader = new FXMLLoader(getClass().getResource("/fxml/AddExperience.fxml"));
                    Parent addRoot = addLoader.load();
                    Stage stage = (Stage) goToDetailsButton.getScene().getWindow();
                    ResponsiveUtils.setupResponsiveStage(stage, "Ajouter une Expérience", addRoot);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            
            // Afficher l'interface
            Stage stage = (Stage) goToDetailsButton.getScene().getWindow();
            ResponsiveUtils.setupResponsiveStage(stage, "Détails de l'Expérience", root);
            
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showError("Erreur lors de la navigation : " + e.getMessage());
        }
    }
    
    private boolean validateInputs() {
        if (titreField.getText().isEmpty()) {
            showError("Le titre est obligatoire");
            return false;
        }
        if (descriptionArea.getText().isEmpty()) {
            showError("La description est obligatoire");
            return false;
        }
        if (selectedImageFile == null) {
            showError("Veuillez sélectionner une image");
            return false;
        }
        if (lieuField.getText().isEmpty()) {
            showError("Le lieu est obligatoire");
            return false;
        }
        if (categorieField.getValue() == null) {
            showError("La catégorie est obligatoire");
            return false;
        }
        if (datePicker.getValue() == null) {
            showError("La date est obligatoire");
            return false;
        }
        if (idClientField.getText().isEmpty()) {
            showError("L'ID client est obligatoire");
            return false;
        }
        try {
            Integer.parseInt(idClientField.getText());
        } catch (NumberFormatException e) {
            showError("L'ID client doit être un nombre entier");
            return false;
        }
        return true;
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
