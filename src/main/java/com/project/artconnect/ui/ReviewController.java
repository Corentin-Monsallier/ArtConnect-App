package com.project.artconnect.ui;

import com.project.artconnect.model.Review;
import com.project.artconnect.service.ReviewService;
import com.project.artconnect.util.ServiceProvider;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReviewController {

    @FXML
    private TableView<Review> reviewTable;

    @FXML
    private TableColumn<Review, Integer> memberColumn;

    @FXML
    private TableColumn<Review, Integer> artworkColumn;

    @FXML
    private TableColumn<Review, Integer> ratingColumn;

    @FXML
    private TableColumn<Review, String> commentColumn;

    @FXML
    private TableColumn<Review, String> dateColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> ratingFilter;

    private final ReviewService reviewService =
            ServiceProvider.getReviewService();

    @FXML
    public void initialize() {

        memberColumn.setCellValueFactory(
                new PropertyValueFactory<>("id_member"));

        artworkColumn.setCellValueFactory(
                new PropertyValueFactory<>("id_artwork"));

        ratingColumn.setCellValueFactory(
                new PropertyValueFactory<>("rating"));

        commentColumn.setCellValueFactory(
                new PropertyValueFactory<>("comment"));

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("review_date"));

        loadReviews();
        loadRatings();

        ratingFilter.setOnAction(event -> handleSearch());
    }

    private void loadReviews() {

        reviewTable.setItems(
                FXCollections.observableArrayList(
                        reviewService.getAllReviews()));
    }

    private void loadRatings() {

        ratingFilter.setItems(
                FXCollections.observableArrayList(
                        "All ratings",
                        "1",
                        "2",
                        "3",
                        "4",
                        "5"
                )
        );

        ratingFilter.setValue("All ratings");
    }

    @FXML
    private void handleSearch() {

        String search =
                searchField.getText().toLowerCase();

        String selectedRating =
                ratingFilter.getValue();

        ObservableList<Review> filtered =
                FXCollections.observableArrayList();

        for (Review review : reviewService.getAllReviews()) {

            boolean commentMatch =
                    review.getComment()
                            .toLowerCase()
                            .contains(search);

            boolean ratingMatch =
                    selectedRating.equals("All ratings")
                            || String.valueOf(review.getRating())
                            .equals(selectedRating);

            if (commentMatch && ratingMatch) {
                filtered.add(review);
            }
        }

        reviewTable.setItems(filtered);
    }

    @FXML
    private void handleReset() {

        searchField.clear();
        ratingFilter.setValue("All ratings");
        loadReviews();
    }
}