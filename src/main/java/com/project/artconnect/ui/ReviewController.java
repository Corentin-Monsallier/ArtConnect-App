package com.project.artconnect.ui;

import java.time.LocalDateTime;
import java.util.List;

import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.Member;
import com.project.artconnect.model.Review;
import com.project.artconnect.service.ArtworkService;
import com.project.artconnect.service.MemberService;
import com.project.artconnect.service.ReviewService;
import com.project.artconnect.util.ServiceProvider;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class ReviewController {

    @FXML private TableView<Review> reviewTable;
    @FXML private TableColumn<Review, Integer> memberColumn;
    @FXML private TableColumn<Review, Integer> artworkColumn;
    @FXML private TableColumn<Review, Integer> ratingColumn;
    @FXML private TableColumn<Review, String> commentColumn;
    @FXML private TableColumn<Review, String> dateColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> ratingFilter;

    private final ReviewService reviewService = ServiceProvider.getReviewService();
    private final MemberService memberService = ServiceProvider.getMemberService();
    private final ArtworkService artworkService = ServiceProvider.getArtworkService();

    @FXML
    public void initialize() {
        memberColumn.setCellValueFactory(new PropertyValueFactory<>("id_member"));
        artworkColumn.setCellValueFactory(new PropertyValueFactory<>("id_artwork"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("review_date"));
        loadReviews();
        ratingFilter.setItems(FXCollections.observableArrayList("All ratings", "1", "2", "3", "4", "5"));
        ratingFilter.setValue("All ratings");
        ratingFilter.setOnAction(e -> handleSearch());
    }

    private void loadReviews() {
        reviewTable.setItems(FXCollections.observableArrayList(reviewService.getAllReviews()));
    }

    @FXML private void handleSearch() {
        String search = searchField.getText().toLowerCase();
        String sel = ratingFilter.getValue();
        ObservableList<Review> filtered = FXCollections.observableArrayList();
        for (Review r : reviewService.getAllReviews()) {
            boolean c = r.getComment() != null && r.getComment().toLowerCase().contains(search);
            boolean rt = sel.equals("All ratings") || String.valueOf(r.getRating()).equals(sel);
            if (c && rt) filtered.add(r);
        }
        reviewTable.setItems(filtered);
    }

    @FXML private void handleReset() { searchField.clear(); ratingFilter.setValue("All ratings"); loadReviews(); }

    @FXML private void handleAdd() {
        buildDialog(null).showAndWait().ifPresent(r -> { reviewService.createReview(r); loadReviews(); });
    }

    @FXML private void handleEdit() {
        Review sel = reviewTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a review to edit."); return; }
        buildDialog(sel).showAndWait().ifPresent(r -> { reviewService.updateReview(r); loadReviews(); });
    }

    @FXML private void handleDelete() {
        Review sel = reviewTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a review to delete."); return; }
        new Alert(Alert.AlertType.CONFIRMATION, "Delete this review?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try { reviewService.deleteReview(sel.getId_member(), sel.getId_artwork()); loadReviews(); }
                        catch (Exception e) { warn("Cannot delete: " + e.getMessage()); }
                    }
                });
    }

    private Dialog<Review> buildDialog(Review existing) {
        Dialog<Review> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Review" : "Edit Review");
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        List<Member> members = memberService.getAllMembers();
        List<Artwork> artworks = artworkService.getAllArtworks();

        ComboBox<Member> memberF = new ComboBox<>(FXCollections.observableArrayList(members));
        memberF.setConverter(new StringConverter<>() {
            public String toString(Member m) { return m == null ? "" : "#" + m.getId_member() + " – " + m.getName_user(); }
            public Member fromString(String s) { return null; }
        });
        memberF.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Artwork> artworkF = new ComboBox<>(FXCollections.observableArrayList(artworks));
        artworkF.setConverter(new StringConverter<>() {
            public String toString(Artwork a) { return a == null ? "" : "#" + a.getId_artwork() + " – " + a.getTitle_art(); }
            public Artwork fromString(String s) { return null; }
        });
        artworkF.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Integer> ratingF = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        ratingF.setMaxWidth(Double.MAX_VALUE);

        TextArea commentF = new TextArea(existing != null ? existing.getComment() : "");
        commentF.setPrefRowCount(4);
        commentF.setWrapText(true);

        if (existing != null) {
            members.stream().filter(m -> m.getId_member() == existing.getId_member()).findFirst().ifPresent(memberF::setValue);
            artworks.stream().filter(a -> a.getId_artwork() == existing.getId_artwork()).findFirst().ifPresent(artworkF::setValue);
            ratingF.setValue(existing.getRating());
            memberF.setDisable(true);
            artworkF.setDisable(true);
        }

        VBox box = new VBox(10,
                new Label("Member:"), memberF,
                new Label("Artwork:"), artworkF,
                new Label("Rating (1-5):"), ratingF,
                new Label("Comment:"), commentF
        );
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setPrefWidth(480);
        dialog.getDialogPane().setPrefHeight(460);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                if (existing == null && (memberF.getValue() == null || artworkF.getValue() == null)) {
                    warn("Please select a member and an artwork."); return null;
                }
                if (ratingF.getValue() == null) { warn("Please select a rating."); return null; }
                Review r = existing != null ? existing : new Review();
                if (existing == null) {
                    r.setId_member(memberF.getValue().getId_member());
                    r.setId_artwork(artworkF.getValue().getId_artwork());
                }
                r.setRating(ratingF.getValue());
                r.setComment(commentF.getText());
                r.setReview_date(LocalDateTime.now());
                return r;
            }
            return null;
        });
        return dialog;
    }

    private void warn(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }
}