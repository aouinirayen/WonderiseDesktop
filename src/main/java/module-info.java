module com.esprit.wonderwise {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;
    requires java.net.http;
    requires org.controlsfx.controls;

    opens com.esprit.wonderwise to javafx.fxml;
    opens com.esprit.wonderwise.Controller.BackOffice to javafx.fxml;
    opens com.esprit.wonderwise.Controller.FrontOffice to javafx.fxml;
    opens com.esprit.wonderwise.Model to javafx.base;

    exports com.esprit.wonderwise;
    exports com.esprit.wonderwise.Controller.BackOffice;
    exports com.esprit.wonderwise.Controller.FrontOffice;
    exports com.esprit.wonderwise.Model;
    exports com.esprit.wonderwise.Service;
}