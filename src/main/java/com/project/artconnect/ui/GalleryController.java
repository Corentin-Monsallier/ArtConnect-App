package com.project.artconnect.ui;

import com.project.artconnect.model.Gallery;
import com.project.artconnect.service.GalleryService;
import com.project.artconnect.util.ServiceProvider;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

        galleryTable.setItems(
                FXCollections.observableArrayList(
                        galleryService.getAllGalleries()));
    }
}