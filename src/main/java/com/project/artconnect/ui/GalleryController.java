package com.project.artconnect.ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.project.artconnect.config.DatabaseConfig;
import com.project.artconnect.model.Address;
import com.project.artconnect.model.City;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.service.AddressService;
import com.project.artconnect.service.CityService;
import com.project.artconnect.service.GalleryService;
import com.project.artconnect.util.ServiceProvider;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
    private TableColumn<Gallery, Integer> idColumn;

    @FXML
    private TableColumn<Gallery, String> nameColumn;

    @FXML
    private TableColumn<Gallery, Integer> ratingColumn;

    @FXML
    private TableColumn<Gallery, String> websiteColumn;

    @FXML
    private TableColumn<Gallery, Integer> addressIdColumn;

    @FXML
    private TableColumn<Gallery, Integer> numberColumn;

    @FXML
    private TableColumn<Gallery, String> streetColumn;

    @FXML
    private TableColumn<Gallery, Integer> cityIdColumn;

    @FXML
    private TableColumn<Gallery, String> cityColumn;

    @FXML
    private TableColumn<Gallery, Integer> codeColumn;

    @FXML
    private TableColumn<Gallery, String> countryColumn;

    @FXML
    private TableColumn<Gallery, String> hoursColumn;

    @FXML
    private TextField searchField;

    private ObservableList<Gallery> galleryList;

    private final GalleryService galleryService =
            ServiceProvider.getGalleryService();

    private final AddressService addressService =
            ServiceProvider.getAddressService();

    private final CityService cityService =
            ServiceProvider.getCityService();

    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id_gallery"));

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name_gallery"));

        ratingColumn.setCellValueFactory(
                new PropertyValueFactory<>("rating"));

        websiteColumn.setCellValueFactory(
                new PropertyValueFactory<>("website_gallery"));

        addressIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("address_id"));

        numberColumn.setCellValueFactory(cellData -> {
            Address address = getAddressById(
                    cellData.getValue().getAddress_id());

            if (address == null) {
                return new SimpleIntegerProperty(0).asObject();
            }

            return new SimpleIntegerProperty(
                    address.getNumber()).asObject();
        });

        streetColumn.setCellValueFactory(cellData -> {
            Address address = getAddressById(
                    cellData.getValue().getAddress_id());

            if (address == null) {
                return new SimpleStringProperty("");
            }

            return new SimpleStringProperty(
                    safeString(address.getStreet()));
        });

        cityIdColumn.setCellValueFactory(cellData -> {
            Address address = getAddressById(
                    cellData.getValue().getAddress_id());

            if (address == null) {
                return new SimpleIntegerProperty(0).asObject();
            }

            return new SimpleIntegerProperty(
                    address.getId_city()).asObject();
        });

        cityColumn.setCellValueFactory(cellData -> {
            City city = getCityByGallery(
                    cellData.getValue());

            if (city == null) {
                return new SimpleStringProperty("");
            }

            return new SimpleStringProperty(
                    safeString(city.getCity()));
        });

        codeColumn.setCellValueFactory(cellData -> {
            City city = getCityByGallery(
                    cellData.getValue());

            if (city == null) {
                return new SimpleIntegerProperty(0).asObject();
            }

            return new SimpleIntegerProperty(
                    city.getCode()).asObject();
        });

        countryColumn.setCellValueFactory(cellData -> {
            City city = getCityByGallery(
                    cellData.getValue());

            if (city == null) {
                return new SimpleStringProperty("");
            }

            return new SimpleStringProperty(
                    safeString(city.getCountry()));
        });

        hoursColumn.setCellValueFactory(cellData -> {
            Gallery gallery = cellData.getValue();

            return new SimpleStringProperty(
                    getGalleryHoursText(gallery.getId_gallery()));
        });

        loadGalleries();
    }

    private void loadGalleries() {

        galleryList = FXCollections.observableArrayList(
                galleryService.getAllGalleries());

        galleryTable.setItems(galleryList);
    }

    private Address getAddressById(int addressId) {

        for (Address address : addressService.getAllAddresses()) {

            if (address.getAddress_id() == addressId) {
                return address;
            }
        }

        return null;
    }

    private City getCityByGallery(Gallery gallery) {

        Address address = getAddressById(
                gallery.getAddress_id());

        if (address == null) {
            return null;
        }

        for (City city : cityService.getAllCities()) {

            if (city.getId_city() == address.getId_city()) {
                return city;
            }
        }

        return null;
    }

    private String getGalleryHoursText(int idGallery) {

        StringBuilder hours = new StringBuilder();

        String sql = """
                SELECT day_of_week, open_time, close_time
                FROM Gallery_Hours
                WHERE id_gallery = ?
                ORDER BY id_gallery, day_of_week
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        DatabaseConfig.URL,
                        DatabaseConfig.USER,
                        DatabaseConfig.PASSWORD);

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, idGallery);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                if (hours.length() > 0) {
                    hours.append(" | ");
                }

                hours.append(resultSet.getString("day_of_week"))
                        .append(": ")
                        .append(resultSet.getString("open_time"))
                        .append("-")
                        .append(resultSet.getString("close_time"));
            }

        } catch (Exception e) {
            return "";
        }

        return hours.toString();
    }

    private String safeString(String value) {

        if (value == null) {
            return "";
        }

        return value;
    }

    @FXML
    private void handleSearch() {

        String keyword = searchField.getText();

        if (keyword == null) {
            keyword = "";
        }

        keyword = keyword.toLowerCase();

        ObservableList<Gallery> filteredList =
                FXCollections.observableArrayList();

        for (Gallery gallery : galleryList) {

            Address address = getAddressById(
                    gallery.getAddress_id());

            City city = getCityByGallery(gallery);

            String hours =
                    getGalleryHoursText(gallery.getId_gallery());

            boolean matches =
                    String.valueOf(gallery.getId_gallery()).contains(keyword)
                    || safeString(gallery.getName_gallery()).toLowerCase().contains(keyword)
                    || String.valueOf(gallery.getRating()).contains(keyword)
                    || safeString(gallery.getWebsite_gallery()).toLowerCase().contains(keyword)
                    || String.valueOf(gallery.getAddress_id()).contains(keyword)
                    || addressMatches(address, keyword)
                    || cityMatches(city, keyword)
                    || hours.toLowerCase().contains(keyword);

            if (matches) {
                filteredList.add(gallery);
            }
        }

        galleryTable.setItems(filteredList);
    }

    private boolean addressMatches(Address address, String keyword) {

        if (address == null) {
            return false;
        }

        return String.valueOf(address.getAddress_id()).contains(keyword)
                || String.valueOf(address.getNumber()).contains(keyword)
                || safeString(address.getStreet()).toLowerCase().contains(keyword)
                || String.valueOf(address.getId_city()).contains(keyword);
    }

    private boolean cityMatches(City city, String keyword) {

        if (city == null) {
            return false;
        }

        return String.valueOf(city.getId_city()).contains(keyword)
                || safeString(city.getCity()).toLowerCase().contains(keyword)
                || String.valueOf(city.getCode()).contains(keyword)
                || safeString(city.getCountry()).toLowerCase().contains(keyword);
    }

    @FXML
    private void handleReset() {

        searchField.clear();

        loadGalleries();
    }
}