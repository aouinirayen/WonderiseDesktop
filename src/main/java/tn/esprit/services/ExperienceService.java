package tn.esprit.services;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import tn.esprit.models.Experience;

public class ExperienceService {
    private Connection conn;
    private static final String INSERT_QUERY = "INSERT INTO experience (titre, description, image, lieu, categorie, id_client, date, date_creation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE experience SET titre=?, description=?, image=?, lieu=?, categorie=?, id_client=?, date=? WHERE id=?";
    private static final String DELETE_QUERY = "DELETE FROM experience WHERE id=?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM experience";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM experience WHERE id=?";

    public ExperienceService() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/country", "root", "");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
        }
    }

    public void add(Experience experience) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, experience.getTitre());
            pst.setString(2, experience.getDescription());
            pst.setString(3, experience.getImage());
            pst.setString(4, experience.getLieu());
            pst.setString(5, experience.getCategorie());
            pst.setInt(6, experience.getIdClient());
            pst.setDate(7, Date.valueOf(experience.getDate()));
            pst.setDate(8, Date.valueOf(LocalDate.now()));
            
            pst.executeUpdate();
            
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    experience.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void update(Experience experience) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(UPDATE_QUERY)) {
            pst.setString(1, experience.getTitre());
            pst.setString(2, experience.getDescription());
            pst.setString(3, experience.getImage());
            pst.setString(4, experience.getLieu());
            pst.setString(5, experience.getCategorie());
            pst.setInt(6, experience.getIdClient());
            pst.setDate(7, Date.valueOf(experience.getDate()));
            pst.setInt(8, experience.getId());
            
            pst.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(DELETE_QUERY)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    public List<Experience> readAll() throws SQLException {
        List<Experience> experiences = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL_QUERY)) {
            
            while (rs.next()) {
                experiences.add(mapResultSetToExperience(rs));
            }
        }
        return experiences;
    }

    public List<Experience> getAll() throws SQLException {
        List<Experience> experiences = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL_QUERY)) {
            
            while (rs.next()) {
                experiences.add(mapResultSetToExperience(rs));
            }
        }
        return experiences;
    }

    public Experience getById(int id) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(SELECT_BY_ID_QUERY)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToExperience(rs);
                }
            }
        }
        return null;
    }

    private Experience mapResultSetToExperience(ResultSet rs) throws SQLException {
        return new Experience(
            rs.getInt("id"),
            rs.getString("titre"),
            rs.getString("description"),
            rs.getString("image"),
            rs.getString("lieu"),
            rs.getString("categorie"),
            rs.getInt("id_client"),
            rs.getDate("date").toLocalDate(),
            rs.getDate("date_creation").toLocalDate()
        );
    }
}
