package com.project.artconnect.service;

import java.util.List;
import java.util.Optional;

import com.project.artconnect.model.Workshop;

public interface WorkshopService {

    List<Workshop> getAllWorkshops();

    Optional<Workshop> getWorkshopById(int id);

    void createWorkshop(Workshop workshop);

    void updateWorkshop(Workshop workshop);

    void deleteWorkshop(int id);

    List<Workshop> getWorkshopsByArtist(int artistId);
}