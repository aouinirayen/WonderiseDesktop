package com.esprit.wonderwise.Model;

import java.time.LocalDate;

public class Reponse {
    private int id;
    private String reponse;
    private LocalDate date;
    private Reclamation reclamation;

    public Reponse() {
        this.date = LocalDate.now();
    }

    public Reponse(int id, String reponse, LocalDate date, Reclamation reclamation) {
        this.id = id;
        this.reponse = reponse;
        this.date = date;
        this.reclamation = reclamation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Reclamation getReclamation() {
        return reclamation;
    }

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
    }

    @Override
    public String toString() {
        return "Reponse{" +
                "id=" + id +
                ", reponse='" + reponse + '\'' +
                ", date=" + date +
                ", reclamation=" + (reclamation != null ? reclamation.getId() : "null") +
                '}';
    }
}
