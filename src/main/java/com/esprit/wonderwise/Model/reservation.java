package com.esprit.wonderwise.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class reservation {
    private int id;
    private int offreId;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String ville;
    private int nombrePersonne;
    private LocalDate dateDepart;
    private LocalTime heureDepart;
    private String typeVoyage;
    private String modePaiement;
    private String preferencesVoyage;
    private String commentaire;
    private LocalDateTime dateReservation;
    private String statut;
    private LocalDateTime datePaiement;
    private String stripePaymentId;
    private String regimeAlimentaire;


    public reservation() {
    }

    public reservation(int id, int offreId, String nom, String prenom, String email, String telephone, String ville, int nombrePersonne, LocalDate dateDepart, LocalTime heureDepart, String typeVoyage, String modePaiement, String preferencesVoyage, String commentaire, LocalDateTime dateReservation, String statut, LocalDateTime datePaiement, String stripePaymentId, String regimeAlimentaire) {
        this.id = id;
        this.offreId = offreId;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.ville = ville;
        this.nombrePersonne = nombrePersonne;
        this.dateDepart = dateDepart;
        this.heureDepart = heureDepart;
        this.typeVoyage = typeVoyage;
        this.modePaiement = modePaiement;
        this.preferencesVoyage = preferencesVoyage;
        this.commentaire = commentaire;
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.datePaiement = datePaiement;
        this.stripePaymentId = stripePaymentId;
        this.regimeAlimentaire = regimeAlimentaire;
    }


    public reservation(int offreId, String nom, String prenom, String email, String telephone, String ville, int nombrePersonne, LocalDate dateDepart, LocalTime heureDepart, String typeVoyage, String modePaiement, String preferencesVoyage, String commentaire, LocalDateTime dateReservation, String statut, LocalDateTime datePaiement, String stripePaymentId, String regimeAlimentaire) {
        this.offreId = offreId;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.ville = ville;
        this.nombrePersonne = nombrePersonne;
        this.dateDepart = dateDepart;
        this.heureDepart = heureDepart;
        this.typeVoyage = typeVoyage;
        this.modePaiement = modePaiement;
        this.preferencesVoyage = preferencesVoyage;
        this.commentaire = commentaire;
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.datePaiement = datePaiement;
        this.stripePaymentId = stripePaymentId;
        this.regimeAlimentaire = regimeAlimentaire;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOffreId() {
        return offreId;
    }

    public void setOffreId(int offreId) {
        this.offreId = offreId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public int getNombrePersonne() {
        return nombrePersonne;
    }

    public void setNombrePersonne(int nombrePersonne) {
        this.nombrePersonne = nombrePersonne;
    }

    public LocalDate getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(LocalDate dateDepart) {
        this.dateDepart = dateDepart;
    }

    public LocalTime getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(LocalTime heureDepart) {
        this.heureDepart = heureDepart;
    }

    public String getTypeVoyage() {
        return typeVoyage;
    }

    public void setTypeVoyage(String typeVoyage) {
        this.typeVoyage = typeVoyage;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getPreferencesVoyage() {
        return preferencesVoyage;
    }

    public void setPreferencesVoyage(String preferencesVoyage) {
        this.preferencesVoyage = preferencesVoyage;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getStripePaymentId() {
        return stripePaymentId;
    }

    public void setStripePaymentId(String stripePaymentId) {
        this.stripePaymentId = stripePaymentId;
    }

    public String getRegimeAlimentaire() {
        return regimeAlimentaire;
    }

    public void setRegimeAlimentaire(String regimeAlimentaire) {
        this.regimeAlimentaire = regimeAlimentaire;
    }


    @Override
    public String toString() {
        return "reservation{" +
                "id=" + id +
                ", offreId=" + offreId +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", ville='" + ville + '\'' +
                ", nombrePersonne=" + nombrePersonne +
                ", dateDepart=" + dateDepart +
                ", heureDepart=" + heureDepart +
                ", typeVoyage='" + typeVoyage + '\'' +
                ", modePaiement='" + modePaiement + '\'' +
                ", preferencesVoyage='" + preferencesVoyage + '\'' +
                ", commentaire='" + commentaire + '\'' +
                ", dateReservation=" + dateReservation +
                ", statut='" + statut + '\'' +
                ", datePaiement=" + datePaiement +
                ", stripePaymentId='" + stripePaymentId + '\'' +
                ", regimeAlimentaire='" + regimeAlimentaire + '\'' +
                '}';
    }
}
