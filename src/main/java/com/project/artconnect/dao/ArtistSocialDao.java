package com.project.artconnect.dao;

import java.util.List;

import com.project.artconnect.model.ArtistSocial;

/**
 * Data Access Object for Artist entity.
 */
public interface ArtistSocialDao {
    List<ArtistSocial> findAll();

    void save(ArtistSocial artistSocial);

    void update(ArtistSocial artistSocial);

    void delete(int id);

    List<ArtistSocial> findByArtistId(int id_artist);
}
