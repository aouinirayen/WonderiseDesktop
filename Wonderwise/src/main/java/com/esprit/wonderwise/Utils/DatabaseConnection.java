package com.esprit.wonderwise.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/gestion";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            // Vérifie si MySQL est actif
            try (java.net.Socket socket = new java.net.Socket("localhost", 3306)) {
                // OK
            } catch (java.io.IOException e) {
                throw new SQLException("Cannot connect to MySQL server. Make sure it's running on port 3306.");
            }

            // Charge le driver JDBC
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Check your dependencies.", e);
            }

            // Crée et retourne une nouvelle connexion
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // Teste l'accès à la base
            try (java.sql.Statement stmt = conn.createStatement()) {
                stmt.execute("USE gestion");
            }

            return conn;

        } catch (SQLException e) {
            throw new SQLException("Database connection error: " + e.getMessage(), e);
        }
    }
}
    