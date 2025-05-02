package com.esprit.wonderwise.Controller.FrontOffice;

import com.calendarfx.view.DateControl;
import com.calendarfx.view.page.DayPage;
import com.calendarfx.view.page.MonthPage;
import com.calendarfx.view.page.WeekPage;
import com.calendarfx.view.page.YearPage;
import com.esprit.wonderwise.Model.Evenement;
import com.esprit.wonderwise.Service.EvenementService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// CalendarFX imports
import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import javafx.stage.StageStyle;
import org.controlsfx.control.PopOver;

import java.time.ZoneId;

public class EvenementFrontController {

    @FXML private FlowPane evenementCards;
    @FXML private Button calendarBtn;
    @FXML private javafx.scene.control.TextField searchField;
    @FXML private javafx.scene.control.ComboBox<String> filterCombo;
    private EvenementService evenementService = new EvenementService();
    private static final String IMAGE_DESTINATION_DIR = "C:\\xampp\\htdocs\\pidev3\\";

    @FXML
    public void initialize() {
        // Populate filterCombo with categories
        List<Evenement> allEvents = evenementService.readAll();
        java.util.Set<String> categories = new java.util.HashSet<>();
        for (Evenement evt : allEvents) {
            if (evt.getCategorie() != null && !evt.getCategorie().isEmpty()) {
                categories.add(evt.getCategorie());
            }
        }
        filterCombo.getItems().clear();
        filterCombo.getItems().add("Toutes les catégories");
        filterCombo.getItems().addAll(categories);
        filterCombo.getSelectionModel().selectFirst();

        // Add listeners
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applySearchFilter());
        filterCombo.valueProperty().addListener((obs, oldVal, newVal) -> applySearchFilter());

