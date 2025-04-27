package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.User;
import com.esprit.wonderwise.Utils.DataSource;

import java.sql.*;

public class UserService {
    private Connection con;

    public UserService() {
        con = DataSource.getInstance().getCnx();
    }

    public User findById(int id) {
        String query = "SELECT * FROM user WHERE id = ?";
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setPrenom(rs.getString("prenom"));
                user.setNom(rs.getString("nom"));
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
