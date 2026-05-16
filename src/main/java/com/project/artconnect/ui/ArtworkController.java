package com.project.artconnect.ui;

import java.util.List;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.ArtworkStatus;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.service.ArtworkService;
import com.project.artconnect.util.ServiceProvider;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class ArtworkController {

    @FXML private TableView<Artwork> artworkTable;
    @FXML private TableColumn<Artwork, Integer> idColumn;
    @FXML private TableColumn<Artwork, String> titleColumn;
    @FXML private TableColumn<Artwork, Integer> yearColumn;
    @FXML private TableColumn<Artwork, String> typeColumn;
    @FXML private TableColumn<Artwork, String> mediumColumn;
    @FXML private TableColumn<Artwork, String> dimensionsColumn;
    @FXML private TableColumn<Artwork, String> descriptionColumn;
    @FXML private TableColumn<Artwork, Double> priceColumn;
    @FXML private TableColumn<Artwork, String> statusColumn;
    @FXML private TableColumn<Artwork, Integer> artistColumn;
    @FXML private TableColumn<Artwork, String> tagsColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ComboBox<String> mediumFilter;

    private final ArtworkService artworkService = ServiceProvider.getArtworkService();
    private final ArtistService artistService = ServiceProvider.getArtistService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_artwork"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title_art"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("creation_year"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        mediumColumn.setCellValueFactory(new PropertyValueFactory<>("medium"));
        dimensionsColumn.setCellValueFactory(new PropertyValueFactory<>("dimensions"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("id_artist"));
        tagsColumn.setCellValueFactory(new PropertyValueFactory<>("tags"));
        loadArtworks();
        loadFilters();
        statusFilter.setOnAction(e -> handleSearch());
        typeFilter.setOnAction(e -> handleSearch());
        mediumFilter.setOnAction(e -> handleSearch());
    }

    private void loadArtworks() {
        artworkTable.setItems(FXCollections.observableArrayList(artworkService.getAllArtworks()));
    }

    private void loadFilters() {
        statusFilter.setItems(FXCollections.observableArrayList("All status", "AVAILABLE", "SOLD", "RESERVED"));
        statusFilter.setValue("All status");

        ObservableList<String> types = FXCollections.observableArrayList("All types");
        artworkService.getAllArtworks().stream().map(Artwork::getType)
                .filter(t -> t != null && !t.isBlank()).distinct().sorted().forEach(types::add);
        typeFilter.setItems(types); typeFilter.setValue("All types");

        ObservableList<String> mediums = FXCollections.observableArrayList("All mediums");
        artworkService.getAllArtworks().stream().map(Artwork::getMedium)
                .filter(m -> m != null && !m.isBlank()).distinct().sorted().forEach(mediums::add);
        mediumFilter.setItems(mediums); mediumFilter.setValue("All mediums");
    }

    @FXML private void handleSearch() {
        String search = searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        String type = typeFilter.getValue();
        String medium = mediumFilter.getValue();
        ObservableList<Artwork> filtered = FXCollections.observableArrayList();
        for (Artwork a : artworkService.getAllArtworks()) {
            boolean t = a.getTitle_art() != null && a.getTitle_art().toLowerCase().contains(search);
            boolean s = status.equals("All status") || a.getStatus().name().equalsIgnoreCase(status);
            boolean ty = type.equals("All types") || (a.getType() != null && a.getType().equalsIgnoreCase(type));
            boolean m = medium.equals("All mediums") || (a.getMedium() != null && a.getMedium().equalsIgnoreCase(medium));
            if (t && s && ty && m) filtered.add(a);
        }
        artworkTable.setItems(filtered);
    }

    @FXML private void handleReset() {
        searchField.clear();
        statusFilter.setValue("All status"); typeFilter.setValue("All types"); mediumFilter.setValue("All mediums");
        loadArtworks();
    }

    @FXML private void handleAdd() {
        buildDialog(null).showAndWait().ifPresent(a -> { artworkService.createArtwork(a); loadArtworks(); loadFilters(); });
    }

    @FXML private void handleEdit() {
        Artwork sel = artworkTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select an artwork to edit."); return; }
        buildDialog(sel).showAndWait().ifPresent(a -> { artworkService.updateArtwork(a); loadArtworks(); });
    }

    @FXML private void handleDelete() {
        Artwork sel = artworkTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select an artwork to delete."); return; }
        new Alert(Alert.AlertType.CONFIRMATION,
                "Delete artwork \"" + sel.getTitle_art() + "\"?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try { artworkService.deleteArtwork(sel.getTitle_art()); loadArtworks(); }
                        catch (Exception e) { warn("Cannot delete: " + e.getMessage()); }
                    }
                });
    }

    private Dialog<Artwork> buildDialog(Artwork existing) {
        Dialog<Artwork> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Artwork" : "Edit Artwork");
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField titleF = new TextField(existing != null ? existing.getTitle_art() : "");
        TextField yearF  = new TextField(existing != null ? String.valueOf(existing.getCreation_year()) : "");
        TextField typeF  = new TextField(existing != null ? existing.getType() : "");
        TextField medF   = new TextField(existing != null ? existing.getMedium() : "");
        TextField dimsF  = new TextField(existing != null ? existing.getDimensions() : "");
        TextField descF  = new TextField(existing != null ? existing.getDescription() : "");
        TextField priceF = new TextField(existing != null ? String.valueOf(existing.getPrice()) : "");

        ComboBox<ArtworkStatus> statusF = new ComboBox<>(FXCollections.observableArrayList(ArtworkStatus.values()));
        if (existing != null) statusF.setValue(existing.getStatus());
        statusF.setMaxWidth(Double.MAX_VALUE);

        List<Artist> artists = artistService.getAllArtists();
        ComboBox<Artist> artistF = new ComboBox<>(FXCollections.observableArrayList(artists));
        artistF.setConverter(new StringConverter<>() {
            public String toString(Artist a) { return a == null ? "" : "#" + a.getId_artist() + " – " + a.getName_user(); }
            public Artist fromString(String s) { return null; }
        });
        artistF.setMaxWidth(Double.MAX_VALUE);
        if (existing != null)
            artists.stream().filter(a -> a.getId_artist() == existing.getId_artist()).findFirst().ifPresent(artistF::setValue);

        VBox box = new VBox(8,
                new Label("Title:"), titleF,
                new Label("Creation Year:"), yearF,
                new Label("Type:"), typeF,
                new Label("Medium:"), medF,
                new Label("Dimensions:"), dimsF,
                new Label("Description:"), descF,
                new Label("Price:"), priceF,
                new Label("Status:"), statusF,
                new Label("Artist:"), artistF
        );
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setPrefWidth(440);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                if (artistF.getValue() == null) { warn("Please select an artist."); return null; }
                Artwork a = existing != null ? existing : new Artwork();
                a.setTitle_art(titleF.getText());
                try { a.setCreation_year(Integer.parseInt(yearF.getText())); } catch (NumberFormatException ignored) {}
                a.setType(typeF.getText()); a.setMedium(medF.getText());
                a.setDimensions(dimsF.getText()); a.setDescription(descF.getText());
                try { a.setPrice(Double.parseDouble(priceF.getText())); } catch (NumberFormatException ignored) {}
                a.setStatus(statusF.getValue());
                a.setId_artist(artistF.getValue().getId_artist());
                return a;
            }
            return null;
        });
        return dialog;
    }

    private void warn(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }
}