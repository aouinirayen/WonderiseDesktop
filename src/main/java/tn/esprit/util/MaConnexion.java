package tn.esprit.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MaConnexion {
    //DB
    final String URL="jdbc:mysql://localhost:3306/country";
    final String USERNAME="root";
    final String PASSWORD="";

    //att

   private Connection cnx;
   static MaConnexion instance;
   //constructor
    public MaConnexion(){
        try {
            cnx= DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("connexion établie avec succes");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Connection getCnx() {
        return cnx;
    }
    public static MaConnexion getInstance(){
        if(instance ==null)
            instance=new MaConnexion();
        return instance;
    }
}
