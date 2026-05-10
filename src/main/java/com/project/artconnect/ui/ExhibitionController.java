package com.project.artconnect.ui;

import com.project.artconnect.model.Exhibition;
import com.project.artconnect.service.ExhibitionService;
import com.project.artconnect.util.ServiceProvider;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ExhibitionController {

    @FXML
    private TableView<Exhibition> exhibitionTable;

    @FXML
    private TableColumn<Exhibition, String> titleColumn;

    @FXML
    private TableColumn<Exhibition, String> galleryColumn;

    @FXML
    private TableColumn<Exhibition, String> dateColumn;

    @FXML
    private TableColumn<Exhibition, String> themeColumn;

    private final ExhibitionService exhibitionService = ServiceProvider.getExhibitionService();

    @FXML
    public void initialize() {

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title_exhib"));

        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));

        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue()
                        .getStart_date()
                        .toString()));

        galleryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                String.valueOf(cellData.getValue().getId_gallery())));

        exhibitionTable.setItems(FXCollections.observableArrayList(exhibitionService.getAllExhibitions()));
    }
}