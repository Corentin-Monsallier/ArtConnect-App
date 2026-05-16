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
        String sql = "SELECT * FROM Artist a JOIN User_ u ON a.id_user = u.id_user";
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                Artist artist = new Artist();
                artist.setId_user(result.getInt("id_user"));
                artist.setName_user(result.getString("name_user"));
                artist.setEmail(result.getString("email"));
                artist.setBirth_year(result.getInt("birth_year"));
                artist.setPhone(result.getString("phone"));
                artist.setCity(result.getString("city"));
                artist.setId_artist(result.getInt("id_artist"));
                artist.setBio(result.getString("bio"));
                artist.setWebsite_artist(result.getString("website_artist"));
                artist.setIs_active(result.getBoolean("is_active"));
                artists.add(artist);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return artists;
    }

    @Override
    public void save(Artist artist) {
        String sqlUser = "INSERT INTO User_(name_user, email, birth_year, phone, city) VALUES (?, ?, ?, ?, ?)";
        String sqlArtist = "INSERT INTO Artist(bio, website_artist, is_active, id_user) VALUES (?, ?, ?, ?)";
        try (Connection connection = ConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                PreparedStatement userStmt = connection.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
                userStmt.setString(1, artist.getName_user());
                userStmt.setString(2, artist.getEmail());
                userStmt.setInt(3, artist.getBirth_year());
                userStmt.setString(4, artist.getPhone());
                userStmt.setString(5, artist.getCity());
                userStmt.executeUpdate();
                ResultSet keys = userStmt.getGeneratedKeys();
                int userId = 0;
                if (keys.next()) userId = keys.getInt(1);
                PreparedStatement artistStmt = connection.prepareStatement(sqlArtist);
                artistStmt.setString(1, artist.getBio());
                artistStmt.setString(2, artist.getWebsite_artist());
                artistStmt.setBoolean(3, artist.isIs_active());
                artistStmt.setInt(4, userId);
                artistStmt.executeUpdate();
                connection.commit();
            } catch (SQLException e) { connection.rollback(); e.printStackTrace(); }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void update(Artist artist) {
        String sqlUser = "UPDATE User_ SET name_user=?, email=?, birth_year=?, phone=?, city=? WHERE id_user=?";
        String sqlArtist = "UPDATE Artist SET bio=?, website_artist=?, is_active=? WHERE id_artist=?";
        try (Connection connection = ConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                PreparedStatement userStmt = connection.prepareStatement(sqlUser);
                userStmt.setString(1, artist.getName_user());
                userStmt.setString(2, artist.getEmail());
                userStmt.setInt(3, artist.getBirth_year());
                userStmt.setString(4, artist.getPhone());
                userStmt.setString(5, artist.getCity());
                userStmt.setInt(6, artist.getId_user());
                userStmt.executeUpdate();
                PreparedStatement artistStmt = connection.prepareStatement(sqlArtist);
                artistStmt.setString(1, artist.getBio());
                artistStmt.setString(2, artist.getWebsite_artist());
                artistStmt.setBoolean(3, artist.isIs_active());
                artistStmt.setInt(4, artist.getId_artist());
                artistStmt.executeUpdate();
                connection.commit();
            } catch (SQLException e) { connection.rollback(); e.printStackTrace(); }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void delete(int id_artist) {
        String sql = "DELETE a, u FROM Artist a JOIN User_ u ON a.id_user = u.id_user WHERE a.id_artist = ?";
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id_artist);
            statement.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}