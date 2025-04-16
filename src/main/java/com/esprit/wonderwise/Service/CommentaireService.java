package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.Commentaire;
import com.esprit.wonderwise.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService {

    private Connection conn;

    public CommentaireService() {
        conn = DataSource.getInstance().getCnx();
    }

    // Create
    public void create(Commentaire commentaire) {
        String query = "INSERT INTO commentaire (evenement_id, commentaire, date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, commentaire.getEvenementId());
            stmt.setString(2, commentaire.getCommentaire());
            stmt.setTimestamp(3, Timestamp.valueOf(commentaire.getDate()));
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                commentaire.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Read All by Evenement ID
    public List<Commentaire> readByEvenementId(int evenementId) {
        List<Commentaire> commentaires = new ArrayList<>();
        String query = "SELECT * FROM commentaire WHERE evenement_id = ? ORDER BY date DESC";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, evenementId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Commentaire commentaire = new Commentaire();
                commentaire.setId(rs.getInt("id"));
                commentaire.setEvenementId(rs.getInt("evenement_id"));
                commentaire.setCommentaire(rs.getString("commentaire"));
                commentaire.setDate(rs.getTimestamp("date").toLocalDateTime());
                commentaires.add(commentaire);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentaires;
    }

    // Update
    public void update(Commentaire commentaire) {
        String query = "UPDATE commentaire SET commentaire = ?, date = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, commentaire.getCommentaire());
            stmt.setTimestamp(2, Timestamp.valueOf(commentaire.getDate()));
            stmt.setInt(3, commentaire.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete
    public void delete(int id) {
        String query = "DELETE FROM commentaire WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}