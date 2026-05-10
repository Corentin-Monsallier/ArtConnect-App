package com.project.artconnect.service;

import java.util.List;

import com.project.artconnect.model.Review;

public interface ReviewService {

    List<Review> getAllReviews();

    void createReview(Review review);

    void updateReview(Review review);

    void deleteReview(int memberId, int artworkId);

    List<Review> getReviewsByArtwork(int artworkId);

    List<Review> getReviewsByMember(int memberId);
}