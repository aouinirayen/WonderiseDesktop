package com.esprit.wonderwise.Model;

public class TraditionalFood {
    private int id;
    private int countryId;
    private String name;
    private String img;
    private String description;
    private String recipe;

    // Constructors
    public TraditionalFood() {}

    public TraditionalFood(int id, int countryId, String name, String img, String description, String recipe) {
        this.id = id;
        this.countryId = countryId;
        this.name = name;
        this.img = img;
        this.description = description;
        this.recipe = recipe;
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
    public String getRecipe() { return recipe; }
    public void setRecipe(String recipe) { this.recipe = recipe; }

    @Override
    public String toString() {
        return name;
    }
}