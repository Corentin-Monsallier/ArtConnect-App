package com.project.artconnect.dao;

import java.util.List;

import com.project.artconnect.model.Workshop;

public interface WorkshopDao {
    List<Workshop> findAll();
    
    void save(Workshop workshop);
    
    void update(Workshop workshop);
    
    void delete(int id);
    
    List<Workshop> findByArtist(int id_artist);
}