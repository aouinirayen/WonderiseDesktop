package com.esprit.wonderwise.Service;


import com.esprit.wonderwise.Model.Reclamation;
import com.esprit.wonderwise.Model.Reponse;
import com.esprit.wonderwise.Model.Status;
import com.esprit.wonderwise.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService implements InterfaceCRUD<Reclamation> {
    Connection con;
    Statement ste;

    public ReclamationService() {
        con = DataSource.getInstance().getCnx();
        try {
            ste = con.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void add(Reclamation r) {
        String req = "INSERT INTO reclamation (objet, description, date, status, user_id) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, r.getObjet());
            pst.setString(2, r.getDescription());
            pst.setDate(3, Date.valueOf(r.getDate()));
            pst.setString(4, r.getStatus().toString());
            pst.setInt(5, r.getUserId());
            pst.executeUpdate();
            
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                r.setId(rs.getInt(1));
            }
            
            System.out.println("Reclamation ajoutee avec ID: " + r.getId());
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la reclamation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(Reclamation r) {
        String req = "UPDATE reclamation SET objet = ?, description = ?, date = ?, status = ?, user_id = ? WHERE id = ?";
        try {
            PreparedStatement pst = con.prepareStatement(req);
            pst.setString(1, r.getObjet());
            pst.setString(2, r.getDescription());
            pst.setDate(3, Date.valueOf(r.getDate()));
            pst.setString(4, r.getStatus().toString());
            pst.setInt(5, r.getUserId());
            pst.setInt(6, r.getId());
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Reclamation mise a jour avec succes !");
            } else {
                System.out.println("Aucune reclamation trouvee avec l'ID: " + r.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise a jour: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean delete(Reclamation r) {
        String deleteReponses = "DELETE FROM reponse WHERE reclamation_id = ?";
        try {
            PreparedStatement pst = con.prepareStatement(deleteReponses);
            pst.setInt(1, r.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression des reponses: " + e.getMessage());
            return false;
        }

        String req = "DELETE FROM reclamation WHERE id = ?";
        try {
            PreparedStatement pst = con.prepareStatement(req);
            pst.setInt(1, r.getId());
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Reclamation supprimee avec succes !");
            } else {
                System.out.println("Aucune reclamation trouvee avec l'ID: " + r.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Reclamation> find() {
        List<Reclamation> reclamations = new ArrayList<>();
        String req = "SELECT * FROM reclamation";
        try {
            ResultSet rs = ste.executeQuery(req);
            while (rs.next()) {
                Reclamation r = new Reclamation();
                r.setId(rs.getInt("id"));
                r.setUserId(rs.getInt("user_id"));
                r.setObjet(rs.getString("objet"));
                r.setDescription(rs.getString("description"));
                r.setDate(rs.getDate("date").toLocalDate());
                
                String statusStr = rs.getString("status");
                try {
                    r.setStatus(Status.valueOf(statusStr));
                } catch (IllegalArgumentException e) {
                    System.out.println("Status invalide dans la base de donnees: " + statusStr);
                    r.setStatus(Status.ENVOYEE);
                }
                
                // Charger les réponses associées
                ReponseService reponseService = new ReponseService();
                List<Reponse> reponses = reponseService.findByReclamationId(r.getId());
                r.setReponses(reponses);
                
                reclamations.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche: " + e.getMessage());
            e.printStackTrace();
        }
        return reclamations;
    }

    public Reclamation findById(int id) {
        String req = "SELECT * FROM reclamation WHERE id = ?";
        try {
            PreparedStatement pst = con.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Reclamation r = new Reclamation();
                r.setId(rs.getInt("id"));
                r.setUserId(rs.getInt("user_id"));
                r.setObjet(rs.getString("objet"));
                r.setDescription(rs.getString("description"));
                r.setDate(rs.getDate("date").toLocalDate());
                
                String statusStr = rs.getString("status");
                try {
                    r.setStatus(Status.valueOf(statusStr));
                } catch (IllegalArgumentException e) {
                    System.out.println("Status invalide dans la base de donnees: " + statusStr);
                    r.setStatus(Status.ENVOYEE);
                }
                
                ReponseService reponseService = new ReponseService();
                List<Reponse> reponses = reponseService.findByReclamationId(r.getId());
                r.setReponses(reponses);
                
                return r;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche par ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Reclamation> findByStatus(Status status) {
        String req = "SELECT * FROM reclamation WHERE status = ?";
        List<Reclamation> reclamations = new ArrayList<>();
        try {
            PreparedStatement pst = con.prepareStatement(req);
            pst.setString(1, status.toString());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Reclamation r = new Reclamation();
                r.setId(rs.getInt("id"));
                r.setUserId(rs.getInt("user_id"));
                r.setObjet(rs.getString("objet"));
                r.setDescription(rs.getString("description"));
                r.setDate(rs.getDate("date").toLocalDate());
                r.setStatus(status);
                reclamations.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche par status: " + e.getMessage());
            e.printStackTrace();
        }
        return reclamations;
    }



    public List<Reclamation> findPage(int page, int itemsPerPage) {
        List<Reclamation> reclamations = new ArrayList<>();
        String query = "SELECT * FROM reclamation LIMIT ? OFFSET ?";

        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, itemsPerPage);
            pst.setInt(2, (page - 1) * itemsPerPage);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Reclamation r = new Reclamation();
                r.setId(rs.getInt("id"));
                r.setUserId(rs.getInt("user_id"));
                r.setObjet(rs.getString("objet"));
                r.setDescription(rs.getString("description"));
                r.setDate(rs.getDate("date").toLocalDate());

                String statusStr = rs.getString("status");
                try {
                    r.setStatus(Status.valueOf(statusStr));
                } catch (IllegalArgumentException e) {
                    r.setStatus(Status.ENVOYEE);
                }

                reclamations.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement de la page : " + e.getMessage());
        }

        return reclamations;
    }

    public int getTotalPages(int itemsPerPage) {
        String query = "SELECT COUNT(*) FROM reclamation";
        int totalItems = 0;

        try (Statement st = con.createStatement()) {
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                totalItems = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du calcul des pages totales : " + e.getMessage());
        }

        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }
}
