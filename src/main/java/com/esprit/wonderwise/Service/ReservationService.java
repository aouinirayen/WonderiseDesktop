package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.reservation;
import com.esprit.wonderwise.Utils.DataSource;
import com.esprit.wonderwise.Service.OffreService;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {

    private Connection cnx = DataSource.getInstance().getCnx();
    private OffreService offreService = new OffreService();

    public void create(reservation res) throws SQLException {
        // Début de la transaction
        cnx.setAutoCommit(false);
        try {
            String sql = "INSERT INTO reservation (offre_id, nom, prenom, email, telephone, ville, nombre_personne, date_depart, heure_depart, type_voyage, mode_paiement, preferences_voyage, commentaire, date_reservation, statut, date_paiement, stripe_payment_id, regime_alimentaire) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, res.getOffreId());
                stmt.setString(2, res.getNom());
                stmt.setString(3, res.getPrenom());
                stmt.setString(4, res.getEmail());
                stmt.setString(5, res.getTelephone());
                stmt.setString(6, res.getVille());
                stmt.setInt(7, res.getNombrePersonne());
                stmt.setDate(8, res.getDateDepart() != null ? Date.valueOf(res.getDateDepart()) : null);
                stmt.setTime(9, res.getHeureDepart() != null ? Time.valueOf(res.getHeureDepart()) : null);
                stmt.setString(10, res.getTypeVoyage());
                stmt.setString(11, res.getModePaiement());
                stmt.setString(12, res.getPreferencesVoyage());
                stmt.setString(13, res.getCommentaire());
                stmt.setTimestamp(14, res.getDateReservation() != null ? Timestamp.valueOf(res.getDateReservation()) : null);
                stmt.setString(15, res.getStatut());
                stmt.setTimestamp(16, res.getDatePaiement() != null ? Timestamp.valueOf(res.getDatePaiement()) : null);
                stmt.setString(17, res.getStripePaymentId());
                stmt.setString(18, res.getRegimeAlimentaire());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        res.setId(rs.getInt(1));
                    }
                }
            }

            // Mise à jour du nombre de places disponibles
            offreService.updatePlacesDisponibles(res.getOffreId(), res.getNombrePersonne());

            // Validation de la transaction
            cnx.commit();
        } catch (SQLException e) {
            // En cas d'erreur, annulation de la transaction
            cnx.rollback();
            throw e;
        } finally {
            // Rétablissement du mode auto-commit
            cnx.setAutoCommit(true);
        }
    }

    public List<reservation> readAll() throws SQLException {
        List<reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation";
        try (Statement stmt = cnx.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reservation res = new reservation();
                res.setId(rs.getInt("id"));
                res.setOffreId(rs.getInt("offre_id"));
                res.setNom(rs.getString("nom"));
                res.setPrenom(rs.getString("prenom"));
                res.setEmail(rs.getString("email"));
                res.setTelephone(rs.getString("telephone"));
                res.setVille(rs.getString("ville"));
                res.setNombrePersonne(rs.getInt("nombre_personne"));
                res.setDateDepart(rs.getDate("date_depart") != null ? rs.getDate("date_depart").toLocalDate() : null);
                res.setHeureDepart(rs.getTime("heure_depart") != null ? rs.getTime("heure_depart").toLocalTime() : null);
                res.setTypeVoyage(rs.getString("type_voyage"));
                res.setModePaiement(rs.getString("mode_paiement"));
                res.setPreferencesVoyage(rs.getString("preferences_voyage"));
                res.setCommentaire(rs.getString("commentaire"));
                res.setDateReservation(rs.getTimestamp("date_reservation") != null ? rs.getTimestamp("date_reservation").toLocalDateTime() : null);
                res.setStatut(rs.getString("statut"));
                res.setDatePaiement(rs.getTimestamp("date_paiement") != null ? rs.getTimestamp("date_paiement").toLocalDateTime() : null);
                res.setStripePaymentId(rs.getString("stripe_payment_id"));
                res.setRegimeAlimentaire(rs.getString("regime_alimentaire"));
                reservations.add(res);
            }
        }
        return reservations;
    }

    public void update(reservation res) throws SQLException {
        String sql = "UPDATE reservation SET offre_id = ?, nom = ?, prenom = ?, email = ?, telephone = ?, ville = ?, nombre_personne = ?, date_depart = ?, heure_depart = ?, type_voyage = ?, mode_paiement = ?, preferences_voyage = ?, commentaire = ?, date_reservation = ?, statut = ?, date_paiement = ?, stripe_payment_id = ?, regime_alimentaire = ? WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, res.getOffreId());
            stmt.setString(2, res.getNom());
            stmt.setString(3, res.getPrenom());
            stmt.setString(4, res.getEmail());
            stmt.setString(5, res.getTelephone());
            stmt.setString(6, res.getVille());
            stmt.setInt(7, res.getNombrePersonne());
            stmt.setDate(8, res.getDateDepart() != null ? Date.valueOf(res.getDateDepart()) : null);
            stmt.setTime(9, res.getHeureDepart() != null ? Time.valueOf(res.getHeureDepart()) : null);
            stmt.setString(10, res.getTypeVoyage());
            stmt.setString(11, res.getModePaiement());
            stmt.setString(12, res.getPreferencesVoyage());
            stmt.setString(13, res.getCommentaire());
            stmt.setTimestamp(14, res.getDateReservation() != null ? Timestamp.valueOf(res.getDateReservation()) : null);
            stmt.setString(15, res.getStatut());
            stmt.setTimestamp(16, res.getDatePaiement() != null ? Timestamp.valueOf(res.getDatePaiement()) : null);
            stmt.setString(17, res.getStripePaymentId());
            stmt.setString(18, res.getRegimeAlimentaire());
            stmt.setInt(19, res.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM reservation WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}