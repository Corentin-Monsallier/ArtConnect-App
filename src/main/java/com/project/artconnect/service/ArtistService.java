package com.project.artconnect.service;

import java.util.List;
import java.util.Optional;

import com.project.artconnect.model.Artist;

public interface ArtistService {
    List<Artist> getAllArtists();

    Optional<Artist> getArtistByName(String name);

    void createArtist(Artist artist);

    void updateArtist(Artist artist);

    void deleteArtist(int id);

    List<Artist> searchArtists(String query, String disciplineName, String city);
}