        loadEvenements(null, null);
        if (calendarBtn != null) {
            calendarBtn.setOnAction(e -> openCalendarView());
        }
    }

    private void loadEvenements(String search, String category) {
        evenementCards.getChildren().clear();
        List<Evenement> evenements = evenementService.readAll();
        LocalDateTime now = LocalDateTime.now();

        // Check and delete events with < 10 interested and within 2 hours of starting
        for (Evenement evenement : evenements) {
            java.sql.Date eventDate = evenement.getDate();
            java.sql.Time eventTime = evenement.getHeure();

            if (eventDate != null && eventTime != null) {
                LocalDateTime eventDateTime = LocalDateTime.of(eventDate.toLocalDate(), eventTime.toLocalTime());
                // Check if the event is within 2 hours from now
                LocalDateTime twoHoursBeforeEvent = eventDateTime.minusHours(2);

                if (now.isAfter(twoHoursBeforeEvent) && now.isBefore(eventDateTime)) {
                    // Check if interested count is less than 10
                    if (evenement.getInterested() < 10) {
                        evenementService.delete(evenement.getId()); // Call the delete method
                        continue; // Skip adding this event to the UI
                    }
                }
            }
        }

        // Reload events after deletion to ensure UI reflects changes
        evenements = evenementService.readAll();

        // Filter by search and category
        List<Evenement> filtered = new ArrayList<>();
        for (Evenement evt : evenements) {
            boolean matchesSearch = (search == null || search.isEmpty()) ||
                evt.getNom().toLowerCase().contains(search.toLowerCase()) ||
                (evt.getLieu() != null && evt.getLieu().toLowerCase().contains(search.toLowerCase()));
            boolean matchesCategory = (category == null || category.equals("Toutes les catégories")) ||
                (evt.getCategorie() != null && evt.getCategorie().equals(category));
            if (matchesSearch && matchesCategory) {
                filtered.add(evt);
            }
        }
        for (Evenement evenement : filtered) {
            // Create the card (VBox)
            VBox card = new VBox(8);
            card.setPrefWidth(240);
            card.setAlignment(Pos.TOP_CENTER);
            card.setPadding(new Insets(12));
            card.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);");

            // Rectangular Image - Full width with rounded corners
            ImageView imageView = new ImageView();
            File imageFile = new File(IMAGE_DESTINATION_DIR + evenement.getPhoto());
            Image image;
            if (imageFile.exists()) {
                image = new Image(imageFile.toURI().toString(), 200, 120, true, true);
            } else {
                image = new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/images/notfound.png"), 200, 120, true, true);
            }
            imageView.setImage(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(false);
            Rectangle clip = new Rectangle(200, 120);
            clip.setArcWidth(16);
            clip.setArcHeight(16);
            imageView.setClip(clip);

            // Name Label
            Label name = new Label(evenement.getNom());
            name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1F2A44; -fx-alignment: center; -fx-wrap-text: true; -fx-max-width: 200;");
            name.setWrapText(true);

            // Date and Time (Single Line with | Before Clock Icon)
            HBox dateTimeBox = new HBox(6);
            dateTimeBox.setAlignment(Pos.CENTER);
            ImageView dateIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/calendar.png"), 14, 14, true, true));
            Label date = new Label(evenement.getDate().toString());
            date.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");
            Label separator = new Label(" | ");
            separator.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");
            ImageView timeIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/clock.png"), 14, 14, true, true));
            Label time = new Label(evenement.getHeure().toString());
            time.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");
            dateTimeBox.getChildren().addAll(dateIcon, date, separator, timeIcon, time);

            // Location
            HBox locationBox = new HBox(6);
            locationBox.setAlignment(Pos.CENTER);
            ImageView locationIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/gps.png"), 14, 14, true, true));
            Label location = new Label(evenement.getLieu());
            location.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");
            locationBox.getChildren().addAll(locationIcon, location);

            // Category and Country
            HBox categoryCountryBox = new HBox(6);
            categoryCountryBox.setAlignment(Pos.CENTER);
            Label categoryLabel = new Label(evenement.getCategorie());
            categoryLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #146C43; -fx-background-color: #D1E7DD; -fx-padding: 3 6; -fx-background-radius: 4; -fx-font-weight: bold;");
            Label country = new Label(evenement.getPays());
            country.setStyle("-fx-font-size: 11px; -fx-text-fill: #1E6091; -fx-background-color: #D7E9F7; -fx-padding: 3 6; -fx-background-radius: 4; -fx-font-weight: bold;");
            categoryCountryBox.getChildren().addAll(categoryLabel, country);

            // Price and Places
            HBox pricePlacesBox = new HBox(10);
            pricePlacesBox.setAlignment(Pos.CENTER);
            Label price = new Label(evenement.getPrix() + "€");
            price.setStyle("-fx-font-size: 12px; -fx-text-fill: #1F2A44; -fx-font-weight: bold;");
            Label places = new Label("Places: " + evenement.getPlaceMax());
            places.setStyle("-fx-font-size: 12px; -fx-text-fill: #1F2A44; -fx-font-weight: bold;");
            pricePlacesBox.getChildren().addAll(price, places);

            // Status (Below Price and Places, Full Width)
            HBox statusBox = new HBox();
            statusBox.setAlignment(Pos.CENTER);
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDate currentDate = currentDateTime.toLocalDate();
            LocalTime currentTime = currentDateTime.toLocalTime();
            java.sql.Date eventDate = evenement.getDate();
            java.sql.Time eventTime = evenement.getHeure();
            String statut;
            String statutColor;
            if (eventDate != null && eventTime != null) {
                LocalDate eventLocalDate = eventDate.toLocalDate();
                LocalTime eventLocalTime = eventTime.toLocalTime();
                if (eventLocalDate.isEqual(currentDate) && !currentTime.isBefore(eventLocalTime)) {
                    statut = "Actif";
                    statutColor = "#22C55E"; // Green for active
                    if (!"Actif".equals(evenement.getStatus())) {
                        evenement.setStatus("Actif");
                        evenementService.update(evenement);
                    }
                } else {
                    statut = "Inactif";
                    statutColor = "#EF4444"; // Red for inactive
                    if (!"Inactif".equals(evenement.getStatus())) {
                        evenement.setStatus("Inactif");
                        evenementService.update(evenement);
                    }
                }
            } else {
                statut = "Inactif";
                statutColor = "#EF4444";
                if (!"Inactif".equals(evenement.getStatus())) {
                    evenement.setStatus("Inactif");
                    evenementService.update(evenement);
                }
            }
            Label status = new Label(statut);
            status.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; " +
                    "-fx-text-fill: " + statutColor + "; " +
                    "-fx-border-color: " + statutColor + "; " +
                    "-fx-border-width: 1; -fx-border-radius: 4; -fx-background-color: transparent; " +
                    "-fx-padding: 2 6; -fx-pref-width: 200; -fx-alignment: center; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);");
            statusBox.getChildren().add(status);

            // Button
            Button detailsBtn = new Button("Voir plus");
            detailsBtn.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand; -fx-pref-width: 180;");
            detailsBtn.setOnAction(e -> {
                try {
                    showDetails(evenement);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            detailsBtn.setOnMouseEntered(e -> detailsBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand; -fx-pref-width: 180;"));
            detailsBtn.setOnMouseExited(e -> detailsBtn.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand; -fx-pref-width: 180;"));

            // Interested Icon, Label, and Count
            HBox interestedBox = new HBox(6);
            interestedBox.setAlignment(Pos.CENTER);
            ImageView interestedIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/esprit/wonderwise/icons/interested.png"), 16, 16, true, true));
            Label interestedLabel = new Label("Interested");
            interestedLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2563EB; -fx-font-weight: bold;");
            Label interestedCount = new Label(String.valueOf(evenement.getInterested()));
            interestedCount.setStyle("-fx-font-size: 12px; -fx-text-fill: #2563EB; -fx-font-weight: bold;");
            interestedBox.getChildren().addAll(interestedIcon, interestedLabel, interestedCount);

            // Click handler for interested
            interestedBox.setOnMouseClicked(e -> {
                int currentCount = evenement.getInterested();
                evenement.setInterested(currentCount + 1);
                evenementService.update(evenement);
                interestedCount.setText(String.valueOf(evenement.getInterested()));
            });
            interestedBox.setStyle("-fx-cursor: hand;");

            // Add all elements to the card
            card.getChildren().addAll(imageView, name, dateTimeBox, locationBox, categoryCountryBox, pricePlacesBox, interestedBox, statusBox, detailsBtn);
            evenementCards.getChildren().add(card);
        }
    }

    private void openCalendarView() {
        Platform.runLater(() -> {
            System.out.println("Opening CalendarView");

            // Create CalendarView
            CalendarView calendarView = new CalendarView();

            // Disable PopOver for entry details on all DateControl views
            DayPage dayPage = calendarView.getDayPage();
            WeekPage weekPage = calendarView.getWeekPage();
            MonthPage monthPage = calendarView.getMonthPage();
            YearPage yearPage = calendarView.getYearPage();

            DateControl[] controls = {dayPage, weekPage, monthPage, yearPage};
            for (DateControl control : controls) {
                control.setEntryDetailsCallback(param -> null); // Disable PopOver
            }

            Calendar eventCalendar = new Calendar("Événements");
            eventCalendar.setStyle(Calendar.Style.STYLE3);

            // Populate calendar with events
            List<Evenement> evenements = evenementService.readAll();
            for (Evenement evt : evenements) {
                Entry<String> entry = evenementToEntry(evt);
                eventCalendar.addEntry(entry);
            }

            CalendarSource source = new CalendarSource("Source");
            source.getCalendars().add(eventCalendar);
            calendarView.getCalendarSources().add(source);
            calendarView.showMonthPage();

            // Create and configure stage
            Stage stage = new Stage();
            stage.setTitle("Calendrier des événements");
            stage.initStyle(StageStyle.DECORATED);
            stage.setScene(new Scene(calendarView, 900, 600));

            // Handle stage close request
            stage.setOnCloseRequest(event -> {
                System.out.println("Closing CalendarView");
                calendarView.getCalendarSources().clear();
                eventCalendar.clear();
                stage.close();
                System.out.println("CalendarView closed");
            });

            // Monitor stage showing property
            stage.showingProperty().addListener((obs, wasShowing, isShowing) -> {
                if (!isShowing) {
                    System.out.println("Stage is no longer showing, cleaning up...");
                    calendarView.getCalendarSources().clear();
                    eventCalendar.clear();
                }
            });

            stage.show();
            System.out.println("CalendarView shown");
        });
    }

    private Entry<String> evenementToEntry(Evenement evt) {
        String title = evt.getNom();
        java.sql.Date date = evt.getDate();
        java.sql.Time time = evt.getHeure();
        java.time.LocalDate localDate = date.toLocalDate();
        java.time.LocalTime localTime = time.toLocalTime();
        java.time.LocalDateTime start = java.time.LocalDateTime.of(localDate, localTime);
        // End at 23:59 of the event day
        java.time.LocalDateTime end = java.time.LocalDateTime.of(localDate, java.time.LocalTime.of(23, 59));
        Entry<String> entry = new Entry<>(title);
        entry.setLocation(evt.getLieu());
        entry.setInterval(start, end);
        entry.setZoneId(ZoneId.systemDefault());
        return entry;
    }

    private void showDetails(Evenement evenement) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esprit/wonderwise/FrontOffice/Evenement/EvenementDetailsFront.fxml"));
        Parent root = loader.load();
        EvenementDetailsFrontController controller = loader.getController();
        controller.setDetailsData(evenement);
        Stage stage = (Stage) evenementCards.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    // --- Search and Filter Helper ---
    private void applySearchFilter() {
        String search = (searchField != null) ? searchField.getText() : null;
        String category = (filterCombo != null && filterCombo.getValue() != null) ? filterCombo.getValue() : null;
        loadEvenements(search, category);
    }
}