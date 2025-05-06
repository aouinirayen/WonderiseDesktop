package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.Reclamation;
import com.esprit.wonderwise.Model.Reponse;
import com.esprit.wonderwise.Model.Status;
import com.esprit.wonderwise.Utils.BadWordsFilter;
import com.esprit.wonderwise.Utils.DataSource;
import java.sql.*;
import java.util.*;

public class ReclamationService implements InterfaceCRUD<Reclamation> {
    private final Connection con = DataSource.getInstance().getCnx();

    @Override
    public void add(Reclamation r) {
        // Check for bad words in both object and description
        if (BadWordsFilter.containsBadWords(r.getObjet()) ||
                BadWordsFilter.containsBadWords(r.getDescription())) {
            System.out.println("Reclamation contains inappropriate language and was not added");
            throw new IllegalArgumentException("Your reclamation contains inappropriate language");
        }

        String req = "INSERT INTO reclamation (objet, description, date, status, user_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, r.getObjet());
            pst.setString(2, r.getDescription());
            pst.setDate(3, java.sql.Date.valueOf(r.getDate()));
            pst.setString(4, r.getStatus().name());
            pst.setInt(5, r.getUserId());
            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    r.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding reclamation: " + e.getMessage());
            throw new RuntimeException("Failed to add reclamation", e);
        }
    }

    @Override
    public void update(Reclamation r) {
        String req = "UPDATE reclamation SET objet=?, description=?, date=?, status=?, user_id=? WHERE id=?";
        try (PreparedStatement pst = con.prepareStatement(req)) {
            pst.setString(1, r.getObjet());
            pst.setString(2, r.getDescription());
            pst.setDate(3, java.sql.Date.valueOf(r.getDate()));
            pst.setString(4, r.getStatus().name());
            pst.setInt(5, r.getUserId());
            pst.setInt(6, r.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating reclamation: " + e.getMessage());
        }
    }

    public boolean updateStatus(int id, Status newStatus) {
        String query = "UPDATE reclamation SET status = ? WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, newStatus.name()); // attention ici on utilise .name() pas .getLabel()
            pst.setInt(2, id);
            int rowsUpdated = pst.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("Error updating status: " + e.getMessage());
            return false;
        }
    }


    @Override
    public boolean delete(Reclamation r) {
        try {
            // First delete associated responses
            String deleteResponses = "DELETE FROM reponse WHERE reclamation_id=?";
            try (PreparedStatement pst = con.prepareStatement(deleteResponses)) {
                pst.setInt(1, r.getId());
                pst.executeUpdate();
            }

            // Then delete the reclamation
            String deleteReclamation = "DELETE FROM reclamation WHERE id=?";
            try (PreparedStatement pst = con.prepareStatement(deleteReclamation)) {
                pst.setInt(1, r.getId());
                return pst.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error deleting reclamation: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Reclamation> find() {
        List<Reclamation> reclamations = new ArrayList<>();
        String req = "SELECT * FROM reclamation";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                reclamations.add(mapResultSetToReclamation(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error finding reclamations: " + e.getMessage());
        }
        return reclamations;
    }

    public Reclamation findById(int id) {
        String req = "SELECT * FROM reclamation WHERE id=?";
        try (PreparedStatement pst = con.prepareStatement(req)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReclamation(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding reclamation by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Reclamation> findPage(int page, int itemsPerPage) {
        return findFilteredPage(page, itemsPerPage, null, "");
    }

    public List<Reclamation> findFilteredPage(int page, int itemsPerPage, Status status, String searchQuery) {
        List<Reclamation> reclamations = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM reclamation WHERE 1=1");

        if (status != null) {
            query.append(" AND status = ?");
        }
        if (!searchQuery.isEmpty()) {
            query.append(" AND (objet LIKE ? OR description LIKE ?)");
        }

        query.append(" ORDER BY CASE status " +
                "WHEN 'ENVOYEE' THEN 1 " +
                "WHEN 'EN_COURS' THEN 2 " +
                "WHEN 'TRAITEE' THEN 3 " +
                "WHEN 'REJETEE' THEN 4 " +
                "ELSE 5 END " +
                "LIMIT ? OFFSET ?");

        try (PreparedStatement pst = con.prepareStatement(query.toString())) {
            int paramIndex = 1;

            if (status != null) {
                pst.setString(paramIndex++, status.name());
            }
            if (!searchQuery.isEmpty()) {
                String searchPattern = "%" + searchQuery + "%";
                pst.setString(paramIndex++, searchPattern);
                pst.setString(paramIndex++, searchPattern);
            }

            pst.setInt(paramIndex++, itemsPerPage);
            pst.setInt(paramIndex, (page - 1) * itemsPerPage);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                reclamations.add(mapResultSetToReclamation(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error in findFilteredPage: " + e.getMessage());
        }
        return reclamations;
    }

    public int getTotalPages(int itemsPerPage) {
        return getTotalFilteredPages(itemsPerPage, null, "");
    }

    public int getTotalFilteredPages(int itemsPerPage, Status status, String searchQuery) {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM reclamation WHERE 1=1");

        if (status != null) {
            query.append(" AND status=?");
        }
        if (!searchQuery.isEmpty()) {
            query.append(" AND (objet LIKE ? OR description LIKE ?)");
        }

        try (PreparedStatement pst = con.prepareStatement(query.toString())) {
            int paramIndex = 1;

            if (status != null) {
                pst.setString(paramIndex++, status.name());
            }
            if (!searchQuery.isEmpty()) {
                String searchPattern = "%" + searchQuery + "%";
                pst.setString(paramIndex++, searchPattern);
                pst.setString(paramIndex++, searchPattern);
            }

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int totalItems = rs.getInt(1);
                    return totalItems == 0 ? 1 : (int) Math.ceil((double) totalItems / itemsPerPage);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting total pages: " + e.getMessage());
        }
        return 1;
    }

    public Map<String, Integer> getReclamationsFrequentes() {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT objet, COUNT(*) as count FROM reclamation GROUP BY objet ORDER BY count DESC LIMIT 5";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                stats.put(rs.getString("objet"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting reclamation stats: " + e.getMessage());
        }
        return stats;
    }

    private Reclamation mapResultSetToReclamation(ResultSet rs) throws SQLException {
        Reclamation r = new Reclamation();
        r.setId(rs.getInt("id"));
        r.setObjet(rs.getString("objet"));
        r.setDescription(rs.getString("description"));
        r.setDate(rs.getDate("date").toLocalDate());
        r.setUserId(rs.getInt("user_id"));

        try {
            String statusStr = rs.getString("status");
            // Handle legacy status values if needed
            if (statusStr.equalsIgnoreCase("traitée")) {
                r.setStatus(Status.TRAITEE);
            } else if (statusStr.equalsIgnoreCase("rejetée")) {
                r.setStatus(Status.REJETEE);
            } else {
                r.setStatus(Status.valueOf(statusStr));
            }
        } catch (IllegalArgumentException e) {
            r.setStatus(Status.ENVOYEE);
        }

        ReponseService reponseService = new ReponseService();
        r.setReponses(reponseService.findByReclamationId(r.getId()));

        return r;
    }//
    public boolean deleteReclamationsByType(String type) {
        try {
            // 1. Supprimer d'abord les réponses associées
            String deleteResponses = "DELETE FROM reponse WHERE reclamation_id IN " +
                    "(SELECT id FROM reclamation WHERE objet = ?)";
            try (PreparedStatement pst = con.prepareStatement(deleteResponses)) {
                pst.setString(1, type);
                pst.executeUpdate();
            }

            // 2. Puis supprimer les réclamations
            String deleteReclamations = "DELETE FROM reclamation WHERE objet = ?";
            try (PreparedStatement pst = con.prepareStatement(deleteReclamations)) {
                pst.setString(1, type);
                int rowsDeleted = pst.executeUpdate();
                return rowsDeleted > 0; // Retourne true si au moins une réclamation a été supprimée
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression: " + e.getMessage());
            return false;
        }
    }

}