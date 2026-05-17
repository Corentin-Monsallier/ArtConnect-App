package com.project.artconnect.ui;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.artconnect.config.DatabaseConfig;
import com.project.artconnect.model.Address;
import com.project.artconnect.model.City;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.service.AddressService;
import com.project.artconnect.service.CityService;
import com.project.artconnect.service.GalleryService;
import com.project.artconnect.util.ConnectionManager;
import com.project.artconnect.util.ServiceProvider;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class GalleryController {

    @FXML private TableView<Gallery> galleryTable;
    @FXML private TableColumn<Gallery, Integer> idColumn;
    @FXML private TableColumn<Gallery, String> nameColumn;
    @FXML private TableColumn<Gallery, Integer> ratingColumn;
    @FXML private TableColumn<Gallery, String> websiteColumn;
    @FXML private TableColumn<Gallery, Integer> addressIdColumn;
    @FXML private TableColumn<Gallery, Integer> numberColumn;
    @FXML private TableColumn<Gallery, String> streetColumn;
    @FXML private TableColumn<Gallery, Integer> cityIdColumn;
    @FXML private TableColumn<Gallery, String> cityColumn;
    @FXML private TableColumn<Gallery, Integer> codeColumn;
    @FXML private TableColumn<Gallery, String> countryColumn;
    @FXML private TableColumn<Gallery, String> hoursColumn;
    @FXML private TextField searchField;

    private ObservableList<Gallery> galleryList;
    private final GalleryService galleryService = ServiceProvider.getGalleryService();
    private final AddressService addressService = ServiceProvider.getAddressService();
    private final CityService cityService = ServiceProvider.getCityService();
    private final Map<Integer, String> galleryHoursMap = new HashMap<>();

    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_gallery"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name_gallery"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website_gallery"));
        addressIdColumn.setCellValueFactory(new PropertyValueFactory<>("address_id"));
        numberColumn.setCellValueFactory(c -> { Address a = addr(c.getValue()); return new SimpleIntegerProperty(a == null ? 0 : a.getNumber()).asObject(); });
        streetColumn.setCellValueFactory(c -> { Address a = addr(c.getValue()); return new SimpleStringProperty(a == null ? "" : safe(a.getStreet())); });
        cityIdColumn.setCellValueFactory(c -> { Address a = addr(c.getValue()); return new SimpleIntegerProperty(a == null ? 0 : a.getId_city()).asObject(); });
        cityColumn.setCellValueFactory(c -> { City ct = city(c.getValue()); return new SimpleStringProperty(ct == null ? "" : safe(ct.getCity())); });
        codeColumn.setCellValueFactory(c -> { City ct = city(c.getValue()); return new SimpleIntegerProperty(ct == null ? 0 : ct.getCode()).asObject(); });
        countryColumn.setCellValueFactory(c -> { City ct = city(c.getValue()); return new SimpleStringProperty(ct == null ? "" : safe(ct.getCountry())); });
        hoursColumn.setCellValueFactory(c -> new SimpleStringProperty(galleryHoursMap.getOrDefault(c.getValue().getId_gallery(), "")));
        loadGalleryHours();
        loadGalleries();
    }

    private void loadGalleries() {
        galleryList = FXCollections.observableArrayList(galleryService.getAllGalleries());
        galleryTable.setItems(galleryList);
    }

    private void loadGalleryHours() {
        galleryHoursMap.clear();
        String sql = "SELECT id_gallery, day_of_week, open_time, close_time FROM Gallery_Hours ORDER BY id_gallery, FIELD(day_of_week,'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday')";
        try (Connection c = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement s = c.prepareStatement(sql); ResultSet rs = s.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_gallery");
                String t = rs.getString("day_of_week") + ": " + rs.getString("open_time") + "-" + rs.getString("close_time");
                galleryHoursMap.merge(id, t, (a, b) -> a + " | " + b);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Address addr(Gallery g) {
        return addressService.getAllAddresses().stream().filter(a -> a.getAddress_id() == g.getAddress_id()).findFirst().orElse(null);
    }

    private City city(Gallery g) {
        Address a = addr(g); if (a == null) return null;
        return cityService.getAllCities().stream().filter(c -> c.getId_city() == a.getId_city()).findFirst().orElse(null);
    }

    private String safe(String v) { return v == null ? "" : v; }

    @FXML private void handleSearch() {
        String kw = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        ObservableList<Gallery> filtered = FXCollections.observableArrayList();
        for (Gallery g : galleryList) {
            Address a = addr(g); City c = city(g);
            String hours = galleryHoursMap.getOrDefault(g.getId_gallery(), "");
            if (safe(g.getName_gallery()).toLowerCase().contains(kw)
                    || safe(g.getWebsite_gallery()).toLowerCase().contains(kw)
                    || hours.toLowerCase().contains(kw)
                    || (a != null && (String.valueOf(a.getNumber()).contains(kw) || safe(a.getStreet()).toLowerCase().contains(kw)))
                    || (c != null && (safe(c.getCity()).toLowerCase().contains(kw) || safe(c.getCountry()).toLowerCase().contains(kw))))
                filtered.add(g);
        }
        galleryTable.setItems(filtered);
    }

    @FXML private void handleReset() { searchField.clear(); loadGalleryHours(); loadGalleries(); }

    @FXML private void handleAdd() {
        buildDialog(null).showAndWait().ifPresent(result -> {
            Gallery g = result.gallery;
            galleryService.createGallery(g);
            saveHours(g.getId_gallery(), result.hours);
            loadGalleryHours();
            loadGalleries();
        });
    }

    @FXML private void handleEdit() {
        Gallery sel = galleryTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a gallery to edit."); return; }
        buildDialog(sel).showAndWait().ifPresent(result -> {
            galleryService.updateGallery(result.gallery);
            deleteHours(result.gallery.getId_gallery());
            saveHours(result.gallery.getId_gallery(), result.hours);
            loadGalleryHours();
            loadGalleries();
        });
    }

    @FXML private void handleDelete() {
        Gallery sel = galleryTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a gallery to delete."); return; }
        new Alert(Alert.AlertType.CONFIRMATION,
                "Delete gallery \"" + sel.getName_gallery() + "\"?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try { galleryService.deleteGallery(sel.getId_gallery()); loadGalleryHours(); loadGalleries(); }
                        catch (Exception e) { warn("Cannot delete: " + e.getMessage()); }
                    }
                });
    }

    private void saveHours(int galleryId, List<String[]> hours) {
        String sql = "INSERT INTO Gallery_Hours(id_gallery, day_of_week, open_time, close_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String[] row : hours) {
                if (row[1].isBlank() || row[2].isBlank()) continue;
                ps.setInt(1, galleryId);
                ps.setString(2, row[0]);
                ps.setString(3, row[1]);
                ps.setString(4, row[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void deleteHours(int galleryId) {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Gallery_Hours WHERE id_gallery=?")) {
            ps.setInt(1, galleryId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private int resolveAddressId(int number, String street, String cityName, int postal, String country) {
        List<City> cities = cityService.getAllCities();
        City matchedCity = cities.stream()
                .filter(c -> safe(c.getCity()).equalsIgnoreCase(cityName.trim()) && safe(c.getCountry()).equalsIgnoreCase(country.trim()))
                .findFirst().orElse(null);
        int cityId;
        if (matchedCity != null) {
            cityId = matchedCity.getId_city();
        } else {
            try (Connection conn = ConnectionManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO City(city, code, country) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, cityName.trim()); ps.setInt(2, postal); ps.setString(3, country.trim());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                cityId = rs.next() ? rs.getInt(1) : -1;
            } catch (SQLException e) { e.printStackTrace(); return -1; }
        }
        Address matchedAddr = addressService.getAllAddresses().stream()
                .filter(a -> a.getNumber() == number && safe(a.getStreet()).equalsIgnoreCase(street.trim()) && a.getId_city() == cityId)
                .findFirst().orElse(null);
        if (matchedAddr != null) return matchedAddr.getAddress_id();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO Address(number, street, id_city) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, number); ps.setString(2, street.trim()); ps.setInt(3, cityId);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) { e.printStackTrace(); return -1; }
    }

    private static class DialogResult {
        Gallery gallery;
        List<String[]> hours;
        DialogResult(Gallery gallery, List<String[]> hours) { this.gallery = gallery; this.hours = hours; }
    }

    private Dialog<DialogResult> buildDialog(Gallery existing) {
        Dialog<DialogResult> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Gallery" : "Edit Gallery");
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        Address existingAddr = existing != null ? addr(existing) : null;
        City existingCity = existing != null ? city(existing) : null;

        TextField nameF    = new TextField(existing != null ? existing.getName_gallery() : "");
        TextField ratingF  = new TextField(existing != null ? String.valueOf(existing.getRating()) : "");
        TextField websiteF = new TextField(existing != null ? safe(existing.getWebsite_gallery()) : "");
        TextField numberF  = new TextField(existingAddr != null ? String.valueOf(existingAddr.getNumber()) : "");
        TextField streetF  = new TextField(existingAddr != null ? safe(existingAddr.getStreet()) : "");
        TextField cityF    = new TextField(existingCity != null ? safe(existingCity.getCity()) : "");
        TextField postalF  = new TextField(existingCity != null ? String.valueOf(existingCity.getCode()) : "");
        TextField countryF = new TextField(existingCity != null ? safe(existingCity.getCountry()) : "");

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10); infoGrid.setVgap(8);
        infoGrid.addRow(0, new Label("Name:"), nameF);
        infoGrid.addRow(1, new Label("Rating (1-5):"), ratingF);
        infoGrid.addRow(2, new Label("Website:"), websiteF);
        infoGrid.addRow(3, new Label("Street Number:"), numberF);
        infoGrid.addRow(4, new Label("Street:"), streetF);
        infoGrid.addRow(5, new Label("City:"), cityF);
        infoGrid.addRow(6, new Label("Postal Code:"), postalF);
        infoGrid.addRow(7, new Label("Country:"), countryF);

        Map<String, String[]> existingHoursMap = new HashMap<>();
        if (existing != null) {
            String raw = galleryHoursMap.getOrDefault(existing.getId_gallery(), "");
            for (String part : raw.split(" \\| ")) {
                String[] kv = part.split(": ");
                if (kv.length == 2) {
                    String[] times = kv[1].split("-");
                    if (times.length == 2) existingHoursMap.put(kv[0].trim(), times);
                }
            }
        }

        GridPane hoursGrid = new GridPane();
        hoursGrid.setHgap(10); hoursGrid.setVgap(6);
        hoursGrid.addRow(0, new Label("Day"), new Label("Open (HH:mm)"), new Label("Close (HH:mm)"));
        TextField[] openFields  = new TextField[DAYS.length];
        TextField[] closeFields = new TextField[DAYS.length];
        for (int i = 0; i < DAYS.length; i++) {
            String[] times = existingHoursMap.get(DAYS[i]);
            openFields[i]  = new TextField(times != null ? times[0] : "");
            closeFields[i] = new TextField(times != null ? times[1] : "");
            openFields[i].setPromptText("09:00");
            closeFields[i].setPromptText("18:00");
            hoursGrid.addRow(i + 1, new Label(DAYS[i]), openFields[i], closeFields[i]);
        }

        VBox content = new VBox(12, infoGrid, new Label("Opening Hours:"), hoursGrid);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(new ScrollPane(content));
        dialog.getDialogPane().setPrefWidth(500);
        dialog.getDialogPane().setPrefHeight(600);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                try {
                    int number = Integer.parseInt(numberF.getText().trim());
                    int postal  = Integer.parseInt(postalF.getText().trim());
                    int addrId  = resolveAddressId(number, streetF.getText(), cityF.getText(), postal, countryF.getText());
                    if (addrId == -1) { warn("Failed to save address."); return null; }
                    Gallery g = existing != null ? existing : new Gallery();
                    g.setName_gallery(nameF.getText());
                    try { g.setRating(Integer.parseInt(ratingF.getText())); } catch (NumberFormatException ignored) {}
                    g.setWebsite_gallery(websiteF.getText());
                    g.setAddress_id(addrId);

                    List<String[]> hours = new ArrayList<>();
                    for (int i = 0; i < DAYS.length; i++) {
                        hours.add(new String[]{DAYS[i], openFields[i].getText().trim(), closeFields[i].getText().trim()});
                    }
                    return new DialogResult(g, hours);
                } catch (NumberFormatException e) { warn("Street number and postal code must be numbers."); return null; }
            }
            return null;
        });
        return dialog;
    }

    private void warn(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }
}