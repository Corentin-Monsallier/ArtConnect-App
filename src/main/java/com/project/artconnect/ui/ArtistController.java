package com.project.artconnect.ui;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.util.ServiceProvider;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ArtistController {

    @FXML
    private TableView<Artist> artistTable;

    @FXML
    private TableColumn<Artist, String> nameColumn;

    @FXML
    private TableColumn<Artist, String> cityColumn;

    @FXML
    private TableColumn<Artist, String> emailColumn;

    @FXML
    private TableColumn<Artist, Integer> yearColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> disciplineFilter;

    private final ArtistService artistService = ServiceProvider.getArtistService();

    @FXML
    public void initialize() {

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name_user"));

        cityColumn.setCellValueFactory(
                new PropertyValueFactory<>("city"));

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email"));

        yearColumn.setCellValueFactory(
                new PropertyValueFactory<>("birth_year"));

        loadArtists();

        loadDisciplines();
    }

    private void loadArtists() {

        ObservableList<Artist> artists = FXCollections.observableArrayList(
                artistService.getAllArtists());

        artistTable.setItems(artists);
    }

    private void loadDisciplines() {

        ObservableList<String> disciplines = FXCollections.observableArrayList();

        disciplines.add("All");

        for (Discipline d : artistService.getAllDisciplines()) {

            disciplines.add(d.getName_discipline());
        }

        disciplineFilter.setItems(disciplines);

        disciplineFilter.setValue("All");
    }

    @FXML
    private void handleSearch() {

        String searchText = searchField.getText();

        String discipline = disciplineFilter.getValue();

        ObservableList<Artist> filteredArtists = FXCollections.observableArrayList(
                artistService.searchArtists(
                        searchText,
                        null,
                        discipline));

        artistTable.setItems(filteredArtists);
    }

    @FXML
    private void handleReset() {

        searchField.clear();

        disciplineFilter.setValue("All");

        loadArtists();
    }
}