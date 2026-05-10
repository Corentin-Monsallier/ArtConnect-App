package com.project.artconnect.service.impl;

import java.util.List;
import java.util.Optional;

import com.project.artconnect.dao.GalleryDao;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.service.GalleryService;

public class JdbcGalleryService implements GalleryService {

    private final GalleryDao galleryDao;

    public JdbcGalleryService(GalleryDao galleryDao) {
        this.galleryDao = galleryDao;
    }

    @Override
    public List<Gallery> getAllGalleries() {
        return galleryDao.findAll();
    }

    @Override
    public Optional<Gallery> getGalleryById(int id) {
        return galleryDao.findAll()
                .stream()
                .filter(g -> g.getId_gallery() == id)
                .findFirst();
    }

    @Override
    public void createGallery(Gallery gallery) {
        galleryDao.save(gallery);
    }

    @Override
    public void updateGallery(Gallery gallery) {
        galleryDao.update(gallery);
    }

    @Override
    public void deleteGallery(int id) {
        galleryDao.delete(id);
    }

    @Override
    public List<Gallery> getGalleriesByAddress(int addressId) {
        return galleryDao.findByCityId(addressId);
    }
}