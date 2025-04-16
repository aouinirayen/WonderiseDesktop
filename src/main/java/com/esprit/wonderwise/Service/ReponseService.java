package com.esprit.wonderwise.Service;



import com.esprit.wonderwise.Model.Reclamation;
import com.esprit.wonderwise.Model.Reponse;
import com.esprit.wonderwise.Utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseService implements InterfaceCRUD<Reponse> {
    Connection con;
    Statement ste;

    public ReponseService() {

        con = DataSource.getInstance().getCnx();
        try {
            ste = con.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void add(Reponse r) {
        String req = "INSERT INTO reponse (reponse, date, reclamation_id) VALUES (?, ?, ?)";
        try {
            PreparedStatement pst = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, r.getReponse());
            pst.setDate(2, Date.valueOf(r.getDate()));
            pst.setInt(3, r.getReclamation() != null ? r.getReclamation().getId() : null);
            pst.executeUpdate();
            
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                r.setId(rs.getInt(1));
            }
            
            System.out.println("Réponse ajoutée avec ID: " + r.getId());
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la réponse: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void update(Reponse r) {
        String req = "UPDATE reponse SET reponse = ?, date = ?, reclamation_id = ? WHERE id = ?";
        try {
            PreparedStatement pst = con.prepareStatement(req);
            pst.setString(1, r.getReponse());
            pst.setDate(2, Date.valueOf(r.getDate()));
            pst.setInt(3, r.getReclamation() != null ? r.getReclamation().getId() : null);
            pst.setInt(4, r.getId());
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Réponse mise à jour avec succès !");
            } else {
                System.out.println("Aucune réponse trouvée avec l'ID: " + r.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean delete(Reponse r) {
        String req = "DELETE FROM reponse WHERE id = ?";
        try {
            PreparedStatement pst = con.prepareStatement(req);
            pst.setInt(1, r.getId());
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Réponse supprimée avec succès !");
                return true; // ✅ Suppression réussie
            } else {
                System.out.println("Aucune réponse trouvée avec l'ID: " + r.getId());
                return false; // ID inexistant
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
            return false; // ✅ Retourne false en cas d'exception
        }
    }


    @Override
    public List<Reponse> find() {
        List<Reponse> reponses = new ArrayList<>();
        String req = "SELECT * FROM reponse";
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Reponse r = new Reponse();
                r.setId(rs.getInt("id"));
                r.setReponse(rs.getString("reponse"));
                r.setDate(rs.getDate("date").toLocalDate());
                
                int reclamationId = rs.getInt("reclamation_id");
                if (!rs.wasNull()) {
                    ReclamationService reclamationService = new ReclamationService();
                    Reclamation reclamation = reclamationService.findById(reclamationId);
                    r.setReclamation(reclamation);
                }
                
                reponses.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche: " + e.getMessage());
            e.printStackTrace();
        }
        return reponses;
    }

    public List<Reponse> findByReclamationId(int reclamationId) {
        String req = "SELECT * FROM reponse WHERE reclamation_id = ?";
        List<Reponse> reponses = new ArrayList<>();
        try {
            PreparedStatement pst = con.prepareStatement(req);
            pst.setInt(1, reclamationId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Reponse r = new Reponse();
                r.setId(rs.getInt("id"));
                r.setReponse(rs.getString("reponse"));
                r.setDate(rs.getDate("date").toLocalDate());
                
                Reclamation reclamation = new Reclamation();
                reclamation.setId(reclamationId);
                r.setReclamation(reclamation);
                
                reponses.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche par réclamation ID: " + e.getMessage());
            e.printStackTrace();
        }
        return reponses;
    }


    public void addReponseToReclamation(String reponseText, int reclamationId) {
        Reclamation reclamation = new ReclamationService().findById(reclamationId);
        if (reclamation != null) {
            Reponse reponse = new Reponse();
            reponse.setReponse(reponseText);
            reponse.setDate(java.time.LocalDate.now());
            reponse.setReclamation(reclamation);
            add(reponse);
            System.out.println("Réponse ajoutée à la réclamation ID: " + reclamationId);
        } else {
            System.out.println("Réclamation avec ID " + reclamationId + " non trouvée.");
        }
    }
}
