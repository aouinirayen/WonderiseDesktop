package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.Country;
import com.esprit.wonderwise.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CountryService {
    private Connection conn;

    public CountryService() {
        conn = DataSource.getInstance().getCnx();
    }

    public void add(Country country) {
        String query = "INSERT INTO country (name, img, description, currency, iso_code, calling_code, climate) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, country.getName());
            ps.setString(2, country.getImg());
            ps.setString(3, country.getDescription());
            ps.setString(4, country.getCurrency());
            ps.setString(5, country.getIsoCode());
            ps.setString(6, country.getCallingCode());
            ps.setString(7, country.getClimate());
            ps.executeUpdate();
            System.out.println("Country added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Country> readAll() {
        List<Country> countries = new ArrayList<>();
        String query = "SELECT * FROM country ORDER BY id DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Country country = new Country(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("img"),
                        rs.getString("description"),
                        rs.getString("currency"),
                        rs.getString("iso_code"),
                        rs.getString("calling_code"),
                        rs.getString("climate")
                );
                countries.add(country);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return countries;
    }

    public void update(Country country) {
        String query = "UPDATE country SET name=?, img=?, description=?, currency=?, iso_code=?, calling_code=?, climate=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, country.getName());
            ps.setString(2, country.getImg());
            ps.setString(3, country.getDescription());
            ps.setString(4, country.getCurrency());
            ps.setString(5, country.getIsoCode());
            ps.setString(6, country.getCallingCode());
            ps.setString(7, country.getClimate());
            ps.setInt(8, country.getId());
            ps.executeUpdate();
            System.out.println("Country updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM country WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Country deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Country getById(int id) {
        String query = "SELECT * FROM country WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Country(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("img"),
                        rs.getString("description"),
                        rs.getString("currency"),
                        rs.getString("iso_code"),
                        rs.getString("calling_code"),
                        rs.getString("climate")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}