package com.esprit.wonderwise.Controller.FrontOffice;

import com.esprit.wonderwise.Model.Art;
import com.esprit.wonderwise.Model.Celebrity;
import com.esprit.wonderwise.Model.Country;
import com.esprit.wonderwise.Model.Monument;
import com.esprit.wonderwise.Model.Rating;
import com.esprit.wonderwise.Model.TraditionalFood;
import com.esprit.wonderwise.Service.ArtService;
import com.esprit.wonderwise.Service.CelebrityService;
import com.esprit.wonderwise.Service.CountryService;
import com.esprit.wonderwise.Service.MonumentService;
import com.esprit.wonderwise.Service.RatingService;
import com.esprit.wonderwise.Service.TraditionalFoodService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Pagination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CountryFrontController {

    @FXML private FlowPane countryCards;
    @FXML private Pagination pagination;
    @FXML private FlowPane artsFlowPane;
    @FXML private Pagination artsPagination;
    @FXML private FlowPane monumentsFlowPane;
    @FXML private Pagination monumentsPagination;
    @FXML private FlowPane foodsFlowPane;
    @FXML private Pagination foodsPagination;
    @FXML private FlowPane celebritiesFlowPane;
    @FXML private Pagination celebritiesPagination;
    @FXML private ImageView detailImageView;
    @FXML private Label nameLabel, currencyLabel, isoLabel, callingLabel, climateLabel;
    @FXML private Text descLabel;
    @FXML private TextField searchField;
    @FXML private javafx.scene.control.ComboBox<String> currencyCombo;
    @FXML private javafx.scene.control.ComboBox<String> climateCombo;
    @FXML private TextField categorySearchField;

    private RatingService ratingService = new RatingService();
    private CountryService countryService = new CountryService();
    private ArtService artService = new ArtService();
    private MonumentService monumentService = new MonumentService();
    private TraditionalFoodService foodService = new TraditionalFoodService();
    private CelebrityService celebrityService = new CelebrityService();
    Country selectedCountry;
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";
    private List<Country> allCountries;
    private List<Country> filteredCountries;
    private List<Art> filteredArts;
    private List<Monument> filteredMonuments;
    private List<TraditionalFood> filteredFoods;
    private List<Celebrity> filteredCelebrities;
    private static final int ITEMS_PER_PAGE = 2;
    private static final int ITEMS_PER_PAGE_DETAILS = 2;

    @FXML
    public void initialize() {
        if (countryCards != null && pagination != null) {
            allCountries = countryService.readAll();
            filteredCountries = allCountries;
            List<String> currencyOptions = new java.util.ArrayList<>();
            currencyOptions.add("All");
            currencyOptions.addAll(allCountries.stream().map(Country::getCurrency).distinct().sorted().toList());
            currencyCombo.getItems().setAll(currencyOptions);
            currencyCombo.getSelectionModel().selectFirst();

            List<String> climateOptions = new java.util.ArrayList<>();
            climateOptions.add("All");
            climateOptions.addAll(allCountries.stream().map(Country::getClimate).distinct().sorted().toList());
            climateCombo.getItems().setAll(climateOptions);
            climateCombo.getSelectionModel().selectFirst();

            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyAdvancedSearch());
            currencyCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyAdvancedSearch());
            climateCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyAdvancedSearch());
            updatePagination();
            if (countryCards.getParent() instanceof ScrollPane) {
                ScrollPane scrollPane = (ScrollPane) countryCards.getParent();
                scrollPane.setPrefHeight(500);
                scrollPane.setFitToWidth(true);
            }
        }

        if (categorySearchField != null) {
            categorySearchField.textProperty().addListener((obs, oldVal, newVal) -> applyCategorySearch());
        }
    }

    private void applyAdvancedSearch() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String currency = currencyCombo.getValue();
        String climate = climateCombo.getValue();
        filteredCountries = allCountries.stream()
                .filter(c -> (keyword.isEmpty() || c.getName().toLowerCase().contains(keyword) || c.getIsoCode().toLowerCase().contains(keyword) || c.getCurrency().toLowerCase().contains(keyword)))
                .filter(c -> (currency == null || currency.equals("All") || currency.isEmpty() || c.getCurrency().equals(currency)))
                .filter(c -> (climate == null || climate.equals("All") || climate.isEmpty() || c.getClimate().equals(climate)))
                .toList();
        updatePagination();
    }

    private void applyCategorySearch() {
        String keyword = categorySearchField.getText() == null ? "" : categorySearchField.getText().trim().toLowerCase();

        List<Art> arts = artService.readByCountryId(selectedCountry.getId());
        filteredArts = arts.stream()
                .filter(a -> keyword.isEmpty() || a.getName().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
        updateArtsPagination();

        List<Monument> monuments = monumentService.readByCountryId(selectedCountry.getId());
        filteredMonuments = monuments.stream()
                .filter(m -> keyword.isEmpty() || m.getName().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
        updateMonumentsPagination();

        List<TraditionalFood> foods = foodService.readByCountryId(selectedCountry.getId());
        filteredFoods = foods.stream()
                .filter(f -> keyword.isEmpty() || f.getName().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
        updateFoodsPagination();

        List<Celebrity> celebrities = celebrityService.readByCountryId(selectedCountry.getId());
        filteredCelebrities = celebrities.stream()
                .filter(c -> keyword.isEmpty() || c.getName().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
        updateCelebritiesPagination();
    }

    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) filteredCountries.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(Math.max(pageCount, 1));
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createCountryPage);
    }

    private void updateArtsPagination() {
        artsPagination.setVisible(!filteredArts.isEmpty());
        int pageCount = (int) Math.ceil((double) filteredArts.size() / ITEMS_PER_PAGE_DETAILS);
        artsPagination.setPageCount(Math.max(pageCount, 1));
        artsPagination.setCurrentPageIndex(0);
        artsPagination.setPageFactory(this::createArtsPage);
    }

    private void updateMonumentsPagination() {
        monumentsPagination.setVisible(!filteredMonuments.isEmpty());
        int pageCount = (int) Math.ceil((double) filteredMonuments.size() / ITEMS_PER_PAGE_DETAILS);
        monumentsPagination.setPageCount(Math.max(pageCount, 1));
        monumentsPagination.setCurrentPageIndex(0);
        monumentsPagination.setPageFactory(this::createMonumentsPage);
    }

    private void updateFoodsPagination() {
        foodsPagination.setVisible(!filteredFoods.isEmpty());
        int pageCount = (int) Math.ceil((double) filteredFoods.size() / ITEMS_PER_PAGE_DETAILS);
        foodsPagination.setPageCount(Math.max(pageCount, 1));
        foodsPagination.setCurrentPageIndex(0);
        foodsPagination.setPageFactory(this::createFoodsPage);
    }

    private void updateCelebritiesPagination() {
        celebritiesPagination.setVisible(!filteredCelebrities.isEmpty());
        int pageCount = (int) Math.ceil((double) filteredCelebrities.size() / ITEMS_PER_PAGE_DETAILS);
        celebritiesPagination.setPageCount(Math.max(pageCount, 1));
        celebritiesPagination.setCurrentPageIndex(0);
        celebritiesPagination.setPageFactory(this::createCelebritiesPage);
    }

    private VBox createCountryPage(int pageIndex) {
        countryCards.getChildren().clear();
        int start = pageIndex * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredCountries.size());
        for (int i = start; i < end; i++) {
            Country country = filteredCountries.get(i);
            VBox card = createCountryCard(country);
            countryCards.getChildren().add(card);
        }
        return new VBox();
    }

    private VBox createArtsPage(int pageIndex) {
        artsFlowPane.getChildren().clear();
        if (filteredArts.isEmpty()) {
            Text noResults = new Text("No arts found.");
            noResults.setStyle("-fx-font-size: 16px; -fx-fill: #718096; -fx-font-family: 'Poppins', 'Arial', sans-serif;");
            artsFlowPane.getChildren().add(noResults);
            artsFlowPane.setAlignment(Pos.CENTER);
            return new VBox();
        }
        artsFlowPane.setAlignment(Pos.CENTER);
        int start = pageIndex * ITEMS_PER_PAGE_DETAILS;
        int end = Math.min(start + ITEMS_PER_PAGE_DETAILS, filteredArts.size());
        for (int i = start; i < end; i++) {
            Art art = filteredArts.get(i);
            VBox artCard = createArtCard(art);
            artsFlowPane.getChildren().add(artCard);
        }
        return new VBox();
    }

    private VBox createMonumentsPage(int pageIndex) {
        monumentsFlowPane.getChildren().clear();
        if (filteredMonuments.isEmpty()) {
            Text noResults = new Text("No monuments found.");
            noResults.setStyle("-fx-font-size: 16px; -fx-fill: #718096; -fx-font-family: 'Poppins', 'Arial', sans-serif;");
            monumentsFlowPane.getChildren().add(noResults);
            monumentsFlowPane.setAlignment(Pos.CENTER);
            return new VBox();
        }
        monumentsFlowPane.setAlignment(Pos.CENTER);
        int start = pageIndex * ITEMS_PER_PAGE_DETAILS;
        int end = Math.min(start + ITEMS_PER_PAGE_DETAILS, filteredMonuments.size());
        for (int i = start; i < end; i++) {
            Monument monument = filteredMonuments.get(i);
            VBox monumentCard = createMonumentCard(monument);
            monumentsFlowPane.getChildren().add(monumentCard);
        }
        return new VBox();
    }

    private VBox createFoodsPage(int pageIndex) {
        foodsFlowPane.getChildren().clear();
        if (filteredFoods.isEmpty()) {
            Text noResults = new Text("No traditional foods found.");
            noResults.setStyle("-fx-font-size: 16px; -fx-fill: #718096; -fx-font-family: 'Poppins', 'Arial', sans-serif;");
            foodsFlowPane.getChildren().add(noResults);
            foodsFlowPane.setAlignment(Pos.CENTER);
            return new VBox();
        }
        foodsFlowPane.setAlignment(Pos.CENTER);
        int start = pageIndex * ITEMS_PER_PAGE_DETAILS;
        int end = Math.min(start + ITEMS_PER_PAGE_DETAILS, filteredFoods.size());
        for (int i = start; i < end; i++) {
            TraditionalFood food = filteredFoods.get(i);
            VBox foodCard = createFoodCard(food);
            foodsFlowPane.getChildren().add(foodCard);
        }
        return new VBox();
    }

    private VBox createCelebritiesPage(int pageIndex) {
        celebritiesFlowPane.getChildren().clear();
        if (filteredCelebrities.isEmpty()) {
            Text noResults = new Text("No celebrities found.");
            noResults.setStyle("-fx-font-size: 16px; -fx-fill: #718096; -fx-font-family: 'Poppins', 'Arial', sans-serif;");
            celebritiesFlowPane.getChildren().add(noResults);
            celebritiesFlowPane.setAlignment(Pos.CENTER);
            return new VBox();
        }
        celebritiesFlowPane.setAlignment(Pos.CENTER);
        int start = pageIndex * ITEMS_PER_PAGE_DETAILS;
        int end = Math.min(start + ITEMS_PER_PAGE_DETAILS, filteredCelebrities.size());
        for (int i = start; i < end; i++) {
            Celebrity celebrity = filteredCelebrities.get(i);
            VBox celebrityCard = createCelebrityCard(celebrity);
            celebritiesFlowPane.getChildren().add(celebrityCard);
        }
        return new VBox();
    }

    private VBox createCountryCard(Country country) {
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
        } else {
            Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 200, 150, true, true);
            imageView.setImage(fallback);
        }
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
        imageView.getStyleClass().add("rounded-image");

        Label name = new Label(country.getName());
        name.setStyle("-fx-font-size: 18px; -fx-text-fill: #2C3E50; -fx-font-weight: bold;");

        Label snippet = new Label("ISO: " + country.getIsoCode() + " | Currency: " + country.getCurrency());
        snippet.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");

        // --- Like/Dislike with counts (original) ---
        HBox ratingBox = new HBox(14);
        ratingBox.setAlignment(Pos.CENTER);
        ratingBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 14; -fx-padding: 4 10 4 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 1, 0, 0, 1); -fx-margin: 6 0 0 0;");

        VBox likeGroup = new VBox(1);
        likeGroup.setAlignment(Pos.CENTER);
        Button likeBtn = new Button();
        ImageView likeIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/like.png")));
        likeIcon.setFitWidth(22);
        likeIcon.setFitHeight(22);
        likeBtn.setGraphic(likeIcon);
        likeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        likeBtn.setOnMouseEntered(e -> likeBtn.setStyle("-fx-background-color: #e0f7fa; -fx-background-radius: 50%; -fx-cursor: hand;"));
        likeBtn.setOnMouseExited(e -> likeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;"));
        Label likeCount = new Label(String.valueOf(ratingService.countLikes(country.getId())));
        likeCount.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2D3748; -fx-padding: 1 0 0 0;");
        likeGroup.getChildren().addAll(likeBtn, likeCount);

        VBox dislikeGroup = new VBox(1);
        dislikeGroup.setAlignment(Pos.CENTER);
        Button dislikeBtn = new Button();
        ImageView dislikeIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/dislike.png")));
        dislikeIcon.setFitWidth(22);
        dislikeIcon.setFitHeight(22);
        dislikeBtn.setGraphic(dislikeIcon);
        dislikeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        dislikeBtn.setOnMouseEntered(e -> dislikeBtn.setStyle("-fx-background-color: #ffeaea; -fx-background-radius: 50%; -fx-cursor: hand;"));
        dislikeBtn.setOnMouseExited(e -> dislikeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;"));
        Label dislikeCount = new Label(String.valueOf(ratingService.countDislikes(country.getId())));
        dislikeCount.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2D3748; -fx-padding: 1 0 0 0;");
        dislikeGroup.getChildren().addAll(dislikeBtn, dislikeCount);

        Runnable handleLike = () -> {

            ratingService.add(new Rating(0, country.getId(), true, java.time.LocalDateTime.now()));
            int newLikes = ratingService.countLikes(country.getId());
            int newDislikes = ratingService.countDislikes(country.getId());

            likeCount.setText(String.valueOf(newLikes));
            dislikeCount.setText(String.valueOf(newDislikes));
        };
        Runnable handleDislike = () -> {

            ratingService.add(new Rating(0, country.getId(), false, java.time.LocalDateTime.now()));
            int newLikes = ratingService.countLikes(country.getId());
            int newDislikes = ratingService.countDislikes(country.getId());

            likeCount.setText(String.valueOf(newLikes));
            dislikeCount.setText(String.valueOf(newDislikes));
        };
        likeBtn.setOnAction(ev -> {  handleLike.run(); });
        dislikeBtn.setOnAction(ev -> {  handleDislike.run(); });

        ratingBox.getChildren().addAll(likeGroup, dislikeGroup);
        // --- End Like/Dislike with counts ---

        // --- Facebook-style Reaction Bar (NO COUNTS) ---
        HBox reactionBar = new HBox(18);
        reactionBar.setAlignment(Pos.CENTER);
        reactionBar.setStyle("-fx-padding: 8 0 8 0; -fx-background-color: transparent;");

        // Heart (Like)
        Button heartBtn = new Button();
        ImageView heartIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/heart.png")));
        heartIcon.setFitWidth(28);
        heartIcon.setFitHeight(28);
        heartBtn.setGraphic(heartIcon);
        heartBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 4;");
        heartBtn.setOnMouseEntered(e -> heartBtn.setStyle("-fx-background-color: #ffe6eb; -fx-background-radius: 50%; -fx-cursor: hand; -fx-padding: 4;"));
        heartBtn.setOnMouseExited(e -> heartBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 4;"));

        // Angry
        Button angryBtn = new Button();
        ImageView angryIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/angry.png")));
        angryIcon.setFitWidth(28);
        angryIcon.setFitHeight(28);
        angryBtn.setGraphic(angryIcon);
        angryBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 4;");
        angryBtn.setOnMouseEntered(e -> angryBtn.setStyle("-fx-background-color: #ffe1c4; -fx-background-radius: 50%; -fx-cursor: hand; -fx-padding: 4;"));
        angryBtn.setOnMouseExited(e -> angryBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 4;"));

        // Cry (Dislike)
        Button cryBtn = new Button();
        ImageView cryIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/cry.png")));
        cryIcon.setFitWidth(28);
        cryIcon.setFitHeight(28);
        cryBtn.setGraphic(cryIcon);
        cryBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 4;");
        cryBtn.setOnMouseEntered(e -> cryBtn.setStyle("-fx-background-color: #e8f4ff; -fx-background-radius: 50%; -fx-cursor: hand; -fx-padding: 4;"));
        cryBtn.setOnMouseExited(e -> cryBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 4;"));

        // Reaction actions (trigger like/dislike logic directly)
        heartBtn.setOnAction(ev -> {  handleLike.run(); });
        cryBtn.setOnAction(ev -> {  handleDislike.run(); });
        angryBtn.setOnAction(ev -> {  handleDislike.run(); });

        reactionBar.getChildren().addAll(heartBtn, angryBtn, cryBtn);
        // --- End Facebook-style Reaction Bar (NO COUNTS) ---

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

        card.getChildren().addAll(imageView, name, snippet, reactionBar, ratingBox, buttons);
        return card;
    }

    private VBox createArtCard(Art art) {
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
        return artCard;
    }

    private VBox createMonumentCard(Monument monument) {
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
        return monumentCard;
    }

    private VBox createFoodCard(TraditionalFood food) {
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
        return foodCard;
    }

    private VBox createCelebrityCard(Celebrity celebrity) {
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
        return celebrityCard;
    }

    public void showDetails(Country country) throws IOException {
        selectedCountry = country;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Country/CountryDetailsFront.fxml"));
        Parent root = loader.load();
        CountryFrontController controller = loader.getController();
        controller.setDetailsData(country);
        root.setUserData(this);
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

        filteredArts = artService.readByCountryId(country.getId());
        updateArtsPagination();

        filteredMonuments = monumentService.readByCountryId(country.getId());
        updateMonumentsPagination();

        filteredFoods = foodService.readByCountryId(country.getId());
        updateFoodsPagination();

        filteredCelebrities = celebrityService.readByCountryId(country.getId());
        updateCelebritiesPagination();
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
        root.setUserData(this);
        Stage stage = (Stage) artsFlowPane.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void showMonumentDetails(Monument monument) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Monument/MonumentDetailsFront.fxml"));
        Parent root = loader.load();
        MonumentFrontController controller = loader.getController();
        controller.setDetailsData(monument);
        root.setUserData(this);
        Stage stage = (Stage) monumentsFlowPane.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void showTraditionalFoodDetails(TraditionalFood food) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/TraditionalFood/TraditionalFoodDetailsFront.fxml"));
        Parent root = loader.load();
        TraditionalFoodFrontController controller = loader.getController();
        controller.setDetailsData(food);
        root.setUserData(this);
        Stage stage = (Stage) foodsFlowPane.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void showCelebrityDetails(Celebrity celebrity) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Celebrity/CelebrityDetailsFront.fxml"));
        Parent root = loader.load();
        CelebrityFrontController controller = loader.getController();
        controller.setDetailsData(celebrity);
        root.setUserData(this);
        Stage stage = (Stage) celebritiesFlowPane.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}