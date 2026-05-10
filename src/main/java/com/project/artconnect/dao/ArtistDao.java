package com.project.artconnect.dao;

import java.util.List;

import com.project.artconnect.model.Artist;

/**
 * Data Access Object for Artist entity.
 */
public interface ArtistDao {
    List<Artist> findAll();

    void save(Artist artist);

    void update(Artist artist);

    void delete(int id);
}
