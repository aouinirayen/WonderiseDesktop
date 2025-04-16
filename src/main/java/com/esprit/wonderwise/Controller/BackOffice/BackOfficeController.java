package com.esprit.wonderwise.Controller.BackOffice;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class BackOfficeController {

    @FXML
    private Pane contentPane;

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
            Pane newContent = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}