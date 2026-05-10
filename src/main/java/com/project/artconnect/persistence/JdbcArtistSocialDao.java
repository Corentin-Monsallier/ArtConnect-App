package com.project.artconnect.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.project.artconnect.dao.ArtistSocialDao;
import com.project.artconnect.model.ArtistSocial;
import com.project.artconnect.util.ConnectionManager;

public class JdbcArtistSocialDao implements ArtistSocialDao {

    @Override
    public List<ArtistSocial> findAll() {

        List<ArtistSocial> socials = new ArrayList<>();

        String sql = "SELECT * FROM Artist_Social";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                ArtistSocial social = new ArtistSocial();

                social.setId_social(result.getInt("id_social"));
                social.setPlatform(result.getString("platform"));
                social.setLink(result.getString("link"));
                social.setId_artist(result.getInt("id_artist"));

                socials.add(social);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return socials;
    }

    @Override
    public void save(ArtistSocial artistSocial) {

        String sql = "INSERT INTO Artist_Social(platform, link, id_artist) " + "VALUES (?, ?, ?)";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, artistSocial.getPlatform());
            statement.setString(2, artistSocial.getLink());
            statement.setInt(3, artistSocial.getId_artist());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void update(ArtistSocial artistSocial) {

        String sql = "UPDATE Artist_Social " + "SET platform=?, link=?, id_artist=? " + "WHERE id_social=?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, artistSocial.getPlatform());
            statement.setString(2, artistSocial.getLink());
            statement.setInt(3, artistSocial.getId_artist());
            statement.setInt(4, artistSocial.getId_social());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void delete(int id) {

        String sql = "DELETE FROM Artist_Social WHERE id_social=?";

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
    public List<ArtistSocial> findByArtistId(int id_artist) {

        List<ArtistSocial> socials = new ArrayList<>();

        String sql = "SELECT * FROM Artist_Social WHERE id_artist=?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id_artist);

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                ArtistSocial social = new ArtistSocial();

                social.setId_social(result.getInt("id_social"));
                social.setPlatform(result.getString("platform"));
                social.setLink(result.getString("link"));
                social.setId_artist(result.getInt("id_artist"));

                socials.add(social);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return socials;
    }
}