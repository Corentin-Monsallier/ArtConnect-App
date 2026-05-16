package com.project.artconnect.ui;

import com.project.artconnect.model.Artwork;
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
    private TableColumn<Artwork, Integer> idColumn;

    @FXML
    private TableColumn<Artwork, String> titleColumn;

    @FXML
    private TableColumn<Artwork, Integer> yearColumn;

    @FXML
    private TableColumn<Artwork, String> typeColumn;

    @FXML
    private TableColumn<Artwork, String> mediumColumn;

    @FXML
    private TableColumn<Artwork, String> dimensionsColumn;

    @FXML
    private TableColumn<Artwork, String> descriptionColumn;

    @FXML
    private TableColumn<Artwork, Double> priceColumn;

    @FXML
    private TableColumn<Artwork, String> statusColumn;

    @FXML
    private TableColumn<Artwork, Integer> artistColumn;

    @FXML
    private TableColumn<Artwork, String> tagsColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private ComboBox<String> typeFilter;

    @FXML
    private ComboBox<String> mediumFilter;

    private final ArtworkService artworkService =
            ServiceProvider.getArtworkService();

    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id_artwork"));

        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>("title_art"));

        yearColumn.setCellValueFactory(
                new PropertyValueFactory<>("creation_year"));

        typeColumn.setCellValueFactory(
                new PropertyValueFactory<>("type"));

        mediumColumn.setCellValueFactory(
                new PropertyValueFactory<>("medium"));

        dimensionsColumn.setCellValueFactory(
                new PropertyValueFactory<>("dimensions"));

        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>("description"));

        priceColumn.setCellValueFactory(
                new PropertyValueFactory<>("price"));

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status"));

        artistColumn.setCellValueFactory(
                new PropertyValueFactory<>("id_artist"));

        tagsColumn.setCellValueFactory(
                new PropertyValueFactory<>("tags"));

        loadArtworks();
        loadStatuses();
        loadTypes();
        loadMediums();

        statusFilter.setOnAction(event -> handleSearch());
        typeFilter.setOnAction(event -> handleSearch());
        mediumFilter.setOnAction(event -> handleSearch());

    }

    private void loadArtworks() {

        artworkTable.setItems(
                FXCollections.observableArrayList(
                        artworkService.getAllArtworks()));
    }

    private void loadStatuses() {

        statusFilter.setItems(
                FXCollections.observableArrayList(
                        "All status",
                        "available",
                        "sold",
                        "reserved"
                )
        );

        statusFilter.setValue("All status");
    }

    private void loadTypes() {

        typeFilter.setItems(
                FXCollections.observableArrayList(
                        "All types",
                        "painting",
                        "photography",
                        "digital",
                        "sculpture",
                        "illustration"
                )
        );

        typeFilter.setValue("All types");
    }

    private void loadMediums() {

        mediumFilter.setItems(
                FXCollections.observableArrayList(
                        "All mediums",
                        "oil",
                        "acrylic",
                        "canvas",
                        "digital",
                        "mixed media"
                )
        );

        mediumFilter.setValue("All mediums");
    }

    @FXML
    private void handleSearch() {

        String search =
                searchField.getText().toLowerCase();

        String status =
                statusFilter.getValue();

        String type =
                typeFilter.getValue();

        String medium =
                mediumFilter.getValue();

        ObservableList<Artwork> filtered =
                FXCollections.observableArrayList();

        for (Artwork artwork : artworkService.getAllArtworks()) {

            boolean titleMatch =
                    artwork.getTitle_art()
                            .toLowerCase()
                            .contains(search);

            boolean statusMatch =
                    status.equals("All")
                            || artwork.getStatus()
                            .toString()
                            .equalsIgnoreCase(status);

            boolean typeMatch =
                    type.equals("All")
                            || artwork.getType()
                            .equalsIgnoreCase(type);

            boolean mediumMatch =
                    medium.equals("All")
                            || artwork.getMedium()
                            .equalsIgnoreCase(medium);

            if (titleMatch
                    && statusMatch
                    && typeMatch
                    && mediumMatch) {

                filtered.add(artwork);
            }
        }

        artworkTable.setItems(filtered);
    }

    @FXML
    private void handleReset() {

        searchField.clear();

        statusFilter.setValue("All status");
        typeFilter.setValue("All types");
        mediumFilter.setValue("All mediums");

        loadArtworks();
    }
}