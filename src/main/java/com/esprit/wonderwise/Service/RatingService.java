package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.offre;

public class RatingService {
    
    public void rateOffer(offre offer, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        if (offer.getRating() == null) {
            offer.setRating((double) rating);
            offer.setRatingCount(1);
        } else {
            double currentTotal = offer.getRating() * offer.getRatingCount();
            int newCount = offer.getRatingCount() + 1;
            double newRating = (currentTotal + rating) / newCount;
            
            offer.setRating(newRating);
            offer.setRatingCount(newCount);
        }
    }
    
    public double getAverageRating(offre offer) {
        return offer.getRating() != null ? offer.getRating() : 0.0;
    }
    
    public int getRatingCount(offre offer) {
        return offer.getRatingCount() != null ? offer.getRatingCount() : 0;
    }
}
