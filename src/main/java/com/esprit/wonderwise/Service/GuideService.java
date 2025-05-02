package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.Guide;
import com.esprit.wonderwise.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuideService {
    private Connection conn;

    public GuideService() {
        conn = DataSource.getInstance().getCnx();
    }

    public void create(Guide guide) {
        String query = "INSERT INTO guide (nom, prenom, email, num_telephone, description, facebook, instagram, photo, nombre_avis) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, guide.getNom());
            stmt.setString(2, guide.getPrenom());
            stmt.setString(3, guide.getEmail());
            stmt.setString(4, guide.getNumTelephone());
            stmt.setString(5, guide.getDescription());
            stmt.setString(6, guide.getFacebook());
            stmt.setString(7, guide.getInstagram());
            stmt.setString(8, guide.getPhoto());
            stmt.setInt(9, guide.getNombreAvis());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                guide.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Guide> readAll() {
        List<Guide> guides = new ArrayList<>();
        String query = "SELECT * FROM guide ORDER BY id DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Guide guide = new Guide(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("num_telephone"),
                        rs.getString("description"),
                        rs.getString("facebook"),
                        rs.getString("instagram"),
                        rs.getString("photo"),
                        rs.getInt("nombre_avis")
                );
                guides.add(guide);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guides;
    }

    public Guide readById(int id) {
        String query = "SELECT * FROM guide WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Guide(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("num_telephone"),
                        rs.getString("description"),
                        rs.getString("facebook"),
                        rs.getString("instagram"),
                        rs.getString("photo"),
                        rs.getInt("nombre_avis")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Guide guide) {
        String query = "UPDATE guide SET nom = ?, prenom = ?, email = ?, num_telephone = ?, description = ?, " +
                "facebook = ?, instagram = ?, photo = ?, nombre_avis = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, guide.getNom());
            stmt.setString(2, guide.getPrenom());
            stmt.setString(3, guide.getEmail());
            stmt.setString(4, guide.getNumTelephone());
            stmt.setString(5, guide.getDescription());
            stmt.setString(6, guide.getFacebook());
            stmt.setString(7, guide.getInstagram());
            stmt.setString(8, guide.getPhoto());
            stmt.setInt(9, guide.getNombreAvis());
            stmt.setInt(10, guide.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM guide WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}