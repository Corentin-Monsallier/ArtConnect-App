package com.project.artconnect.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.project.artconnect.dao.WorkshopDao;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.util.ConnectionManager;

public class JdbcWorkshopDao implements WorkshopDao {

    @Override
    public List<Workshop> findAll() {
        List<Workshop> workshops = new ArrayList<>();

        String sql = "SELECT * FROM Workshop";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                Workshop workshop = new Workshop();

                workshop.setId_workshop(result.getInt("id_workshop"));
                workshop.setTitle_workshop(result.getString("title_workshop"));
                workshop.setDate_workshop(result.getTimestamp("date_workshop").toLocalDateTime());
                workshop.setDuration_minutes(result.getInt("duration_minutes"));
                workshop.setMax_participants(result.getInt("max_participants"));
                workshop.setPrice(result.getDouble("price"));
                workshop.setLevel(result.getString("level"));
                workshop.setLocation(result.getString("location"));
                workshop.setDescription(result.getString("description"));
                workshop.setId_artist(result.getInt("id_artist"));

                workshops.add(workshop);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return workshops;
    }

    @Override
    public void save(Workshop workshop) {

        String sql = "INSERT INTO Workshop(title_workshop, date_workshop, duration_minutes, max_participants, price, level, location, description, id_artist) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, workshop.getTitle_workshop());
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(workshop.getDate_workshop()));
            statement.setInt(3, workshop.getDuration_minutes());
            statement.setInt(4, workshop.getMax_participants());
            statement.setDouble(5, workshop.getPrice());
            statement.setString(6, workshop.getLevel());
            statement.setString(7, workshop.getLocation());
            statement.setString(8, workshop.getDescription());
            statement.setInt(9, workshop.getId_artist());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                workshop.setId_workshop(generatedKeys.getInt(1));
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void update(Workshop workshop) {

        String sql = "UPDATE Workshop SET title_workshop=?, date_workshop=?, duration_minutes=?, max_participants=?, price=?, level=?, location=?, description=?, id_artist=? "
                   + "WHERE id_workshop=?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, workshop.getTitle_workshop());
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(workshop.getDate_workshop()));
            statement.setInt(3, workshop.getDuration_minutes());
            statement.setInt(4, workshop.getMax_participants());
            statement.setDouble(5, workshop.getPrice());
            statement.setString(6, workshop.getLevel());
            statement.setString(7, workshop.getLocation());
            statement.setString(8, workshop.getDescription());
            statement.setInt(9, workshop.getId_artist());
            statement.setInt(10, workshop.getId_workshop());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void delete(int id) {

        String sql = "DELETE FROM Workshop WHERE id_workshop=?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public List<Workshop> findByArtist(int id_artist) {

        List<Workshop> workshops = new ArrayList<>();

        String sql = "SELECT * FROM Workshop WHERE id_artist = ?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id_artist);

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                Workshop workshop = new Workshop();

                workshop.setId_workshop(result.getInt("id_workshop"));
                workshop.setTitle_workshop(result.getString("title_workshop"));
                workshop.setDate_workshop(result.getTimestamp("date_workshop").toLocalDateTime());
                workshop.setDuration_minutes(result.getInt("duration_minutes"));
                workshop.setMax_participants(result.getInt("max_participants"));
                workshop.setPrice(result.getDouble("price"));
                workshop.setLevel(result.getString("level"));
                workshop.setLocation(result.getString("location"));
                workshop.setDescription(result.getString("description"));
                workshop.setId_artist(result.getInt("id_artist"));

                workshops.add(workshop);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return workshops;
    }
}