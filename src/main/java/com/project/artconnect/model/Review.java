package com.project.artconnect.model;

import java.time.LocalDateTime;

public class Review {
    private int id_member;
    private int id_artwork;
    private int rating;
    private String comment;
    private LocalDateTime review_date;

    // constructors
    public Review() {}

    public Review(int id_member, int id_artwork, int rating, String comment, LocalDateTime review_date) {
        this.id_member = id_member;
        this.id_artwork = id_artwork;
        this.rating = rating;
        this.comment = comment;
        this.review_date = review_date;
    }

    // getters and setters
    public int getId_member() { return id_member; }
    public void setId_member(int id_member) { this.id_member = id_member; }

    public int getId_artwork() { return id_artwork; }
    public void setId_artwork(int id_artwork) { this.id_artwork = id_artwork; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getReview_date() { return review_date; }
    public void setReview_date(LocalDateTime review_date) { this.review_date = review_date; }

    // toString method
    @Override
    public String toString() {
        return "Review{member=" + id_member + ", artwork=" + id_artwork + ", rating=" + rating + "}";
    }
}