module com.esprit.wonderwise {
    // Modules JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // JDBC
    requires java.sql;

    // Modules Google OAuth
    requires com.google.api.client;
    requires com.google.api.client.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires google.api.client;
    requires google.http.client.jackson2;
    requires google.api.services.oauth2.v2.rev157;

    // Module HTTP Server requis pour LocalServerReceiver (Jetty)
    requires jdk.httpserver;

    // Mail et hashage
    requires jakarta.mail;
    requires jbcrypt;

    // Ouverture pour JavaFX FXMLLoader
    opens com.esprit.wonderwise to javafx.fxml;
    opens com.esprit.wonderwise.Controller to javafx.fxml;
    opens com.esprit.wonderwise.Controller.Auth to javafx.fxml;
    opens com.esprit.wonderwise.Controller.BackOffice to javafx.fxml;
    opens com.esprit.wonderwise.Controller.FrontOffice to javafx.fxml;
    opens com.esprit.wonderwise.Controller.FrontOffice.Users to javafx.fxml;

    // Exports des packages
    exports com.esprit.wonderwise;
    exports com.esprit.wonderwise.Controller;
    exports com.esprit.wonderwise.Controller.Auth;
    exports com.esprit.wonderwise.Controller.BackOffice;
    exports com.esprit.wonderwise.Controller.FrontOffice;
}
