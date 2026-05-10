package com.project.artconnect.service.impl;

import java.util.List;

import com.project.artconnect.dao.ReviewDao;
import com.project.artconnect.model.Review;
import com.project.artconnect.service.ReviewService;

public class JdbcReviewService implements ReviewService {

    private final ReviewDao reviewDao;

    public JdbcReviewService(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewDao.findAll();
    }

    @Override
    public void createReview(Review review) {
        reviewDao.save(review);
    }

    @Override
    public void updateReview(Review review) {
        reviewDao.update(review);
    }

    @Override
    public void deleteReview(int memberId, int artworkId) {
        reviewDao.delete(memberId, artworkId);
    }

    @Override
    public List<Review> getReviewsByArtwork(int artworkId) {
        return reviewDao.findByArtworkId(artworkId);
    }

    @Override
    public List<Review> getReviewsByMember(int memberId) {
        return reviewDao.findByMemberId(memberId);
    }
}