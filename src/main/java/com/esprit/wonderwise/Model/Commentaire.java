package com.esprit.wonderwise.Model;

import java.time.LocalDateTime;

public class Commentaire {
    private int id;
    private int evenementId;
    private String commentaire;
    private LocalDateTime date;

    // Constructors
    public Commentaire() {}

    public Commentaire(int evenementId, String commentaire, LocalDateTime date) {
        this.evenementId = evenementId;
        this.commentaire = commentaire;
        this.date = date;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEvenementId() {
        return evenementId;
    }

    public void setEvenementId(int evenementId) {
        this.evenementId = evenementId;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}