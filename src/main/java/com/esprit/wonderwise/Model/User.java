package com.esprit.wonderwise.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class User {
    private int id;
    private String email;
    private String password;
    private List<String> roles;

    private String nom;
    private String prenom;
    private String fullName;
    private String username;
    private String profilePhoto;

    private LocalDate birthDate;
    private String gender;
    private String nationality;
    private String street;
    private String postalCode;
    private String city;
    private String country;
    private String phone;

    private List<String> interests;
    private LocalDateTime createdAt;
    private LocalDateTime dateInscription;
    private boolean isBlocked;

    // Constructeurs
    public User() {
        this.createdAt = LocalDateTime.now();
        this.dateInscription = LocalDateTime.now();
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getFullName() {
        return prenom + " " + nom;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }

    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean blocked) { isBlocked = blocked; }

    @Override
    public String toString() {
        return getFullName() + " (" + email + ")";
    }
}
