package com.project.artconnect.service.impl;

import java.util.List;

import com.project.artconnect.dao.CityDao;
import com.project.artconnect.model.City;
import com.project.artconnect.persistence.JdbcCityDao;
import com.project.artconnect.service.CityService;

public class JdbcCityService
        implements CityService {

    private final CityDao cityDao;

    public JdbcCityService() {
        this.cityDao = new JdbcCityDao();
    }

    @Override
    public List<City> getAllCities() {
        return cityDao.findAll();
    }

    @Override
    public void createCity(City city) {
        cityDao.save(city);
    }

    @Override
    public void updateCity(City city) {
        cityDao.update(city);
    }

    @Override
    public void deleteCity(int id) {
        cityDao.delete(id);
    }
}