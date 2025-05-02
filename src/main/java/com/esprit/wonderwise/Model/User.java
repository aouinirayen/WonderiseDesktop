package com.esprit.wonderwise.Model;

import java.time.LocalDate;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String email;
    private String phone;
    private String profilePicture;
    private LocalDate dateOfBirth;
    private String address;
    private String createdAt;
    private String lastLogin;
    private String resetToken;
    private String gender;
    private String status;

    public User() {}

    public User(int id, String username, String password, String role, String email, String phone, String profilePicture, LocalDate dateOfBirth, String address, String createdAt, String lastLogin, String resetToken, String gender, String status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.profilePicture = profilePicture;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.resetToken = resetToken;
        this.gender = gender;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

