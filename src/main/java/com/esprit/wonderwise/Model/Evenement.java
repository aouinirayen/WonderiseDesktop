package com.esprit.wonderwise.Model;

import java.sql.Date;
import java.sql.Time;

public class Evenement {
    private int id;
    private String nom;
    private Date date;
    private Time heure;
    private String lieu;
    private String description;
    private int placeMax;
    private double prix;
    private String status;
    private String pays;
    private String categorie;
    private String photo;
    private int guideId;
    private int likesCount;
    private boolean isFavorite;
    private double latitude;
    private double longitude;
    private boolean isAnnule;
    private int isInterested;
    private boolean isLiked;

    // Constructors
    public Evenement() {}

    public Evenement(int id, String nom, Date date, Time heure, String lieu, String description, int placeMax,
                     double prix, String status, String pays, String categorie, String photo, int guideId,
                     int likesCount, boolean isFavorite, double latitude, double longitude, boolean isAnnule,
                     int isInterested, boolean isLiked) {
        this.id = id;
        this.nom = nom;
        this.date = date;
        this.heure = heure;
        this.lieu = lieu;
        this.description = description;
        this.placeMax = placeMax;
        this.prix = prix;
        this.status = status;
        this.pays = pays;
        this.categorie = categorie;
        this.photo = photo;
        this.guideId = guideId;
        this.likesCount = likesCount;
        this.isFavorite = isFavorite;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isAnnule = isAnnule;
        this.isInterested = isInterested;
        this.isLiked = isLiked;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public Time getHeure() { return heure; }
    public void setHeure(Time heure) { this.heure = heure; }
    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getPlaceMax() { return placeMax; }
    public void setPlaceMax(int placeMax) { this.placeMax = placeMax; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public int getGuideId() { return guideId; }
    public void setGuideId(int guideId) { this.guideId = guideId; }
    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public boolean isAnnule() { return isAnnule; }
    public void setAnnule(boolean annule) { isAnnule = annule; }
    public int getInterested() { return isInterested; }
    public void setInterested(int interested) { isInterested = interested; }
    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }

    @Override
    public String toString() {
        return nom;
    }
}