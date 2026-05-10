package com.project.artconnect.dao;

import java.util.List;

import com.project.artconnect.model.City;

public interface CityDao {
    List<City> findAll();

    void save(City city);

    void update(City city);

    void delete(int id);
}
