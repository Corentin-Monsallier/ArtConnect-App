package com.project.artconnect.ui;

import com.project.artconnect.model.Exhibition;
import com.project.artconnect.service.ExhibitionService;
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

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> themeFilter;

    private final ExhibitionService exhibitionService =
            ServiceProvider.getExhibitionService();

    @FXML
    public void initialize() {

        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>("title_exhib"));

        themeColumn.setCellValueFactory(
                new PropertyValueFactory<>("theme"));

        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue()
                                .getStart_date()
                                .toString()));

        galleryColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        String.valueOf(
                                cellData.getValue()
                                        .getId_gallery())));

        loadExhibitions();

        loadThemes();

        themeFilter.setOnAction(event -> handleSearch());
    }

    private void loadExhibitions() {

        ObservableList<Exhibition> exhibitions =
                FXCollections.observableArrayList(
                        exhibitionService.getAllExhibitions());

        exhibitionTable.setItems(exhibitions);
    }

    private void loadThemes() {

        ObservableList<String> themes =
                FXCollections.observableArrayList();

        themes.add("All");

        themes.addAll(exhibitionService.getAllThemes());

        themeFilter.setItems(themes);

        themeFilter.setValue("All");
    }

    @FXML
    private void handleSearch() {

        String searchText =
                searchField.getText().toLowerCase();

        String selectedTheme =
                themeFilter.getValue();

        ObservableList<Exhibition> filteredExhibitions =
                FXCollections.observableArrayList();

        for (Exhibition exhibition :
                exhibitionService.getAllExhibitions()) {

            boolean matchesSearch =
                    exhibition.getTitle_exhib()
                            .toLowerCase()
                            .contains(searchText);

            boolean matchesTheme =
                    selectedTheme.equals("All")
                    || exhibition.getTheme()
                            .equalsIgnoreCase(selectedTheme);

            if (matchesSearch && matchesTheme) {
                filteredExhibitions.add(exhibition);
            }
        }

        exhibitionTable.setItems(filteredExhibitions);
    }

    @FXML
    private void handleReset() {

        searchField.clear();

        themeFilter.setValue("All");

        loadExhibitions();
    }
}