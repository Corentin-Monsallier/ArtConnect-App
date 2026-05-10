package com.project.artconnect.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.project.artconnect.dao.DisciplineDao;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.util.ConnectionManager;

public class JdbcDisciplineDao implements DisciplineDao {

    @Override
    public List<Discipline> findAll() {

        List<Discipline> disciplines = new ArrayList<>();

        String sql = "SELECT * FROM Discipline";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Discipline discipline = new Discipline();

                discipline.setId_discipline(result.getInt("id_discipline"));
                discipline.setName_discipline(result.getString("name_discipline"));
                disciplines.add(discipline);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return disciplines;
    }

    @Override
    public void save(Discipline discipline) {

        String sql = "INSERT INTO Discipline(name_discipline) " + "VALUES (?)";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, discipline.getName_discipline());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void update(Discipline discipline) {

        String sql = "UPDATE Discipline " + "SET name_discipline=? " + "WHERE id_discipline=?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, discipline.getName_discipline());
            statement.setInt(2, discipline.getId_discipline());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void delete(int id) {

        String sql = "DELETE FROM Discipline WHERE id_discipline=?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public List<Discipline> findByArtistId(int id_artist) {

        List<Discipline> disciplines = new ArrayList<>();

        String sql = "SELECT d.* " + "FROM Discipline d " + "JOIN Artist_Discipline ad "
                + "ON d.id_discipline = ad.id_discipline " + "WHERE ad.id_artist=?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id_artist);

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                Discipline discipline = new Discipline();

                discipline.setId_discipline(result.getInt("id_discipline"));
                discipline.setName_discipline(result.getString("name_discipline"));

                disciplines.add(discipline);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return disciplines;
    }
}