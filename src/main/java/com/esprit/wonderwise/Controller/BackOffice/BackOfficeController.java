package com.esprit.wonderwise.Controller.BackOffice;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class BackOfficeController {

    @FXML
    private VBox contentPane;

    @FXML
    private NavBarBackController navBarController;

    @FXML
    public void initialize() {
        if (navBarController != null) {
            navBarController.setBackOfficeController(this);
        }
    }

    public void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loadContent(loader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadContent(FXMLLoader loader) throws IOException {
        Node newContent = loader.load();
        
        // Set VBox.grow priority for the new content
        VBox.setVgrow(newContent, javafx.scene.layout.Priority.ALWAYS);

        Object controller = loader.getController();
        if (controller instanceof AddOffreController) {
            ((AddOffreController) controller).setBackOfficeController(this);
        } else if (controller instanceof ManageOffresController) {
            ((ManageOffresController) controller).setBackOfficeController(this);
        } else if (controller instanceof AddReservationController) {
            ((AddReservationController) controller).setBackOfficeController(this);
        } else if (controller instanceof ManageReservationsController) {
            ((ManageReservationsController) controller).setBackOfficeController(this);
        }

        contentPane.getChildren().clear();
        contentPane.getChildren().add(newContent);
    }
}