package com.project.artconnect.ui;

import com.project.artconnect.model.Gallery;
import com.project.artconnect.service.GalleryService;
import com.project.artconnect.util.ServiceProvider;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class GalleryController {

    @FXML
    private TableView<Gallery> galleryTable;

    @FXML
    private TableColumn<Gallery, String> nameColumn;

    @FXML
    private TableColumn<Gallery, Integer> ratingColumn;

    @FXML
    private TableColumn<Gallery, String> websiteColumn;

    @FXML
    private TableColumn<Gallery, Integer> addressColumn;

    @FXML
    private TextField searchField;

    private ObservableList<Gallery> galleryList;

    private final GalleryService galleryService =
            ServiceProvider.getGalleryService();

    @FXML
    public void initialize() {

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name_gallery"));

        ratingColumn.setCellValueFactory(
                new PropertyValueFactory<>("rating"));

        websiteColumn.setCellValueFactory(
                new PropertyValueFactory<>("website_gallery"));

        addressColumn.setCellValueFactory(
                new PropertyValueFactory<>("address_id"));

        galleryList = FXCollections.observableArrayList(
                galleryService.getAllGalleries());

        galleryTable.setItems(galleryList);
    }

    @FXML
    private void handleSearch() {

        String keyword = searchField.getText().toLowerCase();

        ObservableList<Gallery> filteredList =
                FXCollections.observableArrayList();

        for (Gallery gallery : galleryList) {

            boolean matchesName =
                    gallery.getName_gallery()
                            .toLowerCase()
                            .contains(keyword);

            boolean matchesWebsite =
                    gallery.getWebsite_gallery()
                            .toLowerCase()
                            .contains(keyword);

            boolean matchesRating =
                    String.valueOf(gallery.getRating())
                            .contains(keyword);

            boolean matchesAddress =
                    String.valueOf(gallery.getAddress_id())
                            .contains(keyword);

            if (matchesName || matchesWebsite || matchesRating || matchesAddress) {
                filteredList.add(gallery);
            }
        }

        galleryTable.setItems(filteredList);
    }

    @FXML
    private void handleReset() {

        searchField.clear();

        galleryTable.setItems(galleryList);
    }
}