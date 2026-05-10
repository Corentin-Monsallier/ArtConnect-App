package com.project.artconnect.service;

import java.util.List;
import java.util.Optional;

import com.project.artconnect.model.Exhibition;

public interface ExhibitionService {

    List<Exhibition> getAllExhibitions();

    Optional<Exhibition> getExhibitionById(int id);

    void createExhibition(Exhibition exhibition);

    void updateExhibition(Exhibition exhibition);

    void deleteExhibition(int id);

    List<Exhibition> getExhibitionsByGallery(int galleryId);
}