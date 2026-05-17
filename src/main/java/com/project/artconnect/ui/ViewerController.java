package com.project.artconnect.ui;

import java.util.List;
import java.util.Map;

import com.project.artconnect.util.DiscoverBuilder;
import com.project.artconnect.util.NavigationHelper;
import com.project.artconnect.util.ViewHelper;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class ViewerController {

    @FXML private FlowPane discoverPane;

    // Artworks
    @FXML private TableView<Map<String, String>> artworkTable;
    @FXML private TableColumn<Map<String, String>, String> vArtTitleCol;
    @FXML private TableColumn<Map<String, String>, String> vArtArtistCol;
    @FXML private TableColumn<Map<String, String>, String> vArtTypeCol;
    @FXML private TableColumn<Map<String, String>, String> vArtMediumCol;
    @FXML private TableColumn<Map<String, String>, String> vArtYearCol;
    @FXML private TableColumn<Map<String, String>, String> vArtPriceCol;
    @FXML private TableColumn<Map<String, String>, String> vArtStatusCol;
    @FXML private TextField artworkSearch;
    @FXML private ComboBox<String> artworkStatusFilter;
    @FXML private ComboBox<String> artworkTypeFilter;
    @FXML private ComboBox<String> artworkMediumFilter;
    @FXML private ComboBox<String> artworkDisciplineFilter;

    // Reviews
    @FXML private TableView<Map<String, String>> reviewTable;
    @FXML private TableColumn<Map<String, String>, String> vRvArtworkCol;
    @FXML private TableColumn<Map<String, String>, String> vRvRatingCol;
    @FXML private TableColumn<Map<String, String>, String> vRvCommentCol;
    @FXML private TableColumn<Map<String, String>, String> vRvReviewerCol;
    @FXML private TableColumn<Map<String, String>, String> vRvDateCol;
    @FXML private TextField reviewSearch;
    @FXML private ComboBox<String> reviewRatingFilter;

    // Exhibitions
    @FXML private TableView<Map<String, String>> exhibitionTable;
    @FXML private TableColumn<Map<String, String>, String> vExTitleCol;
    @FXML private TableColumn<Map<String, String>, String> vExGalleryCol;
    @FXML private TableColumn<Map<String, String>, String> vExThemeCol;
    @FXML private TableColumn<Map<String, String>, String> vExStartCol;
    @FXML private TableColumn<Map<String, String>, String> vExEndCol;
    @FXML private TextField exhibitionSearch;
    @FXML private ComboBox<String> exhibitionGalleryFilter;

    // Workshops
    @FXML private TableView<Map<String, String>> workshopTable;
    @FXML private TableColumn<Map<String, String>, String> vWsTitleCol;
    @FXML private TableColumn<Map<String, String>, String> vWsInstructorCol;
    @FXML private TableColumn<Map<String, String>, String> vWsDateCol;
    @FXML private TableColumn<Map<String, String>, String> vWsLevelCol;
    @FXML private TableColumn<Map<String, String>, String> vWsPriceCol;
    @FXML private TableColumn<Map<String, String>, String> vWsLocationCol;
    @FXML private TableColumn<Map<String, String>, String> vWsSpotsCol;
    @FXML private TableColumn<Map<String, String>, String> vWsAvailCol;
    @FXML private TextField workshopSearch;
    @FXML private ComboBox<String> workshopAvailFilter;

    // Galleries
    @FXML private TableView<Map<String, String>> galleryTable;
    @FXML private TableColumn<Map<String, String>, String> vGalNameCol;
    @FXML private TableColumn<Map<String, String>, String> vGalCityCol;
    @FXML private TableColumn<Map<String, String>, String> vGalRatingCol;
    @FXML private TableColumn<Map<String, String>, String> vGalWebCol;
    @FXML private TableColumn<Map<String, String>, String> vGalHoursCol;
    @FXML private TextField gallerySearch;

    // Artists
    @FXML private TableView<Map<String, String>> artistTable;
    @FXML private TableColumn<Map<String, String>, String> vArtistNameCol;
    @FXML private TableColumn<Map<String, String>, String> vArtistCityCol;
    @FXML private TableColumn<Map<String, String>, String> vArtistEmailCol;
    @FXML private TableColumn<Map<String, String>, String> vArtistBirthCol;
    @FXML private TableColumn<Map<String, String>, String> vArtistBioCol;
    @FXML private TableColumn<Map<String, String>, String> vArtistWebCol;
    @FXML private TableColumn<Map<String, String>, String> vArtistSocialCol;
    @FXML private TableColumn<Map<String, String>, String> vArtistDisciplineCol;
    @FXML private TextField artistSearch;

    // Exhibition artworks panel
    @FXML private TableView<Map<String, String>> exhibitionArtworkTable;
    @FXML private TableColumn<Map<String, String>, String> vExArtTitleCol;
    @FXML private TableColumn<Map<String, String>, String> vExArtArtistCol;
    @FXML private TableColumn<Map<String, String>, String> vExArtTypeCol;
    @FXML private TableColumn<Map<String, String>, String> vExArtStatusCol;

    private List<Map<String, String>> allArtworks;
    private List<Map<String, String>> allReviews;
    private List<Map<String, String>> allExhibitions;
    private List<Map<String, String>> allWorkshops;
    private List<Map<String, String>> allGalleries;
    private List<Map<String, String>> allArtists;

    @FXML
    public void initialize() {
        DiscoverBuilder.populate(discoverPane);
        setupArtworks();
        setupReviews();
        setupExhibitions();
        setupWorkshops();
        setupGalleries();
        setupArtists();
    }

    //  Artworks
    private static final String ARTWORK_SQL =
            "SELECT a.id_artwork, a.title_art, u.name_user AS artist_name, a.type, a.medium, " +
                    "a.creation_year, a.price, a.status, a.id_artist " +
                    "FROM V_Artwork_By_Artist a " +
                    "JOIN Artist ar ON a.id_artist = ar.id_artist " +
                    "JOIN User_ u ON ar.id_user = u.id_user";

    private void setupArtworks() {
        vArtTitleCol.setCellValueFactory(c -> col(c, "title_art"));
        vArtArtistCol.setCellValueFactory(c -> col(c, "artist_name"));
        vArtTypeCol.setCellValueFactory(c -> col(c, "type"));
        vArtMediumCol.setCellValueFactory(c -> col(c, "medium"));
        vArtYearCol.setCellValueFactory(c -> col(c, "creation_year"));
        vArtPriceCol.setCellValueFactory(c -> col(c, "price"));
        vArtStatusCol.setCellValueFactory(c -> col(c, "status"));

        allArtworks = ViewHelper.query("SELECT * FROM V_Artwork_By_Artist");

        // Status filter
        ObservableList<String> statuses = FXCollections.observableArrayList("All statuses", "available", "sold", "reserved");
        artworkStatusFilter.setItems(statuses); artworkStatusFilter.setValue("All statuses");
        artworkStatusFilter.setOnAction(e -> handleArtworkSearch());

        // Type filter
        ObservableList<String> types = FXCollections.observableArrayList("All types");
        allArtworks.stream().map(r -> r.getOrDefault("type","")).filter(s -> !s.isEmpty()).distinct().sorted().forEach(types::add);
        artworkTypeFilter.setItems(types); artworkTypeFilter.setValue("All types");
        artworkTypeFilter.setOnAction(e -> handleArtworkSearch());

        // Medium filter
        ObservableList<String> mediums = FXCollections.observableArrayList("All mediums");
        allArtworks.stream().map(r -> r.getOrDefault("medium","")).filter(s -> !s.isEmpty()).distinct().sorted().forEach(mediums::add);
        artworkMediumFilter.setItems(mediums); artworkMediumFilter.setValue("All mediums");
        artworkMediumFilter.setOnAction(e -> handleArtworkSearch());

        // Discipline filter
        ObservableList<String> disciplines = FXCollections.observableArrayList("All disciplines");
        ViewHelper.query("SELECT DISTINCT name_discipline FROM Discipline ORDER BY name_discipline")
                .forEach(r -> disciplines.add(r.getOrDefault("name_discipline","")));
        artworkDisciplineFilter.setItems(disciplines); artworkDisciplineFilter.setValue("All disciplines");
        artworkDisciplineFilter.setOnAction(e -> handleArtworkSearch());

        artworkTable.setItems(FXCollections.observableArrayList(allArtworks));
    }

    @FXML private void handleArtworkSearch() {
        String txt    = artworkSearch.getText().toLowerCase();
        String status = artworkStatusFilter.getValue();
        String type   = artworkTypeFilter.getValue();
        String medium = artworkMediumFilter.getValue();
        String disc   = artworkDisciplineFilter.getValue();

        // If discipline filter active, get artwork ids from that discipline
        List<String> discArtworkIds = null;
        if (disc != null && !disc.equals("All disciplines")) {
            List<Map<String, String>> discRows = ViewHelper.query(
                    "SELECT DISTINCT a.id_artwork FROM V_Artworks_By_Discipline vd " +
                            "JOIN Artwork a ON vd.title_art = a.title_art " +
                            "WHERE vd.name_discipline=?", disc);
            discArtworkIds = discRows.stream().map(r -> r.getOrDefault("id_artwork","")).toList();
        }
        final List<String> finalDiscIds = discArtworkIds;

        ObservableList<Map<String, String>> f = FXCollections.observableArrayList();
        for (Map<String, String> row : allArtworks) {
            boolean t = row.getOrDefault("title_art","").toLowerCase().contains(txt)
                    || row.getOrDefault("artist_name","").toLowerCase().contains(txt);
            boolean s = "All statuses".equals(status) || row.getOrDefault("status","").equalsIgnoreCase(status);
            boolean ty = "All types".equals(type) || row.getOrDefault("type","").equalsIgnoreCase(type);
            boolean m = "All mediums".equals(medium) || row.getOrDefault("medium","").equalsIgnoreCase(medium);
            boolean d = finalDiscIds == null || finalDiscIds.contains(row.getOrDefault("id_artwork",""));
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

    //  Reviews
    private void setupReviews() {
        vRvArtworkCol.setCellValueFactory(c -> col(c, "title_art"));
        vRvRatingCol.setCellValueFactory(c -> col(c, "rating"));
        vRvCommentCol.setCellValueFactory(c -> col(c, "comment"));
        vRvReviewerCol.setCellValueFactory(c -> col(c, "name_user"));
        vRvDateCol.setCellValueFactory(c -> col(c, "review_date"));

        reviewRatingFilter.setItems(FXCollections.observableArrayList("All ratings","1","2","3","4","5"));
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
            boolean t = row.getOrDefault("title_art","").toLowerCase().contains(txt)
                    || row.getOrDefault("name_user","").toLowerCase().contains(txt)
                    || row.getOrDefault("comment","").toLowerCase().contains(txt);
            boolean r = "All ratings".equals(rating) || row.getOrDefault("rating","").equals(rating);
            if (t && r) f.add(row);
        }
        reviewTable.setItems(f);
    }

    @FXML private void handleReviewReset() {
        reviewSearch.clear(); reviewRatingFilter.setValue("All ratings");
        reviewTable.setItems(FXCollections.observableArrayList(allReviews));
    }

    //  Exhibitions
    private static final String EXHIB_SQL =
            "SELECT e.title_exhib, g.name_gallery, e.theme, e.start_date, e.end_date " +
                    "FROM Exhibition e JOIN Gallery g ON e.id_gallery = g.id_gallery ORDER BY e.start_date";

    private void setupExhibitions() {
        vExTitleCol.setCellValueFactory(c -> col(c, "title_exhib"));
        vExGalleryCol.setCellValueFactory(c -> col(c, "name_gallery"));
        vExThemeCol.setCellValueFactory(c -> col(c, "theme"));
        vExStartCol.setCellValueFactory(c -> col(c, "start_date"));
        vExEndCol.setCellValueFactory(c -> col(c, "end_date"));

        ObservableList<String> galleries = FXCollections.observableArrayList("All galleries");
        ViewHelper.query("SELECT name_gallery FROM Gallery ORDER BY name_gallery")
                .forEach(r -> galleries.add(r.getOrDefault("name_gallery","")));
        exhibitionGalleryFilter.setItems(galleries); exhibitionGalleryFilter.setValue("All galleries");
        exhibitionGalleryFilter.setOnAction(e -> handleExhibitionSearch());

        allExhibitions = ViewHelper.query(EXHIB_SQL);
        exhibitionTable.setItems(FXCollections.observableArrayList(allExhibitions));

        // Artworks in selected exhibition
        if (exhibitionArtworkTable != null) {
            vExArtTitleCol.setCellValueFactory(c -> col(c, "title_art"));
            vExArtArtistCol.setCellValueFactory(c -> col(c, "artist_name"));
            vExArtTypeCol.setCellValueFactory(c -> col(c, "type"));
            vExArtStatusCol.setCellValueFactory(c -> col(c, "status"));
            exhibitionTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
                if (sel == null) { exhibitionArtworkTable.getItems().clear(); return; }
                String title = sel.getOrDefault("title_exhib", "");
                exhibitionArtworkTable.setItems(FXCollections.observableArrayList(ViewHelper.query(
                        "SELECT a.title_art, u.name_user AS artist_name, a.type, a.status " +
                                "FROM Exhibition_Artwork ea " +
                                "JOIN Artwork a ON ea.id_artwork = a.id_artwork " +
                                "JOIN Artist ar ON a.id_artist = ar.id_artist " +
                                "JOIN User_ u ON ar.id_user = u.id_user " +
                                "JOIN Exhibition e ON ea.id_exhibition = e.id_exhibition " +
                                "WHERE e.title_exhib=? ORDER BY a.title_art", title)));
            });
        }
    }

    @FXML private void handleExhibitionSearch() {
        String txt = exhibitionSearch.getText().toLowerCase();
        String gal = exhibitionGalleryFilter.getValue();
        ObservableList<Map<String, String>> f = FXCollections.observableArrayList();
        for (Map<String, String> row : allExhibitions) {
            boolean t = row.getOrDefault("title_exhib","").toLowerCase().contains(txt)
                    || row.getOrDefault("theme","").toLowerCase().contains(txt);
            boolean g = "All galleries".equals(gal) || row.getOrDefault("name_gallery","").equals(gal);
            if (t && g) f.add(row);
        }
        exhibitionTable.setItems(f);
    }

    @FXML private void handleExhibitionReset() {
        exhibitionSearch.clear(); exhibitionGalleryFilter.setValue("All galleries");
        exhibitionTable.setItems(FXCollections.observableArrayList(allExhibitions));
    }

    //  Workshops
    private static final String WS_SQL =
            "SELECT w.id_workshop, w.title_workshop, u.name_user AS instructor, w.date_workshop, " +
                    "w.level, w.price, w.location, v.remaining_spots, v.availability_status " +
                    "FROM V_Workshop_Availability v " +
                    "JOIN Workshop w ON v.id_workshop = w.id_workshop " +
                    "JOIN Artist a ON w.id_artist = a.id_artist " +
                    "JOIN User_ u ON a.id_user = u.id_user";

    private void setupWorkshops() {
        vWsTitleCol.setCellValueFactory(c -> col(c, "title_workshop"));
        vWsInstructorCol.setCellValueFactory(c -> col(c, "instructor"));
        vWsDateCol.setCellValueFactory(c -> col(c, "date_workshop"));
        vWsLevelCol.setCellValueFactory(c -> col(c, "level"));
        vWsPriceCol.setCellValueFactory(c -> col(c, "price"));
        vWsLocationCol.setCellValueFactory(c -> col(c, "location"));
        vWsSpotsCol.setCellValueFactory(c -> col(c, "remaining_spots"));
        vWsAvailCol.setCellValueFactory(c -> col(c, "availability_status"));

        workshopAvailFilter.setItems(FXCollections.observableArrayList("All", "available", "full"));
        workshopAvailFilter.setValue("All");
        workshopAvailFilter.setOnAction(e -> handleWorkshopSearch());

        allWorkshops = ViewHelper.query(WS_SQL);
        workshopTable.setItems(FXCollections.observableArrayList(allWorkshops));
    }

    @FXML private void handleWorkshopSearch() {
        String txt  = workshopSearch.getText().toLowerCase();
        String avail = workshopAvailFilter.getValue();
        ObservableList<Map<String, String>> f = FXCollections.observableArrayList();
        for (Map<String, String> row : allWorkshops) {
            boolean t = row.getOrDefault("title_workshop","").toLowerCase().contains(txt)
                    || row.getOrDefault("location","").toLowerCase().contains(txt)
                    || row.getOrDefault("instructor","").toLowerCase().contains(txt);
            boolean a = "All".equals(avail) || row.getOrDefault("availability_status","").equalsIgnoreCase(avail);
            if (t && a) f.add(row);
        }
        workshopTable.setItems(f);
    }

    @FXML private void handleWorkshopReset() {
        workshopSearch.clear(); workshopAvailFilter.setValue("All");
        workshopTable.setItems(FXCollections.observableArrayList(allWorkshops));
    }

    //  Galleries
    private static final String GAL_SQL =
            "SELECT g.name_gallery, ci.city, g.rating, g.website_gallery, " +
                    "GROUP_CONCAT(CONCAT(gh.day_of_week,': ',gh.open_time,'-',gh.close_time) " +
                    "ORDER BY FIELD(gh.day_of_week,'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') " +
                    "SEPARATOR ' | ') AS hours " +
                    "FROM Gallery g " +
                    "LEFT JOIN Gallery_Hours gh ON g.id_gallery = gh.id_gallery " +
                    "LEFT JOIN Address ad ON g.address_id = ad.address_id " +
                    "LEFT JOIN City ci ON ad.id_city = ci.id_city " +
                    "GROUP BY g.id_gallery, g.name_gallery, ci.city, g.rating, g.website_gallery";

    private void setupGalleries() {
        vGalNameCol.setCellValueFactory(c -> col(c, "name_gallery"));
        vGalCityCol.setCellValueFactory(c -> col(c, "city"));
        vGalRatingCol.setCellValueFactory(c -> col(c, "rating"));
        vGalWebCol.setCellValueFactory(c -> col(c, "website_gallery"));
        vGalHoursCol.setCellValueFactory(c -> col(c, "hours"));
        allGalleries = ViewHelper.query(GAL_SQL);
        galleryTable.setItems(FXCollections.observableArrayList(allGalleries));
    }

    @FXML private void handleGallerySearch() {
        String txt = gallerySearch.getText().toLowerCase();
        ObservableList<Map<String, String>> f = FXCollections.observableArrayList();
        for (Map<String, String> row : allGalleries) {
            if (row.getOrDefault("name_gallery","").toLowerCase().contains(txt)
                    || row.getOrDefault("city","").toLowerCase().contains(txt)) f.add(row);
        }
        galleryTable.setItems(f);
    }

    @FXML private void handleGalleryReset() {
        gallerySearch.clear();
        galleryTable.setItems(FXCollections.observableArrayList(allGalleries));
    }

    //  Artists
    private static final String ARTIST_SQL =
            "SELECT u.name_user, u.city, u.email, u.birth_year, ar.bio, ar.website_artist, " +
                    "GROUP_CONCAT(DISTINCT d.name_discipline ORDER BY d.name_discipline SEPARATOR ', ') AS disciplines, " +
                    "GROUP_CONCAT(DISTINCT CONCAT(s.platform,': ',s.link) ORDER BY s.platform SEPARATOR ' | ') AS socials " +
                    "FROM Artist ar " +
                    "JOIN User_ u ON ar.id_user = u.id_user " +
                    "LEFT JOIN Artist_Discipline ad ON ar.id_artist = ad.id_artist " +
                    "LEFT JOIN Discipline d ON ad.id_discipline = d.id_discipline " +
                    "LEFT JOIN Artist_Social s ON ar.id_artist = s.id_artist " +
                    "WHERE ar.is_active = TRUE " +
                    "GROUP BY ar.id_artist, u.name_user, u.city, u.email, u.birth_year, ar.bio, ar.website_artist " +
                    "ORDER BY u.name_user";

    private void setupArtists() {
        vArtistNameCol.setCellValueFactory(c -> col(c, "name_user"));
        vArtistCityCol.setCellValueFactory(c -> col(c, "city"));
        vArtistEmailCol.setCellValueFactory(c -> col(c, "email"));
        vArtistBirthCol.setCellValueFactory(c -> col(c, "birth_year"));
        vArtistBioCol.setCellValueFactory(c -> col(c, "bio"));
        vArtistWebCol.setCellValueFactory(c -> col(c, "website_artist"));
        vArtistSocialCol.setCellValueFactory(c -> col(c, "socials"));
        vArtistDisciplineCol.setCellValueFactory(c -> col(c, "disciplines"));
        allArtists = ViewHelper.query(ARTIST_SQL);
        artistTable.setItems(FXCollections.observableArrayList(allArtists));
    }

    @FXML private void handleArtistSearch() {
        String txt = artistSearch.getText().toLowerCase();
        ObservableList<Map<String, String>> f = FXCollections.observableArrayList();
        for (Map<String, String> row : allArtists)
            if (row.getOrDefault("name_user","").toLowerCase().contains(txt)
                    || row.getOrDefault("city","").toLowerCase().contains(txt)
                    || row.getOrDefault("disciplines","").toLowerCase().contains(txt)) f.add(row);
        artistTable.setItems(f);
    }

    @FXML private void handleArtistReset() {
        artistSearch.clear();
        artistTable.setItems(FXCollections.observableArrayList(allArtists));
    }

    //  Navigation 
    @FXML private void handleLogin() {
        NavigationHelper.goToLogin((Stage) discoverPane.getScene().getWindow());
    }
    @FXML private void handleRegister() {
        NavigationHelper.goToRegister((Stage) discoverPane.getScene().getWindow());
    }

    //  Helper 
    private SimpleStringProperty col(TableColumn.CellDataFeatures<Map<String, String>, String> c, String key) {
        return new SimpleStringProperty(c.getValue().getOrDefault(key, ""));
    }
}