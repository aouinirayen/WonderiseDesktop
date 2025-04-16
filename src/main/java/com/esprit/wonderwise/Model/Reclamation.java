package com.esprit.wonderwise.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reclamation {
    private int id;
    private int userId;  
    private String objet;
    private String description;
    private LocalDate date;
    private Status status = Status.ENVOYEE;
    private List<Reponse> reponses = new ArrayList<>();

    public Reclamation() {
        this.userId = 1; 
    }

    public Reclamation(String objet, String description) {
        this.objet = objet;
        this.description = description;
        this.date = LocalDate.now();
        this.status = Status.ENVOYEE;
        this.userId = 1; 
    }

    public Reclamation(int id, int userId, String objet, String description, LocalDate date, Status status) {
        this.id = id;
        this.userId = userId;
        this.objet = objet;
        this.description = description;
        this.date = date;
        this.status = status;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public List<Reponse> getReponses() {
        return reponses;
    }

    public void setReponses(List<Reponse> reponses) {
        this.reponses = reponses;
    }

    public void addReponse(Reponse reponse) {
        reponses.add(reponse);
        reponse.setReclamation(this);
    }

    public void removeReponse(Reponse reponse) {
        reponses.remove(reponse);
        reponse.setReclamation(null);
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", userId=" + userId +
                ", objet='" + objet + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", status=" + status.getLabel() +
                ", nombre de réponses=" + reponses.size() +
                '}';
    }

    public void setStatus(com.esprit.wonderwise.Model.Status status) {

    }
}