package com.project.artconnect.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.service.WorkshopService;
import com.project.artconnect.util.ServiceProvider;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class WorkshopController {

    @FXML private TableView<Workshop> workshopTable;
    @FXML private TableColumn<Workshop, Integer> idColumn;
    @FXML private TableColumn<Workshop, String> titleColumn;
    @FXML private TableColumn<Workshop, String> dateColumn;
    @FXML private TableColumn<Workshop, Integer> durationColumn;
    @FXML private TableColumn<Workshop, Integer> maxParticipantsColumn;
    @FXML private TableColumn<Workshop, Double> priceColumn;
    @FXML private TableColumn<Workshop, String> levelColumn;
    @FXML private TableColumn<Workshop, String> locationColumn;
    @FXML private TableColumn<Workshop, String> descriptionColumn;
    @FXML private TableColumn<Workshop, Integer> artistIdColumn;
    @FXML private TableColumn<Workshop, String> artistNameColumn;
    @FXML private TableColumn<Workshop, String> artistEmailColumn;
    @FXML private TableColumn<Workshop, String> artistCityColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> levelFilter;

    private final WorkshopService workshopService = ServiceProvider.getWorkshopService();
    private final ArtistService artistService = ServiceProvider.getArtistService();
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_workshop"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title_workshop"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration_minutes"));
        maxParticipantsColumn.setCellValueFactory(new PropertyValueFactory<>("max_participants"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        artistIdColumn.setCellValueFactory(new PropertyValueFactory<>("id_artist"));
        dateColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDate_workshop() == null ? "" : c.getValue().getDate_workshop().format(DT_FMT)));
        artistNameColumn.setCellValueFactory(c -> { Artist a = getArtist(c.getValue().getId_artist()); return new SimpleStringProperty(a == null ? "" : safe(a.getName_user())); });
        artistEmailColumn.setCellValueFactory(c -> { Artist a = getArtist(c.getValue().getId_artist()); return new SimpleStringProperty(a == null ? "" : safe(a.getEmail())); });
        artistCityColumn.setCellValueFactory(c -> { Artist a = getArtist(c.getValue().getId_artist()); return new SimpleStringProperty(a == null ? "" : safe(a.getCity())); });
        loadWorkshops(); loadLevels();
        levelFilter.setOnAction(e -> handleSearch());
    }

    private void loadWorkshops() { workshopTable.setItems(FXCollections.observableArrayList(workshopService.getAllWorkshops())); }
    private void loadLevels() {
        ObservableList<String> levels = FXCollections.observableArrayList("All Levels");
        levels.addAll(workshopService.getAllLevels());
        levelFilter.setItems(levels); levelFilter.setValue("All Levels");
    }
    private Artist getArtist(int id) { return artistService.getAllArtists().stream().filter(a -> a.getId_artist() == id).findFirst().orElse(null); }
    private String safe(String v) { return v == null ? "" : v; }

    @FXML private void handleSearch() {
        String txt = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String sel = levelFilter.getValue();
        ObservableList<Workshop> filtered = FXCollections.observableArrayList();
        for (Workshop w : workshopService.getAllWorkshops()) {
            Artist a = getArtist(w.getId_artist());
            boolean ms = safe(w.getTitle_workshop()).toLowerCase().contains(txt)
                    || safe(w.getLevel()).toLowerCase().contains(txt)
                    || safe(w.getLocation()).toLowerCase().contains(txt)
                    || (a != null && safe(a.getName_user()).toLowerCase().contains(txt));
            boolean ml = sel == null || sel.equals("All Levels") || safe(w.getLevel()).equalsIgnoreCase(sel);
            if (ms && ml) filtered.add(w);
        }
        workshopTable.setItems(filtered);
    }

    @FXML private void handleReset() { searchField.clear(); levelFilter.setValue("All Levels"); loadWorkshops(); }

    @FXML private void handleAdd() {
        buildDialog(null).showAndWait().ifPresent(w -> { workshopService.createWorkshop(w); loadWorkshops(); loadLevels(); });
    }

    @FXML private void handleEdit() {
        Workshop sel = workshopTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a workshop to edit."); return; }
        buildDialog(sel).showAndWait().ifPresent(w -> { workshopService.updateWorkshop(w); loadWorkshops(); });
    }

    @FXML private void handleDelete() {
        Workshop sel = workshopTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a workshop to delete."); return; }
        new Alert(Alert.AlertType.CONFIRMATION,
                "Delete workshop \"" + sel.getTitle_workshop() + "\"?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try { workshopService.deleteWorkshop(sel.getId_workshop()); loadWorkshops(); }
                        catch (Exception e) { warn("Cannot delete: " + e.getMessage()); }
                    }
                });
    }

    private Dialog<Workshop> buildDialog(Workshop existing) {
        Dialog<Workshop> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Workshop" : "Edit Workshop");
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField titleF    = new TextField(existing != null ? existing.getTitle_workshop() : "");
        TextField dateF     = new TextField(existing != null && existing.getDate_workshop() != null ? existing.getDate_workshop().format(DT_FMT) : "");
        dateF.setPromptText("yyyy-MM-dd HH:mm");
        TextField durationF = new TextField(existing != null ? String.valueOf(existing.getDuration_minutes()) : "");
        TextField maxF      = new TextField(existing != null ? String.valueOf(existing.getMax_participants()) : "");
        TextField priceF    = new TextField(existing != null ? String.valueOf(existing.getPrice()) : "");
        TextField levelF    = new TextField(existing != null ? safe(existing.getLevel()) : "");
        TextField locationF = new TextField(existing != null ? safe(existing.getLocation()) : "");
        TextField descF     = new TextField(existing != null ? safe(existing.getDescription()) : "");

        List<Artist> artists = artistService.getAllArtists();
        ComboBox<Artist> artistF = new ComboBox<>(FXCollections.observableArrayList(artists));
        artistF.setConverter(new StringConverter<>() {
            public String toString(Artist a) { return a == null ? "" : "#" + a.getId_artist() + " – " + a.getName_user(); }
            public Artist fromString(String s) { return null; }
        });
        artistF.setMaxWidth(Double.MAX_VALUE);
        if (existing != null) artists.stream().filter(a -> a.getId_artist() == existing.getId_artist()).findFirst().ifPresent(artistF::setValue);

        VBox box = new VBox(8,
                new Label("Title:"), titleF,
                new Label("Date (yyyy-MM-dd HH:mm):"), dateF,
                new Label("Duration (min):"), durationF,
                new Label("Max Participants:"), maxF,
                new Label("Price:"), priceF,
                new Label("Level:"), levelF,
                new Label("Location:"), locationF,
                new Label("Description:"), descF,
                new Label("Artist:"), artistF
        );
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setPrefWidth(460);
        dialog.getDialogPane().setPrefHeight(560);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                if (artistF.getValue() == null) { warn("Please select an artist."); return null; }
                Workshop w = existing != null ? existing : new Workshop();
                w.setTitle_workshop(titleF.getText());
                try { w.setDate_workshop(LocalDateTime.parse(dateF.getText(), DT_FMT)); } catch (DateTimeParseException ignored) {}
                try { w.setDuration_minutes(Integer.parseInt(durationF.getText())); } catch (NumberFormatException ignored) {}
                try { w.setMax_participants(Integer.parseInt(maxF.getText())); } catch (NumberFormatException ignored) {}
                try { w.setPrice(Double.parseDouble(priceF.getText())); } catch (NumberFormatException ignored) {}
                w.setLevel(levelF.getText()); w.setLocation(locationF.getText()); w.setDescription(descF.getText());
                w.setId_artist(artistF.getValue().getId_artist());
                return w;
            }
            return null;
        });
        return dialog;
    }

    private void warn(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }
}