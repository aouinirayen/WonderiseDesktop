package com.esprit.wonderwise.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

    private Connection cnx;
    private  String url="jdbc:mysql://localhost:3306/projetrecl";
    private  String login="root";
    private  String pwd="";
    private static DataSource instance;

    private DataSource() {
        try {
            cnx= DriverManager.getConnection(url,login,pwd);
            System.out.println("✅ Database connection successful.");
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static DataSource getInstance(){
        if (instance==null)
            instance=new DataSource();
        return instance;
    };

    public Connection getCnx() {
        return cnx;
    }

}