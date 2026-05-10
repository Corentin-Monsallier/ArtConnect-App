package com.project.artconnect.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.project.artconnect.dao.ExhibitionDao;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.util.ConnectionManager;

public class JdbcExhibitionDao implements ExhibitionDao{

    @Override
    public List<Exhibition> findAll() {
        List<Exhibition> exhibitions = new ArrayList<>();
        String sql = "SELECT * FROM Exhibition";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            
                while (result.next()) {
                    Exhibition exhibition = new Exhibition();
                    
                    exhibition.setId_exhibition(result.getInt("id_exhibition"));
                    exhibition.setTitle_exhib(result.getString("title_exhib"));
                    exhibition.setCurator_name(result.getString("curator_name"));
                    exhibition.setStart_date(result.getDate("start_date").toLocalDate());
                    exhibition.setEnd_date(result.getDate("end_date").toLocalDate());
                    exhibition.setTheme(result.getString("theme"));
                    exhibition.setDescription(result.getString("description"));
                    exhibition.setId_gallery(result.getInt("id_gallery"));
                    
                    exhibitions.add(exhibition);
                }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return exhibitions;
    }

    @Override
    public void save(Exhibition exhibition) {
        String sql = "INSERT INTO Exhibition(title_exhib, curator_name, start_date, end_date, theme, description, id_gallery) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, exhibition.getTitle_exhib());
            statement.setString(2, exhibition.getCurator_name());
            statement.setDate(3, java.sql.Date.valueOf(exhibition.getStart_date()));
            statement.setDate(4, java.sql.Date.valueOf(exhibition.getEnd_date()));
            statement.setString(5, exhibition.getTheme());
            statement.setString(6, exhibition.getDescription());
            statement.setInt(7, exhibition.getId_gallery());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    exhibition.setId_exhibition(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void update(Exhibition exhibition) {
        String sql = "UPDATE Exhibition SET title_exhib=?, curator_name=?, start_date=?, end_date=?, theme=?, description=?, id_gallery=? "
                   + "WHERE id_exhibition=?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, exhibition.getTitle_exhib());
            statement.setString(2, exhibition.getCurator_name());
            statement.setDate(3, exhibition.getStart_date() != null ? java.sql.Date.valueOf(exhibition.getStart_date()) : null);
            statement.setDate(4, exhibition.getEnd_date() != null ? java.sql.Date.valueOf(exhibition.getEnd_date()) : null);
            statement.setString(5, exhibition.getTheme());
            statement.setString(6, exhibition.getDescription());
            statement.setInt(7, exhibition.getId_gallery());
            statement.setInt(8, exhibition.getId_exhibition());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Exhibition WHERE id_exhibition=?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public List<Exhibition> findByGalleryId(int id_gallery) {
        List<Exhibition> exhibitions = new ArrayList<>();

        String sql = "SELECT * FROM Exhibition WHERE id_gallery = ?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id_gallery);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Exhibition exhibition = new Exhibition();
                exhibition.setId_exhibition(result.getInt("id_exhibition"));
                exhibition.setTitle_exhib(result.getString("title_exhib"));
                exhibition.setCurator_name(result.getString("curator_name"));
                exhibition.setStart_date(result.getDate("start_date").toLocalDate());
                exhibition.setEnd_date(result.getDate("end_date").toLocalDate());
                exhibition.setTheme(result.getString("theme"));
                exhibition.setDescription(result.getString("description"));
                exhibition.setId_gallery(result.getInt("id_gallery"));
                
                exhibitions.add(exhibition);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return exhibitions;
    }
}
