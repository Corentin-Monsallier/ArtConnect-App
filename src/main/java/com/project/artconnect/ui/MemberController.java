package com.project.artconnect.ui;

import java.sql.*;
import java.util.List;
import java.util.Map;

import com.project.artconnect.model.*;
import com.project.artconnect.service.*;
import com.project.artconnect.util.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MemberController {

    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private FlowPane discoverPane;

    // Artworks
    @FXML private TableView<Map<String, String>> artworkTable;
    @FXML private TableColumn<Map<String, String>, String> mArtTitleCol;
    @FXML private TableColumn<Map<String, String>, String> mArtArtistCol;
    @FXML private TableColumn<Map<String, String>, String> mArtTypeCol;
    @FXML private TableColumn<Map<String, String>, String> mArtMediumCol;
    @FXML private TableColumn<Map<String, String>, String> mArtYearCol;
    @FXML private TableColumn<Map<String, String>, String> mArtPriceCol;
    @FXML private TableColumn<Map<String, String>, String> mArtStatusCol;
    @FXML private TableColumn<Map<String, String>, String> mArtDisciplinesCol; // FIX: colonne disciplines
    @FXML private TextField artworkSearch;
    @FXML private ComboBox<String> artworkStatusFilter;
    @FXML private ComboBox<String> artworkTypeFilter;
    @FXML private ComboBox<String> artworkMediumFilter;
    @FXML private ComboBox<String> artworkDisciplineFilter;

    // Reviews (all)
    @FXML private TableView<Map<String, String>> reviewTable;
    @FXML private TableColumn<Map<String, String>, String> mRvArtworkCol;
    @FXML private TableColumn<Map<String, String>, String> mRvRatingCol;
    @FXML private TableColumn<Map<String, String>, String> mRvCommentCol;
    @FXML private TableColumn<Map<String, String>, String> mRvReviewerCol;
    @FXML private TableColumn<Map<String, String>, String> mRvDateCol;
    @FXML private TextField reviewSearch;
    @FXML private ComboBox<String> reviewRatingFilter;

    // Exhibitions
    @FXML private TableView<Map<String, String>> exhibitionTable;
    @FXML private TableColumn<Map<String, String>, String> mExTitleCol;
    @FXML private TableColumn<Map<String, String>, String> mExGalleryCol;
    @FXML private TableColumn<Map<String, String>, String> mExThemeCol;
    @FXML private TableColumn<Map<String, String>, String> mExStartCol;
    @FXML private TableColumn<Map<String, String>, String> mExEndCol;
    @FXML private TextField exhibitionSearch;
    @FXML private ComboBox<String> exhibitionGalleryFilter;

    // Workshops
    @FXML private TableView<Map<String, String>> workshopTable;
    @FXML private TableColumn<Map<String, String>, String> mWsTitleCol;
    @FXML private TableColumn<Map<String, String>, String> mWsInstructorCol;
    @FXML private TableColumn<Map<String, String>, String> mWsDateCol;
    @FXML private TableColumn<Map<String, String>, String> mWsLevelCol;
    @FXML private TableColumn<Map<String, String>, String> mWsPriceCol;
    @FXML private TableColumn<Map<String, String>, String> mWsSpotsCol;
    @FXML private TableColumn<Map<String, String>, String> mWsAvailCol;
    @FXML private TextField workshopSearch;
    @FXML private ComboBox<String> workshopAvailFilter;

    // My Bookings
    @FXML private TableView<Map<String, String>> bookingTable;
    @FXML private TableColumn<Map<String, String>, String> mBkWorkshopCol;
    @FXML private TableColumn<Map<String, String>, String> mBkDateCol;
    @FXML private TableColumn<Map<String, String>, String> mBkStatusCol;
    @FXML private TextField bookingSearch;
    @FXML private ComboBox<String> bookingStatusFilter;

    // My Reviews
    @FXML private TableView<Map<String, String>> myReviewTable;
    @FXML private TableColumn<Map<String, String>, String> mMyRvArtworkCol;
    @FXML private TableColumn<Map<String, String>, String> mMyRvRatingCol;
    @FXML private TableColumn<Map<String, String>, String> mMyRvCommentCol;
    @FXML private TableColumn<Map<String, String>, String> mMyRvDateCol;
    @FXML private TextField myReviewSearch;

    // Profile
    @FXML private TextField profileName;
    @FXML private TextField profileEmail;
    @FXML private TextField profilePhone;
    @FXML private TextField profileCity;
    @FXML private TextField profileBirthYear;
    @FXML private Label profileMsg;

    private final MemberService memberService = ServiceProvider.getMemberService();

    private Member currentMember;
    private List<Map<String, String>> allArtworks;
    private List<Map<String, String>> allReviews;
    private List<Map<String, String>> allExhibitions;
    private List<Map<String, String>> allWorkshops;
    private List<Map<String, String>> allMyBookings;
    private List<Map<String, String>> allMyReviews;

    // FIX: LEFT JOIN sur V_Workshop_Availability pour inclure les workshops sans réservation
    private static final String WS_SQL =
            "SELECT w.id_workshop, w.title_workshop, u.name_user AS instructor, w.date_workshop, " +
                    "w.level, w.price, w.location, " +
                    "COALESCE(v.remaining_spots, w.max_participants) AS remaining_spots, " +
                    "COALESCE(v.availability_status, 'available') AS availability_status " +
                    "FROM Workshop w " +
                    "LEFT JOIN V_Workshop_Availability v ON v.id_workshop = w.id_workshop " +
                    "JOIN Artist a ON w.id_artist = a.id_artist " +
                    "JOIN User_ u ON a.id_user = u.id_user " +
                    "ORDER BY w.date_workshop";

    private static final String EXHIB_SQL =
            "SELECT e.title_exhib, g.name_gallery, e.theme, e.start_date, e.end_date " +
                    "FROM Exhibition e JOIN Gallery g ON e.id_gallery = g.id_gallery ORDER BY e.start_date";

    @FXML
    public void initialize() {
        SessionManager s = SessionManager.getInstance();
        welcomeLabel.setText("Welcome, " + s.getUserName() + "!");
        if (statusLabel != null) statusLabel.setText("Member");

        currentMember = memberService.getAllMembers().stream()
                .filter(m -> m.getId_user() == s.getUserId())
                .findFirst().orElse(null);

        if (currentMember == null) {
            List<Map<String, String>> rows = ViewHelper.query(
                    "SELECT id_member FROM Member_ WHERE id_user=?", s.getUserId());
            if (!rows.isEmpty()) {
                int memberId = Integer.parseInt(rows.get(0).getOrDefault("id_member", "0"));
                currentMember = memberService.getAllMembers().stream()
                        .filter(m -> m.getId_member() == memberId).findFirst().orElse(null);
            }
        }

        DiscoverBuilder.populate(discoverPane);
        setupArtworks();
        setupReviews();
        setupExhibitions();
        setupWorkshops();
        setupBookings();
        setupMyReviews();
        setupProfile();
    }

    // Artworks
    private void setupArtworks() {
        mArtTitleCol.setCellValueFactory(c -> col(c, "title_art"));
        mArtArtistCol.setCellValueFactory(c -> col(c, "artist_name"));
        mArtTypeCol.setCellValueFactory(c -> col(c, "type"));
        mArtMediumCol.setCellValueFactory(c -> col(c, "medium"));         // FIX: medium présent dans la vue corrigée
        mArtYearCol.setCellValueFactory(c -> col(c, "creation_year"));
        mArtPriceCol.setCellValueFactory(c -> col(c, "price"));
        mArtStatusCol.setCellValueFactory(c -> col(c, "status"));
        if (mArtDisciplinesCol != null)                                    // FIX: disciplines (colonne optionnelle)
            mArtDisciplinesCol.setCellValueFactory(c -> col(c, "disciplines"));

        allArtworks = ViewHelper.query("SELECT * FROM V_Artwork_By_Artist");

        ObservableList<String> statuses = FXCollections.observableArrayList("All statuses", "available", "sold", "reserved");
        artworkStatusFilter.setItems(statuses);
        artworkStatusFilter.setValue("All statuses");
        artworkStatusFilter.setOnAction(e -> handleArtworkSearch());

        ObservableList<String> types = FXCollections.observableArrayList("All types");
        allArtworks.stream().map(r -> r.getOrDefault("type", ""))
                .filter(s -> !s.isEmpty()).distinct().sorted().forEach(types::add);
        artworkTypeFilter.setItems(types);
        artworkTypeFilter.setValue("All types");
        artworkTypeFilter.setOnAction(e -> handleArtworkSearch());

        ObservableList<String> mediums = FXCollections.observableArrayList("All mediums");
        allArtworks.stream().map(r -> r.getOrDefault("medium", ""))
                .filter(s -> !s.isEmpty()).distinct().sorted().forEach(mediums::add);
        artworkMediumFilter.setItems(mediums);
        artworkMediumFilter.setValue("All mediums");
        artworkMediumFilter.setOnAction(e -> handleArtworkSearch());

        ObservableList<String> disciplines = FXCollections.observableArrayList("All disciplines");
        ViewHelper.query("SELECT DISTINCT name_discipline FROM Discipline ORDER BY name_discipline")
                .forEach(r -> disciplines.add(r.getOrDefault("name_discipline", "")));
        artworkDisciplineFilter.setItems(disciplines);
        artworkDisciplineFilter.setValue("All disciplines");
        artworkDisciplineFilter.setOnAction(e -> handleArtworkSearch());

        artworkTable.setItems(FXCollections.observableArrayList(allArtworks));
    }

    @FXML private void handleArtworkSearch() {
        String txt    = artworkSearch.getText().toLowerCase();
        String status = artworkStatusFilter.getValue();
        String type   = artworkTypeFilter.getValue();
        String medium = artworkMediumFilter.getValue();
        String disc   = artworkDisciplineFilter.getValue();

        List<String> discIds = null;
        if (disc != null && !disc.equals("All disciplines")) {
            discIds = ViewHelper.query(
                            "SELECT DISTINCT a.id_artwork FROM Artwork a " +
                                    "JOIN Artist ar ON a.id_artist = ar.id_artist " +
                                    "JOIN Artist_Discipline ad ON ar.id_artist = ad.id_artist " +
                                    "JOIN Discipline d ON ad.id_discipline = d.id_discipline " +
                                    "WHERE d.name_discipline=?", disc)
                    .stream().map(r -> r.getOrDefault("id_artwork", "")).toList();
        }
        final List<String> finalDiscIds = discIds;

        ObservableList<Map<String, String>> f = FXCollections.observableArrayList();
        for (Map<String, String> row : allArtworks) {
            boolean t  = row.getOrDefault("title_art", "").toLowerCase().contains(txt)
                    || row.getOrDefault("artist_name", "").toLowerCase().contains(txt);
            boolean s  = "All statuses".equals(status) || row.getOrDefault("status", "").equalsIgnoreCase(status);
            boolean ty = "All types".equals(type)   || row.getOrDefault("type", "").equalsIgnoreCase(type);
            boolean m  = "All mediums".equals(medium) || row.getOrDefault("medium", "").equalsIgnoreCase(medium);
            boolean d  = finalDiscIds == null || finalDiscIds.contains(row.getOrDefault("id_artwork", ""));
            if (t && s && ty && m && d) f.add(row);
        }
        artworkTable.setItems(f);
    }

    @FXML private void handleArtworkReset() {
        artworkSearch.clear();
        artworkStatusFilter.setValue("All statuses");
        artworkTypeFilter.setValue("All types");
        artworkMediumFilter.setValue("All mediums");
        artworkDisciplineFilter.setValue("All disciplines");
        artworkTable.setItems(FXCollections.observableArrayList(allArtworks));
    }

    @FXML private void handleBuyArtwork() {
        Map<String, String> sel = artworkTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select an artwork."); return; }
        if (!"available".equalsIgnoreCase(sel.getOrDefault("status", ""))) {
            warn("This artwork is not available (status: " + sel.get("status") + ")."); return;
        }
        new Alert(Alert.AlertType.CONFIRMATION,
                "Buy \"" + sel.get("title_art") + "\" for $" + sel.get("price") + "?",
                ButtonType.YES, ButtonType.NO).showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try (Connection conn = ConnectionManager.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "UPDATE Artwork SET status='sold' WHERE id_artwork=?")) {
                    ps.setInt(1, Integer.parseInt(sel.getOrDefault("id_artwork", "0")));
                    ps.executeUpdate();
                    allArtworks = ViewHelper.query("SELECT * FROM V_Artwork_By_Artist");
                    artworkTable.setItems(FXCollections.observableArrayList(allArtworks));
                    info("Purchase successful! Artwork is now SOLD.");
                } catch (Exception e) { warn("Purchase failed: " + e.getMessage()); }
            }
        });
    }

    // FIX: bouton "Write Review" directement depuis l'artwork sélectionné dans le tableau
    @FXML private void handleWriteReviewFromArtwork() {
        if (currentMember == null) { warn("Member profile not found for your account."); return; }
        Map<String, String> sel = artworkTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select an artwork to review."); return; }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Write a Review");
        ButtonType saveBtn = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        Label artworkLabel = new Label("Artwork: " + sel.getOrDefault("title_art", ""));
        artworkLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<Integer> ratingF = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        ratingF.setMaxWidth(Double.MAX_VALUE);
        ratingF.setPromptText("Select rating");
        TextArea commentF = new TextArea();
        commentF.setPrefRowCount(3);
        commentF.setWrapText(true);

        VBox box = new VBox(8, artworkLabel, new Label("Rating:"), ratingF, new Label("Comment:"), commentF);
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setPrefWidth(440);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                if (ratingF.getValue() == null) { warn("Please select a rating."); return null; }
                int artworkId = Integer.parseInt(sel.getOrDefault("id_artwork", "0"));
                try (Connection conn = ConnectionManager.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO Review(id_member, id_artwork, rating, comment, review_date) " +
                                     "VALUES (?,?,?,?,NOW())")) {
                    ps.setInt(1, currentMember.getId_member());
                    ps.setInt(2, artworkId);
                    ps.setInt(3, ratingF.getValue());
                    ps.setString(4, commentF.getText());
                    ps.executeUpdate();
                    loadMyReviews();
                    // Rafraîchit aussi l'onglet Reviews (tous)
                    allReviews = ViewHelper.query("SELECT * FROM V_Artwork_By_Review");
                    reviewTable.setItems(FXCollections.observableArrayList(allReviews));
                    info("Review submitted!");
                } catch (SQLException e) { warn("Failed: " + e.getMessage()); }
            }
            return null;
        });
        dialog.showAndWait();
    }

    // Reviews (all)  
    private void setupReviews() {
        mRvArtworkCol.setCellValueFactory(c -> col(c, "title_art"));
        mRvRatingCol.setCellValueFactory(c -> col(c, "rating"));
        mRvCommentCol.setCellValueFactory(c -> col(c, "comment"));
        mRvReviewerCol.setCellValueFactory(c -> col(c, "name_user"));
        mRvDateCol.setCellValueFactory(c -> col(c, "review_date"));

        reviewRatingFilter.setItems(FXCollections.observableArrayList("All ratings", "1", "2", "3", "4", "5"));
        reviewRatingFilter.setValue("All ratings");
        reviewRatingFilter.setOnAction(e -> handleReviewSearch());

        allReviews = ViewHelper.query("SELECT * FROM V_Artwork_By_Review");
        reviewTable.setItems(FXCollections.observableArrayList(allReviews));
    }

    @FXML private void handleReviewSearch() {
        String txt    = reviewSearch.getText().toLowerCase();
        String rating = reviewRatingFilter.getValue();
        ObservableList<Map<String, String>> f = FXCollections.observableArrayList();
        for (Map<String, String> row : allReviews) {
            boolean t = row.getOrDefault("title_art", "").toLowerCase().contains(txt)
                    || row.getOrDefault("name_user", "").toLowerCase().contains(txt)
                    || row.getOrDefault("comment", "").toLowerCase().contains(txt);
            boolean r = "All ratings".equals(rating) || row.getOrDefault("rating", "").equals(rating);
            if (t && r) f.add(row);
        }
        reviewTable.setItems(f);
    }

    @FXML private void handleReviewReset() {
        reviewSearch.clear();
        reviewRatingFilter.setValue("All ratings");
        reviewTable.setItems(FXCollections.observableArrayList(allReviews));
    }

    // Exhibitions
    private void setupExhibitions() {
        mExTitleCol.setCellValueFactory(c -> col(c, "title_exhib"));
        mExGalleryCol.setCellValueFactory(c -> col(c, "name_gallery"));
        mExThemeCol.setCellValueFactory(c -> col(c, "theme"));
        mExStartCol.setCellValueFactory(c -> col(c, "start_date"));
        mExEndCol.setCellValueFactory(c -> col(c, "end_date"));

        ObservableList<String> galleries = FXCollections.observableArrayList("All galleries");
        ViewHelper.query("SELECT name_gallery FROM Gallery ORDER BY name_gallery")
                .forEach(r -> galleries.add(r.getOrDefault("name_gallery", "")));
        exhibitionGalleryFilter.setItems(galleries);
        exhibitionGalleryFilter.setValue("All galleries");
        exhibitionGalleryFilter.setOnAction(e -> handleExhibitionSearch());

        allExhibitions = ViewHelper.query(EXHIB_SQL);
        exhibitionTable.setItems(FXCollections.observableArrayList(allExhibitions));
    }

    @FXML private void handleExhibitionSearch() {
        String txt = exhibitionSearch.getText().toLowerCase();
        String gal = exhibitionGalleryFilter.getValue();
        ObservableList<Map<String, String>> f = FXCollections.observableArrayList();
        for (Map<String, String> row : allExhibitions) {
            boolean t = row.getOrDefault("title_exhib", "").toLowerCase().contains(txt)
                    || row.getOrDefault("theme", "").toLowerCase().contains(txt);
            boolean g = "All galleries".equals(gal) || row.getOrDefault("name_gallery", "").equals(gal);
            if (t && g) f.add(row);
        }
        exhibitionTable.setItems(f);
    }

    @FXML private void handleExhibitionReset() {
        exhibitionSearch.clear();
        exhibitionGalleryFilter.setValue("All galleries");
        exhibitionTable.setItems(FXCollections.observableArrayList(allExhibitions));
    }

    // Workshops
    private void setupWorkshops() {
        mWsTitleCol.setCellValueFactory(c -> col(c, "title_workshop"));
        mWsInstructorCol.setCellValueFactory(c -> col(c, "instructor"));
        mWsDateCol.setCellValueFactory(c -> col(c, "date_workshop"));
        mWsLevelCol.setCellValueFactory(c -> col(c, "level"));
        mWsPriceCol.setCellValueFactory(c -> col(c, "price"));
        mWsSpotsCol.setCellValueFactory(c -> col(c, "remaining_spots"));
        mWsAvailCol.setCellValueFactory(c -> col(c, "availability_status"));

        // FIX: label "All statuses" au lieu de "All"
        workshopAvailFilter.setItems(FXCollections.observableArrayList("All statuses", "available", "full"));
        workshopAvailFilter.setValue("All statuses");
        workshopAvailFilter.setOnAction(e -> handleWorkshopSearch());

        allWorkshops = ViewHelper.query(WS_SQL);
        workshopTable.setItems(FXCollections.observableArrayList(allWorkshops));
    }

    @FXML private void handleWorkshopSearch() {
        String txt   = workshopSearch.getText().toLowerCase();
        String avail = workshopAvailFilter.getValue();
        ObservableList<Map<String, String>> f = FXCollections.observableArrayList();
        for (Map<String, String> row : allWorkshops) {
            boolean t = row.getOrDefault("title_workshop", "").toLowerCase().contains(txt)
                    || row.getOrDefault("location", "").toLowerCase().contains(txt)
                    || row.getOrDefault("instructor", "").toLowerCase().contains(txt);
            // FIX: comparaison avec "All statuses"
            boolean a = "All statuses".equals(avail) || row.getOrDefault("availability_status", "").equalsIgnoreCase(avail);
            if (t && a) f.add(row);
        }
        workshopTable.setItems(f);
    }

    @FXML private void handleWorkshopReset() {
        workshopSearch.clear();
        workshopAvailFilter.setValue("All statuses"); // FIX: cohérence avec la nouvelle valeur par défaut
        workshopTable.setItems(FXCollections.observableArrayList(allWorkshops));
    }

    @FXML private void handleBookWorkshop() {
        if (currentMember == null) { warn("Member profile not found for your account."); return; }
        Map<String, String> sel = workshopTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a workshop."); return; }
        if ("full".equalsIgnoreCase(sel.getOrDefault("availability_status", ""))) {
            warn("This workshop is full."); return;
        }
        int wsId = Integer.parseInt(sel.getOrDefault("id_workshop", "0"));
        List<Map<String, String>> existing = ViewHelper.query(
                "SELECT * FROM Booking WHERE id_member=? AND id_workshop=? AND payment_status!='cancelled'",
                currentMember.getId_member(), wsId);
        if (!existing.isEmpty()) { warn("You already booked this workshop."); return; }

        new Alert(Alert.AlertType.CONFIRMATION,
                "Book \"" + sel.get("title_workshop") + "\" for $" + sel.get("price") + "?",
                ButtonType.YES, ButtonType.NO).showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try (Connection conn = ConnectionManager.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO Booking(id_member, id_workshop, booking_date, payment_status) " +
                                     "VALUES (?,?,NOW(),'pending')")) {
                    ps.setInt(1, currentMember.getId_member());
                    ps.setInt(2, wsId);
                    ps.executeUpdate();
                    // Rafraîchit les bookings ET les workshops (spots restants changent)
                    loadBookings();
                    allWorkshops = ViewHelper.query(WS_SQL);
                    workshopTable.setItems(FXCollections.observableArrayList(allWorkshops));
                    info("Workshop booked! Go to My Bookings to pay.");
                } catch (SQLException e) { warn("Booking failed: " + e.getMessage()); }
            }
        });
    }

    // My Bookings
    private void setupBookings() {
        mBkWorkshopCol.setCellValueFactory(c -> col(c, "title_workshop"));
        mBkDateCol.setCellValueFactory(c -> col(c, "booking_date"));
        mBkStatusCol.setCellValueFactory(c -> col(c, "payment_status"));

        bookingStatusFilter.setItems(FXCollections.observableArrayList("All statuses", "pending", "paid", "cancelled"));
        bookingStatusFilter.setValue("All statuses");
        bookingStatusFilter.setOnAction(e -> handleBookingSearch());

        loadBookings();
    }

    private void loadBookings() {
        if (currentMember == null) return;
        allMyBookings = ViewHelper.query(
                "SELECT w.title_workshop, b.booking_date, b.payment_status, b.id_workshop " +
                        "FROM Booking b JOIN Workshop w ON b.id_workshop = w.id_workshop " +
                        "WHERE b.id_member=? ORDER BY b.booking_date DESC",
                currentMember.getId_member());
        bookingTable.setItems(FXCollections.observableArrayList(allMyBookings));
    }

    @FXML private void handleBookingSearch() {
        String txt    = bookingSearch.getText().toLowerCase();
        String status = bookingStatusFilter.getValue();
        ObservableList<Map<String, String>> f = FXCollections.observableArrayList();
        for (Map<String, String> row : allMyBookings) {
            boolean t = row.getOrDefault("title_workshop", "").toLowerCase().contains(txt);
            boolean s = "All statuses".equals(status) || row.getOrDefault("payment_status", "").equalsIgnoreCase(status);
            if (t && s) f.add(row);
        }
        bookingTable.setItems(f);
    }

    @FXML private void handleBookingReset() {
        bookingSearch.clear();
        bookingStatusFilter.setValue("All statuses");
        loadBookings();
    }

    @FXML private void handlePayBooking() {
        Map<String, String> sel = bookingTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a booking."); return; }
        if ("paid".equalsIgnoreCase(sel.getOrDefault("payment_status", ""))) {
            warn("Already paid."); return;
        }
        if ("cancelled".equalsIgnoreCase(sel.getOrDefault("payment_status", ""))) {
            warn("Cannot pay a cancelled booking."); return;
        }
        int wsId = Integer.parseInt(sel.getOrDefault("id_workshop", "0"));
        new Alert(Alert.AlertType.CONFIRMATION, "Confirm payment?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try (Connection conn = ConnectionManager.getConnection();
                             PreparedStatement ps = conn.prepareStatement(
                                     "UPDATE Booking SET payment_status='paid' WHERE id_member=? AND id_workshop=?")) {
                            ps.setInt(1, currentMember.getId_member());
                            ps.setInt(2, wsId);
                            ps.executeUpdate();
                            loadBookings();
                            info("Payment confirmed!");
                        } catch (SQLException e) { warn("Error: " + e.getMessage()); }
                    }
                });
    }

    @FXML private void handleCancelBooking() {
        Map<String, String> sel = bookingTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a booking."); return; }
        if ("cancelled".equalsIgnoreCase(sel.getOrDefault("payment_status", ""))) {
            warn("Already cancelled."); return;
        }
        int wsId = Integer.parseInt(sel.getOrDefault("id_workshop", "0"));
        new Alert(Alert.AlertType.CONFIRMATION, "Cancel this booking?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try (Connection conn = ConnectionManager.getConnection();
                             PreparedStatement ps = conn.prepareStatement(
                                     "UPDATE Booking SET payment_status='cancelled' WHERE id_member=? AND id_workshop=?")) {
                            ps.setInt(1, currentMember.getId_member());
                            ps.setInt(2, wsId);
                            ps.executeUpdate();
                            // Rafraîchit les bookings ET les workshops (un spot se libère)
                            loadBookings();
                            allWorkshops = ViewHelper.query(WS_SQL);
                            workshopTable.setItems(FXCollections.observableArrayList(allWorkshops));
                        } catch (SQLException e) { warn("Error: " + e.getMessage()); }
                    }
                });
    }

    // My Reviews
    private void setupMyReviews() {
        mMyRvArtworkCol.setCellValueFactory(c -> col(c, "title_art"));
        mMyRvRatingCol.setCellValueFactory(c -> col(c, "rating"));
        mMyRvCommentCol.setCellValueFactory(c -> col(c, "comment"));
        mMyRvDateCol.setCellValueFactory(c -> col(c, "review_date"));
        loadMyReviews();
    }

    private void loadMyReviews() {
        if (currentMember == null) return;
        allMyReviews = ViewHelper.query(
                "SELECT a.id_artwork, a.title_art, r.rating, r.comment, r.review_date " +
                        "FROM Review r JOIN Artwork a ON r.id_artwork = a.id_artwork " +
                        "WHERE r.id_member=? ORDER BY r.review_date DESC",
                currentMember.getId_member());
        myReviewTable.setItems(FXCollections.observableArrayList(allMyReviews));
    }

    @FXML private void handleMyReviewSearch() {
        String txt = myReviewSearch.getText().toLowerCase();
        ObservableList<Map<String, String>> f = FXCollections.observableArrayList();
        for (Map<String, String> row : allMyReviews)
            if (row.getOrDefault("title_art", "").toLowerCase().contains(txt)) f.add(row);
        myReviewTable.setItems(f);
    }

    @FXML private void handleMyReviewReset() {
        myReviewSearch.clear();
        myReviewTable.setItems(FXCollections.observableArrayList(allMyReviews));
    }

    @FXML private void handleWriteReview() {
        if (currentMember == null) { warn("Member profile not found for your account."); return; }
        List<Map<String, String>> artworks = ViewHelper.query(
                "SELECT id_artwork, title_art FROM Artwork ORDER BY title_art");

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Write a Review");
        ButtonType saveBtn = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        ComboBox<String> artworkF = new ComboBox<>(FXCollections.observableArrayList(
                artworks.stream().map(r -> r.get("id_artwork") + " – " + r.get("title_art")).toList()));
        artworkF.setMaxWidth(Double.MAX_VALUE);
        ComboBox<Integer> ratingF = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        ratingF.setMaxWidth(Double.MAX_VALUE);
        TextArea commentF = new TextArea();
        commentF.setPrefRowCount(3);
        commentF.setWrapText(true);

        VBox box = new VBox(8, new Label("Artwork:"), artworkF, new Label("Rating:"), ratingF,
                new Label("Comment:"), commentF);
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setPrefWidth(440);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                if (artworkF.getValue() == null || ratingF.getValue() == null) {
                    warn("Please fill all fields."); return null;
                }
                int artworkId = Integer.parseInt(artworkF.getValue().split(" – ")[0]);
                try (Connection conn = ConnectionManager.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO Review(id_member, id_artwork, rating, comment, review_date) " +
                                     "VALUES (?,?,?,?,NOW())")) {
                    ps.setInt(1, currentMember.getId_member());
                    ps.setInt(2, artworkId);
                    ps.setInt(3, ratingF.getValue());
                    ps.setString(4, commentF.getText());
                    ps.executeUpdate();
                    loadMyReviews();
                    // Rafraîchit aussi l'onglet Reviews (tous)
                    allReviews = ViewHelper.query("SELECT * FROM V_Artwork_By_Review");
                    reviewTable.setItems(FXCollections.observableArrayList(allReviews));
                    info("Review submitted!");
                } catch (SQLException e) { warn("Failed: " + e.getMessage()); }
            }
            return null;
        });
        dialog.showAndWait();
    }

    @FXML private void handleDeleteReview() {
        Map<String, String> sel = myReviewTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a review."); return; }
        new Alert(Alert.AlertType.CONFIRMATION, "Delete this review?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try (Connection conn = ConnectionManager.getConnection();
                             PreparedStatement ps = conn.prepareStatement(
                                     "DELETE FROM Review WHERE id_member=? AND id_artwork=?")) {
                            ps.setInt(1, currentMember.getId_member());
                            ps.setInt(2, Integer.parseInt(sel.getOrDefault("id_artwork", "0")));
                            ps.executeUpdate();
                            loadMyReviews();
                            // Rafraîchit aussi l'onglet Reviews (tous)
                            allReviews = ViewHelper.query("SELECT * FROM V_Artwork_By_Review");
                            reviewTable.setItems(FXCollections.observableArrayList(allReviews));
                        } catch (SQLException e) { warn("Cannot delete: " + e.getMessage()); }
                    }
                });
    }

    // Profile
    private void setupProfile() {
        // FIX: V_User_Info utilise désormais LEFT JOIN → fonctionne pour tous les rôles
        List<Map<String, String>> info = ViewHelper.query(
                "SELECT * FROM V_User_Info WHERE id_user=?",
                SessionManager.getInstance().getUserId());
        if (!info.isEmpty()) {
            Map<String, String> row = info.get(0);
            profileName.setText(row.getOrDefault("name_user", ""));
            profileEmail.setText(row.getOrDefault("email", ""));
            profilePhone.setText(row.getOrDefault("phone", ""));
            profileCity.setText(row.getOrDefault("city", ""));
            profileBirthYear.setText(row.getOrDefault("birth_year", ""));
        }
    }

    @FXML private void handleSaveProfile() {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE User_ SET name_user=?, email=?, phone=?, city=?, birth_year=? WHERE id_user=?")) {
            ps.setString(1, profileName.getText().trim());
            ps.setString(2, profileEmail.getText().trim());
            ps.setString(3, profilePhone.getText().trim());
            ps.setString(4, profileCity.getText().trim());
            try { ps.setInt(5, Integer.parseInt(profileBirthYear.getText().trim())); }
            catch (NumberFormatException e) { ps.setNull(5, java.sql.Types.INTEGER); }
            ps.setInt(6, SessionManager.getInstance().getUserId());
            ps.executeUpdate();
            profileMsg.setStyle("-fx-text-fill: green;");
            profileMsg.setText("Profile updated successfully!");
        } catch (SQLException e) {
            profileMsg.setStyle("-fx-text-fill: red;");
            profileMsg.setText("Error: " + e.getMessage());
        }
    }

    // Logout
    @FXML private void handleLogout() {
        SessionManager.getInstance().logout();
        NavigationHelper.goToViewer((Stage) welcomeLabel.getScene().getWindow());
    }

    // Helpers
    private SimpleStringProperty col(TableColumn.CellDataFeatures<Map<String, String>, String> c, String key) {
        return new SimpleStringProperty(c.getValue().getOrDefault(key, ""));
    }

    private void warn(String msg) { new Alert(Alert.AlertType.WARNING,     msg, ButtonType.OK).showAndWait(); }
    private void info(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
}