package com.project.artconnect.service;

import java.util.List;
import java.util.Optional;

import com.project.artconnect.model.Gallery;

public interface GalleryService {

    List<Gallery> getAllGalleries();

    Optional<Gallery> getGalleryById(int id);

    void createGallery(Gallery gallery);

    void updateGallery(Gallery gallery);

    void deleteGallery(int id);

    List<Gallery> getGalleriesByAddress(int addressId);
}