package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private final String URL = "jdbc:mysql://localhost:3306/country";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    
    private Connection connection;
    private static MyDatabase instance;
    
    private MyDatabase() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to Database.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }
    
    public Connection getConnection() {
        return connection;
    }
}
