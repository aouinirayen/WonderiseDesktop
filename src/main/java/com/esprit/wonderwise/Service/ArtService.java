package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.Art;
import com.esprit.wonderwise.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtService {
    private Connection conn;

    public ArtService() {
        conn = DataSource.getInstance().getCnx();
    }

    public void add(Art art) {
        String query = "INSERT INTO art (country_id, name, img, description, date, type) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, art.getCountryId());
            ps.setString(2, art.getName());
            ps.setString(3, art.getImg());
            ps.setString(4, art.getDescription());
            ps.setString(5, art.getDate());
            ps.setString(6, art.getType());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Art> readAll() {
        List<Art> arts = new ArrayList<>();
        String query = "SELECT * FROM art ORDER BY id DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Art art = new Art(
                        rs.getInt("id"),
                        rs.getInt("country_id"),
                        rs.getString("name"),
                        rs.getString("img"),
                        rs.getString("description"),
                        rs.getString("date"),
                        rs.getString("type")
                );
                arts.add(art);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arts;
    }

    public List<Art> readByCountryId(int countryId) {
        List<Art> arts = new ArrayList<>();
        String query = "SELECT * FROM art WHERE country_id = ? ORDER BY id DESC";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, countryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Art art = new Art(
                        rs.getInt("id"),
                        rs.getInt("country_id"),
                        rs.getString("name"),
                        rs.getString("img"),
                        rs.getString("description"),
                        rs.getString("date"),
                        rs.getString("type")
                );
                arts.add(art);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arts;
    }

    public void update(Art art) {
        String query = "UPDATE art SET country_id=?, name=?, img=?, description=?, date=?, type=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, art.getCountryId());
            ps.setString(2, art.getName());
            ps.setString(3, art.getImg());
            ps.setString(4, art.getDescription());
            ps.setString(5, art.getDate());
            ps.setString(6, art.getType());
            ps.setInt(7, art.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM art WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Art getById(int id) {
        String query = "SELECT * FROM art WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Art(
                        rs.getInt("id"),
                        rs.getInt("country_id"),
                        rs.getString("name"),
                        rs.getString("img"),
                        rs.getString("description"),
                        rs.getString("date"),
                        rs.getString("type")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}