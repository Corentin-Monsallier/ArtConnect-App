package com.project.artconnect.service.impl;

import java.util.List;

import com.project.artconnect.dao.DisciplineDao;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.persistence.JdbcDisciplineDao;
import com.project.artconnect.service.DisciplineService;

public class JdbcDisciplineService
        implements DisciplineService {

    private final DisciplineDao disciplineDao;

    public JdbcDisciplineService() {
        this.disciplineDao = new JdbcDisciplineDao();
    }

    @Override
    public List<Discipline> getAllDisciplines() {
        return disciplineDao.findAll();
    }

    @Override
    public void createDiscipline(Discipline discipline) {
        disciplineDao.save(discipline);
    }

    @Override
    public void updateDiscipline(Discipline discipline) {
        disciplineDao.update(discipline);
    }

    @Override
    public void deleteDiscipline(int id) {
        disciplineDao.delete(id);
    }

    @Override
    public List<Discipline> getDisciplinesByArtistId(int id_artist) {
        return disciplineDao.findByArtistId(id_artist);
    }
}