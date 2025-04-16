package tn.esprit.models;

public class Commentaire {
    private int id;
    private String contenu;
    private String auteur;

    public Commentaire() {
    }

    public Commentaire(String contenu, String auteur) {
        this.contenu = contenu;
        this.auteur = auteur;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getContenu() {
        return contenu;
    }

    public String getAuteur() {
        return auteur;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "id=" + id +
                ", contenu='" + contenu + '\'' +
                ", auteur='" + auteur + '\'' +
                '}';
    }
}
