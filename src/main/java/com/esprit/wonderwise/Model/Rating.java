package com.esprit.wonderwise.Model;

import java.time.LocalDateTime;

public class Rating {
    private int id;
    private int countryId;
    private boolean isLike;
    private LocalDateTime createdAt;

    public Rating() {}

    public Rating(int id, int countryId, boolean isLike, LocalDateTime createdAt) {
        this.id = id;
        this.countryId = countryId;
        this.isLike = isLike;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCountryId() { return countryId; }
    public void setCountryId(int countryId) { this.countryId = countryId; }

    public boolean isLike() { return isLike; }
    public void setLike(boolean like) { isLike = like; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
