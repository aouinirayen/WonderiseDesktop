package com.esprit.wonderwise.Model;

public class Guide {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String numTelephone;
    private String description;
    private String facebook;
    private String instagram;
    private String photo;
    private int nombreAvis;

    // Constructors
    public Guide() {}

    public Guide(int id, String nom, String prenom, String email, String numTelephone, String description, String facebook, String instagram, String photo, int nombreAvis) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.numTelephone = numTelephone;
        this.description = description;
        this.facebook = facebook;
        this.instagram = instagram;
        this.photo = photo;
        this.nombreAvis = nombreAvis;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNumTelephone() { return numTelephone; }
    public void setNumTelephone(String numTelephone) { this.numTelephone = numTelephone; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getFacebook() { return facebook; }
    public void setFacebook(String facebook) { this.facebook = facebook; }
    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public int getNombreAvis() { return nombreAvis; }
    public void setNombreAvis(int nombreAvis) { this.nombreAvis = nombreAvis; }

    @Override
    public String toString() {
        return nom + " " + prenom;
    }
}