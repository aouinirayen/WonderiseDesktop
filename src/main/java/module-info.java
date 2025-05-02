module com.esprit.wonderwise {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.sql;
    requires java.desktop;
    requires com.calendarfx.view;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires jxmapviewer2;

    opens com.esprit.wonderwise to javafx.fxml;
    exports com.esprit.wonderwise;

    exports com.esprit.wonderwise.Controller.BackOffice;
    exports com.esprit.wonderwise.Controller.FrontOffice;

    opens com.esprit.wonderwise.Controller.BackOffice to javafx.fxml;
    opens com.esprit.wonderwise.Controller.FrontOffice to javafx.fxml;

    exports com.esprit.wonderwise.Controller;
    opens com.esprit.wonderwise.Controller to javafx.fxml;
}