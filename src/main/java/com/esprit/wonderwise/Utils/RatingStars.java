package com.esprit.wonderwise.Utils;

import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class RatingStars extends HBox {
    private static final String STAR_PATH = "M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z";
    private final SVGPath[] stars = new SVGPath[5];
    private double rating;
    private boolean editable;
    private Runnable onRatingChanged;

    public RatingStars() {
        this(0, false);
    }

    public RatingStars(double initialRating, boolean editable) {
        this.rating = initialRating;
        this.editable = editable;
        setSpacing(2);
        
        for (int i = 0; i < 5; i++) {
            final int starIndex = i;
            SVGPath star = new SVGPath();
            star.setContent(STAR_PATH);
            star.setScaleX(0.7);
            star.setScaleY(0.7);
            
            if (editable) {
                star.setOnMouseClicked(event -> {
                    setRating(starIndex + 1);
                    if (onRatingChanged != null) {
                        onRatingChanged.run();
                    }
                });
                
                star.setOnMouseEntered(event -> {
                    for (int j = 0; j <= starIndex; j++) {
                        stars[j].setFill(Color.GOLD);
                    }
                    for (int j = starIndex + 1; j < 5; j++) {
                        stars[j].setFill(Color.GRAY);
                    }
                });
                
                star.setOnMouseExited(event -> updateStars());
            }
            
            stars[i] = star;
            getChildren().add(star);
        }
        
        updateStars();
    }

    public void setRating(double rating) {
        this.rating = Math.max(0, Math.min(5, rating));
        updateStars();
    }

    public double getRating() {
        return rating;
    }

    public void setOnRatingChanged(Runnable callback) {
        this.onRatingChanged = callback;
    }

    private void updateStars() {
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars[i].setFill(Color.GOLD);
            } else {
                stars[i].setFill(Color.GRAY);
            }
        }
    }
}
