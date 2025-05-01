package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.Rating;
import com.esprit.wonderwise.Utils.DataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RatingService {
    private Connection conn;

    public RatingService() {
        conn = DataSource.getInstance().getCnx();
    }

    public void add(Rating rating) {
        String query = "INSERT INTO rating (country_id, is_like, created_at) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, rating.getCountryId());
            ps.setBoolean(2, rating.isLike());
            ps.setTimestamp(3, Timestamp.valueOf(rating.getCreatedAt()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Rating> getRatingsByCountry(int countryId) {
        List<Rating> ratings = new ArrayList<>();
        String query = "SELECT * FROM rating WHERE country_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, countryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Rating rating = new Rating(
                    rs.getInt("id"),
                    rs.getInt("country_id"),
                    rs.getBoolean("is_like"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                ratings.add(rating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    public int countLikes(int countryId) {
        String query = "SELECT COUNT(*) FROM rating WHERE country_id = ? AND is_like = 1";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, countryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countDislikes(int countryId) {
        String query = "SELECT COUNT(*) FROM rating WHERE country_id = ? AND is_like = 0";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, countryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
