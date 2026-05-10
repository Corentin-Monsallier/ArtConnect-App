package com.project.artconnect.dao;

import java.util.List;

import com.project.artconnect.model.Gallery;

public interface GalleryDao {
    List<Gallery> findAll();

    void save(Gallery gallery);

    void update(Gallery gallery);
    
    void delete(int id);
    
    List<Gallery> findByCityId(int id_address);
}