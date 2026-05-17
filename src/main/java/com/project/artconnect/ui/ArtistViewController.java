package com.project.artconnect.ui;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import com.project.artconnect.model.*;
import com.project.artconnect.service.*;
import com.project.artconnect.util.*;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ArtistViewController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;

    // My Artworks
    @FXML private TableView<Artwork> myArtworkTable;
    @FXML private TableColumn<Artwork, String> aArtTitleCol;
    @FXML private TableColumn<Artwork, String> aArtTypeCol;
    @FXML private TableColumn<Artwork, String> aArtMediumCol;
    @FXML private TableColumn<Artwork, Double> aArtPriceCol;
    @FXML private TableColumn<Artwork, String> aArtStatusCol;
    @FXML private TableColumn<Artwork, Integer> aArtYearCol;
    @FXML private TextField artworkSearch;
    @FXML private ComboBox<String> artworkStatusFilter;
    @FXML private Label avgPriceLabel;

    // Reviews on my artworks
    @FXML private TableView<Map<String, String>> myReviewTable;
    @FXML private TableColumn<Map<String, String>, String> aRvArtworkCol;
    @FXML private TableColumn<Map<String, String>, String> aRvRatingCol;
    @FXML private TableColumn<Map<String, String>, String> aRvCommentCol;
    @FXML private TableColumn<Map<String, String>, String> aRvDateCol;
    @FXML private TextField reviewSearch;

    // My Workshops
    @FXML private TableView<Workshop> myWorkshopTable;
    @FXML private TableColumn<Workshop, String> aWsTitleCol;
    @FXML private TableColumn<Workshop, String> aWsDateCol;
    @FXML private TableColumn<Workshop, String> aWsLevelCol;
    @FXML private TableColumn<Workshop, Double> aWsPriceCol;
    @FXML private TableColumn<Workshop, Integer> aWsMaxCol;
    @FXML private TableColumn<Workshop, Integer> aWsSpotsCol;
    @FXML private TableColumn<Workshop, String> aWsAvailCol;
    @FXML private TableColumn<Workshop, String> aWsLocationCol;
    @FXML private TextField workshopSearch;

    // Participants
    @FXML private TableView<Map<String, String>> participantTable;
    @FXML private TableColumn<Map<String, String>, String> aPtNameCol;
    @FXML private TableColumn<Map<String, String>, String> aPtStatusCol;
    @FXML private TableColumn<Map<String, String>, String> aPtDateCol;

    // Stats
    @FXML private Label statWorkshopCount;
    @FXML private Label statAvgPrice;
    @FXML private Label statArtworkCount;
    @FXML private TableView<Map<String, String>> myExhibitionTable;
    @FXML private TableColumn<Map<String, String>, String> aExTitleCol;
    @FXML private TableColumn<Map<String, String>, String> aExArtworkCountCol;

    // Profile
    @FXML private TextField profileName;
    @FXML private TextField profileEmail;
    @FXML private TextField profilePhone;
    @FXML private TextField profileCity;
    @FXML private TextArea profileBio;
    @FXML private TextField profileWebsite;
    @FXML private CheckBox profileActive;
    @FXML private Label profileMsg;

    // Socials
    @FXML private TableView<Map<String, String>> socialTable;
    @FXML private TableColumn<Map<String, String>, String> aSocialPlatformCol;
    @FXML private TableColumn<Map<String, String>, String> aSocialLinkCol;

    private final ArtworkService artworkService   = ServiceProvider.getArtworkService();
    private final WorkshopService workshopService = ServiceProvider.getWorkshopService();
    private final BookingService bookingService   = ServiceProvider.getBookingService();
    private final ArtistService artistService     = ServiceProvider.getArtistService();

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Artist currentArtist;
    private List<Artwork> allMyArtworks;
    private List<Map<String, String>> allMyReviews;
    private List<Workshop> allMyWorkshops;

    @FXML
    public void initialize() {
        SessionManager s = SessionManager.getInstance();
        welcomeLabel.setText("Welcome, " + s.getUserName() + "!");

        currentArtist = artistService.getAllArtists().stream()
                .filter(a -> a.getId_user() == s.getUserId()).findFirst().orElse(null);

        boolean isInstructor = currentArtist != null &&
                !workshopService.getWorkshopsByArtist(currentArtist.getId_artist()).isEmpty();
        roleLabel.setText(isInstructor ? "ARTIST / INSTRUCTOR" : "ARTIST");

        setupMyArtworks();
        setupMyReviews();
        setupMyWorkshops();
        setupStats();
        setupProfile();
        setupSocials();

        // Load participants when a workshop is selected
        myWorkshopTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) loadParticipants(sel.getId_workshop());
            else participantTable.getItems().clear();
        });
    }

    //   My Artworks  
    private void setupMyArtworks() {
        aArtTitleCol.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getTitle_art())));
        aArtTypeCol.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getType())));
        aArtMediumCol.setCellValueFactory(c -> new SimpleStringProperty(safe(c.getValue().getMedium())));
        aArtPriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());
        aArtStatusCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getStatus() == null ? "" : c.getValue().getStatus().name()));
        aArtYearCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCreation_year()).asObject());

        artworkStatusFilter.setItems(FXCollections.observableArrayList("All statuses","AVAILABLE","SOLD","RESERVED"));
        artworkStatusFilter.setValue("All statuses");
        artworkStatusFilter.setOnAction(e -> handleArtworkSearch());

        loadMyArtworks();
    }

    private void loadMyArtworks() {
        if (currentArtist == null) return;
        allMyArtworks = artworkService.getArtworksByArtist(currentArtist);
        myArtworkTable.setItems(FXCollections.observableArrayList(allMyArtworks));
        updateAvgPrice();
    }

    private void updateAvgPrice() {
        if (currentArtist == null || allMyArtworks == null || allMyArtworks.isEmpty()) {
            avgPriceLabel.setText("Average price of your artworks: N/A");
            return;
        }
        List<Map<String, String>> rows = ViewHelper.query(
                "SELECT avg_price, artwork_count FROM V_Avg_Artwork_Price WHERE id_artist=?",
                currentArtist.getId_artist());
        if (!rows.isEmpty()) {
            String avg   = rows.get(0).getOrDefault("avg_price","0");
            String count = rows.get(0).getOrDefault("artwork_count","0");
            avgPriceLabel.setText("Average price: $" + avg + "  |  Total artworks: " + count);
        }
    }

    @FXML private void handleArtworkSearch() {
        String txt    = artworkSearch.getText().toLowerCase();
        String status = artworkStatusFilter.getValue();
        ObservableList<Artwork> f = FXCollections.observableArrayList();
        for (Artwork a : allMyArtworks) {
            boolean t = safe(a.getTitle_art()).toLowerCase().contains(txt);
            boolean s = "All statuses".equals(status) || (a.getStatus() != null && a.getStatus().name().equalsIgnoreCase(status));
            if (t && s) f.add(a);
        }
        myArtworkTable.setItems(f);
    }

    @FXML private void handleArtworkReset() {
        artworkSearch.clear(); artworkStatusFilter.setValue("All statuses");
        myArtworkTable.setItems(FXCollections.observableArrayList(allMyArtworks));
    }

    @FXML private void handleAddArtwork() {
        if (currentArtist == null) { warn("Artist profile not found."); return; }
        buildArtworkDialog(null).showAndWait().ifPresent(a -> {
            a.setId_artist(currentArtist.getId_artist());
            try { artworkService.createArtwork(a); loadMyArtworks(); loadMyReviews(); loadStats(); }
            catch (Exception e) { warn("Failed: " + e.getMessage()); }
        });
    }

    @FXML private void handleEditArtwork() {
        Artwork sel = myArtworkTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select an artwork to edit."); return; }
        buildArtworkDialog(sel).showAndWait().ifPresent(a -> {
            try { artworkService.updateArtwork(a); loadMyArtworks(); loadStats(); }
            catch (Exception e) { warn("Failed: " + e.getMessage()); }
        });
    }

    @FXML private void handleDeleteArtwork() {
        Artwork sel = myArtworkTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select an artwork."); return; }
        new Alert(Alert.AlertType.CONFIRMATION, "Delete \"" + sel.getTitle_art() + "\"?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try { artworkService.deleteArtwork(sel.getTitle_art()); loadMyArtworks(); loadMyReviews(); loadStats(); }
                        catch (Exception e) { warn("Cannot delete: " + e.getMessage()); }
                    }
                });
    }

    private Dialog<Artwork> buildArtworkDialog(Artwork existing) {
        Dialog<Artwork> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Artwork" : "Edit Artwork");
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        TextField titleF = new TextField(existing != null ? safe(existing.getTitle_art()) : "");
        TextField yearF  = new TextField(existing != null ? String.valueOf(existing.getCreation_year()) : "");
        TextField typeF  = new TextField(existing != null ? safe(existing.getType()) : "");
        TextField medF   = new TextField(existing != null ? safe(existing.getMedium()) : "");
        TextField dimsF  = new TextField(existing != null ? safe(existing.getDimensions()) : "");
        TextField descF  = new TextField(existing != null ? safe(existing.getDescription()) : "");
        TextField priceF = new TextField(existing != null ? String.valueOf(existing.getPrice()) : "");
        ComboBox<ArtworkStatus> statusF = new ComboBox<>(FXCollections.observableArrayList(ArtworkStatus.values()));
        if (existing != null) statusF.setValue(existing.getStatus());
        statusF.setMaxWidth(Double.MAX_VALUE);
        VBox box = new VBox(8,
                new Label("Title:"), titleF, new Label("Year:"), yearF,
                new Label("Type:"), typeF, new Label("Medium:"), medF,
                new Label("Dimensions:"), dimsF, new Label("Description:"), descF,
                new Label("Price:"), priceF, new Label("Status:"), statusF);
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box); dialog.getDialogPane().setPrefWidth(420);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Artwork a = existing != null ? existing : new Artwork();
                a.setTitle_art(titleF.getText());
                try { a.setCreation_year(Integer.parseInt(yearF.getText())); } catch (NumberFormatException ignored) {}
                a.setType(typeF.getText()); a.setMedium(medF.getText());
                a.setDimensions(dimsF.getText()); a.setDescription(descF.getText());
                try { a.setPrice(Double.parseDouble(priceF.getText())); } catch (NumberFormatException ignored) {}
                a.setStatus(statusF.getValue());
                return a;
            }
            return null;
        });
        return dialog;
    }

    //   Reviews on my artworks  
    private void setupMyReviews() {
        aRvArtworkCol.setCellValueFactory(c -> scol(c, "title_art"));
        aRvRatingCol.setCellValueFactory(c -> scol(c, "rating"));
        aRvCommentCol.setCellValueFactory(c -> scol(c, "comment"));
        aRvDateCol.setCellValueFactory(c -> scol(c, "review_date"));
        loadMyReviews();
    }

    private void loadMyReviews() {
        if (currentArtist == null) return;
        allMyReviews = ViewHelper.query(
                "SELECT a.title_art, r.rating, r.comment, r.review_date " +
                        "FROM V_Review_By_Artwork r " +
                        "JOIN Artwork a ON r.id_artwork = a.id_artwork " +
                        "WHERE a.id_artist=? ORDER BY r.review_date DESC",
                currentArtist.getId_artist());
        myReviewTable.setItems(FXCollections.observableArrayList(allMyReviews));
    }

    @FXML private void handleReviewSearch() {
        String txt = reviewSearch.getText().toLowerCase();
        ObservableList<Map<String, String>> f = FXCollections.observableArrayList();
        for (Map<String, String> row : allMyReviews)
            if (row.getOrDefault("title_art","").toLowerCase().contains(txt)
                    || row.getOrDefault("comment","").toLowerCase().contains(txt)) f.add(row);
        myReviewTable.setItems(f);
    }

    @FXML private void handleReviewReset() {
        reviewSearch.clear();
        myReviewTable.setItems(FXCollections.observableArrayList(allMyReviews));
    }

    //   My Workshops  
    private void setupMyWorkshops() {
        aWsTitleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle_workshop()));
        aWsDateCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDate_workshop() == null ? "" : c.getValue().getDate_workshop().format(DT_FMT)));
        aWsLevelCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLevel()));
        aWsPriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());
        aWsMaxCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMax_participants()).asObject());
        aWsSpotsCol.setCellValueFactory(c -> {
            int max = c.getValue().getMax_participants();
            long booked = bookingService.getAllBookings().stream()
                    .filter(b -> b.getId_workshop() == c.getValue().getId_workshop()
                            && b.getPayment_status() != PaymentStatusType.CANCELLED).count();
            return new SimpleIntegerProperty((int)(max - booked)).asObject();
        });
        aWsAvailCol.setCellValueFactory(c -> {
            int max = c.getValue().getMax_participants();
            long booked = bookingService.getAllBookings().stream()
                    .filter(b -> b.getId_workshop() == c.getValue().getId_workshop()
                            && b.getPayment_status() != PaymentStatusType.CANCELLED).count();
            return new SimpleStringProperty(booked >= max ? "full" : "available");
        });
        aWsLocationCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLocation()));

        // Participants table setup
        aPtNameCol.setCellValueFactory(c -> scol(c, "participant_name"));
        aPtStatusCol.setCellValueFactory(c -> scol(c, "payment_status"));
        aPtDateCol.setCellValueFactory(c -> scol(c, "booking_date"));

        loadMyWorkshops();
    }

    private void loadMyWorkshops() {
        if (currentArtist == null) return;
        allMyWorkshops = workshopService.getWorkshopsByArtist(currentArtist.getId_artist());
        myWorkshopTable.setItems(FXCollections.observableArrayList(allMyWorkshops));
        boolean isInstructor = !allMyWorkshops.isEmpty();
        roleLabel.setText(isInstructor ? "ARTIST / INSTRUCTOR" : "ARTIST");
    }

    private void loadParticipants(int workshopId) {
        participantTable.setItems(FXCollections.observableArrayList(
                ViewHelper.query(
                        "SELECT participant_name, payment_status, booking_date " +
                                "FROM V_Workshop_Participants WHERE id_workshop=?", workshopId)));
    }

    @FXML private void handleWorkshopSearch() {
        String txt = workshopSearch.getText().toLowerCase();
        ObservableList<Workshop> f = FXCollections.observableArrayList();
        for (Workshop w : allMyWorkshops)
            if (safe(w.getTitle_workshop()).toLowerCase().contains(txt)
                    || safe(w.getLocation()).toLowerCase().contains(txt)) f.add(w);
        myWorkshopTable.setItems(f);
    }

    @FXML private void handleWorkshopReset() {
        workshopSearch.clear();
        myWorkshopTable.setItems(FXCollections.observableArrayList(allMyWorkshops));
    }

    @FXML private void handleAddWorkshop() {
        if (currentArtist == null) { warn("Artist profile not found."); return; }
        buildWorkshopDialog(null).showAndWait().ifPresent(w -> {
            w.setId_artist(currentArtist.getId_artist());
            try { workshopService.createWorkshop(w); loadMyWorkshops(); loadStats(); }
            catch (Exception e) { warn("Failed: " + e.getMessage()); }
        });
    }

    @FXML private void handleEditWorkshop() {
        Workshop sel = myWorkshopTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a workshop."); return; }
        buildWorkshopDialog(sel).showAndWait().ifPresent(w -> {
            try { workshopService.updateWorkshop(w); loadMyWorkshops(); loadStats(); }
            catch (Exception e) { warn("Failed: " + e.getMessage()); }
        });
    }

    @FXML private void handleDeleteWorkshop() {
        Workshop sel = myWorkshopTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a workshop."); return; }
        new Alert(Alert.AlertType.CONFIRMATION, "Delete \"" + sel.getTitle_workshop() + "\"?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try { workshopService.deleteWorkshop(sel.getId_workshop()); loadMyWorkshops(); loadStats(); }
                        catch (Exception e) { warn("Cannot delete: " + e.getMessage()); }
                    }
                });
    }

    private Dialog<Workshop> buildWorkshopDialog(Workshop existing) {
        Dialog<Workshop> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Create Workshop" : "Edit Workshop");
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        TextField titleF    = new TextField(existing != null ? existing.getTitle_workshop() : "");
        TextField dateF     = new TextField(existing != null && existing.getDate_workshop() != null
                ? existing.getDate_workshop().format(DT_FMT) : "");
        dateF.setPromptText("yyyy-MM-dd HH:mm");
        TextField durationF = new TextField(existing != null ? String.valueOf(existing.getDuration_minutes()) : "");
        TextField maxF      = new TextField(existing != null ? String.valueOf(existing.getMax_participants()) : "");
        TextField priceF    = new TextField(existing != null ? String.valueOf(existing.getPrice()) : "");
        TextField levelF    = new TextField(existing != null ? safe(existing.getLevel()) : "");
        TextField locationF = new TextField(existing != null ? safe(existing.getLocation()) : "");
        TextField descF     = new TextField(existing != null ? safe(existing.getDescription()) : "");
        VBox box = new VBox(8,
                new Label("Title:"), titleF, new Label("Date (yyyy-MM-dd HH:mm):"), dateF,
                new Label("Duration (min):"), durationF, new Label("Max Participants:"), maxF,
                new Label("Price:"), priceF, new Label("Level:"), levelF,
                new Label("Location:"), locationF, new Label("Description:"), descF);
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box); dialog.getDialogPane().setPrefWidth(440); dialog.getDialogPane().setPrefHeight(540);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Workshop w = existing != null ? existing : new Workshop();
                w.setTitle_workshop(titleF.getText());
                try { w.setDate_workshop(LocalDateTime.parse(dateF.getText(), DT_FMT)); } catch (DateTimeParseException ignored) {}
                try { w.setDuration_minutes(Integer.parseInt(durationF.getText())); } catch (NumberFormatException ignored) {}
                try { w.setMax_participants(Integer.parseInt(maxF.getText())); } catch (NumberFormatException ignored) {}
                try { w.setPrice(Double.parseDouble(priceF.getText())); } catch (NumberFormatException ignored) {}
                w.setLevel(levelF.getText()); w.setLocation(locationF.getText()); w.setDescription(descF.getText());
                return w;
            }
            return null;
        });
        return dialog;
    }

    //   Stats  
    private void setupStats() {
        aExTitleCol.setCellValueFactory(c -> scol(c, "title_exhib"));
        aExArtworkCountCol.setCellValueFactory(c -> scol(c, "artworks_in_exhibition"));
        loadStats();
    }

    private void loadStats() {
        if (currentArtist == null) return;

        // Workshop count label
        List<Map<String, String>> wsCount = ViewHelper.query(
                "SELECT past_workshops FROM V_Artist_Workshop_Count WHERE id_artist=?", currentArtist.getId_artist());
        statWorkshopCount.setText("Past workshops led: " + (wsCount.isEmpty() ? "0" : wsCount.get(0).getOrDefault("past_workshops","0")));

        // Avg price label
        List<Map<String, String>> avgRows = ViewHelper.query(
                "SELECT avg_price, artwork_count FROM V_Avg_Artwork_Price WHERE id_artist=?", currentArtist.getId_artist());
        if (!avgRows.isEmpty()) {
            statAvgPrice.setText("Average artwork price: $" + avgRows.get(0).getOrDefault("avg_price","0"));
            statArtworkCount.setText("Total artworks: " + avgRows.get(0).getOrDefault("artwork_count","0"));
        }

        // Exhibitions table
        myExhibitionTable.setItems(FXCollections.observableArrayList(ViewHelper.query(
                "SELECT title_exhib, artworks_in_exhibition FROM V_Artist_By_Exhibition WHERE id_artist=?",
                currentArtist.getId_artist())));
    }

    //   Profile  
    private void setupProfile() {
        if (currentArtist == null) return;
        profileName.setText(safe(currentArtist.getName_user()));
        profileEmail.setText(safe(currentArtist.getEmail()));
        profilePhone.setText(safe(currentArtist.getPhone()));
        profileCity.setText(safe(currentArtist.getCity()));
        profileBio.setText(safe(currentArtist.getBio()));
        profileWebsite.setText(safe(currentArtist.getWebsite_artist()));
        profileActive.setSelected(currentArtist.isIs_active());
    }

    @FXML private void handleSaveProfile() {
        if (currentArtist == null) { profileMsg.setText("Artist profile not found."); return; }
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement us = conn.prepareStatement(
                        "UPDATE User_ SET name_user=?, email=?, phone=?, city=? WHERE id_user=?");
                us.setString(1, profileName.getText()); us.setString(2, profileEmail.getText());
                us.setString(3, profilePhone.getText()); us.setString(4, profileCity.getText());
                us.setInt(5, SessionManager.getInstance().getUserId());
                us.executeUpdate();

                PreparedStatement as = conn.prepareStatement(
                        "UPDATE Artist SET bio=?, website_artist=?, is_active=? WHERE id_artist=?");
                as.setString(1, profileBio.getText()); as.setString(2, profileWebsite.getText());
                as.setBoolean(3, profileActive.isSelected()); as.setInt(4, currentArtist.getId_artist());
                as.executeUpdate();
                conn.commit();
                profileMsg.setStyle("-fx-text-fill: green;"); profileMsg.setText("Profile saved!");
            } catch (SQLException e) { conn.rollback(); profileMsg.setText("Error: " + e.getMessage()); }
        } catch (SQLException e) { profileMsg.setText("DB error: " + e.getMessage()); }
    }

    //   Socials  
    private void setupSocials() {
        aSocialPlatformCol.setCellValueFactory(c -> scol(c, "platform"));
        aSocialLinkCol.setCellValueFactory(c -> scol(c, "social_link"));
        loadSocials();
    }

    private void loadSocials() {
        if (currentArtist == null) return;
        socialTable.setItems(FXCollections.observableArrayList(ViewHelper.query(
                "SELECT id_social, platform, link AS social_link FROM Artist_Social WHERE id_artist=?",
                currentArtist.getId_artist())));
    }

    @FXML private void handleAddSocial() {
        if (currentArtist == null) return;
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Social Network");
        ButtonType saveBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        TextField platformF = new TextField(); platformF.setPromptText("e.g. Instagram");
        TextField linkF     = new TextField(); linkF.setPromptText("https://...");
        VBox box = new VBox(8, new Label("Platform:"), platformF, new Label("Link:"), linkF);
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box); dialog.getDialogPane().setPrefWidth(380);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn && !platformF.getText().isEmpty()) {
                try (Connection conn = ConnectionManager.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO Artist_Social(platform, link, id_artist) VALUES (?,?,?)")) {
                    ps.setString(1, platformF.getText().trim()); ps.setString(2, linkF.getText().trim());
                    ps.setInt(3, currentArtist.getId_artist());
                    ps.executeUpdate(); loadSocials();
                } catch (SQLException e) { warn("Failed: " + e.getMessage()); }
            }
            return null;
        });
        dialog.showAndWait();
    }

    @FXML private void handleDeleteSocial() {
        Map<String, String> sel = socialTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a social to delete."); return; }
        new Alert(Alert.AlertType.CONFIRMATION, "Delete this social link?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try (Connection conn = ConnectionManager.getConnection();
                             PreparedStatement ps = conn.prepareStatement("DELETE FROM Artist_Social WHERE id_social=?")) {
                            ps.setInt(1, Integer.parseInt(sel.getOrDefault("id_social","0")));
                            ps.executeUpdate(); loadSocials();
                        } catch (SQLException e) { warn("Error: " + e.getMessage()); }
                    }
                });
    }

    //  Logout
    @FXML private void handleLogout() {
        SessionManager.getInstance().logout();
        NavigationHelper.goToViewer((Stage) welcomeLabel.getScene().getWindow());
    }

    //  Helpers
    private SimpleStringProperty scol(TableColumn.CellDataFeatures<Map<String, String>, String> c, String key) {
        return new SimpleStringProperty(c.getValue().getOrDefault(key, ""));
    }

    private String safe(String v) { return v == null ? "" : v; }
    private void warn(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }
}