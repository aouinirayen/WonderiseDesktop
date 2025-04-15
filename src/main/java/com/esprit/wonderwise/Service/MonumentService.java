package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.Monument;
import com.esprit.wonderwise.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonumentService {
    private Connection conn;

    public MonumentService() {
        conn = DataSource.getInstance().getCnx();
    }

    public void add(Monument monument) {
        String query = "INSERT INTO monument (country_id, name, img, description) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, monument.getCountryId());
            ps.setString(2, monument.getName());
            ps.setString(3, monument.getImg());
            ps.setString(4, monument.getDescription());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Monument> readAll() {
        List<Monument> monuments = new ArrayList<>();
        String query = "SELECT * FROM monument ORDER BY id DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Monument monument = new Monument(
                        rs.getInt("id"),
                        rs.getInt("country_id"),
                        rs.getString("name"),
                        rs.getString("img"),
                        rs.getString("description")
                );
                monuments.add(monument);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monuments;
    }

    public List<Monument> readByCountryId(int countryId) {
        List<Monument> monuments = new ArrayList<>();
        String query = "SELECT * FROM monument WHERE country_id = ? ORDER BY id DESC";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, countryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Monument monument = new Monument(
                        rs.getInt("id"),
                        rs.getInt("country_id"),
                        rs.getString("name"),
                        rs.getString("img"),
                        rs.getString("description")
                );
                monuments.add(monument);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monuments;
    }

    public void update(Monument monument) {
        String query = "UPDATE monument SET country_id=?, name=?, img=?, description=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, monument.getCountryId());
            ps.setString(2, monument.getName());
            ps.setString(3, monument.getImg());
            ps.setString(4, monument.getDescription());
            ps.setInt(5, monument.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM monument WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Monument getById(int id) {
        String query = "SELECT * FROM monument WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Monument(
                        rs.getInt("id"),
                        rs.getInt("country_id"),
                        rs.getString("name"),
                        rs.getString("img"),
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}