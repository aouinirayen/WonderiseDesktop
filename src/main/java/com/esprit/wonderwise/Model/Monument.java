package com.esprit.wonderwise.Model;

public class Monument {
    private int id;
    private int countryId;
    private String name;
    private String img;
    private String description;

    // Constructors
    public Monument() {}

    public Monument(int id, int countryId, String name, String img, String description) {
        this.id = id;
        this.countryId = countryId;
        this.name = name;
        this.img = img;
        this.description = description;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCountryId() { return countryId; }
    public void setCountryId(int countryId) { this.countryId = countryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return name;
    }
}