package com.esprit.wonderwise.Model;

import java.time.LocalDateTime;

public class offre {
    private int id;
    private String titre;
    private String description;
    private double prix;
    private int nombrePlaces;
    private int placesDisponibles;
    private LocalDateTime dateCreation;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String image;
    private String pays;
    private Double rating;
    private Integer ratingCount;

    public offre() {
    }

    public offre(int id, String titre, String description, double prix, int nombrePlaces, int placesDisponibles, LocalDateTime dateCreation, LocalDateTime dateDebut, LocalDateTime dateFin, String image, String pays, Double rating, Integer ratingCount) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.prix = prix;
        this.nombrePlaces = nombrePlaces;
        this.placesDisponibles = placesDisponibles;
        this.dateCreation = dateCreation;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.image = image;
        this.pays = pays;
        this.rating = rating;
        this.ratingCount = ratingCount;
    }

    public offre(String titre, String description, double prix, int nombrePlaces, int placesDisponibles, LocalDateTime dateCreation, LocalDateTime dateDebut, LocalDateTime dateFin, String image, String pays, Double rating, Integer ratingCount) {
        this.titre = titre;
        this.description = description;
        this.prix = prix;
        this.nombrePlaces = nombrePlaces;
        this.placesDisponibles = placesDisponibles;
        this.dateCreation = dateCreation;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.image = image;
        this.pays = pays;
        this.rating = rating;
        this.ratingCount = ratingCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(int nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }

    public int getPlacesDisponibles() {
        return placesDisponibles;
    }

    public void setPlacesDisponibles(int placesDisponibles) {
        this.placesDisponibles = placesDisponibles;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    @Override
    public String toString() {
        return "offre{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", nombrePlaces=" + nombrePlaces +
                ", placesDisponibles=" + placesDisponibles +
                ", dateCreation=" + dateCreation +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", image='" + image + '\'' +
                ", pays='" + pays + '\'' +
                ", rating=" + rating +
                ", ratingCount=" + ratingCount +
                '}';
    }
}
