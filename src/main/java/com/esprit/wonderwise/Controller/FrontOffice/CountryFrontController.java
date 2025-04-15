package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.Art;
import com.esprit.wonderwise.Model.Celebrity;
import com.esprit.wonderwise.Model.Country;
import com.esprit.wonderwise.Model.Monument;
import com.esprit.wonderwise.Model.TraditionalFood;
import com.esprit.wonderwise.Service.ArtService;
import com.esprit.wonderwise.Service.CelebrityService;
import com.esprit.wonderwise.Service.CountryService;
import com.esprit.wonderwise.Service.MonumentService;
import com.esprit.wonderwise.Service.TraditionalFoodService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CountryFrontController {

    @FXML private FlowPane countryCards;
    @FXML private FlowPane artsFlowPane;
    @FXML private FlowPane monumentsFlowPane;
    @FXML private FlowPane foodsFlowPane;
    @FXML private FlowPane celebritiesFlowPane;
    @FXML private ImageView detailImageView;
    @FXML private Label nameLabel, currencyLabel, isoLabel, callingLabel, climateLabel;
    @FXML private Text descLabel; // Changed from Label to TextArea

    private CountryService countryService = new CountryService();
    private ArtService artService = new ArtService();
    private MonumentService monumentService = new MonumentService();
    private TraditionalFoodService foodService = new TraditionalFoodService();
    private CelebrityService celebrityService = new CelebrityService();
    Country selectedCountry;
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    @FXML
    public void initialize() {
        if (countryCards != null) {
            loadCountries();
            if (countryCards.getParent() instanceof ScrollPane) {
                ScrollPane scrollPane = (ScrollPane) countryCards.getParent();
                scrollPane.setPrefHeight(500);
                scrollPane.setFitToWidth(true);
            }
        }
    }

    private void loadCountries() {
        countryCards.getChildren().clear();
        List<Country> countries = countryService.readAll();
        for (Country country : countries) {
            VBox card = new VBox(15);
            card.getStyleClass().add("country-card");
            card.setPrefWidth(250);
            card.setPrefHeight(300);
            card.setAlignment(Pos.CENTER);

            ImageView imageView = new ImageView();
            File imageFile = new File(IMAGE_DESTINATION_DIR + country.getImg());
            if (imageFile.exists() && imageFile.isFile()) {
                Image image = new Image(imageFile.toURI().toString(), 200, 150, true, true);
                imageView.setImage(image);
                System.out.println("Country image loaded: " + imageFile.getAbsolutePath());
            } else {
                System.out.println("Country image not found: " + imageFile.getAbsolutePath());
                try {
                    Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 200, 150, true, true);
                    imageView.setImage(fallback);
                } catch (Exception e) {
                    System.err.println("Error loading fallback country image: " + e.getMessage());
                }
            }
            imageView.setFitWidth(200);
            imageView.setFitHeight(150);
            imageView.getStyleClass().add("rounded-image");

            Label name = new Label(country.getName());
            name.setStyle("-fx-font-size: 18px; -fx-text-fill: #2C3E50; -fx-font-weight: bold;");

            Label snippet = new Label("ISO: " + country.getIsoCode() + " | Currency: " + country.getCurrency());
            snippet.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");

            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER);
            Button detailsBtn = new Button("Details");
            detailsBtn.getStyleClass().add("action-button");
            detailsBtn.setOnAction(e -> {
                try {
                    showDetails(country);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            buttons.getChildren().add(detailsBtn);
            card.getChildren().addAll(imageView, name, snippet, buttons);
            countryCards.getChildren().add(card);
        }
    }

    public void showDetails(Country country) throws IOException {
        selectedCountry = country;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Country/CountryDetailsFront.fxml"));
        Parent root = loader.load();
        CountryFrontController controller = loader.getController();
        controller.setDetailsData(country);
        root.setUserData(this); // Store this controller instance
        Stage stage = (Stage) (countryCards != null ? countryCards.getScene().getWindow() : nameLabel.getScene().getWindow());
        stage.setScene(new Scene(root));
    }

    public void setDetailsData(Country country) {
        selectedCountry = country;
        nameLabel.setText(country.getName());
        descLabel.setText(country.getDescription());
        currencyLabel.setText(country.getCurrency());
        isoLabel.setText(country.getIsoCode());
        callingLabel.setText(country.getCallingCode());
        climateLabel.setText(country.getClimate());

        try {
            Image image = new Image(new File(IMAGE_DESTINATION_DIR + country.getImg()).toURI().toString(), 450, 350, true, true);
            detailImageView.setImage(image);
        } catch (Exception e) {
            Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 450, 350, true, true);
            detailImageView.setImage(fallback);
        }

        // Load arts with click handler
        if (artsFlowPane != null) {
            artsFlowPane.getChildren().clear();
            List<Art> arts = artService.readByCountryId(country.getId());
            for (Art art : arts) {
                VBox artCard = new VBox(10);
                artCard.setPrefWidth(200);
                artCard.setAlignment(Pos.CENTER);
                artCard.setStyle("-fx-cursor: hand;");

                ImageView artImage = new ImageView();
                File artImageFile = new File(IMAGE_DESTINATION_DIR + art.getImg());
                if (artImageFile.exists()) {
                    Image image = new Image(artImageFile.toURI().toString(), 150, 100, true, true);
                    artImage.setImage(image);
                } else {
                    Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 150, 100, true, true);
                    artImage.setImage(fallback);
                }
                artImage.getStyleClass().add("rounded-image");

                Label artName = new Label(art.getName());
                artName.setStyle("-fx-font-size: 14px; -fx-text-fill: #2C3E50; -fx-font-weight: bold;");

                artCard.getChildren().addAll(artImage, artName);
                artCard.setOnMouseClicked(e -> {
                    try {
                        showArtDetails(art);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                artsFlowPane.getChildren().add(artCard);
            }
            System.out.println("Loaded " + arts.size() + " arts for country " + country.getName());
        }

        // Load monuments with click handler
        if (monumentsFlowPane != null) {
            monumentsFlowPane.getChildren().clear();
            List<Monument> monuments = monumentService.readByCountryId(country.getId());
            for (Monument monument : monuments) {
                VBox monumentCard = new VBox(10);
                monumentCard.setPrefWidth(200);
                monumentCard.setAlignment(Pos.CENTER);
                monumentCard.setStyle("-fx-cursor: hand;");

                ImageView monumentImage = new ImageView();
                File monumentImageFile = new File(IMAGE_DESTINATION_DIR + monument.getImg());
                if (monumentImageFile.exists()) {
                    Image image = new Image(monumentImageFile.toURI().toString(), 150, 100, true, true);
                    monumentImage.setImage(image);
                } else {
                    Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 150, 100, true, true);
                    monumentImage.setImage(fallback);
                }
                monumentImage.getStyleClass().add("rounded-image");

                Label monumentName = new Label(monument.getName());
                monumentName.setStyle("-fx-font-size: 14px; -fx-text-fill: #2C3E50; -fx-font-weight: bold;");

                monumentCard.getChildren().addAll(monumentImage, monumentName);
                monumentCard.setOnMouseClicked(e -> {
                    try {
                        showMonumentDetails(monument);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                monumentsFlowPane.getChildren().add(monumentCard);
            }
            System.out.println("Loaded " + monuments.size() + " monuments for country " + country.getName());
        }

        // Load traditional foods with click handler
        if (foodsFlowPane != null) {
            foodsFlowPane.getChildren().clear();
            List<TraditionalFood> foods = foodService.readByCountryId(country.getId());
            for (TraditionalFood food : foods) {
                VBox foodCard = new VBox(10);
                foodCard.setPrefWidth(200);
                foodCard.setAlignment(Pos.CENTER);
                foodCard.setStyle("-fx-cursor: hand;");

                ImageView foodImage = new ImageView();
                File foodImageFile = new File(IMAGE_DESTINATION_DIR + food.getImg());
                if (foodImageFile.exists()) {
                    Image image = new Image(foodImageFile.toURI().toString(), 150, 100, true, true);
                    foodImage.setImage(image);
                } else {
                    Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 150, 100, true, true);
                    foodImage.setImage(fallback);
                }
                foodImage.getStyleClass().add("rounded-image");

                Label foodName = new Label(food.getName());
                foodName.setStyle("-fx-font-size: 14px; -fx-text-fill: #2C3E50; -fx-font-weight: bold;");

                foodCard.getChildren().addAll(foodImage, foodName);
                foodCard.setOnMouseClicked(e -> {
                    try {
                        showTraditionalFoodDetails(food);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                foodsFlowPane.getChildren().add(foodCard);
            }
            System.out.println("Loaded " + foods.size() + " traditional foods for country " + country.getName());
        }

        // Load celebrities with click handler
        if (celebritiesFlowPane != null) {
            celebritiesFlowPane.getChildren().clear();
            List<Celebrity> celebrities = celebrityService.readByCountryId(country.getId());
            for (Celebrity celebrity : celebrities) {
                VBox celebrityCard = new VBox(10);
                celebrityCard.setPrefWidth(200);
                celebrityCard.setAlignment(Pos.CENTER);
                celebrityCard.setStyle("-fx-cursor: hand;");

                ImageView celebrityImage = new ImageView();
                File celebrityImageFile = new File(IMAGE_DESTINATION_DIR + celebrity.getImg());
                if (celebrityImageFile.exists()) {
                    Image image = new Image(celebrityImageFile.toURI().toString(), 150, 100, true, true);
                    celebrityImage.setImage(image);
                } else {
                    Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 150, 100, true, true);
                    celebrityImage.setImage(fallback);
                }
                celebrityImage.getStyleClass().add("rounded-image");

                Label celebrityName = new Label(celebrity.getName());
                celebrityName.setStyle("-fx-font-size: 14px; -fx-text-fill: #2C3E50; -fx-font-weight: bold;");

                celebrityCard.getChildren().addAll(celebrityImage, celebrityName);
                celebrityCard.setOnMouseClicked(e -> {
                    try {
                        showCelebrityDetails(celebrity);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                celebritiesFlowPane.getChildren().add(celebrityCard);
            }
            System.out.println("Loaded " + celebrities.size() + " celebrities for country " + country.getName());
        }
    }

    @FXML
    public void showCountryList() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Country/CountryFront.fxml"));
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void showArtDetails(Art art) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Art/ArtDetailsFront.fxml"));
        Parent root = loader.load();
        ArtFrontController controller = loader.getController();
        controller.setDetailsData(art);
        root.setUserData(this); // Store this controller instance
        Stage stage = (Stage) artsFlowPane.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void showMonumentDetails(Monument monument) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Monument/MonumentDetailsFront.fxml"));
        Parent root = loader.load();
        MonumentFrontController controller = loader.getController();
        controller.setDetailsData(monument);
        root.setUserData(this); // Store this controller instance
        Stage stage = (Stage) monumentsFlowPane.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void showTraditionalFoodDetails(TraditionalFood food) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/TraditionalFood/TraditionalFoodDetailsFront.fxml"));
        Parent root = loader.load();
        TraditionalFoodFrontController controller = loader.getController();
        controller.setDetailsData(food);
        root.setUserData(this); // Store this controller instance
        Stage stage = (Stage) foodsFlowPane.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void showCelebrityDetails(Celebrity celebrity) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Celebrity/CelebrityDetailsFront.fxml"));
        Parent root = loader.load();
        CelebrityFrontController controller = loader.getController();
        controller.setDetailsData(celebrity);
        root.setUserData(this); // Store this controller instance
        Stage stage = (Stage) celebritiesFlowPane.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}