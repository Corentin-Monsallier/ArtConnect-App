package com.project.artconnect.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.project.artconnect.dao.CityDao;
import com.project.artconnect.model.City;
import com.project.artconnect.util.ConnectionManager;

public class JdbcCityDao implements CityDao {

    @Override
    public List<City> findAll() {

        List<City> cities = new ArrayList<>();

        String sql = "SELECT * FROM City";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                City city = new City();

                city.setId_city(result.getInt("id_city"));
                city.setCity(result.getString("city"));
                city.setCode(result.getInt("code"));
                city.setCountry(result.getString("country"));

                cities.add(city);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return cities;
    }

    @Override
    public void save(City city) {

        String sql = "INSERT INTO City(city, code, country) " + "VALUES (?, ?, ?)";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, city.getCity());
            statement.setInt(2, city.getCode());
            statement.setString(3, city.getCountry());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void update(City city) {

        String sql = "UPDATE City " + "SET city=?, code=?, country=? " + "WHERE id_city=?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, city.getCity());
            statement.setInt(2, city.getCode());
            statement.setString(3, city.getCountry());
            statement.setInt(4, city.getId_city());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void delete(int id) {

        String sql = "DELETE FROM City WHERE id_city=?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}