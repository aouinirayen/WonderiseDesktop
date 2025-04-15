package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.Celebrity;
import com.esprit.wonderwise.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CelebrityService {
    private Connection conn;

    public CelebrityService() {
        conn = DataSource.getInstance().getCnx();
    }

    public void add(Celebrity celebrity) {
        String query = "INSERT INTO selebrity (country_id, name, work, img, description, job, date_of_birth, nationality, notable_works, personal_life, net_worth) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, celebrity.getCountryId());
            ps.setString(2, celebrity.getName());
            ps.setString(3, celebrity.getWork());
            ps.setString(4, celebrity.getImg());
            ps.setString(5, celebrity.getDescription());
            ps.setString(6, celebrity.getJob());
            ps.setDate(7, celebrity.getDateOfBirth());
            ps.setString(8, celebrity.getNationality());
            ps.setString(9, celebrity.getNotableWorks());
            ps.setString(10, celebrity.getPersonalLife());
            ps.setDouble(11, celebrity.getNetWorth());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Celebrity> readAll() {
        List<Celebrity> celebrities = new ArrayList<>();
        String query = "SELECT * FROM selebrity ORDER BY id DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Celebrity celebrity = new Celebrity(
                        rs.getInt("id"),
                        rs.getInt("country_id"),
                        rs.getString("name"),
                        rs.getString("work"),
                        rs.getString("img"),
                        rs.getString("description"),
                        rs.getString("job"),
                        rs.getDate("date_of_birth"),
                        rs.getString("nationality"),
                        rs.getString("notable_works"),
                        rs.getString("personal_life"),
                        rs.getDouble("net_worth")
                );
                celebrities.add(celebrity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return celebrities;
    }

    public List<Celebrity> readByCountryId(int countryId) {
        List<Celebrity> celebrities = new ArrayList<>();
        String query = "SELECT * FROM selebrity WHERE country_id = ? ORDER BY id DESC";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, countryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Celebrity celebrity = new Celebrity(
                        rs.getInt("id"),
                        rs.getInt("country_id"),
                        rs.getString("name"),
                        rs.getString("work"),
                        rs.getString("img"),
                        rs.getString("description"),
                        rs.getString("job"),
                        rs.getDate("date_of_birth"),
                        rs.getString("nationality"),
                        rs.getString("notable_works"),
                        rs.getString("personal_life"),
                        rs.getDouble("net_worth")
                );
                celebrities.add(celebrity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return celebrities;
    }

    public void update(Celebrity celebrity) {
        String query = "UPDATE selebrity SET country_id=?, name=?, work=?, img=?, description=?, job=?, date_of_birth=?, nationality=?, notable_works=?, personal_life=?, net_worth=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, celebrity.getCountryId());
            ps.setString(2, celebrity.getName());
            ps.setString(3, celebrity.getWork());
            ps.setString(4, celebrity.getImg());
            ps.setString(5, celebrity.getDescription());
            ps.setString(6, celebrity.getJob());
            ps.setDate(7, celebrity.getDateOfBirth());
            ps.setString(8, celebrity.getNationality());
            ps.setString(9, celebrity.getNotableWorks());
            ps.setString(10, celebrity.getPersonalLife());
            ps.setDouble(11, celebrity.getNetWorth());
            ps.setInt(12, celebrity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM selebrity WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Celebrity getById(int id) {
        String query = "SELECT * FROM selebrity WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Celebrity(
                        rs.getInt("id"),
                        rs.getInt("country_id"),
                        rs.getString("name"),
                        rs.getString("work"),
                        rs.getString("img"),
                        rs.getString("description"),
                        rs.getString("job"),
                        rs.getDate("date_of_birth"),
                        rs.getString("nationality"),
                        rs.getString("notable_works"),
                        rs.getString("personal_life"),
                        rs.getDouble("net_worth")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}