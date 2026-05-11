package com.project.artconnect.service.impl;

import com.project.artconnect.dao.ArtworkDao;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.Artist;
import com.project.artconnect.persistence.JdbcArtworkDao;
import com.project.artconnect.service.ArtworkService;

import java.util.List;
import java.util.Optional;

public class JdbcArtworkService implements ArtworkService {

    private final ArtworkDao artworkDao;

    public JdbcArtworkService() {
        this.artworkDao = new JdbcArtworkDao();
    }

    @Override
    public List<Artwork> getAllArtworks() {
        return artworkDao.findAll();
    }

    @Override
    public List<String> getAllTypes() {
        return artworkDao.findAll()
                .stream()
                .map(Artwork::getType)
                .distinct()
                .toList();
    }

    @Override
    public Optional<Artwork> getArtworkByTitle(String title) {
        return artworkDao.findAll()
                .stream()
                .filter(a -> a.getTitle_art()
                        .equalsIgnoreCase(title))
                .findFirst();
    }

    @Override
    public List<Artwork> getArtworksByArtist(Artist artist) {
        return artworkDao.findByArtistId(
                artist.getId_artist());
    }

    @Override
    public void createArtwork(Artwork artwork) {
        artworkDao.save(artwork);
    }

    @Override
    public void updateArtwork(Artwork artwork) {
        artworkDao.update(artwork);
    }

    @Override
    public void deleteArtwork(String title) {
        List<Artwork> artworks = artworkDao.findAll();

        for (Artwork artwork : artworks) {

            if (artwork.getTitle_art().equalsIgnoreCase(title)) {
                artworkDao.delete(artwork.getId_artwork());
            }
        }
    }
}