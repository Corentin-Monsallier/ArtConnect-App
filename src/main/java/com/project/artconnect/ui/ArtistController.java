package com.project.artconnect.ui;

import com.project.artconnect.model.Artist;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.util.ServiceProvider;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

    private final ArtistService artistService =
            ServiceProvider.getArtistService();

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
    }

    private void loadArtists() {

        ObservableList<Artist> artists =
                FXCollections.observableArrayList(
                        artistService.getAllArtists());

        artistTable.setItems(artists);
    }

    @FXML
    private void handleSearch() {

        String searchText =
                searchField.getText();

        ObservableList<Artist> filteredArtists =
                FXCollections.observableArrayList(
                        artistService.searchArtists(
                                searchText,
                                null,
                                null));

        artistTable.setItems(filteredArtists);
    }

    @FXML
    private void handleReset() {

        searchField.clear();

        loadArtists();
    }
}