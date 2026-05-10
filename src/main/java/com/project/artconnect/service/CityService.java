package com.project.artconnect.service;

import java.util.List;

import com.project.artconnect.model.City;

public interface CityService {

    List<City> getAllCities();

    void createCity(City city);

    void updateCity(City city);

    void deleteCity(int id);
}