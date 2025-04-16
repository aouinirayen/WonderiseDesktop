package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.offre;
import com.esprit.wonderwise.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OffreService {

    private Connection cnx = DataSource.getInstance().getCnx();

    public void create(offre offre) throws SQLException {
        String sql = "INSERT INTO offre (titre, description, prix, nombre_places, places_disponibles, date_creation, date_debut, date_fin, image, pays, rating, rating_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setString(1, offre.getTitre());
            stmt.setString(2, offre.getDescription());
            stmt.setDouble(3, offre.getPrix());
            stmt.setInt(4, offre.getNombrePlaces());
            stmt.setInt(5, offre.getPlacesDisponibles());
            stmt.setTimestamp(6, Timestamp.valueOf(offre.getDateCreation()));
            stmt.setTimestamp(7, offre.getDateDebut() != null ? Timestamp.valueOf(offre.getDateDebut()) : null);
            stmt.setTimestamp(8, offre.getDateFin() != null ? Timestamp.valueOf(offre.getDateFin()) : null);
            stmt.setString(9, offre.getImage());
            stmt.setString(10, offre.getPays());
            stmt.setObject(11, offre.getRating());
            stmt.setObject(12, offre.getRatingCount());
            stmt.executeUpdate();
        }
    }

    public List<offre> readAll() throws SQLException {
        List<offre> offres = new ArrayList<>();
        String sql = "SELECT * FROM offre";
        try (Statement stmt = cnx.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                offre offre = new offre();
                offre.setId(rs.getInt("id"));
                offre.setTitre(rs.getString("titre"));
                offre.setDescription(rs.getString("description"));
                offre.setPrix(rs.getDouble("prix"));
                offre.setNombrePlaces(rs.getInt("nombre_places"));
                offre.setPlacesDisponibles(rs.getInt("places_disponibles"));
                offre.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                offre.setDateDebut(rs.getTimestamp("date_debut") != null ? rs.getTimestamp("date_debut").toLocalDateTime() : null);
                offre.setDateFin(rs.getTimestamp("date_fin") != null ? rs.getTimestamp("date_fin").toLocalDateTime() : null);
                offre.setImage(rs.getString("image"));
                offre.setPays(rs.getString("pays"));
                offre.setRating(rs.getObject("rating") != null ? rs.getDouble("rating") : null);
                offre.setRatingCount(rs.getObject("rating_count") != null ? rs.getInt("rating_count") : null);
                offres.add(offre);
            }
        }
        return offres;
    }

    public void update(offre offre) throws SQLException {
        String sql = "UPDATE offre SET titre = ?, description = ?, prix = ?, nombre_places = ?, places_disponibles = ?, date_creation = ?, date_debut = ?, date_fin = ?, image = ?, pays = ?, rating = ?, rating_count = ? WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setString(1, offre.getTitre());
            stmt.setString(2, offre.getDescription());
            stmt.setDouble(3, offre.getPrix());
            stmt.setInt(4, offre.getNombrePlaces());
            stmt.setInt(5, offre.getPlacesDisponibles());
            stmt.setTimestamp(6, Timestamp.valueOf(offre.getDateCreation()));
            stmt.setTimestamp(7, offre.getDateDebut() != null ? Timestamp.valueOf(offre.getDateDebut()) : null);
            stmt.setTimestamp(8, offre.getDateFin() != null ? Timestamp.valueOf(offre.getDateFin()) : null);
            stmt.setString(9, offre.getImage());
            stmt.setString(10, offre.getPays());
            stmt.setObject(11, offre.getRating());
            stmt.setObject(12, offre.getRatingCount());
            stmt.setInt(13, offre.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM offre WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}