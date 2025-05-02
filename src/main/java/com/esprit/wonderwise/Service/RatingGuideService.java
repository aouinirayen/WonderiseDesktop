package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.RatingGuide;
import com.esprit.wonderwise.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingGuideService {
    private Connection conn;

    public RatingGuideService() {
        conn = DataSource.getInstance().getCnx();
    }

    public void addRating(RatingGuide rating) {
        String query = "INSERT INTO rating_guide (guide_id, user_id, rating) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, rating.getGuideId());
            stmt.setInt(2, rating.getUserId());
            stmt.setInt(3, rating.getRating());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRating(RatingGuide rating) {
        String query = "UPDATE rating_guide SET rating = ? WHERE guide_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, rating.getRating());
            stmt.setInt(2, rating.getGuideId());
            stmt.setInt(3, rating.getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<RatingGuide> getRatingsForGuide(int guideId) {
        List<RatingGuide> ratings = new ArrayList<>();
        String query = "SELECT * FROM rating_guide WHERE guide_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, guideId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                RatingGuide rating = new RatingGuide(
                        rs.getInt("id"),
                        rs.getInt("guide_id"),
                        rs.getInt("user_id"),
                        rs.getInt("rating")
                );
                ratings.add(rating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    public double getAverageRating(int guideId) {
        String query = "SELECT AVG(rating) AS avg_rating FROM rating_guide WHERE guide_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, guideId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getUserRatingForGuide(int guideId, int userId) {
        String query = "SELECT rating FROM rating_guide WHERE guide_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, guideId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("rating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // No rating found
    }
}
