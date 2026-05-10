package com.project.artconnect.ui;

import com.project.artconnect.model.Workshop;
import com.project.artconnect.service.WorkshopService;
import com.project.artconnect.util.ServiceProvider;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

    private final WorkshopService workshopService = ServiceProvider.getWorkshopService();

    @FXML
    public void initialize() {

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title_workshop"));

        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));

        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue()
                        .getDate_workshop()
                        .toString()));

        instructorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                String.valueOf(cellData.getValue().getId_artist())));

        workshopTable.setItems(FXCollections.observableArrayList(workshopService.getAllWorkshops()));
    }
}