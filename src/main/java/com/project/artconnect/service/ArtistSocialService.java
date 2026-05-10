package com.project.artconnect.service;

import java.util.List;

import com.project.artconnect.model.ArtistSocial;

public interface ArtistSocialService {

    List<ArtistSocial> getAllArtistSocials();

    void createArtistSocial(ArtistSocial artistSocial);

    void updateArtistSocial(ArtistSocial artistSocial);

    void deleteArtistSocial(int id);

    List<ArtistSocial> getArtistSocialsByArtistId(int id_artist);
}