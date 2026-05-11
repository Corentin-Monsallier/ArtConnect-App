package com.project.artconnect.service.impl;

import java.util.List;
import java.util.Optional;

import com.project.artconnect.dao.ExhibitionDao;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.service.ExhibitionService;

public class JdbcExhibitionService implements ExhibitionService {

    private final ExhibitionDao exhibitionDao;

    public JdbcExhibitionService(ExhibitionDao exhibitionDao) {
        this.exhibitionDao = exhibitionDao;
    }

    @Override
    public List<Exhibition> getAllExhibitions() {
        return exhibitionDao.findAll();
    }

    @Override
    public List<String> getAllThemes() {

        return exhibitionDao.findAll()
                .stream()
                .map(Exhibition::getTheme)
                .distinct()
                .toList();
    }

    @Override
    public Optional<Exhibition> getExhibitionById(int id) {

        return exhibitionDao.findAll()
                .stream()
                .filter(e -> e.getId_exhibition() == id)
                .findFirst();
    }

    @Override
    public void createExhibition(Exhibition exhibition) {
        exhibitionDao.save(exhibition);
    }

    @Override
    public void updateExhibition(Exhibition exhibition) {
        exhibitionDao.update(exhibition);
    }

    @Override
    public void deleteExhibition(int id) {
        exhibitionDao.delete(id);
    }

    @Override
    public List<Exhibition> getExhibitionsByGallery(int galleryId) {
        return exhibitionDao.findByGalleryId(galleryId);
    }
}