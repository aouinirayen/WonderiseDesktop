module com.esprit.wonderwise {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires java.desktop;

    requires jakarta.mail;
    requires java.net.http;
    requires org.json;
    requires vosk;

    opens com.esprit.wonderwise to javafx.fxml;
    exports com.esprit.wonderwise;

    exports com.esprit.wonderwise.Controller.BackOffice;
    exports com.esprit.wonderwise.Controller.FrontOffice;

    opens com.esprit.wonderwise.Controller.BackOffice to javafx.fxml;
    opens com.esprit.wonderwise.Controller.FrontOffice to javafx.fxml;

    exports com.esprit.wonderwise.Controller;
    opens com.esprit.wonderwise.Controller to javafx.fxml;
}