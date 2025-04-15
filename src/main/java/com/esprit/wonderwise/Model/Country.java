package com.esprit.wonderwise.Model;

public class Country {
    private int id;
    private String name;
    private String img;
    private String description;
    private String currency;
    private String isoCode;
    private String callingCode;
    private String climate;

    // Constructors
    public Country() {}

    public Country(int id, String name, String img, String description, String currency, String isoCode, String callingCode, String climate) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.description = description;
        this.currency = currency;
        this.isoCode = isoCode;
        this.callingCode = callingCode;
        this.climate = climate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getIsoCode() { return isoCode; }
    public void setIsoCode(String isoCode) { this.isoCode = isoCode; }
    public String getCallingCode() { return callingCode; }
    public void setCallingCode(String callingCode) { this.callingCode = callingCode; }
    public String getClimate() { return climate; }
    public void setClimate(String climate) { this.climate = climate; }

    @Override
    public String toString() {
        return name;
    }
}