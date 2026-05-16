package com.project.artconnect.ui;

import com.project.artconnect.model.Exhibition;
import com.project.artconnect.service.ExhibitionService;
import com.project.artconnect.util.ServiceProvider;

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
    private TableColumn<Exhibition, Integer> idColumn;

    @FXML
    private TableColumn<Exhibition, String> titleColumn;

    @FXML
    private TableColumn<Exhibition, String> curatorColumn;

    @FXML
    private TableColumn<Exhibition, String> startDateColumn;

    @FXML
    private TableColumn<Exhibition, String> endDateColumn;

    @FXML
    private TableColumn<Exhibition, String> themeColumn;

    @FXML
    private TableColumn<Exhibition, String> descriptionColumn;

    @FXML
    private TableColumn<Exhibition, Integer> galleryColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> themeFilter;

    private final ExhibitionService exhibitionService =
            ServiceProvider.getExhibitionService();

    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id_exhibition"));

        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>("title_exhib"));

        curatorColumn.setCellValueFactory(
                new PropertyValueFactory<>("curator_name"));

        startDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("start_date"));

        endDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("end_date"));

        themeColumn.setCellValueFactory(
                new PropertyValueFactory<>("theme"));

        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>("description"));

        galleryColumn.setCellValueFactory(
                new PropertyValueFactory<>("id_gallery"));

        loadExhibitions();
        loadThemes();

        themeFilter.setOnAction(event -> handleSearch());
    }

    private void loadExhibitions() {

        exhibitionTable.setItems(
                FXCollections.observableArrayList(
                        exhibitionService.getAllExhibitions()));
    }

    private void loadThemes() {

        themeFilter.setItems(
                FXCollections.observableArrayList(
                        "All themes",
                        "Modern",
                        "Abstract",
                        "Photography",
                        "Contemporary",
                        "Classic"
                )
        );

        themeFilter.setValue("All themes");
    }

    @FXML
    private void handleSearch() {

        String search =
                searchField.getText().toLowerCase();

        String selectedTheme =
                themeFilter.getValue();

        ObservableList<Exhibition> filtered =
                FXCollections.observableArrayList();

        for (Exhibition exhibition :
                exhibitionService.getAllExhibitions()) {

            boolean titleMatch =
                    exhibition.getTitle_exhib()
                            .toLowerCase()
                            .contains(search);

            boolean themeMatch =
                    selectedTheme.equals("All themes")
                            || exhibition.getTheme()
                            .equalsIgnoreCase(selectedTheme);

            if (titleMatch && themeMatch) {
                filtered.add(exhibition);
            }
        }

        exhibitionTable.setItems(filtered);
    }

    @FXML
    private void handleReset() {

        searchField.clear();
        themeFilter.setValue("All themes");
        loadExhibitions();
    }
}