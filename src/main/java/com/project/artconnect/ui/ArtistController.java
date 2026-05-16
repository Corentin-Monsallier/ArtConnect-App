package com.project.artconnect.ui;

import java.util.List;
import java.util.stream.Collectors;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.ArtistSocial;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.service.ArtistSocialService;
import com.project.artconnect.service.DisciplineService;
import com.project.artconnect.util.ServiceProvider;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class ArtistController {

    @FXML private TableView<Artist> artistTable;
    @FXML private TableColumn<Artist, Integer> idArtistColumn;
    @FXML private TableColumn<Artist, Integer> idUserColumn;
    @FXML private TableColumn<Artist, String> nameColumn;
    @FXML private TableColumn<Artist, String> cityColumn;
    @FXML private TableColumn<Artist, String> emailColumn;
    @FXML private TableColumn<Artist, Integer> yearColumn;
    @FXML private TableColumn<Artist, String> phoneColumn;
    @FXML private TableColumn<Artist, String> bioColumn;
    @FXML private TableColumn<Artist, String> websiteColumn;
    @FXML private TableColumn<Artist, Boolean> activeColumn;
    @FXML private TableColumn<Artist, String> socialsColumn;
    @FXML private TableColumn<Artist, String> disciplinesColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> activeFilter;

    private final ArtistService artistService = ServiceProvider.getArtistService();
    private final ArtistSocialService artistSocialService = ServiceProvider.getArtistSocialService();
    private final DisciplineService disciplineService = ServiceProvider.getDisciplineService();

    @FXML
    public void initialize() {
        idArtistColumn.setCellValueFactory(new PropertyValueFactory<>("id_artist"));
        idUserColumn.setCellValueFactory(new PropertyValueFactory<>("id_user"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name_user"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("birth_year"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        bioColumn.setCellValueFactory(new PropertyValueFactory<>("bio"));
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website_artist"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("is_active"));
        socialsColumn.setCellValueFactory(c -> new SimpleStringProperty(socials(c.getValue().getId_artist())));
        disciplinesColumn.setCellValueFactory(c -> new SimpleStringProperty(disciplines(c.getValue().getId_artist())));
        activeFilter.setItems(FXCollections.observableArrayList("All activity", "true", "false"));
        activeFilter.setValue("All activity");
        activeFilter.setOnAction(e -> handleSearch());
        loadArtists();
    }

    private void loadArtists() {
        artistTable.setItems(FXCollections.observableArrayList(artistService.getAllArtists()));
    }

    private String socials(int id) {
        List<ArtistSocial> s = artistSocialService.getArtistSocialsByArtistId(id);
        if (s == null || s.isEmpty()) return "";
        return s.stream().map(x -> safe(x.getPlatform()) + ": " + safe(x.getLink())).collect(Collectors.joining(" | "));
    }

    private String disciplines(int id) {
        List<Discipline> d = disciplineService.getDisciplinesByArtistId(id);
        if (d == null || d.isEmpty()) return "";
        return d.stream().map(Discipline::getName_discipline).collect(Collectors.joining(" | "));
    }

    private String safe(String v) { return v == null ? "" : v; }

    @FXML private void handleSearch() {
        String txt = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String av = activeFilter.getValue();
        ObservableList<Artist> filtered = FXCollections.observableArrayList();
        for (Artist a : artistService.getAllArtists()) {
            boolean ms = safe(a.getName_user()).toLowerCase().contains(txt)
                    || safe(a.getCity()).toLowerCase().contains(txt)
                    || safe(a.getEmail()).toLowerCase().contains(txt)
                    || safe(a.getBio()).toLowerCase().contains(txt)
                    || socials(a.getId_artist()).toLowerCase().contains(txt)
                    || disciplines(a.getId_artist()).toLowerCase().contains(txt);
            boolean ma = av == null || av.equals("All activity") || a.isIs_active() == Boolean.parseBoolean(av);
            if (ms && ma) filtered.add(a);
        }
        artistTable.setItems(filtered);
    }

    @FXML private void handleReset() { searchField.clear(); activeFilter.setValue("All activity"); loadArtists(); }

    @FXML private void handleAdd() {
        buildDialog(null).showAndWait().ifPresent(a -> { artistService.createArtist(a); loadArtists(); });
    }

    @FXML private void handleEdit() {
        Artist sel = artistTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select an artist to edit."); return; }
        buildDialog(sel).showAndWait().ifPresent(a -> { artistService.updateArtist(a); loadArtists(); });
    }

    @FXML private void handleDelete() {
        Artist sel = artistTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select an artist to delete."); return; }
        new Alert(Alert.AlertType.CONFIRMATION,
                "Delete artist \"" + sel.getName_user() + "\"?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try { artistService.deleteArtist(sel.getId_artist()); loadArtists(); }
                        catch (Exception e) { warn("Cannot delete: " + e.getMessage()); }
                    }
                });
    }

    private Dialog<Artist> buildDialog(Artist existing) {
        Dialog<Artist> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Artist" : "Edit Artist");
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField nameF    = new TextField(existing != null ? safe(existing.getName_user()) : "");
        TextField emailF   = new TextField(existing != null ? safe(existing.getEmail()) : "");
        TextField yearF    = new TextField(existing != null ? String.valueOf(existing.getBirth_year()) : "");
        TextField phoneF   = new TextField(existing != null ? safe(existing.getPhone()) : "");
        TextField cityF    = new TextField(existing != null ? safe(existing.getCity()) : "");
        TextField bioF     = new TextField(existing != null ? safe(existing.getBio()) : "");
        TextField websiteF = new TextField(existing != null ? safe(existing.getWebsite_artist()) : "");
        CheckBox activeF   = new CheckBox("Active");
        if (existing != null) activeF.setSelected(existing.isIs_active());

        VBox box = new VBox(8,
                new Label("Name:"), nameF,
                new Label("Email:"), emailF,
                new Label("Birth Year:"), yearF,
                new Label("Phone:"), phoneF,
                new Label("City:"), cityF,
                new Label("Bio:"), bioF,
                new Label("Website:"), websiteF,
                activeF
        );
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setPrefWidth(420);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Artist a = existing != null ? existing : new Artist();
                a.setName_user(nameF.getText());
                a.setEmail(emailF.getText());
                try { a.setBirth_year(Integer.parseInt(yearF.getText())); } catch (NumberFormatException ignored) {}
                a.setPhone(phoneF.getText());
                a.setCity(cityF.getText());
                a.setBio(bioF.getText());
                a.setWebsite_artist(websiteF.getText());
                a.setIs_active(activeF.isSelected());
                return a;
            }
            return null;
        });
        return dialog;
    }

    private void warn(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }
}