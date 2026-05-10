package com.project.artconnect.dao;

import java.util.List;

import com.project.artconnect.model.Discipline;

public interface DisciplineDao {
    List<Discipline> findAll();

    void save(Discipline discipline);

    void update(Discipline discipline);

    void delete(int id);

    List<Discipline> findByArtistId(int id_artist);
}
