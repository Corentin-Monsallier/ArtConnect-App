package com.project.artconnect.persistence;

import com.project.artconnect.dao.ArtworkDao;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.ArtworkStatus;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation for ArtworkDao.
 */
public class JdbcArtworkDao implements ArtworkDao {

    @Override
    public List<Artwork> findAll() {

        List<Artwork> artworks = new ArrayList<>();

        String sql = "SELECT * FROM Artwork";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Artwork artwork = new Artwork();

                artwork.setId_artwork(result.getInt("id_artwork"));
                artwork.setTitle_art(result.getString("title_art"));
                artwork.setCreation_year(result.getInt("creation_year"));
                artwork.setType(result.getString("type"));
                artwork.setMedium(result.getString("medium"));
                artwork.setDimensions(result.getString("dimensions"));
                artwork.setDescription(result.getString("description"));
                artwork.setPrice(result.getDouble("price"));

                artwork.setStatus(
                        ArtworkStatus.valueOf(
                                result.getString("status").toUpperCase()));

                artwork.setId_artist(result.getInt("id_artist"));

                artworks.add(artwork);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return artworks;
    }

    @Override
    public void save(Artwork artwork) {

        String sql = "INSERT INTO Artwork(title_art, creation_year, type, medium, dimensions, description, price, status, id_artist) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, artwork.getTitle_art());
            statement.setInt(2, artwork.getCreation_year());
            statement.setString(3, artwork.getType());
            statement.setString(4, artwork.getMedium());
            statement.setString(5, artwork.getDimensions());
            statement.setString(6, artwork.getDescription());
            statement.setDouble(7, artwork.getPrice());
            statement.setString(8, artwork.getStatus().name().toLowerCase());
            statement.setInt(9, artwork.getId_artist());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Artwork artwork) {

        String sql = "UPDATE Artwork "
                + "SET title_art=?, creation_year=?, type=?, medium=?, dimensions=?, description=?, price=?, status=?, id_artist=? "
                + "WHERE id_artwork=?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, artwork.getTitle_art());
            statement.setInt(2, artwork.getCreation_year());
            statement.setString(3, artwork.getType());
            statement.setString(4, artwork.getMedium());
            statement.setString(5, artwork.getDimensions());
            statement.setString(6, artwork.getDescription());
            statement.setDouble(7, artwork.getPrice());
            statement.setString(8, artwork.getStatus().name().toLowerCase());
            statement.setInt(9, artwork.getId_artist());
            statement.setInt(10, artwork.getId_artwork());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String title) {

        String sql = "DELETE FROM Artwork WHERE title_art = ?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, title);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Artwork> findByArtistName(String artistName) {

        List<Artwork> artworks = new ArrayList<>();

        String sql = "SELECT aw.* " + "FROM Artwork aw " + "JOIN Artist a ON aw.id_artist = a.id_artist "
                + "JOIN User_ u ON a.id_user = u.id_user " + "WHERE u.name_user = ?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, artistName);

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                Artwork artwork = new Artwork();

                artwork.setId_artwork(result.getInt("id_artwork"));
                artwork.setTitle_art(result.getString("title_art"));
                artwork.setCreation_year(result.getInt("creation_year"));
                artwork.setType(result.getString("type"));
                artwork.setMedium(result.getString("medium"));
                artwork.setDimensions(result.getString("dimensions"));
                artwork.setDescription(result.getString("description"));
                artwork.setPrice(result.getDouble("price"));

                artwork.setStatus(
                        ArtworkStatus.valueOf(
                                result.getString("status").toUpperCase()));

                artwork.setId_artist(result.getInt("id_artist"));

                artworks.add(artwork);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return artworks;
    }
}
