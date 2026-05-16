package com.project.artconnect.ui;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.service.WorkshopService;
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

public class WorkshopController {

    @FXML
    private TableView<Workshop> workshopTable;

    @FXML
    private TableColumn<Workshop, Integer> idColumn;

    @FXML
    private TableColumn<Workshop, String> titleColumn;

    @FXML
    private TableColumn<Workshop, String> dateColumn;

    @FXML
    private TableColumn<Workshop, Integer> durationColumn;

    @FXML
    private TableColumn<Workshop, Integer> maxParticipantsColumn;

    @FXML
    private TableColumn<Workshop, Double> priceColumn;

    @FXML
    private TableColumn<Workshop, String> levelColumn;

    @FXML
    private TableColumn<Workshop, String> locationColumn;

    @FXML
    private TableColumn<Workshop, String> descriptionColumn;

    @FXML
    private TableColumn<Workshop, Integer> artistIdColumn;

    @FXML
    private TableColumn<Workshop, String> artistNameColumn;

    @FXML
    private TableColumn<Workshop, String> artistEmailColumn;

    @FXML
    private TableColumn<Workshop, String> artistCityColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> levelFilter;

    private final WorkshopService workshopService =
            ServiceProvider.getWorkshopService();

    private final ArtistService artistService =
            ServiceProvider.getArtistService();

    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id_workshop"));

        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>("title_workshop"));

        durationColumn.setCellValueFactory(
                new PropertyValueFactory<>("duration_minutes"));

        maxParticipantsColumn.setCellValueFactory(
                new PropertyValueFactory<>("max_participants"));

        priceColumn.setCellValueFactory(
                new PropertyValueFactory<>("price"));

        levelColumn.setCellValueFactory(
                new PropertyValueFactory<>("level"));

        locationColumn.setCellValueFactory(
                new PropertyValueFactory<>("location"));

        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>("description"));

        artistIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("id_artist"));

        dateColumn.setCellValueFactory(cellData -> {
            Workshop workshop = cellData.getValue();

            if (workshop.getDate_workshop() == null) {
                return new SimpleStringProperty("");
            }

            return new SimpleStringProperty(
                    workshop.getDate_workshop().toString());
        });

        artistNameColumn.setCellValueFactory(cellData -> {
            Artist artist = getArtistById(
                    cellData.getValue().getId_artist());

            if (artist == null) {
                return new SimpleStringProperty("");
            }

            return new SimpleStringProperty(
                    safeString(artist.getName_user()));
        });

        artistEmailColumn.setCellValueFactory(cellData -> {
            Artist artist = getArtistById(
                    cellData.getValue().getId_artist());

            if (artist == null) {
                return new SimpleStringProperty("");
            }

            return new SimpleStringProperty(
                    safeString(artist.getEmail()));
        });

        artistCityColumn.setCellValueFactory(cellData -> {
            Artist artist = getArtistById(
                    cellData.getValue().getId_artist());

            if (artist == null) {
                return new SimpleStringProperty("");
            }

            return new SimpleStringProperty(
                    safeString(artist.getCity()));
        });

        loadWorkshops();

        loadLevels();

        levelFilter.setOnAction(event -> handleSearch());
    }

    private void loadWorkshops() {

        ObservableList<Workshop> workshops =
                FXCollections.observableArrayList(
                        workshopService.getAllWorkshops());

        workshopTable.setItems(workshops);
    }

    private void loadLevels() {

        ObservableList<String> levels =
                FXCollections.observableArrayList();

        levels.add("All Levels");

        levels.addAll(workshopService.getAllLevels());

        levelFilter.setItems(levels);

        levelFilter.setValue("All Levels");
    }

    private Artist getArtistById(int idArtist) {

        for (Artist artist : artistService.getAllArtists()) {

            if (artist.getId_artist() == idArtist) {
                return artist;
            }
        }

        return null;
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

        String selectedLevel = levelFilter.getValue();

        ObservableList<Workshop> filteredWorkshops =
                FXCollections.observableArrayList();

        for (Workshop workshop : workshopService.getAllWorkshops()) {

            Artist artist = getArtistById(
                    workshop.getId_artist());

            String dateText = "";

            if (workshop.getDate_workshop() != null) {
                dateText = workshop.getDate_workshop().toString();
            }

            boolean matchesSearch =
                    String.valueOf(workshop.getId_workshop()).contains(searchText)
                    || safeString(workshop.getTitle_workshop()).toLowerCase().contains(searchText)
                    || dateText.toLowerCase().contains(searchText)
                    || String.valueOf(workshop.getDuration_minutes()).contains(searchText)
                    || String.valueOf(workshop.getMax_participants()).contains(searchText)
                    || String.valueOf(workshop.getPrice()).contains(searchText)
                    || safeString(workshop.getLevel()).toLowerCase().contains(searchText)
                    || safeString(workshop.getLocation()).toLowerCase().contains(searchText)
                    || safeString(workshop.getDescription()).toLowerCase().contains(searchText)
                    || String.valueOf(workshop.getId_artist()).contains(searchText)
                    || artistMatches(artist, searchText);

            boolean matchesLevel =
                    selectedLevel == null
                    || selectedLevel.equals("All Levels")
                    || safeString(workshop.getLevel()).equalsIgnoreCase(selectedLevel);

            if (matchesSearch && matchesLevel) {
                filteredWorkshops.add(workshop);
            }
        }

        workshopTable.setItems(filteredWorkshops);
    }

    private boolean artistMatches(Artist artist, String searchText) {

        if (artist == null) {
            return false;
        }

        return String.valueOf(artist.getId_artist()).contains(searchText)
                || safeString(artist.getName_user()).toLowerCase().contains(searchText)
                || safeString(artist.getEmail()).toLowerCase().contains(searchText)
                || safeString(artist.getCity()).toLowerCase().contains(searchText);
    }

    @FXML
    private void handleReset() {

        searchField.clear();

        levelFilter.setValue("All Levels");

        loadWorkshops();
    }
}