package com.project.artconnect.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.project.artconnect.dao.ArtworkDao;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.ArtworkStatus;
import com.project.artconnect.util.ConnectionManager;

public class JdbcArtworkDao implements ArtworkDao {

    @Override
    public List<Artwork> findAll() {

        List<Artwork> artworks = new ArrayList<>();

        String sql =
                "SELECT a.*, GROUP_CONCAT(t.name SEPARATOR ', ') AS tags " +
                "FROM Artwork a " +
                "LEFT JOIN Artwork_Tag at ON a.id_artwork = at.id_artwork " +
                "LEFT JOIN Tag t ON at.id_tag = t.id_tag " +
                "GROUP BY a.id_artwork";

        try (
                Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                Artwork artwork = new Artwork();

                artwork.setId_artwork(
                        rs.getInt("id_artwork"));

                artwork.setTitle_art(
                        rs.getString("title_art"));

                artwork.setCreation_year(
                        rs.getInt("creation_year"));

                artwork.setType(
                        rs.getString("type"));

                artwork.setMedium(
                        rs.getString("medium"));

                artwork.setDimensions(
                        rs.getString("dimensions"));

                artwork.setDescription(
                        rs.getString("description"));

                artwork.setPrice(
                        rs.getDouble("price"));

                artwork.setStatus(
                        ArtworkStatus.valueOf(
                                rs.getString("status")
                                        .toUpperCase()));

                artwork.setId_artist(
                        rs.getInt("id_artist"));

                artwork.setTags(
                        rs.getString("tags"));

                artworks.add(artwork);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return artworks;
    }

    @Override
    public void save(Artwork artwork) {

        String sql =
                "INSERT INTO Artwork " +
                "(title_art, creation_year, type, medium, dimensions, description, price, status, id_artist) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, artwork.getTitle_art());
            stmt.setInt(2, artwork.getCreation_year());
            stmt.setString(3, artwork.getType());
            stmt.setString(4, artwork.getMedium());
            stmt.setString(5, artwork.getDimensions());
            stmt.setString(6, artwork.getDescription());
            stmt.setDouble(7, artwork.getPrice());
            stmt.setString(8, artwork.getStatus().toString().toLowerCase());
            stmt.setInt(9, artwork.getId_artist());

            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void update(Artwork artwork) {

        String sql =
                "UPDATE Artwork SET " +
                "title_art=?, creation_year=?, type=?, medium=?, dimensions=?, description=?, price=?, status=?, id_artist=? " +
                "WHERE id_artwork=?";

        try (
                Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, artwork.getTitle_art());
            stmt.setInt(2, artwork.getCreation_year());
            stmt.setString(3, artwork.getType());
            stmt.setString(4, artwork.getMedium());
            stmt.setString(5, artwork.getDimensions());
            stmt.setString(6, artwork.getDescription());
            stmt.setDouble(7, artwork.getPrice());
            stmt.setString(8, artwork.getStatus().toString().toLowerCase());
            stmt.setInt(9, artwork.getId_artist());
            stmt.setInt(10, artwork.getId_artwork());

            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void delete(int id) {

        String sql =
                "DELETE FROM Artwork WHERE id_artwork=?";

        try (
                Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public List<Artwork> findByArtistId(int id_artist) {

        List<Artwork> artworks = new ArrayList<>();

        String sql =
                "SELECT a.*, GROUP_CONCAT(t.name SEPARATOR ', ') AS tags " +
                "FROM Artwork a " +
                "LEFT JOIN Artwork_Tag at ON a.id_artwork = at.id_artwork " +
                "LEFT JOIN Tag t ON at.id_tag = t.id_tag " +
                "WHERE a.id_artist=? " +
                "GROUP BY a.id_artwork";

        try (
                Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id_artist);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Artwork artwork = new Artwork();

                artwork.setId_artwork(
                        rs.getInt("id_artwork"));

                artwork.setTitle_art(
                        rs.getString("title_art"));

                artwork.setCreation_year(
                        rs.getInt("creation_year"));

                artwork.setType(
                        rs.getString("type"));

                artwork.setMedium(
                        rs.getString("medium"));

                artwork.setDimensions(
                        rs.getString("dimensions"));

                artwork.setDescription(
                        rs.getString("description"));

                artwork.setPrice(
                        rs.getDouble("price"));

                artwork.setStatus(
                        ArtworkStatus.valueOf(
                                rs.getString("status")
                                        .toUpperCase()));

                artwork.setId_artist(
                        rs.getInt("id_artist"));

                artwork.setTags(
                        rs.getString("tags"));

                artworks.add(artwork);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return artworks;
    }
}