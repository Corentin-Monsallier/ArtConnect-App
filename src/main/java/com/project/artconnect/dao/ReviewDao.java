package com.project.artconnect.dao;

import java.util.List;

import com.project.artconnect.model.Review;

public interface ReviewDao {
    List<Review> findAll();

    void save(Review review);

    
    void update(Review review);
    
    void delete(int id_member, int id_artwork);
    
    List<Review> findByArtworkId(int id_artwork);
    
    List<Review> findByMemberId(int id_member);
}