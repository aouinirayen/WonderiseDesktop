package com.esprit.wonderwise.Controller.BackOffice;

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
import com.esprit.wonderwise.Utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;

public class CountryController {
    @FXML
    private FlowPane countryCards;
    @FXML
    private FlowPane artsFlowPane;
    @FXML
    private FlowPane monumentsFlowPane;
    @FXML
    private FlowPane foodsFlowPane;
    @FXML
    private FlowPane celebritiesFlowPane;
    @FXML
    private TextField nameField, imgPathField, descField, currencyField, isoField, callingField, climateField;
    @FXML
    private Label nameLabel, currencyLabel, isoLabel, callingLabel, climateLabel;
    @FXML
    private Text descLabel;
    @FXML
    private ImageView detailImageView;
    // Advanced search controls
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> currencyCombo;
    @FXML
    private ComboBox<String> climateCombo;

    private List<Country> allCountries = null;
    private List<Country> filteredCountries = null;

    private CountryService countryService = new CountryService();
    private ArtService artService = new ArtService();
    private MonumentService monumentService = new MonumentService();
    private TraditionalFoodService foodService = new TraditionalFoodService();
    private CelebrityService celebrityService = new CelebrityService();
    private Country selectedCountry;
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    @FXML
    private void exportCountriesToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Countries to Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("countries.xlsx");
        Stage stage = getCurrentStage();
        java.io.File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Countries");
                // Create header style
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) 12);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                // Create alternating row style
                CellStyle altRowStyle = workbook.createCellStyle();
                altRowStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                altRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                // Write header
                String[] header = {"Country Name", "Description", "Currency", "ISO Code", "Calling Code", "Climate"};
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < header.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(header[i]);
                    cell.setCellStyle(headerStyle);
                }
                // Write data rows
                int rowIdx = 1;
                for (Country country : filteredCountries) {
                    Row row = sheet.createRow(rowIdx);
                    Cell[] cells = new Cell[6];
                    cells[0] = row.createCell(0);
                    cells[0].setCellValue(country.getName());
                    cells[1] = row.createCell(1);
                    cells[1].setCellValue(country.getDescription());
                    cells[2] = row.createCell(2);
                    cells[2].setCellValue(country.getCurrency());
                    cells[3] = row.createCell(3);
                    cells[3].setCellValue(country.getIsoCode());
                    cells[4] = row.createCell(4);
                    cells[4].setCellValue(country.getCallingCode());
                    cells[5] = row.createCell(5);
                    cells[5].setCellValue(country.getClimate());
                    if (rowIdx % 2 == 0) {
                        for (Cell cell : cells) cell.setCellStyle(altRowStyle);
                    }
                    rowIdx++;
                }
                // Autosize columns
                for (int i = 0; i < header.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }
            } catch (Exception e) {
                DialogUtils.showCustomDialog("Export Error", "Failed to export countries: " + e.getMessage(), false, stage);
            }
        }
    }

    @FXML
    public void initialize() {
        if (countryCards != null) {
            // Load all countries only once
            allCountries = countryService.readAll();
            filteredCountries = allCountries;

            // Populate ComboBoxes with 'All' option
            List<String> currencyOptions = new java.util.ArrayList<>();
            currencyOptions.add("All");
            currencyOptions.addAll(allCountries.stream().map(Country::getCurrency).distinct().sorted().toList());
            if (currencyCombo != null) {
                currencyCombo.getItems().setAll(currencyOptions);
                currencyCombo.getSelectionModel().selectFirst();
            }

            List<String> climateOptions = new java.util.ArrayList<>();
            climateOptions.add("All");
            climateOptions.addAll(allCountries.stream().map(Country::getClimate).distinct().sorted().toList());
            if (climateCombo != null) {
                climateCombo.getItems().setAll(climateOptions);
                climateCombo.getSelectionModel().selectFirst();
            }

            // Add listeners for live search/filter
            if (searchField != null)
                searchField.textProperty().addListener((obs, oldVal, newVal) -> applyAdvancedSearch());
            if (currencyCombo != null)
                currencyCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyAdvancedSearch());
            if (climateCombo != null)
                climateCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyAdvancedSearch());

            loadCountries();

            if (countryCards.getParent() instanceof ScrollPane) {
                ScrollPane scrollPane = (ScrollPane) countryCards.getParent();
                scrollPane.setPrefHeight(500);
                scrollPane.setFitToWidth(true);
            }
        }
        if (currencyField != null) {
            currencyField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.length() > 3) currencyField.setText(oldVal);
            });
        }
        if (isoField != null) {
            isoField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.length() > 2) isoField.setText(oldVal);
            });
        }
        if (callingField != null) {
            callingField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.length() > 5) callingField.setText(oldVal);
            });
        }
    }

    private void applyAdvancedSearch() {
        String keyword = (searchField != null && searchField.getText() != null) ? searchField.getText().trim().toLowerCase() : "";
        String currency = (currencyCombo != null) ? currencyCombo.getValue() : null;
        String climate = (climateCombo != null) ? climateCombo.getValue() : null;
        filteredCountries = allCountries.stream()
                .filter(c -> (keyword.isEmpty() || c.getName().toLowerCase().contains(keyword) || c.getIsoCode().toLowerCase().contains(keyword) || c.getCurrency().toLowerCase().contains(keyword)))
                .filter(c -> (currency == null || currency.equals("All") || currency.isEmpty() || c.getCurrency().equals(currency)))
                .filter(c -> (climate == null || climate.equals("All") || climate.isEmpty() || c.getClimate().equals(climate)))
                .toList();
        loadCountries();
    }

    private void loadCountries() {
        countryCards.getChildren().clear();
        List<Country> countries = (filteredCountries != null) ? filteredCountries : countryService.readAll();

        if (countries == null || countries.isEmpty()) {
            VBox indicator = new VBox(16);
            indicator.setAlignment(Pos.CENTER);
            indicator.setStyle("-fx-padding: 40; -fx-background-color: #f8fafd; -fx-background-radius: 12;");

            ImageView icon = new ImageView();
            try {
                Image emptyIcon = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/empty-box.png"));
                icon.setImage(emptyIcon);
            } catch (Exception e) {
                // fallback: no icon
            }
            icon.setFitWidth(90);
            icon.setFitHeight(90);
            icon.setPreserveRatio(true);
            icon.setSmooth(true);

            Label mainText = new Label("No countries found");
            mainText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
            Label subText = new Label("Try adjusting your search or add a new country.");
            subText.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

            indicator.getChildren().addAll(icon, mainText, subText);
            countryCards.getChildren().add(indicator);
            return;
        }

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
            } else {
                try {
                    Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 200, 150, true, true);
                    imageView.setImage(fallback);
                } catch (Exception e) {
                    System.err.println("Error loading fallback image: " + e.getMessage());
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
            Button editBtn = new Button("Edit");
            Button deleteBtn = new Button("Delete");

            detailsBtn.getStyleClass().add("action-button");
            editBtn.getStyleClass().add("action-button");
            deleteBtn.getStyleClass().addAll("action-button", "delete-button");

            detailsBtn.setOnAction(e -> {
                try {
                    showDetails(country);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            editBtn.setOnAction(e -> {
                try {
                    showEditCountry(country);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            deleteBtn.setOnAction(e -> {
                try {
                    deleteCountry(country.getId());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            buttons.getChildren().addAll(detailsBtn, editBtn, deleteBtn);
            card.getChildren().addAll(imageView, name, snippet, buttons);
            countryCards.getChildren().add(card);
        }
    }

    @FXML
    public void showAddCountry() throws IOException {
        loadScene("CountryAdd.fxml", null);
    }

    @FXML
    public void addCountry() throws IOException {
        if (!validateInputs()) return;

        String imageFileName = copyImageToDestination(imgPathField.getText());
        Country country = new Country(
                0, nameField.getText(), imageFileName, descField.getText(),
                currencyField.getText(), isoField.getText(), callingField.getText(), climateField.getText()
        );
        countryService.add(country);
        DialogUtils.showCustomDialog("Success", "Country added successfully!", true, getCurrentStage());
        showCountryList();
    }

    @FXML
    public void handleImgBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imgPathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void showEditCountry(Country country) throws IOException {
        selectedCountry = country;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Country/CountryEdit.fxml"));
        Parent root = loader.load();
        CountryController controller = loader.getController();
        controller.setCountryData(country);
        controller.selectedCountry = country;
        Stage stage = (Stage) (countryCards != null ? countryCards.getScene().getWindow() : nameField.getScene().getWindow());
        stage.setScene(new Scene(root));
    }

    @FXML
    public void updateCountry() throws IOException {
        if (selectedCountry == null) {
            DialogUtils.showCustomDialog("Error", "No country selected for update.", false, getCurrentStage());
            return;
        }
        if (!validateInputs()) return;

        String imageFileName = copyImageToDestination(imgPathField.getText());
        selectedCountry.setName(nameField.getText());
        selectedCountry.setImg(imageFileName);
        selectedCountry.setDescription(descField.getText());
        selectedCountry.setCurrency(currencyField.getText());
        selectedCountry.setIsoCode(isoField.getText());
        selectedCountry.setCallingCode(callingField.getText());
        selectedCountry.setClimate(climateField.getText());
        countryService.update(selectedCountry);
        DialogUtils.showCustomDialog("Success", "Country updated successfully!", true, getCurrentStage());
        showCountryList();
    }

    private String copyImageToDestination(String sourcePath) throws IOException {
        if (sourcePath == null || sourcePath.isEmpty()) return "";
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) return "";
        String fileName = sourceFile.getName();
        File destFile = new File(IMAGE_DESTINATION_DIR + fileName);
        try {
            // First copy the image
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            // Apply watermark
            com.esprit.wonderwise.Utils.WatermarkUtils.applyTextWatermark(destFile, destFile, "WonderWise");
        } catch (Exception e) {
            // If watermarking fails, fallback to normal copy
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.err.println("Failed to apply watermark: " + e.getMessage());
        }
        return fileName;
    }

    public void setCountryData(Country country) {
        nameField.setText(country.getName());
        imgPathField.setText(IMAGE_DESTINATION_DIR + country.getImg());
        descField.setText(country.getDescription());
        currencyField.setText(country.getCurrency());
        isoField.setText(country.getIsoCode());
        callingField.setText(country.getCallingCode());
        climateField.setText(country.getClimate());
    }

    @FXML
    public void deleteCountry(int id) throws IOException {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this country?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            countryService.delete(id);
            DialogUtils.showCustomDialog("Success", "Country deleted successfully!", true, getCurrentStage());
            showCountryList();
        }
    }

    @FXML
    public void showDetails(Country country) throws IOException {
        selectedCountry = country;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/BackOffice/Country/CountryDetails.fxml"));
        Parent root = loader.load();
        CountryController controller = loader.getController();
        controller.setDetailsData(country);
        Stage stage = (Stage) (countryCards != null ? countryCards.getScene().getWindow() : nameLabel.getScene().getWindow());
        stage.setScene(new Scene(root));
    }

    public void setDetailsData(Country country) {
        nameLabel.setText(country.getName());
        descLabel.setText(country.getDescription());
        currencyLabel.setText("Currency: " + country.getCurrency());
        isoLabel.setText("ISO Code: " + country.getIsoCode());
        callingLabel.setText("Calling Code: " + country.getCallingCode());
        climateLabel.setText("Climate: " + country.getClimate());

        File imageFile = new File(IMAGE_DESTINATION_DIR + country.getImg());
        if (imageFile.exists() && imageFile.isFile()) {
            Image image = new Image(imageFile.toURI().toString(), 300, 200, true, true);
            detailImageView.setImage(image);
            System.out.println("Detail image loaded: " + imageFile.getAbsolutePath());
        } else {
            System.out.println("Detail image not found: " + imageFile.getAbsolutePath());
            try {
                Image fallback = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 300, 200, true, true);
                detailImageView.setImage(fallback);
            } catch (Exception e) {
                System.err.println("Error loading fallback detail image: " + e.getMessage());
            }
        }

        // Load arts
        if (artsFlowPane != null) {
            artsFlowPane.getChildren().clear();
            List<Art> arts = artService.readByCountryId(country.getId());
            if (arts == null || arts.isEmpty()) {
                VBox indicator = new VBox(16);
                indicator.setAlignment(Pos.CENTER);
                indicator.setStyle("-fx-padding: 40; -fx-background-color: #f8fafd; -fx-background-radius: 12;");
                ImageView icon = new ImageView();
                try {
                    Image emptyIcon = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/empty-box.png"));
                    icon.setImage(emptyIcon);
                } catch (Exception e) {
                    // fallback: no icon
                }
                icon.setFitWidth(90);
                icon.setFitHeight(90);
                icon.setPreserveRatio(true);
                icon.setSmooth(true);
                Label msg = new Label("No arts available for this country");
                msg.setStyle("-fx-text-fill: #7b8794; -fx-font-size: 16px; -fx-font-family: 'Poppins', 'Arial', sans-serif;");
                indicator.getChildren().addAll(icon, msg);
                artsFlowPane.getChildren().add(indicator);
            } else {
                for (Art art : arts) {
                    VBox artCard = new VBox(10);
                    artCard.setPrefWidth(200);
                    artCard.setAlignment(Pos.CENTER);
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
                    artsFlowPane.getChildren().add(artCard);
                }
            }
            System.out.println("Loaded " + arts.size() + " arts for country " + country.getName());
        } else {
            System.out.println("artsFlowPane is null, cannot load arts.");
        }

        // Load monuments
        if (monumentsFlowPane != null) {
            monumentsFlowPane.getChildren().clear();
            List<Monument> monuments = monumentService.readByCountryId(country.getId());
            if (monuments == null || monuments.isEmpty()) {
                VBox indicator = new VBox(16);
                indicator.setAlignment(Pos.CENTER);
                indicator.setStyle("-fx-padding: 40; -fx-background-color: #f8fafd; -fx-background-radius: 12;");
                ImageView icon = new ImageView();
                try {
                    Image emptyIcon = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/empty-box.png"));
                    icon.setImage(emptyIcon);
                } catch (Exception e) {
                    // fallback: no icon
                }
                icon.setFitWidth(90);
                icon.setFitHeight(90);
                icon.setPreserveRatio(true);
                icon.setSmooth(true);
                Label msg = new Label("No monuments available for this country");
                msg.setStyle("-fx-text-fill: #7b8794; -fx-font-size: 16px; -fx-font-family: 'Poppins', 'Arial', sans-serif;");
                indicator.getChildren().addAll(icon, msg);
                monumentsFlowPane.getChildren().add(indicator);
            } else {
                for (Monument monument : monuments) {
                    VBox monumentCard = new VBox(10);
                    monumentCard.setPrefWidth(200);
                    monumentCard.setAlignment(Pos.CENTER);
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
                    monumentsFlowPane.getChildren().add(monumentCard);
                }
            }
            System.out.println("Loaded " + monuments.size() + " monuments for country " + country.getName());
        } else {
            System.out.println("monumentsFlowPane is null, cannot load monuments.");
        }

        // Load traditional foods
        if (foodsFlowPane != null) {
            foodsFlowPane.getChildren().clear();
            List<TraditionalFood> foods = foodService.readByCountryId(country.getId());
            if (foods == null || foods.isEmpty()) {
                VBox indicator = new VBox(16);
                indicator.setAlignment(Pos.CENTER);
                indicator.setStyle("-fx-padding: 40; -fx-background-color: #f8fafd; -fx-background-radius: 12;");
                ImageView icon = new ImageView();
                try {
                    Image emptyIcon = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/empty-box.png"));
                    icon.setImage(emptyIcon);
                } catch (Exception e) {
                    // fallback: no icon
                }
                icon.setFitWidth(90);
                icon.setFitHeight(90);
                icon.setPreserveRatio(true);
                icon.setSmooth(true);
                Label msg = new Label("No traditional foods available for this country");
                msg.setStyle("-fx-text-fill: #7b8794; -fx-font-size: 16px; -fx-font-family: 'Poppins', 'Arial', sans-serif;");
                indicator.getChildren().addAll(icon, msg);
                foodsFlowPane.getChildren().add(indicator);
            } else {
                for (TraditionalFood food : foods) {
                    VBox foodCard = new VBox(10);
                    foodCard.setPrefWidth(200);
                    foodCard.setAlignment(Pos.CENTER);
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
                    foodsFlowPane.getChildren().add(foodCard);
                }
            }
            System.out.println("Loaded " + foods.size() + " traditional foods for country " + country.getName());
        } else {
            System.out.println("foodsFlowPane is null, cannot load traditional foods.");
        }

        // Load celebrities
        if (celebritiesFlowPane != null) {
            celebritiesFlowPane.getChildren().clear();
            List<Celebrity> celebrities = celebrityService.readByCountryId(country.getId());
            if (celebrities == null || celebrities.isEmpty()) {
                VBox indicator = new VBox(16);
                indicator.setAlignment(Pos.CENTER);
                indicator.setStyle("-fx-padding: 40; -fx-background-color: #f8fafd; -fx-background-radius: 12;");
                ImageView icon = new ImageView();
                try {
                    Image emptyIcon = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/empty-box.png"));
                    icon.setImage(emptyIcon);
                } catch (Exception e) {
                    // fallback: no icon
                }
                icon.setFitWidth(90);
                icon.setFitHeight(90);
                icon.setPreserveRatio(true);
                icon.setSmooth(true);
                Label msg = new Label("No celebrities available for this country");
                msg.setStyle("-fx-text-fill: #7b8794; -fx-font-size: 16px; -fx-font-family: 'Poppins', 'Arial', sans-serif;");
                indicator.getChildren().addAll(icon, msg);
                celebritiesFlowPane.getChildren().add(indicator);
            } else {
                for (Celebrity celebrity : celebrities) {
                    VBox celebrityCard = new VBox(10);
                    celebrityCard.setPrefWidth(200);
                    celebrityCard.setAlignment(Pos.CENTER);
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
                    celebritiesFlowPane.getChildren().add(celebrityCard);
                }
            }
            System.out.println("Loaded " + celebrities.size() + " celebrities for country " + country.getName());
        } else {
            System.out.println("celebritiesFlowPane is null, cannot load celebrities.");
        }
    }

    @FXML
    public void showCountryList() throws IOException {
        loadScene("Country.fxml", null);
    }

    private void loadScene(String fxml, Button sourceButton) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/esprit/wonderwise/BackOffice/Country/" + fxml));
        Stage stage;
        if (sourceButton != null) {
            stage = (Stage) sourceButton.getScene().getWindow();
        } else if (countryCards != null) {
            stage = (Stage) countryCards.getScene().getWindow();
        } else if (nameField != null) {
            stage = (Stage) nameField.getScene().getWindow();
        } else {
            stage = (Stage) nameLabel.getScene().getWindow();
        }
        stage.setScene(new Scene(root));
    }

    private boolean validateInputs() {
        if (nameField.getText().isEmpty()) {
            DialogUtils.showCustomDialog("Validation Error", "Name cannot be empty.", false, getCurrentStage());
            return false;
        }
        if (currencyField.getText().length() > 3) {
            DialogUtils.showCustomDialog("Validation Error", "Currency must be 3 characters or less.", false, getCurrentStage());
            return false;
        }
        if (isoField.getText().length() != 2) {
            DialogUtils.showCustomDialog("Validation Error", "ISO Code must be exactly 2 characters.", false, getCurrentStage());
            return false;
        }
        if (callingField.getText().isEmpty() || !callingField.getText().startsWith("+") || callingField.getText().length() > 5) {
            DialogUtils.showCustomDialog("Validation Error", "Calling Code must start with '+' and be 5 characters or less.", false, getCurrentStage());
            return false;
        }
        return true;
    }

    private Stage getCurrentStage() {
        return (Stage) (countryCards != null ? countryCards.getScene().getWindow() :
                nameField != null ? nameField.getScene().getWindow() :
                        nameLabel.getScene().getWindow());
    }

}