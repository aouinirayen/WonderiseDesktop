package com.esprit.wonderwise.Controller.FrontOffice;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CalendarController {
    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;
    @FXML private Button previousButton;
    @FXML private Button nextButton;

    private YearMonth currentYearMonth;
    private List<StackPane> allCalendarDays = new ArrayList<>(35);
    private ObjectProperty<LocalDate> selectedDate = new SimpleObjectProperty<>();
    private PackagesController packagesController;

    public void setPackagesController(PackagesController packagesController) {
        this.packagesController = packagesController;
    }

    public ObjectProperty<LocalDate> selectedDateProperty() {
        return selectedDate;
    }

    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        populateCalendar(currentYearMonth);

        previousButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            populateCalendar(currentYearMonth);
        });

        nextButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            populateCalendar(currentYearMonth);
        });
    }

    private void populateCalendar(YearMonth yearMonth) {
        // Update the month/year label
        monthYearLabel.setText(yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        // Get the first day of the month
        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
        
        // Get the day of week for the first day (1 = Monday, 7 = Sunday)
        int dayOfWeekValue = calendarDate.getDayOfWeek().getValue();

        // Clear the calendar grid
        calendarGrid.getChildren().clear();

        // Add day labels
        String[] dayNames = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.getStyleClass().add("calendar-day-header");
            calendarGrid.add(dayLabel, i, 0);
        }

        int row = 1;
        int col = dayOfWeekValue - 1;

        // Fill calendar with days
        for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
            final LocalDate date = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), i);
            StackPane dayPane = new StackPane();
            dayPane.getStyleClass().add("calendar-day-cell");
            
            Text dayText = new Text(String.valueOf(i));
            dayText.getStyleClass().add("calendar-day-text");
            
            // Highlight current day
            if (date.equals(LocalDate.now())) {
                dayPane.getStyleClass().add("current-day");
            }

            // Highlight selected date
            if (date.equals(selectedDate.get())) {
                dayPane.getStyleClass().add("selected-day");
            }
            
            // Add click handler
            dayPane.setOnMouseClicked(e -> {
                // Remove selected style from all cells
                calendarGrid.getChildren().stream()
                    .filter(node -> node instanceof StackPane)
                    .map(node -> (StackPane) node)
                    .forEach(cell -> cell.getStyleClass().remove("selected-day"));
                
                // Add selected style to clicked cell
                dayPane.getStyleClass().add("selected-day");
                
                // Update selected date
                selectedDate.set(date);
                
                // Filter packages if controller is set
                if (packagesController != null) {
                    packagesController.filterByDate(date);
                }
            });
            
            dayPane.getChildren().add(dayText);
            calendarGrid.add(dayPane, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }
}
