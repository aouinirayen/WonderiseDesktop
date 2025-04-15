package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.TraditionalFood;
import com.esprit.wonderwise.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TraditionalFoodService {
    private Connection conn;

    public TraditionalFoodService() {
        conn = DataSource.getInstance().getCnx();
    }

    public void add(TraditionalFood food) {
        String query = "INSERT INTO traditional_food (country_id, name, img, description, recipe) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, food.getCountryId());
            ps.setString(2, food.getName());
            ps.setString(3, food.getImg());
            ps.setString(4, food.getDescription());
            ps.setString(5, food.getRecipe());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<TraditionalFood> readAll() {
        List<TraditionalFood> foods = new ArrayList<>();
        String query = "SELECT * FROM traditional_food ORDER BY id DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                TraditionalFood food = new TraditionalFood(
                        rs.getInt("id"),
                        rs.getInt("country_id"),
                        rs.getString("name"),
                        rs.getString("img"),
                        rs.getString("description"),
                        rs.getString("recipe")
                );
                foods.add(food);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foods;
    }

    public List<TraditionalFood> readByCountryId(int countryId) {
        List<TraditionalFood> foods = new ArrayList<>();
        String query = "SELECT * FROM traditional_food WHERE country_id = ? ORDER BY id DESC";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, countryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TraditionalFood food = new TraditionalFood(
                        rs.getInt("id"),
                        rs.getInt("country_id"),
                        rs.getString("name"),
                        rs.getString("img"),
                        rs.getString("description"),
                        rs.getString("recipe")
                );
                foods.add(food);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foods;
    }

    public void update(TraditionalFood food) {
        String query = "UPDATE traditional_food SET country_id=?, name=?, img=?, description=?, recipe=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, food.getCountryId());
            ps.setString(2, food.getName());
            ps.setString(3, food.getImg());
            ps.setString(4, food.getDescription());
            ps.setString(5, food.getRecipe());
            ps.setInt(6, food.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM traditional_food WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TraditionalFood getById(int id) {
        String query = "SELECT * FROM traditional_food WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new TraditionalFood(
                        rs.getInt("id"),
                        rs.getInt("country_id"),
                        rs.getString("name"),
                        rs.getString("img"),
                        rs.getString("description"),
                        rs.getString("recipe")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}