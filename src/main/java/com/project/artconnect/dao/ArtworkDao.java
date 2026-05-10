package com.project.artconnect.dao;

import java.util.List;

import com.project.artconnect.model.Artwork;

public interface ArtworkDao {
    List<Artwork> findAll();

    void save(Artwork artwork);

    void update(Artwork artwork);

    void delete(int id);

    List<Artwork> findByArtistId(int id_artist);
}
