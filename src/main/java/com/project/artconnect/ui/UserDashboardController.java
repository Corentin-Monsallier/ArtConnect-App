package com.project.artconnect.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.ArtworkStatus;
import com.project.artconnect.model.Booking;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Member;
import com.project.artconnect.model.PaymentStatusType;
import com.project.artconnect.model.Review;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.persistence.JdbcBookingDao;
import com.project.artconnect.service.*;
import com.project.artconnect.util.ConnectionManager;
import com.project.artconnect.util.ServiceProvider;
import com.project.artconnect.util.SessionManager;
import com.project.artconnect.util.ViewHelper;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class UserDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    @FXML private FlowPane discoverPane;

    @FXML private TableView<Artwork> artworkTable;
    @FXML private TableColumn<Artwork, Integer> artIdCol;
    @FXML private TableColumn<Artwork, String> artTitleCol;
    @FXML private TableColumn<Artwork, String> artTypeCol;
    @FXML private TableColumn<Artwork, String> artMediumCol;
    @FXML private TableColumn<Artwork, Double> artPriceCol;
    @FXML private TableColumn<Artwork, String> artStatusCol;
    @FXML private TableColumn<Artwork, String> artArtistCol;
    @FXML private TextField artworkSearch;
    @FXML private TextField workshopSearch;

    @FXML private TableView<Workshop> workshopTable;
    @FXML private TableColumn<Workshop, Integer> wsIdCol;
    @FXML private TableColumn<Workshop, String> wsTitleCol;
    @FXML private TableColumn<Workshop, String> wsDateCol;
    @FXML private TableColumn<Workshop, String> wsLevelCol;
    @FXML private TableColumn<Workshop, Double> wsPriceCol;
    @FXML private TableColumn<Workshop, String> wsLocationCol;
    @FXML private TableColumn<Workshop, Integer> wsSpotsCol;

    @FXML private TableView<Exhibition> exhibitionTable;
    @FXML private TableColumn<Exhibition, String> exTitleCol;
    @FXML private TableColumn<Exhibition, String> exThemeCol;
    @FXML private TableColumn<Exhibition, String> exStartCol;
    @FXML private TableColumn<Exhibition, String> exEndCol;
    @FXML private TableColumn<Exhibition, String> exGalleryCol;
    @FXML private TableColumn<Exhibition, String> exArtworksCol;

    @FXML private TableView<Review> reviewTable;
    @FXML private TableColumn<Review, String> rvArtworkCol;
    @FXML private TableColumn<Review, Integer> rvRatingCol;
    @FXML private TableColumn<Review, String> rvCommentCol;
    @FXML private TableColumn<Review, String> rvDateCol;

    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, String> bkWorkshopCol;
    @FXML private TableColumn<Booking, String> bkDateCol;
    @FXML private TableColumn<Booking, String> bkStatusCol;

    @FXML private TableView<Artwork> artworkPurchaseTable;
    @FXML private TableColumn<Artwork, String> apTitleCol;
    @FXML private TableColumn<Artwork, Double> apPriceCol;
    @FXML private TableColumn<Artwork, String> apStatusCol;

    private final ArtworkService artworkService = ServiceProvider.getArtworkService();
    private final WorkshopService workshopService = ServiceProvider.getWorkshopService();
    private final ExhibitionService exhibitionService = ServiceProvider.getExhibitionService();
    private final ReviewService reviewService = ServiceProvider.getReviewService();
    private final BookingService bookingService = ServiceProvider.getBookingService();
    private final GalleryService galleryService = ServiceProvider.getGalleryService();
    private final ArtistService artistService = ServiceProvider.getArtistService();
    private final MemberService memberService = ServiceProvider.getMemberService();
    private final JdbcBookingDao bookingDao = new JdbcBookingDao();

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        SessionManager session = SessionManager.getInstance();
        welcomeLabel.setText("Welcome, " + session.getUserName() + "!");
        roleLabel.setText("Role: " + session.getRole().name());

        setupArtworks();
        setupWorkshops();
        setupExhibitions();
        setupReviews();
        setupBookings();
        setupArtworkPurchases();
        setupDiscover();
    }

    private void setupDiscover() {
        exhibitionService.getAllExhibitions().stream().limit(3).forEach(e -> {
            VBox card = makeCard("#e3f2fd", "#2196f3");
            card.getChildren().addAll(
                    bold("FEATURED EXHIBITION"),
                    bold(e.getTitle_exhib()),
                    new Label("Theme: " + e.getTheme()),
                    new Label("Gallery ID: " + e.getId_gallery())
            );
            discoverPane.getChildren().add(card);
        });
        workshopService.getAllWorkshops().stream().limit(3).forEach(w -> {
            VBox card = makeCard("#f1f8e9", "#4caf50");
            card.getChildren().addAll(
                    bold("UPCOMING WORKSHOP"),
                    bold(w.getTitle_workshop()),
                    new Label("Level: " + w.getLevel()),
                    new Label("Price: $" + w.getPrice())
            );
            discoverPane.getChildren().add(card);
        });
    }

    private VBox makeCard(String bg, String border) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color:" + bg + ";-fx-border-color:" + border + ";-fx-border-radius:5;-fx-background-radius:5;");
        card.setPrefWidth(240);
        return card;
    }

    private Label bold(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold;");
        return l;
    }

    private void setupArtworks() {
        artIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId_artwork()).asObject());
        artTitleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle_art()));
        artTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        artMediumCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMedium()));
        artPriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());
        artStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));
        artArtistCol.setCellValueFactory(c -> {
            var artist = artistService.getAllArtists().stream()
                    .filter(a -> a.getId_artist() == c.getValue().getId_artist()).findFirst().orElse(null);
            return new SimpleStringProperty(artist == null ? "" : artist.getName_user());
        });
        loadArtworks();
    }

    private void loadArtworks() {
        artworkTable.setItems(FXCollections.observableArrayList(artworkService.getAllArtworks()));
    }

    @FXML private void handleArtworkSearch() {
        String txt = artworkSearch.getText().toLowerCase();
        ObservableList<Artwork> filtered = FXCollections.observableArrayList();
        for (Artwork a : artworkService.getAllArtworks())
            if (a.getTitle_art() != null && a.getTitle_art().toLowerCase().contains(txt)) filtered.add(a);
        artworkTable.setItems(filtered);
    }

    @FXML private void handleArtworkReset() { artworkSearch.clear(); loadArtworks(); }

    @FXML private void handleReserveArtwork() {
        Artwork selected = artworkTable.getSelectionModel().getSelectedItem();
        if (selected == null) { warn("Please select an artwork to reserve."); return; }
        if (selected.getStatus() != ArtworkStatus.AVAILABLE) {
            warn("This artwork is not available for reservation."); return;
        }

        Member member = getCurrentMember();
        if (member == null) { warn("Your account is not linked to a member profile. Only members can reserve artworks."); return; }

        boolean alreadyReserved = isArtworkReservedByMember(selected.getId_artwork(), member.getId_member());
        if (alreadyReserved) { warn("You already have a reservation for this artwork."); return; }

        new Alert(Alert.AlertType.CONFIRMATION,
                "Reserve \"" + selected.getTitle_art() + "\" for $" + selected.getPrice() + "?",
                ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try {
                            reserveArtwork(selected.getId_artwork(), member.getId_member());
                            selected.setStatus(ArtworkStatus.RESERVED);
                            artworkService.updateArtwork(selected);
                            loadArtworks();
                            loadArtworkPurchases();
                            info("Artwork reserved! You can pay in My Purchases.");
                        } catch (Exception e) { warn("Cannot reserve: " + e.getMessage()); }
                    }
                });
    }

    private boolean isArtworkReservedByMember(int artworkId, int memberId) {
        String sql = "SELECT COUNT(*) FROM Artwork_Reservation WHERE id_artwork=? AND id_member=? AND payment_status != 'cancelled'";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, artworkId);
            ps.setInt(2, memberId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private void reserveArtwork(int artworkId, int memberId) throws SQLException {
        String sql = "INSERT INTO Artwork_Reservation(id_artwork, id_member, reservation_date, payment_status) VALUES (?, ?, ?, 'pending')";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, artworkId);
            ps.setInt(2, memberId);
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        }
    }

    private void setupWorkshops() {
        wsIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId_workshop()).asObject());
        wsTitleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle_workshop()));
        wsDateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDate_workshop() == null ? "" : c.getValue().getDate_workshop().format(DT_FMT)));
        wsLevelCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLevel()));
        wsPriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());
        wsLocationCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLocation()));
        wsSpotsCol.setCellValueFactory(c -> {
            int max = c.getValue().getMax_participants();
            long booked = bookingService.getAllBookings().stream()
                    .filter(b -> b.getId_workshop() == c.getValue().getId_workshop()
                            && b.getPayment_status() != PaymentStatusType.CANCELLED)
                    .count();
            return new SimpleIntegerProperty((int)(max - booked)).asObject();
        });
        loadWorkshops();
    }

    private void loadWorkshops() {
        workshopTable.setItems(FXCollections.observableArrayList(workshopService.getAllWorkshops()));
    }

    @FXML private void handleWorkshopSearch() {
        String txt = workshopSearch.getText().toLowerCase();
        ObservableList<Workshop> filtered = FXCollections.observableArrayList();
        for (Workshop w : workshopService.getAllWorkshops())
            if (w.getTitle_workshop() != null && w.getTitle_workshop().toLowerCase().contains(txt)) filtered.add(w);
        workshopTable.setItems(filtered);
    }

    @FXML private void handleWorkshopReset() { workshopSearch.clear(); loadWorkshops(); }

    @FXML private void handleBookWorkshop() {
        Workshop selected = workshopTable.getSelectionModel().getSelectedItem();
        if (selected == null) { warn("Please select a workshop to book."); return; }

        Member member = getCurrentMember();
        if (member == null) { warn("Your account is not linked to a member profile. Only members can book workshops."); return; }

        boolean alreadyBooked = bookingService.getAllBookings().stream()
                .anyMatch(b -> b.getId_member() == member.getId_member()
                        && b.getId_workshop() == selected.getId_workshop()
                        && b.getPayment_status() != PaymentStatusType.CANCELLED);
        if (alreadyBooked) { warn("You already have a booking for this workshop."); return; }

        new Alert(Alert.AlertType.CONFIRMATION,
                "Book \"" + selected.getTitle_workshop() + "\" for $" + selected.getPrice() + "?",
                ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        Booking b = new Booking();
                        b.setId_member(member.getId_member());
                        b.setId_workshop(selected.getId_workshop());
                        b.setBooking_date(LocalDateTime.now());
                        b.setPayment_status(PaymentStatusType.PENDING);
                        try {
                            bookingService.createBooking(b);
                            loadBookings();
                            info("Booking confirmed! Payment status: PENDING.");
                        } catch (Exception e) { warn("Cannot book: " + e.getMessage()); }
                    }
                });
    }

    private void setupExhibitions() {
        exTitleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle_exhib()));
        exThemeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTheme()));
        exStartCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getStart_date())));
        exEndCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getEnd_date())));
        exGalleryCol.setCellValueFactory(c -> {
            var g = galleryService.getAllGalleries().stream()
                    .filter(gl -> gl.getId_gallery() == c.getValue().getId_gallery()).findFirst().orElse(null);
            return new SimpleStringProperty(g == null ? "" : g.getName_gallery());
        });
        if (exArtworksCol != null) {
            exArtworksCol.setCellValueFactory(c -> {
                List<Map<String, String>> rows = ViewHelper.query(
                        "SELECT a.title_art FROM Exhibition_Artwork ea " +
                                "JOIN Artwork a ON ea.id_artwork = a.id_artwork " +
                                "WHERE ea.id_exhibition=? ORDER BY a.title_art",
                        c.getValue().getId_exhibition());
                String titles = rows.stream()
                        .map(r -> r.getOrDefault("title_art", ""))
                        .filter(s -> !s.isBlank())
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");
                return new SimpleStringProperty(titles);
            });
        }
        exhibitionTable.setItems(FXCollections.observableArrayList(exhibitionService.getAllExhibitions()));
    }

    private void setupReviews() {
        rvArtworkCol.setCellValueFactory(c -> {
            var a = artworkService.getAllArtworks().stream()
                    .filter(art -> art.getId_artwork() == c.getValue().getId_artwork()).findFirst().orElse(null);
            return new SimpleStringProperty(a == null ? "" : a.getTitle_art());
        });
        rvRatingCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getRating()).asObject());
        rvCommentCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getComment()));
        rvDateCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getReview_date())));
        loadReviews();
    }

    private void loadReviews() {
        Member member = getCurrentMember();
        if (member == null) return;
        reviewTable.setItems(FXCollections.observableArrayList(
                reviewService.getReviewsByMember(member.getId_member())));
    }

    @FXML private void handleWriteReview() {
        Member member = getCurrentMember();
        if (member == null) { warn("Only members can write reviews."); return; }

        Dialog<Review> dialog = new Dialog<>();
        dialog.setTitle("Write a Review");
        ButtonType saveBtn = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        List<Artwork> artworks = artworkService.getAllArtworks();
        ComboBox<Artwork> artworkF = new ComboBox<>(FXCollections.observableArrayList(artworks));
        artworkF.setConverter(new StringConverter<>() {
            public String toString(Artwork a) { return a == null ? "" : a.getTitle_art(); }
            public Artwork fromString(String s) { return null; }
        });
        artworkF.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Integer> ratingF = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        ratingF.setMaxWidth(Double.MAX_VALUE);
        TextArea commentF = new TextArea();
        commentF.setPrefRowCount(3);
        commentF.setWrapText(true);

        VBox box = new VBox(8,
                new Label("Artwork:"), artworkF,
                new Label("Rating:"), ratingF,
                new Label("Comment:"), commentF
        );
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setPrefWidth(440);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                if (artworkF.getValue() == null || ratingF.getValue() == null) { warn("Please fill all fields."); return null; }
                Review r = new Review();
                r.setId_member(member.getId_member());
                r.setId_artwork(artworkF.getValue().getId_artwork());
                r.setRating(ratingF.getValue());
                r.setComment(commentF.getText());
                r.setReview_date(LocalDateTime.now());
                return r;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(r -> {
            try { reviewService.createReview(r); loadReviews(); info("Review submitted!"); }
            catch (Exception e) { warn("Cannot submit review: " + e.getMessage()); }
        });
    }

    @FXML private void handleDeleteReview() {
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

    private void setupBookings() {
        bkWorkshopCol.setCellValueFactory(c -> {
            var w = workshopService.getAllWorkshops().stream()
                    .filter(ws -> ws.getId_workshop() == c.getValue().getId_workshop()).findFirst().orElse(null);
            return new SimpleStringProperty(w == null ? "" : w.getTitle_workshop());
        });
        bkDateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getBooking_date() == null ? "" : c.getValue().getBooking_date().format(DT_FMT)));
        bkStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPayment_status().name()));
        loadBookings();
    }

    private void loadBookings() {
        Member member = getCurrentMember();
        if (member == null) return;
        bookingTable.setItems(FXCollections.observableArrayList(
                bookingService.getBookingsByMemberId(member.getId_member())));
    }

    @FXML private void handlePayBooking() {
        Booking sel = bookingTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a booking to pay."); return; }
        if (sel.getPayment_status() == PaymentStatusType.PAID) { warn("This booking is already paid."); return; }
        if (sel.getPayment_status() == PaymentStatusType.CANCELLED) { warn("This booking is cancelled."); return; }
        new Alert(Alert.AlertType.CONFIRMATION, "Confirm payment for this booking?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        sel.setPayment_status(PaymentStatusType.PAID);
                        try { bookingService.updateBooking(sel); loadBookings(); info("Payment confirmed!"); }
                        catch (Exception e) { warn("Cannot process payment: " + e.getMessage()); }
                    }
                });
    }

    @FXML private void handleCancelBooking() {
        Booking sel = bookingTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a booking to cancel."); return; }
        if (sel.getPayment_status() == PaymentStatusType.CANCELLED) { warn("This booking is already cancelled."); return; }
        new Alert(Alert.AlertType.CONFIRMATION, "Cancel this booking?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        sel.setPayment_status(PaymentStatusType.CANCELLED);
                        try { bookingService.updateBooking(sel); loadBookings(); }
                        catch (Exception e) { warn("Cannot cancel: " + e.getMessage()); }
                    }
                });
    }

    private void setupArtworkPurchases() {
        apTitleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle_art()));
        apPriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());
        apStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));
        loadArtworkPurchases();
    }

    private void loadArtworkPurchases() {
        Member member = getCurrentMember();
        if (member == null) return;
        List<Map<String, String>> rows = ViewHelper.query(
                "SELECT a.id_artwork, a.title_art, a.price, ar.payment_status " +
                        "FROM Artwork_Reservation ar " +
                        "JOIN Artwork a ON ar.id_artwork = a.id_artwork " +
                        "WHERE ar.id_member=? AND ar.payment_status != 'cancelled' ORDER BY a.title_art",
                member.getId_member());
        ObservableList<Artwork> list = FXCollections.observableArrayList();
        for (Map<String, String> row : rows) {
            Artwork a = new Artwork();
            try { a.setId_artwork(Integer.parseInt(row.getOrDefault("id_artwork", "0"))); } catch (NumberFormatException ignored) {}
            a.setTitle_art(row.getOrDefault("title_art", ""));
            try { a.setPrice(Double.parseDouble(row.getOrDefault("price", "0"))); } catch (NumberFormatException ignored) {}
            String ps = row.getOrDefault("payment_status", "pending").toUpperCase();
            if (ps.equals("PAID")) a.setStatus(ArtworkStatus.SOLD);
            else a.setStatus(ArtworkStatus.RESERVED);
            list.add(a);
        }
        artworkPurchaseTable.setItems(list);
    }

    @FXML private void handlePayArtwork() {
        Artwork sel = artworkPurchaseTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select an artwork reservation to pay."); return; }
        if (sel.getStatus() == ArtworkStatus.SOLD) { warn("This artwork is already paid."); return; }
        if (sel.getStatus() != ArtworkStatus.RESERVED) { warn("This reservation cannot be paid."); return; }

        Member member = getCurrentMember();
        if (member == null) return;

        new Alert(Alert.AlertType.CONFIRMATION,
                "Confirm payment for \"" + sel.getTitle_art() + "\" ($" + sel.getPrice() + ")?",
                ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try {
                            updateArtworkReservationStatus(sel.getId_artwork(), member.getId_member(), "paid");
                            sel.setStatus(ArtworkStatus.SOLD);
                            artworkService.updateArtwork(sel);
                            loadArtworkPurchases();
                            loadArtworks();
                            info("Payment confirmed! Artwork is now yours.");
                        } catch (Exception e) { warn("Cannot process payment: " + e.getMessage()); }
                    }
                });
    }

    @FXML private void handleCancelArtwork() {
        Artwork sel = artworkPurchaseTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select an artwork reservation to cancel."); return; }
        if (sel.getStatus() == ArtworkStatus.SOLD) { warn("This artwork is already paid and cannot be cancelled."); return; }

        Member member = getCurrentMember();
        if (member == null) return;

        new Alert(Alert.AlertType.CONFIRMATION,
                "Cancel reservation for \"" + sel.getTitle_art() + "\"?",
                ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try {
                            updateArtworkReservationStatus(sel.getId_artwork(), member.getId_member(), "cancelled");
                            sel.setStatus(ArtworkStatus.AVAILABLE);
                            artworkService.updateArtwork(sel);
                            loadArtworkPurchases();
                            loadArtworks();
                        } catch (Exception e) { warn("Cannot cancel: " + e.getMessage()); }
                    }
                });
    }

    private void updateArtworkReservationStatus(int artworkId, int memberId, String paymentStatus) throws SQLException {
        String sql = "UPDATE Artwork_Reservation SET payment_status=? WHERE id_artwork=? AND id_member=? AND payment_status != 'cancelled'";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paymentStatus);
            ps.setInt(2, artworkId);
            ps.setInt(3, memberId);
            ps.executeUpdate();
        }
    }

    @FXML private void handleLogout() {
        SessionManager.getInstance().logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/artconnect/ui/LoginView.fxml"));
            Scene scene = new Scene(loader.load(), 600, 500);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("ArtConnect - Login");
            stage.setScene(scene);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Member getCurrentMember() {
        int userId = SessionManager.getInstance().getUserId();
        return memberService.getAllMembers().stream()
                .filter(m -> m.getId_user() == userId).findFirst().orElse(null);
    }

    private void warn(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }
    private void info(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
}