package com.project.artconnect.ui;

import com.project.artconnect.model.Workshop;
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
    private TableColumn<Workshop, String> titleColumn;

    @FXML
    private TableColumn<Workshop, String> instructorColumn;

    @FXML
    private TableColumn<Workshop, String> dateColumn;

    @FXML
    private TableColumn<Workshop, Double> priceColumn;

    @FXML
    private TableColumn<Workshop, String> levelColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> levelFilter;

    private final WorkshopService workshopService =
            ServiceProvider.getWorkshopService();

    @FXML
    public void initialize() {

        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>("title_workshop"));

        levelColumn.setCellValueFactory(
                new PropertyValueFactory<>("level"));

        priceColumn.setCellValueFactory(
                new PropertyValueFactory<>("price"));

        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue()
                                .getDate_workshop()
                                .toString()));

        instructorColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        String.valueOf(
                                cellData.getValue()
                                        .getId_artist())));

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

    @FXML
    private void handleSearch() {

        String searchText =
                searchField.getText().toLowerCase();

        String selectedLevel =
                levelFilter.getValue();

        ObservableList<Workshop> filteredWorkshops =
                FXCollections.observableArrayList();

        for (Workshop workshop :
                workshopService.getAllWorkshops()) {

            boolean matchesSearch =
                    workshop.getTitle_workshop()
                            .toLowerCase()
                            .contains(searchText);

            boolean matchesLevel =
                    selectedLevel.equals("All Levels")
                    || workshop.getLevel()
                            .equalsIgnoreCase(selectedLevel);

            if (matchesSearch && matchesLevel) {
                filteredWorkshops.add(workshop);
            }
        }

        workshopTable.setItems(filteredWorkshops);
    }

    @FXML
    private void handleReset() {

        searchField.clear();

        levelFilter.setValue("All Levels");

        loadWorkshops();
    }
}