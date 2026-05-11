package com.project.artconnect.ui;

import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.ArtworkStatus;
import com.project.artconnect.service.ArtworkService;
import com.project.artconnect.util.ServiceProvider;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ArtworkController {

    @FXML
    private TableView<Artwork> artworkTable;

    @FXML
    private TableColumn<Artwork, String> titleColumn;

    @FXML
    private TableColumn<Artwork, String> typeColumn;

    @FXML
    private TableColumn<Artwork, Double> priceColumn;

    @FXML
    private TableColumn<Artwork, String> statusColumn;

    @FXML
    private TableColumn<Artwork, Integer> artistColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> typeFilter;

    private final ArtworkService artworkService = ServiceProvider.getArtworkService();

    @FXML
    public void initialize() {

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title_art"));

        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        artistColumn.setCellValueFactory(new PropertyValueFactory<>("id_artist"));

        loadArtworks();

        loadTypes();

        typeFilter.setOnAction(event -> handleSearch());
    }

    private void loadArtworks() {

        ObservableList<Artwork> artworks =
                FXCollections.observableArrayList(
                        artworkService.getAllArtworks());

        artworkTable.setItems(artworks);
    }

    private void loadTypes() {

        ObservableList<String> types = FXCollections.observableArrayList();

        types.add("All");

        types.addAll(artworkService.getAllTypes());

        typeFilter.setItems(types);

        typeFilter.setValue("All");
    }

    @FXML
    private void handleSearch() {

        String searchText = searchField.getText().toLowerCase();

        String selectedType = typeFilter.getValue();

        ObservableList<Artwork> filteredArtworks =
                FXCollections.observableArrayList();

        for (Artwork artwork : artworkService.getAllArtworks()) {

            boolean matchesSearch =
                    artwork.getTitle_art()
                            .toLowerCase()
                            .contains(searchText);

            boolean matchesType =
                    selectedType.equals("All")
                    || artwork.getType()
                            .equalsIgnoreCase(selectedType);

            if (matchesSearch && matchesType) {
                filteredArtworks.add(artwork);
            }
        }

        artworkTable.setItems(filteredArtworks);
    }

    @FXML
    private void handleReset() {

        searchField.clear();

        typeFilter.setValue("All");

        loadArtworks();
    }
}