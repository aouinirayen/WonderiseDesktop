package com.esprit.wonderwise.Controller.BackOffice;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;

import com.esprit.wonderwise.Model.User;
import com.esprit.wonderwise.Service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tooltip;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.geometry.Pos;
import javafx.scene.shape.Circle;

public class UsersBackController {
    @FXML private FlowPane usersFlowPane;
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label pendingUsersLabel;
    @FXML private TextField searchField;
    @FXML private Button addUserBtn;
    @FXML private ComboBox<String> roleFilter;
    @FXML private ComboBox<String> statusFilter;

    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        // Populate filters
        roleFilter.getItems().addAll("All", "Client","Admin");
        roleFilter.getSelectionModel().selectFirst();
        statusFilter.getItems().addAll("All", "Active", "Pending");
        statusFilter.getSelectionModel().selectFirst();

        // Set up listeners
        searchField.textProperty().addListener((obs, oldVal, newVal) -> refreshUsers());
        roleFilter.setOnAction(e -> refreshUsers());
        statusFilter.setOnAction(e -> refreshUsers());
        addUserBtn.setOnAction(e -> handleAddUser());

        refreshUsers();
    }

    private void refreshUsers() {
        usersFlowPane.getChildren().clear();
        java.util.List<User> users = userService.getAllUsers();
        String search = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
        String role = roleFilter.getValue();
        String status = statusFilter.getValue();

        int total = 0, active = 0, pending = 0;
        boolean foundAny = false;
        for (User user : users) {
            boolean matches = true;
            if (!search.isEmpty() && !(user.getUsername().toLowerCase().contains(search) || user.getEmail().toLowerCase().contains(search))) {
                matches = false;
            }
            if (role != null && !role.equals("All") && !user.getRole().equalsIgnoreCase(role)) {
                matches = false;
            }
            if (status != null && !status.equals("All") && !user.getStatus().equalsIgnoreCase(status)) {
                matches = false;
            }
            if (matches) {
                usersFlowPane.getChildren().add(createUserCard(user));
                foundAny = true;
                total++;
                if (user.getStatus() != null && user.getStatus().equalsIgnoreCase("Active")) active++;
                if (user.getStatus() != null && user.getStatus().equalsIgnoreCase("Pending")) pending++;
            }
        }
        if (!foundAny) {
            Label noResult = new Label("No users found");
            noResult.setStyle("-fx-font-size: 20px; -fx-text-fill: #b0b3b8; -fx-font-weight: bold;");
            usersFlowPane.getChildren().add(noResult);
            usersFlowPane.setAlignment(Pos.CENTER);
        } else {
            usersFlowPane.setAlignment(Pos.TOP_LEFT);
        }
        totalUsersLabel.setText(String.valueOf(total));
        activeUsersLabel.setText(String.valueOf(active));
        pendingUsersLabel.setText(String.valueOf(pending));
    }

    private VBox createUserCard(User user) {
        VBox cardRoot = new VBox();
        cardRoot.setStyle("-fx-background-color: #f6f7fb; -fx-background-radius: 14; -fx-padding: 18 20; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.07), 0, 0, 0, 0); -fx-cursor: hand;");
        cardRoot.setPrefWidth(340);
        cardRoot.setMinWidth(340);
        cardRoot.setMaxWidth(340);

        HBox mainRow = new HBox(16);
        mainRow.setAlignment(Pos.CENTER_LEFT);

        // Profile image (left)
        Image img;
        if (user.getProfilePicture() == null || user.getProfilePicture().isEmpty()) {
            img = new Image(getClass().getResource("/com/esprit/wonderwise/images/notfound.png").toExternalForm());
        } else {
            try {
                String imagePath = IMAGE_DESTINATION_DIR + java.io.File.separator + user.getProfilePicture();
                java.io.File imageFile = new java.io.File(imagePath);
                if (imageFile.exists()) {
                    img = new Image(imageFile.toURI().toString(), true);
                    if (img.isError()) throw new Exception();
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                img = new Image(getClass().getResource("/com/esprit/wonderwise/images/notfound.png").toExternalForm());
            }
        }
        ImageView imageView = new ImageView(img);
        imageView.setFitHeight(60);
        imageView.setFitWidth(60);
        imageView.setClip(new Circle(30, 30, 30));
        imageView.setStyle("-fx-effect: dropshadow(gaussian, #bfc9d9, 3, 0.2, 0, 2);");

        VBox infoBox = new VBox(4);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        // Username, role, status in one row
        HBox nameRoleStatusRow = new HBox(8);
        nameRoleStatusRow.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(user.getUsername());
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #23395d;");
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        Label role = new Label(user.getRole());
        role.setStyle("-fx-font-size: 12px; -fx-background-color: #e3eafc; -fx-background-radius: 6; -fx-padding: 2 8; -fx-text-fill: #3b82f6;");
        Label status = new Label(user.getStatus());
        status.setStyle("-fx-font-size: 12px; -fx-background-color: #e9fbe5; -fx-background-radius: 6; -fx-padding: 2 8; -fx-text-fill: #27ae60;");
        if (user.getStatus() != null && user.getStatus().equalsIgnoreCase("Pending")) {
            status.setStyle("-fx-font-size: 12px; -fx-background-color: #fff3f3; -fx-background-radius: 6; -fx-padding: 2 8; -fx-text-fill: #e74c3c;");
        }
        nameRoleStatusRow.getChildren().addAll(name, spacer, role, status);

        // Email below
        Label email = new Label(user.getEmail());
        email.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");
        infoBox.getChildren().addAll(nameRoleStatusRow, email);

        mainRow.getChildren().addAll(imageView, infoBox);

        // Buttons row (below info)
        HBox btns = new HBox(12);
        btns.setAlignment(Pos.CENTER_LEFT);
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 12px; -fx-padding: 4 18;");
        editBtn.setOnAction(e -> handleEditUser(user));
        Button delBtn = new Button("Delete");
        delBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-size: 12px; -fx-padding: 4 18;");
        delBtn.setOnAction(e -> handleDeleteUser(user));
        btns.getChildren().addAll(editBtn, delBtn);

        cardRoot.getChildren().addAll(mainRow, btns);
        cardRoot.setSpacing(12);

        // Animation
        FadeTransition ft = new FadeTransition(Duration.millis(600), cardRoot);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        Tooltip.install(cardRoot, new Tooltip(user.getUsername() + "\n" + user.getEmail()));

        cardRoot.setOnMouseEntered(e -> cardRoot.setStyle("-fx-background-color: #e3eafc; -fx-background-radius: 14; -fx-padding: 18 20; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.18), 0, 0, 0, 0); -fx-cursor: hand;"));
        cardRoot.setOnMouseExited(e -> cardRoot.setStyle("-fx-background-color: #f6f7fb; -fx-background-radius: 14; -fx-padding: 18 20; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.07), 0, 0, 0, 0); -fx-cursor: hand;"));

        return cardRoot;
    }

    private void handleEditUser(User user) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Users/UserEdit.fxml"));
            javafx.scene.Parent root = loader.load();
            com.esprit.wonderwise.Controller.BackOffice.UserEditController controller = loader.getController();
            controller.setUser(user);
            javafx.stage.Stage stage = (javafx.stage.Stage) usersFlowPane.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure you want to delete this user?");
        alert.setContentText("Username: " + user.getUsername() + "\nEmail: " + user.getEmail());
        ButtonType okButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, cancelButton);
        alert.showAndWait().ifPresent(type -> {
            if (type == okButton) {
                boolean deleted = userService.deleteUser(user.getId());
                if (deleted) {
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("User Deleted");
                    info.setHeaderText(null);
                    info.setContentText("User deleted successfully.");
                    info.showAndWait();
                    refreshUsers();
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText(null);
                    error.setContentText("Failed to delete user. Please try again.");
                    error.showAndWait();
                }
            }
        });
    }

    private void handleAddUser() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Users/UserAdd.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) usersFlowPane.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            // Optionally: set title, clear selection, etc.
            // stage.setTitle("Add User");
            // After adding, refreshUsers() should be called from the Add controller after save/cancel.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

