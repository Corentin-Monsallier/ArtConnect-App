package com.project.artconnect.service.impl;

import java.util.List;

import com.project.artconnect.dao.ArtistSocialDao;
import com.project.artconnect.model.ArtistSocial;
import com.project.artconnect.persistence.JdbcArtistSocialDao;
import com.project.artconnect.service.ArtistSocialService;

public class JdbcArtistSocialService
        implements ArtistSocialService {

    private final ArtistSocialDao artistSocialDao;

    public JdbcArtistSocialService() {
        this.artistSocialDao = new JdbcArtistSocialDao();
    }

    @Override
    public List<ArtistSocial> getAllArtistSocials() {
        return artistSocialDao.findAll();
    }

    @Override
    public void createArtistSocial(ArtistSocial artistSocial) {
        artistSocialDao.save(artistSocial);
    }

    @Override
    public void updateArtistSocial(ArtistSocial artistSocial) {
        artistSocialDao.update(artistSocial);
    }

    @Override
    public void deleteArtistSocial(int id) {
        artistSocialDao.delete(id);
    }

    @Override
    public List<ArtistSocial> getArtistSocialsByArtistId(int id_artist) {
        return artistSocialDao
                .findByArtistId(id_artist);
    }
}