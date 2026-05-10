package com.project.artconnect.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.project.artconnect.dao.ReviewDao;
import com.project.artconnect.model.Review;
import com.project.artconnect.util.ConnectionManager;

public class JdbcReviewDao implements ReviewDao {

    @Override
    public List<Review> findAll() {
        List<Review> reviews = new ArrayList<>();

        String sql = "SELECT * FROM Review";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                Review review = new Review();
                review.setId_member(result.getInt("id_member"));
                review.setId_artwork(result.getInt("id_artwork"));
                review.setRating(result.getInt("rating"));
                review.setComment(result.getString("comment"));
                review.setReview_date(result.getTimestamp("review_date").toLocalDateTime());
                
                reviews.add(review);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return reviews;
    }

    @Override
    public void save(Review review) {
        String sql = "INSERT INTO Review(id_member, id_artwork, rating, comment, review_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, review.getId_member());
            statement.setInt(2, review.getId_artwork());
            statement.setInt(3, review.getRating());
            statement.setString(4, review.getComment());
            statement.setTimestamp(5, java.sql.Timestamp.valueOf(review.getReview_date()));

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void update(Review review) {
        String sql = "UPDATE Review SET rating=?, comment=?, review_date=? WHERE id_member=? AND id_artwork=?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, review.getRating());
            statement.setString(2, review.getComment());
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(review.getReview_date()));
            statement.setInt(4, review.getId_member());
            statement.setInt(5, review.getId_artwork());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void delete(int id_member, int id_artwork) {
        String sql = "DELETE FROM Review WHERE id_member=? AND id_artwork=?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id_member);
            statement.setInt(2, id_artwork);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public List<Review> findByArtworkId(int id_artwork) {
        List<Review> reviews = new ArrayList<>();

        String sql = "SELECT * FROM Review WHERE id_artwork = ?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id_artwork);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Review review = new Review();
                review.setId_member(result.getInt("id_member"));
                review.setId_artwork(result.getInt("id_artwork"));
                review.setRating(result.getInt("rating"));
                review.setComment(result.getString("comment"));
                review.setReview_date(result.getTimestamp("review_date").toLocalDateTime());
                
                reviews.add(review);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return reviews;
    }

    @Override
    public List<Review> findByMemberId(int id_member) {
        List<Review> reviews = new ArrayList<>();

        String sql = "SELECT * FROM Review WHERE id_member = ?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id_member);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Review review = new Review();
                review.setId_member(result.getInt("id_member"));
                review.setId_artwork(result.getInt("id_artwork"));
                review.setRating(result.getInt("rating"));
                review.setComment(result.getString("comment"));
                review.setReview_date(result.getTimestamp("review_date").toLocalDateTime());
                
                reviews.add(review);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return reviews;
    }
}