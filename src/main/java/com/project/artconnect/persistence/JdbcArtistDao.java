package com.project.artconnect.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.UserRole;
import com.project.artconnect.util.ConnectionManager;

public class JdbcArtistDao implements ArtistDao {

    @Override
    public List<Artist> findAll() {
        List<Artist> artists = new ArrayList<>();
        String sql = "SELECT * FROM Artist a JOIN User_ u ON a.id_user = u.id_user";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement s = c.prepareStatement(sql);
             ResultSet rs = s.executeQuery()) {
            while (rs.next()) {
                Artist artist = new Artist();
                artist.setId_user(rs.getInt("id_user"));
                artist.setName_user(rs.getString("name_user"));
                artist.setEmail(rs.getString("email"));
                artist.setBirth_year(rs.getInt("birth_year"));
                artist.setPhone(rs.getString("phone"));
                artist.setCity(rs.getString("city"));
                artist.setId_artist(rs.getInt("id_artist"));
                artist.setBio(rs.getString("bio"));
                artist.setWebsite_artist(rs.getString("website_artist"));
                artist.setIs_active(rs.getBoolean("is_active"));
                artists.add(artist);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return artists;
    }

    @Override
    public void save(Artist artist) {
        String sqlUser   = "INSERT INTO User_(name_user, email, birth_year, phone, city, password_user, role_user) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlArtist = "INSERT INTO Artist(bio, website_artist, is_active, id_user) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement us = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
                us.setString(1, artist.getName_user());
                us.setString(2, artist.getEmail());
                us.setInt(3, artist.getBirth_year());
                us.setString(4, artist.getPhone());
                us.setString(5, artist.getCity());
                // default password = email, default role = ARTIST
                us.setString(6, artist.getEmail());
                us.setString(7, UserRole.ARTIST.name());
                us.executeUpdate();
                ResultSet keys = us.getGeneratedKeys();
                int userId = keys.next() ? keys.getInt(1) : -1;

                PreparedStatement as = conn.prepareStatement(sqlArtist);
                as.setString(1, artist.getBio());
                as.setString(2, artist.getWebsite_artist());
                as.setBoolean(3, artist.isIs_active());
                as.setInt(4, userId);
                as.executeUpdate();
                conn.commit();
            } catch (SQLException e) { conn.rollback(); e.printStackTrace(); }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void update(Artist artist) {
        String sqlUser   = "UPDATE User_ SET name_user=?, email=?, birth_year=?, phone=?, city=? WHERE id_user=?";
        String sqlArtist = "UPDATE Artist SET bio=?, website_artist=?, is_active=? WHERE id_artist=?";
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement us = conn.prepareStatement(sqlUser);
                us.setString(1, artist.getName_user());
                us.setString(2, artist.getEmail());
                us.setInt(3, artist.getBirth_year());
                us.setString(4, artist.getPhone());
                us.setString(5, artist.getCity());
                us.setInt(6, artist.getId_user());
                us.executeUpdate();

                PreparedStatement as = conn.prepareStatement(sqlArtist);
                as.setString(1, artist.getBio());
                as.setString(2, artist.getWebsite_artist());
                as.setBoolean(3, artist.isIs_active());
                as.setInt(4, artist.getId_artist());
                as.executeUpdate();
                conn.commit();
            } catch (SQLException e) { conn.rollback(); e.printStackTrace(); }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void delete(int id_artist) {
        String sql = "DELETE a, u FROM Artist a JOIN User_ u ON a.id_user = u.id_user WHERE a.id_artist = ?";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id_artist);
            s.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}