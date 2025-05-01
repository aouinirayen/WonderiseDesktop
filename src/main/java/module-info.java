module com.esprit.wonderwise {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires javafx.swing;
    requires org.apache.poi.ooxml;
    requires webcam.capture;
    requires com.github.librepdf.openpdf;

    opens com.esprit.wonderwise to javafx.fxml;
    exports com.esprit.wonderwise;

    exports com.esprit.wonderwise.Controller.BackOffice;
    exports com.esprit.wonderwise.Controller.FrontOffice;

    opens com.esprit.wonderwise.Controller.BackOffice to javafx.fxml;
    opens com.esprit.wonderwise.Controller.FrontOffice to javafx.fxml;

    exports com.esprit.wonderwise.Controller;
    opens com.esprit.wonderwise.Controller to javafx.fxml;
}