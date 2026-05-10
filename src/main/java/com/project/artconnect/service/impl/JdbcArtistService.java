package com.project.artconnect.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.service.ArtistService;

public class JdbcArtistService implements ArtistService {

    private final ArtistDao artistDao;

    public JdbcArtistService(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }

    @Override
    public List<Artist> getAllArtists() {

        return artistDao.findAll();
    }

    @Override
    public Optional<Artist> getArtistByName(String name) {

        return artistDao.findAll()
                .stream()
                .filter(a -> a.getName_user()
                        .equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public void createArtist(Artist artist) {

        artistDao.save(artist);
    }

    @Override
    public void updateArtist(Artist artist) {

        artistDao.update(artist);
    }

    @Override
    public void deleteArtist(int id) {

        artistDao.delete(id);
    }

    @Override
    public List<Discipline> getAllDisciplines() {

        return new ArrayList<>();
    }

    @Override
    public List<Artist> searchArtists(
            String name,
            String city,
            String discipline) {

        List<Artist> artists = artistDao.findAll();

        return artists.stream()

                .filter(a -> name == null ||
                        name.isEmpty() ||
                        a.getName_user()
                                .toLowerCase()
                                .contains(name.toLowerCase()))

                .filter(a -> city == null ||
                        city.isEmpty() ||
                        a.getCity()
                                .equalsIgnoreCase(city))

                .toList();
    }
}