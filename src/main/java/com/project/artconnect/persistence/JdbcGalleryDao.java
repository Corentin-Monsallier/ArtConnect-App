package com.project.artconnect.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.project.artconnect.dao.GalleryDao;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.util.ConnectionManager;

public class JdbcGalleryDao implements GalleryDao {

    @Override
    public List<Gallery> findAll() {
        List<Gallery> galleries = new ArrayList<>();

        String sql = "SELECT * FROM Gallery";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                Gallery gallery = new Gallery();
                gallery.setId_gallery(result.getInt("id_gallery"));
                gallery.setName_gallery(result.getString("name_gallery"));
                gallery.setRating(result.getInt("rating"));
                gallery.setWebsite_gallery(result.getString("website_gallery"));
                gallery.setAddress_id(result.getInt("address_id"));
                
                galleries.add(gallery);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return galleries;
    }

    @Override
    public void save(Gallery gallery) {
        String sql = "INSERT INTO Gallery(name_gallery, rating, website_gallery, address_id) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, gallery.getName_gallery());
            statement.setInt(2, gallery.getRating());
            statement.setString(3, gallery.getWebsite_gallery());
            statement.setInt(4, gallery.getAddress_id());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                gallery.setId_gallery(generatedKeys.getInt(1));
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void update(Gallery gallery) {
        String sql = "UPDATE Gallery SET name_gallery=?, rating=?, website_gallery=?, address_id=? WHERE id_gallery=?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, gallery.getName_gallery());
            statement.setInt(2, gallery.getRating());
            statement.setString(3, gallery.getWebsite_gallery());
            statement.setInt(4, gallery.getAddress_id());
            statement.setInt(5, gallery.getId_gallery());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Gallery WHERE id_gallery=?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public List<Gallery> findByCityId(int id_address) {
        List<Gallery> galleries = new ArrayList<>();

        String sql = "SELECT * FROM Gallery WHERE address_id = ?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id_address);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Gallery gallery = new Gallery();
                gallery.setId_gallery(result.getInt("id_gallery"));
                gallery.setName_gallery(result.getString("name_gallery"));
                gallery.setRating(result.getInt("rating"));
                gallery.setWebsite_gallery(result.getString("website_gallery"));
                gallery.setAddress_id(result.getInt("address_id"));
                
                galleries.add(gallery);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return galleries;
    }
}