package com.project.artconnect.service.impl;

import java.util.List;
import java.util.Optional;

import com.project.artconnect.dao.WorkshopDao;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.service.WorkshopService;

public class JdbcWorkshopService implements WorkshopService {

    private final WorkshopDao workshopDao;

    public JdbcWorkshopService(WorkshopDao workshopDao) {
        this.workshopDao = workshopDao;
    }

    public JdbcWorkshopService(WorkshopDao workshopDao, Object ignored) {
        this.workshopDao = workshopDao;
    }

    @Override
    public List<Workshop> getAllWorkshops() {
        return workshopDao.findAll();
    }

    @Override
    public Optional<Workshop> getWorkshopById(int id) {
        return workshopDao.findAll()
                .stream()
                .filter(w -> w.getId_workshop() == id)
                .findFirst();
    }

    @Override
    public void createWorkshop(Workshop workshop) {
        workshopDao.save(workshop);
    }

    @Override
    public void updateWorkshop(Workshop workshop) {
        workshopDao.update(workshop);
    }

    @Override
    public void deleteWorkshop(int id) {
        workshopDao.delete(id);
    }

    @Override
    public List<Workshop> getWorkshopsByArtist(int artistId) {
        return workshopDao.findByArtist(artistId);
    }
}