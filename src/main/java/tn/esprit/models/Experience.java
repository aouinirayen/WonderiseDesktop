package tn.esprit.models;

import java.time.LocalDate;

public class Experience {
    private int id;
    private String titre;
    private String description;
    private String image;
    private String lieu;
    private String categorie;
    private int idClient;
    private LocalDate date;
    private LocalDate dateCreation;
    private String nouveauAttribut1;
    private String nouveauAttribut2;

    // Constructeurs
    public Experience(int id, String titre, String description, String lieu, LocalDate date, String image, String categorie) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.lieu = lieu;
        this.date = date;
        this.image = image;
        this.categorie = categorie;
        this.dateCreation = LocalDate.now();
    }

    public Experience(String titre, String description, String image, String lieu, 
                     String categorie, int idClient, LocalDate date) {
        this.titre = titre;
        this.description = description;
        this.image = image;
        this.lieu = lieu;
        this.categorie = categorie;
        this.idClient = idClient;
        this.date = date;
        this.dateCreation = LocalDate.now();
    }

    public Experience(int id, String titre, String description, String image, 
                     String lieu, String categorie, int idClient, LocalDate date, 
                     LocalDate dateCreation) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.image = image;
        this.lieu = lieu;
        this.categorie = categorie;
        this.idClient = idClient;
        this.date = date;
        this.dateCreation = dateCreation;
    }

    public Experience(int id, String titre, String description, String image, 
                     String lieu, String categorie, int idClient, LocalDate date, 
                     LocalDate dateCreation, String nouveauAttribut1, String nouveauAttribut2) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.image = image;
        this.lieu = lieu;
        this.categorie = categorie;
        this.idClient = idClient;
        this.date = date;
        this.dateCreation = dateCreation;
        this.nouveauAttribut1 = nouveauAttribut1;
        this.nouveauAttribut2 = nouveauAttribut2;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getLieu() {
        return lieu;
    }

    public String getCategorie() {
        return categorie;
    }

    public int getIdClient() {
        return idClient;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public String getNouveauAttribut1() {
        return nouveauAttribut1;
    }

    public String getNouveauAttribut2() {
        return nouveauAttribut2;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setNouveauAttribut1(String nouveauAttribut1) {
        this.nouveauAttribut1 = nouveauAttribut1;
    }

    public void setNouveauAttribut2(String nouveauAttribut2) {
        this.nouveauAttribut2 = nouveauAttribut2;
    }

    @Override
    public String toString() {
        return "Experience{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", lieu='" + lieu + '\'' +
                ", categorie='" + categorie + '\'' +
                ", idClient=" + idClient +
                ", date=" + date +
                ", dateCreation=" + dateCreation +
                ", nouveauAttribut1='" + nouveauAttribut1 + '\'' +
                ", nouveauAttribut2='" + nouveauAttribut2 + '\'' +
                '}';
    }
}
