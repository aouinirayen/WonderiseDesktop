package com.esprit.wonderwise.Model;

public class RatingGuide {
    private int id;
    private int guideId;
    private int userId;
    private int rating; // 1-5 stars

    public RatingGuide() {}

    public RatingGuide(int id, int guideId, int userId, int rating) {
        this.id = id;
        this.guideId = guideId;
        this.userId = userId;
        this.rating = rating;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGuideId() { return guideId; }
    public void setGuideId(int guideId) { this.guideId = guideId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
}
