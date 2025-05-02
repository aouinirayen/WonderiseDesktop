package com.esprit.wonderwise.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {



    private DataSource() {
        try {
            cnx= DriverManager.getConnection(url,login,pwd);
            System.out.println("✅ Database connection successful.");
        } catch (SQLException e) {
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