package com.project.artconnect.ui;

import java.util.List;
import java.util.stream.Collectors;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.ArtistSocial;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.service.ArtistSocialService;
import com.project.artconnect.util.ServiceProvider;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ArtistController {

    @FXML
    private TableView<Artist> artistTable;

    @FXML
    private TableColumn<Artist, Integer> idArtistColumn;

    @FXML
    private TableColumn<Artist, Integer> idUserColumn;

    @FXML
    private TableColumn<Artist, String> nameColumn;

    @FXML
    private TableColumn<Artist, String> cityColumn;

    @FXML
    private TableColumn<Artist, String> emailColumn;

    @FXML
    private TableColumn<Artist, Integer> yearColumn;

    @FXML
    private TableColumn<Artist, String> phoneColumn;

    @FXML
    private TableColumn<Artist, String> bioColumn;

    @FXML
    private TableColumn<Artist, String> websiteColumn;

    @FXML
    private TableColumn<Artist, Boolean> activeColumn;

    @FXML
    private TableColumn<Artist, String> socialsColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> activeFilter;

    private final ArtistService artistService =
            ServiceProvider.getArtistService();

    private final ArtistSocialService artistSocialService =
            ServiceProvider.getArtistSocialService();

    @FXML
    public void initialize() {

        idArtistColumn.setCellValueFactory(
                new PropertyValueFactory<>("id_artist"));

        idUserColumn.setCellValueFactory(
                new PropertyValueFactory<>("id_user"));

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name_user"));

        cityColumn.setCellValueFactory(
                new PropertyValueFactory<>("city"));

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email"));

        yearColumn.setCellValueFactory(
                new PropertyValueFactory<>("birth_year"));

        phoneColumn.setCellValueFactory(
                new PropertyValueFactory<>("phone"));

        bioColumn.setCellValueFactory(
                new PropertyValueFactory<>("bio"));

        websiteColumn.setCellValueFactory(
                new PropertyValueFactory<>("website_artist"));

        activeColumn.setCellValueFactory(
                new PropertyValueFactory<>("is_active"));

        socialsColumn.setCellValueFactory(cellData -> {
            Artist artist = cellData.getValue();

            String socials = getSocialsText(artist.getId_artist());

            return new SimpleStringProperty(socials);
        });

        activeFilter.setItems(FXCollections.observableArrayList(
                "All activity",
                "true",
                "false"
        ));

        activeFilter.setValue("All activity");

        activeFilter.setOnAction(event -> handleSearch());

        loadArtists();
    }

    private void loadArtists() {

        ObservableList<Artist> artists =
                FXCollections.observableArrayList(
                        artistService.getAllArtists());

        artistTable.setItems(artists);
    }

    private String getSocialsText(int idArtist) {

        List<ArtistSocial> socials =
                artistSocialService.getArtistSocialsByArtistId(idArtist);

        if (socials == null || socials.isEmpty()) {
            return "";
        }

        return socials.stream()
                .map(social ->
                        safeString(social.getPlatform())
                                + ": "
                                + safeString(social.getLink()))
                .collect(Collectors.joining(" | "));
    }

    private String safeString(String value) {

        if (value == null) {
            return "";
        }

        return value;
    }

    @FXML
    private void handleSearch() {

        String searchText = searchField.getText();

        if (searchText == null) {
            searchText = "";
        }

        searchText = searchText.toLowerCase();

        String activeValue = activeFilter.getValue();

        ObservableList<Artist> filteredArtists =
                FXCollections.observableArrayList();

        for (Artist artist : artistService.getAllArtists()) {

            String socialsText =
                    getSocialsText(artist.getId_artist()).toLowerCase();

            boolean matchesSearch =
                    String.valueOf(artist.getId_artist()).contains(searchText)
                    || String.valueOf(artist.getId_user()).contains(searchText)
                    || safeString(artist.getName_user()).toLowerCase().contains(searchText)
                    || safeString(artist.getCity()).toLowerCase().contains(searchText)
                    || safeString(artist.getEmail()).toLowerCase().contains(searchText)
                    || String.valueOf(artist.getBirth_year()).contains(searchText)
                    || safeString(artist.getPhone()).toLowerCase().contains(searchText)
                    || safeString(artist.getBio()).toLowerCase().contains(searchText)
                    || safeString(artist.getWebsite_artist()).toLowerCase().contains(searchText)
                    || socialsText.contains(searchText);

            boolean matchesActive = true;

            if (activeValue != null && !activeValue.equals("All activity")) {

                boolean selectedActive =
                        Boolean.parseBoolean(activeValue);

                matchesActive =
                        artist.isIs_active() == selectedActive;
            }

            if (matchesSearch && matchesActive) {
                filteredArtists.add(artist);
            }
        }

        artistTable.setItems(filteredArtists);
    }

    @FXML
    private void handleReset() {

        searchField.clear();

        activeFilter.setValue("All activity");

        loadArtists();
    }
}