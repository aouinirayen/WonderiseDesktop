package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.Evenement;
import com.esprit.wonderwise.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvenementService {
    private Connection conn;

    public EvenementService() {
        conn = DataSource.getInstance().getCnx();
    }

    public void add(Evenement evenement) {
        String query = "INSERT INTO evenement (nom, date, heure, lieu, description, place_max, prix, status, pays, " +
                "categorie, photo, guide_id, likes_count, is_favorite, latitude, longitude, is_annule, " +
                "is_interested, is_liked) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, evenement.getNom());
            stmt.setDate(2, evenement.getDate());
            stmt.setTime(3, evenement.getHeure());
            stmt.setString(4, evenement.getLieu());
            stmt.setString(5, evenement.getDescription());
            stmt.setInt(6, evenement.getPlaceMax());
            stmt.setDouble(7, evenement.getPrix());
            stmt.setString(8, evenement.getStatus());
            stmt.setString(9, evenement.getPays());
            stmt.setString(10, evenement.getCategorie());
            stmt.setString(11, evenement.getPhoto());
            stmt.setInt(12, evenement.getGuideId());
            stmt.setInt(13, evenement.getLikesCount());
            stmt.setBoolean(14, evenement.isFavorite());
            stmt.setDouble(15, evenement.getLatitude());
            stmt.setDouble(16, evenement.getLongitude());
            stmt.setBoolean(17, evenement.isAnnule());
            stmt.setInt(18, evenement.getInterested());
            stmt.setBoolean(19, evenement.isLiked());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                evenement.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Evenement> readAll() {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenement ORDER BY id DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Evenement evenement = new Evenement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDate("date"),
                        rs.getTime("heure"),
                        rs.getString("lieu"),
                        rs.getString("description"),
                        rs.getInt("place_max"),
                        rs.getDouble("prix"),
                        rs.getString("status"),
                        rs.getString("pays"),
                        rs.getString("categorie"),
                        rs.getString("photo"),
                        rs.getInt("guide_id"),
                        rs.getInt("likes_count"),
                        rs.getBoolean("is_favorite"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getBoolean("is_annule"),
                        rs.getInt("is_interested"),
                        rs.getBoolean("is_liked")
                );
                evenements.add(evenement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenements;
    }

    public List<Evenement> readByGuideId(int guideId) {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenement WHERE guide_id = ? ORDER BY id DESC";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, guideId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Evenement evenement = new Evenement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDate("date"),
                        rs.getTime("heure"),
                        rs.getString("lieu"),
                        rs.getString("description"),
                        rs.getInt("place_max"),
                        rs.getDouble("prix"),
                        rs.getString("status"),
                        rs.getString("pays"),
                        rs.getString("categorie"),
                        rs.getString("photo"),
                        rs.getInt("guide_id"),
                        rs.getInt("likes_count"),
                        rs.getBoolean("is_favorite"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getBoolean("is_annule"),
                        rs.getInt("is_interested"),
                        rs.getBoolean("is_liked")
                );
                evenements.add(evenement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenements;
    }

    public Evenement getById(int id) {
        String query = "SELECT * FROM evenement WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Evenement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDate("date"),
                        rs.getTime("heure"),
                        rs.getString("lieu"),
                        rs.getString("description"),
                        rs.getInt("place_max"),
                        rs.getDouble("prix"),
                        rs.getString("status"),
                        rs.getString("pays"),
                        rs.getString("categorie"),
                        rs.getString("photo"),
                        rs.getInt("guide_id"),
                        rs.getInt("likes_count"),
                        rs.getBoolean("is_favorite"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getBoolean("is_annule"),
                        rs.getInt("is_interested"),
                        rs.getBoolean("is_liked")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Evenement evenement) {
        String query = "UPDATE evenement SET nom = ?, date = ?, heure = ?, lieu = ?, description = ?, place_max = ?, " +
                "prix = ?, status = ?, pays = ?, categorie = ?, photo = ?, guide_id = ?, likes_count = ?, " +
                "is_favorite = ?, latitude = ?, longitude = ?, is_annule = ?, is_interested = ?, is_liked = ? " +
                "WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, evenement.getNom());
            stmt.setDate(2, evenement.getDate());
            stmt.setTime(3, evenement.getHeure());
            stmt.setString(4, evenement.getLieu());
            stmt.setString(5, evenement.getDescription());
            stmt.setInt(6, evenement.getPlaceMax());
            stmt.setDouble(7, evenement.getPrix());
            stmt.setString(8, evenement.getStatus());
            stmt.setString(9, evenement.getPays());
            stmt.setString(10, evenement.getCategorie());
            stmt.setString(11, evenement.getPhoto());
            stmt.setInt(12, evenement.getGuideId());
            stmt.setInt(13, evenement.getLikesCount());
            stmt.setBoolean(14, evenement.isFavorite());
            stmt.setDouble(15, evenement.getLatitude());
            stmt.setDouble(16, evenement.getLongitude());
            stmt.setBoolean(17, evenement.isAnnule());
            stmt.setInt(18, evenement.getInterested());
            stmt.setBoolean(19, evenement.isLiked());
            stmt.setInt(20, evenement.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM evenement WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
