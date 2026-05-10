package com.project.artconnect.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.util.ConnectionManager;

public class JdbcArtistDao implements ArtistDao {

    @Override
    public List<Artist> findAll() {

        List<Artist> artists = new ArrayList<>();

        String sql = "SELECT * " + "FROM Artist a " + "JOIN User_ u ON a.id_user = u.id_user";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Artist artist = new Artist();

                // User
                artist.setId_user(result.getInt("id_user"));
                artist.setName_user(result.getString("name_user"));
                artist.setEmail(result.getString("email"));
                artist.setBirth_year(result.getInt("birth_year"));
                artist.setPhone(result.getString("phone"));
                artist.setCity(result.getString("city"));
                // Artist
                artist.setId_artist(result.getInt("id_artist"));
                artist.setBio(result.getString("bio"));
                artist.setWebsite_artist(result.getString("website_artist"));
                artist.setIs_active(result.getBoolean("is_active"));

                artists.add(artist);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return artists;
    }

    @Override
    public void save(Artist artist) {

        String sqlUser = "INSERT INTO User_(name_user, email, birth_year, phone, city) " + "VALUES (?, ?, ?, ?, ?)";

        String sqlArtist = "INSERT INTO Artist(bio, website_artist, is_active, id_user) " + "VALUES (?, ?, ?, ?)";

        try (
                Connection connection = ConnectionManager.getConnection()) {

            connection.setAutoCommit(false);

            PreparedStatement userStatement = connection.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);

            userStatement.setString(1, artist.getName_user());
            userStatement.setString(2, artist.getEmail());
            userStatement.setInt(3, artist.getBirth_year());
            userStatement.setString(4, artist.getPhone());
            userStatement.setString(5, artist.getCity());

            userStatement.executeUpdate();
            ResultSet generatedKeys = userStatement.getGeneratedKeys();

            int generatedUserId = 0;

            if (generatedKeys.next()) {
                generatedUserId = generatedKeys.getInt(1);
            }

            PreparedStatement artistStatement = connection.prepareStatement(sqlArtist);

            artistStatement.setString(1, artist.getBio());
            artistStatement.setString(2, artist.getWebsite_artist());
            artistStatement.setBoolean(3, artist.isIs_active());
            artistStatement.setInt(4, generatedUserId);

            artistStatement.executeUpdate();

            connection.commit();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void update(Artist artist) {

        String sqlUser = "UPDATE User_ " + "SET name_user=?, email=?, birth_year=?, phone=?, city=? "
                + "WHERE id_user=?";

        String sqlArtist = "UPDATE Artist " + "SET bio=?, website_artist=?, is_active=? " + "WHERE id_artist=?";

        try (
                Connection connection = ConnectionManager.getConnection()) {

            PreparedStatement userStatement = connection.prepareStatement(sqlUser);

            userStatement.setString(1, artist.getName_user());
            userStatement.setString(2, artist.getEmail());
            userStatement.setInt(3, artist.getBirth_year());
            userStatement.setString(4, artist.getPhone());
            userStatement.setString(5, artist.getCity());
            userStatement.setInt(6, artist.getId_user());

            userStatement.executeUpdate();

            PreparedStatement artistStatement = connection.prepareStatement(sqlArtist);

            artistStatement.setString(1, artist.getBio());
            artistStatement.setString(2, artist.getWebsite_artist());
            artistStatement.setBoolean(3, artist.isIs_active());
            artistStatement.setInt(4, artist.getId_artist());

            artistStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void delete(int id_artist) {

        String sql = "DELETE a FROM Artist a " + "JOIN User_ u ON a.id_user = u.id_user " + "WHERE u.id_user = ?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id_artist);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
