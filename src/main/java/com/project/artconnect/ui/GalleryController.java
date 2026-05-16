package com.project.artconnect.ui;

import java.sql.*;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

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

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_gallery"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name_gallery"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website_gallery"));
        addressIdColumn.setCellValueFactory(new PropertyValueFactory<>("address_id"));
        numberColumn.setCellValueFactory(c -> { Address a = addr(c.getValue()); return new SimpleIntegerProperty(a==null?0:a.getNumber()).asObject(); });
        streetColumn.setCellValueFactory(c -> { Address a = addr(c.getValue()); return new SimpleStringProperty(a==null?"":safe(a.getStreet())); });
        cityIdColumn.setCellValueFactory(c -> { Address a = addr(c.getValue()); return new SimpleIntegerProperty(a==null?0:a.getId_city()).asObject(); });
        cityColumn.setCellValueFactory(c -> { City ct = city(c.getValue()); return new SimpleStringProperty(ct==null?"":safe(ct.getCity())); });
        codeColumn.setCellValueFactory(c -> { City ct = city(c.getValue()); return new SimpleIntegerProperty(ct==null?0:ct.getCode()).asObject(); });
        countryColumn.setCellValueFactory(c -> { City ct = city(c.getValue()); return new SimpleStringProperty(ct==null?"":safe(ct.getCountry())); });
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
        buildDialog(null).showAndWait().ifPresent(g -> { galleryService.createGallery(g); loadGalleries(); });
    }

    @FXML private void handleEdit() {
        Gallery sel = galleryTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a gallery to edit."); return; }
        buildDialog(sel).showAndWait().ifPresent(g -> { galleryService.updateGallery(g); loadGalleries(); });
    }

    @FXML private void handleDelete() {
        Gallery sel = galleryTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a gallery to delete."); return; }
        new Alert(Alert.AlertType.CONFIRMATION,
                "Delete gallery \"" + sel.getName_gallery() + "\"?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try { galleryService.deleteGallery(sel.getId_gallery()); loadGalleries(); }
                        catch (Exception e) { warn("Cannot delete: " + e.getMessage()); }
                    }
                });
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

    private Dialog<Gallery> buildDialog(Gallery existing) {
        Dialog<Gallery> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Gallery" : "Edit Gallery");
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

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

        grid.addRow(0, new Label("Name:"), nameF);
        grid.addRow(1, new Label("Rating (1-5):"), ratingF);
        grid.addRow(2, new Label("Website:"), websiteF);
        grid.addRow(3, new Label("Street Number:"), numberF);
        grid.addRow(4, new Label("Street:"), streetF);
        grid.addRow(5, new Label("City:"), cityF);
        grid.addRow(6, new Label("Postal Code:"), postalF);
        grid.addRow(7, new Label("Country:"), countryF);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(480);

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
                    return g;
                } catch (NumberFormatException e) { warn("Street number and postal code must be numbers."); return null; }
            }
            return null;
        });
        return dialog;
    }

    private void warn(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }
}