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
        System.out.println(" Exécution de la requête : " + sql);
        
        try (Statement stmt = cnx.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println(" Connexion à la base de données réussie");
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            System.out.println(" Colonnes trouvées :");
            for (int i = 1; i <= columnCount; i++) {
                System.out.println(" - " + metaData.getColumnName(i));
            }
            
            while (rs.next()) {
                offre offre = new offre();
                offre.setId(rs.getInt("id"));
                offre.setTitre(rs.getString("titre"));
                offre.setDescription(rs.getString("description"));
                offre.setPrix(rs.getDouble("prix"));
                offre.setNombrePlaces(rs.getInt("nombre_places"));
                offre.setPlacesDisponibles(rs.getInt("places_disponibles"));
                
                Timestamp dateCreation = rs.getTimestamp("date_creation");
                if (dateCreation != null) {
                    offre.setDateCreation(dateCreation.toLocalDateTime());
                }
                
                Timestamp dateDebut = rs.getTimestamp("date_debut");
                if (dateDebut != null) {
                    offre.setDateDebut(dateDebut.toLocalDateTime());
                }
                
                Timestamp dateFin = rs.getTimestamp("date_fin");
                if (dateFin != null) {
                    offre.setDateFin(dateFin.toLocalDateTime());
                }
                
                offre.setImage(rs.getString("image"));
                offre.setPays(rs.getString("pays"));
                
                Object rating = rs.getObject("rating");
                if (rating != null) {
                    offre.setRating(rs.getDouble("rating"));
                }
                
                Object ratingCount = rs.getObject("rating_count");
                if (ratingCount != null) {
                    offre.setRatingCount(rs.getInt("rating_count"));
                }
                
                offres.add(offre);
                System.out.println(" Offre chargée : " + offre.getTitre());
            }
            
            System.out.println(" Total des offres chargées : " + offres.size());
        } catch (SQLException e) {
            System.out.println(" Erreur lors du chargement des offres : " + e.getMessage());
            e.printStackTrace();
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

    public void updateRating(int offreId, double rating, int ratingCount) throws SQLException {
        String sql = "UPDATE offre SET rating = ?, rating_count = ? WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setDouble(1, rating);
            stmt.setInt(2, ratingCount);
            stmt.setInt(3, offreId);
            stmt.executeUpdate();
        }
    }

    public void updatePlacesDisponibles(int offreId, int nombrePersonnes) throws SQLException {
        String sql = "UPDATE offre SET places_disponibles = places_disponibles - ? WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, nombrePersonnes);
            stmt.setInt(2, offreId);
            stmt.executeUpdate();
        }
    }
}