package com.project.artconnect.service;

import java.util.List;

import com.project.artconnect.model.Discipline;

public interface DisciplineService {

    List<Discipline> getAllDisciplines();

    void createDiscipline(Discipline discipline);

    void updateDiscipline(Discipline discipline);

    void deleteDiscipline(int id);

    List<Discipline> getDisciplinesByArtistId(int id_artist);
}