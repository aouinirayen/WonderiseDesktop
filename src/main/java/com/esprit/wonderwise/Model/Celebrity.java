package com.esprit.wonderwise.Model;

import java.sql.Date;

public class Celebrity {
    private int id;
    private int countryId;
    private String name;
    private String work;
    private String img;
    private String description;
    private String job;
    private Date dateOfBirth;
    private String nationality;
    private String notableWorks;
    private String personalLife;
    private double netWorth;

    // Constructors
    public Celebrity() {}

    public Celebrity(int id, int countryId, String name, String work, String img, String description, String job,
                     Date dateOfBirth, String nationality, String notableWorks, String personalLife, double netWorth) {
        this.id = id;
        this.countryId = countryId;
        this.name = name;
        this.work = work;
        this.img = img;
        this.description = description;
        this.job = job;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
        this.notableWorks = notableWorks;
        this.personalLife = personalLife;
        this.netWorth = netWorth;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCountryId() { return countryId; }
    public void setCountryId(int countryId) { this.countryId = countryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getWork() { return work; }
    public void setWork(String work) { this.work = work; }
    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getJob() { return job; }
    public void setJob(String job) { this.job = job; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public String getNotableWorks() { return notableWorks; }
    public void setNotableWorks(String notableWorks) { this.notableWorks = notableWorks; }
    public String getPersonalLife() { return personalLife; }
    public void setPersonalLife(String personalLife) { this.personalLife = personalLife; }
    public double getNetWorth() { return netWorth; }
    public void setNetWorth(double netWorth) { this.netWorth = netWorth; }

    @Override
    public String toString() {
        return name;
    }
}