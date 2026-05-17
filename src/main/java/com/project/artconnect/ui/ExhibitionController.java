package com.project.artconnect.ui;

import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.service.ExhibitionService;
import com.project.artconnect.service.GalleryService;
import com.project.artconnect.util.ServiceProvider;
import com.project.artconnect.util.ViewHelper;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class ExhibitionController {

    @FXML private TableView<Exhibition> exhibitionTable;
    @FXML private TableColumn<Exhibition, Integer> idColumn;
    @FXML private TableColumn<Exhibition, String> titleColumn;
    @FXML private TableColumn<Exhibition, String> curatorColumn;
    @FXML private TableColumn<Exhibition, String> startDateColumn;
    @FXML private TableColumn<Exhibition, String> endDateColumn;
    @FXML private TableColumn<Exhibition, String> themeColumn;
    @FXML private TableColumn<Exhibition, String> descriptionColumn;
    @FXML private TableColumn<Exhibition, Integer> galleryColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> themeFilter;

    // Artworks in selected exhibition
    @FXML private TableView<Map<String, String>> exhibitionArtworkTable;
    @FXML private TableColumn<Map<String, String>, String> eExArtTitleCol;
    @FXML private TableColumn<Map<String, String>, String> eExArtArtistCol;
    @FXML private TableColumn<Map<String, String>, String> eExArtTypeCol;
    @FXML private TableColumn<Map<String, String>, String> eExArtStatusCol;

    private final ExhibitionService exhibitionService = ServiceProvider.getExhibitionService();
    private final GalleryService galleryService = ServiceProvider.getGalleryService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_exhibition"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title_exhib"));
        curatorColumn.setCellValueFactory(new PropertyValueFactory<>("curator_name"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("start_date"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("end_date"));
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        galleryColumn.setCellValueFactory(new PropertyValueFactory<>("id_gallery"));
        loadExhibitions();
        loadThemes();
        themeFilter.setOnAction(e -> handleSearch());

        // Artworks in selected exhibition
        if (exhibitionArtworkTable != null) {
            eExArtTitleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOrDefault("title_art", "")));
            eExArtArtistCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOrDefault("artist_name", "")));
            eExArtTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOrDefault("type", "")));
            eExArtStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOrDefault("status", "")));
            exhibitionTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
                if (sel == null) { exhibitionArtworkTable.getItems().clear(); return; }
                exhibitionArtworkTable.setItems(FXCollections.observableArrayList(ViewHelper.query(
                        "SELECT a.title_art, u.name_user AS artist_name, a.type, a.status " +
                                "FROM Exhibition_Artwork ea " +
                                "JOIN Artwork a ON ea.id_artwork = a.id_artwork " +
                                "JOIN Artist ar ON a.id_artist = ar.id_artist " +
                                "JOIN User_ u ON ar.id_user = u.id_user " +
                                "WHERE ea.id_exhibition=? ORDER BY a.title_art",
                        sel.getId_exhibition())));
            });
        }
    }

    private void loadExhibitions() {
        exhibitionTable.setItems(FXCollections.observableArrayList(exhibitionService.getAllExhibitions()));
    }

    private void loadThemes() {
        ObservableList<String> themes = FXCollections.observableArrayList("All themes");
        exhibitionService.getAllThemes().stream().filter(t -> t != null && !themes.contains(t)).forEach(themes::add);
        themeFilter.setItems(themes);
        themeFilter.setValue("All themes");
    }

    @FXML private void handleSearch() {
        String search = searchField.getText().toLowerCase();
        String sel = themeFilter.getValue();
        ObservableList<Exhibition> filtered = FXCollections.observableArrayList();
        for (Exhibition e : exhibitionService.getAllExhibitions()) {
            boolean t = e.getTitle_exhib().toLowerCase().contains(search);
            boolean th = sel.equals("All themes") || e.getTheme().equalsIgnoreCase(sel);
            if (t && th) filtered.add(e);
        }
        exhibitionTable.setItems(filtered);
    }

    @FXML private void handleReset() { searchField.clear(); themeFilter.setValue("All themes"); loadExhibitions(); }

    @FXML private void handleAdd() {
        buildDialog(null).showAndWait().ifPresent(e -> { exhibitionService.createExhibition(e); loadExhibitions(); loadThemes(); });
    }

    @FXML private void handleEdit() {
        Exhibition sel = exhibitionTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select an exhibition to edit."); return; }
        buildDialog(sel).showAndWait().ifPresent(e -> { exhibitionService.updateExhibition(e); loadExhibitions(); loadThemes(); });
    }

    @FXML private void handleDelete() {
        Exhibition sel = exhibitionTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select an exhibition to delete."); return; }
        new Alert(Alert.AlertType.CONFIRMATION,
                "Delete exhibition \"" + sel.getTitle_exhib() + "\"?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try { exhibitionService.deleteExhibition(sel.getId_exhibition()); loadExhibitions(); }
                        catch (Exception e) { warn("Cannot delete: " + e.getMessage()); }
                    }
                });
    }

    private Dialog<Exhibition> buildDialog(Exhibition existing) {
        Dialog<Exhibition> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Exhibition" : "Edit Exhibition");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField titleF   = new TextField(existing != null ? existing.getTitle_exhib() : "");
        TextField curatorF = new TextField(existing != null ? existing.getCurator_name() : "");
        DatePicker startF  = new DatePicker(existing != null ? existing.getStart_date() : LocalDate.now());
        DatePicker endF    = new DatePicker(existing != null ? existing.getEnd_date() : LocalDate.now().plusMonths(1));
        TextField themeF   = new TextField(existing != null ? existing.getTheme() : "");
        TextField descF    = new TextField(existing != null ? existing.getDescription() : "");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        ObservableList<Gallery> galleries = FXCollections.observableArrayList(galleryService.getAllGalleries());
        ComboBox<Gallery> galleryF = new ComboBox<>(galleries);
        galleryF.setConverter(new StringConverter<>() {
            public String toString(Gallery g) {
                return g == null ? "" : "#" + g.getId_gallery() + " – " + g.getName_gallery();
            }

            public Gallery fromString(String s) {
                return null;
            }
        });
        galleryF.setMaxWidth(Double.MAX_VALUE);

        if (existing != null) {
            galleries.stream()
                    .filter(g -> g.getId_gallery() == existing.getId_gallery())
                    .findFirst()
                    .ifPresent(galleryF::setValue);
        }

        VBox box = new VBox(8,
                new Label("Title:"), titleF,
                new Label("Curator:"), curatorF,
                new Label("Start Date:"), startF,
                new Label("End Date:"), endF,
                new Label("Theme:"), themeF,
                new Label("Description:"), descF,
                new Label("Gallery:"), galleryF,
                errorLabel
        );

        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setPrefWidth(460);

        Node saveButton = dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            errorLabel.setText("");

            if (galleryF.getValue() == null) {
                errorLabel.setText("Please select a gallery.");
                event.consume();
                return;
            }

            if (startF.getValue() == null || endF.getValue() == null) {
                errorLabel.setText("Please select both start date and end date.");
                event.consume();
                return;
            }

            if (endF.getValue().isBefore(startF.getValue())) {
                errorLabel.setText("Invalid dates: end date must be after start date.");
                event.consume();
            }
        });

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Exhibition e = existing != null ? existing : new Exhibition();
                e.setTitle_exhib(titleF.getText());
                e.setCurator_name(curatorF.getText());
                e.setStart_date(startF.getValue());
                e.setEnd_date(endF.getValue());
                e.setTheme(themeF.getText());
                e.setDescription(descF.getText());
                e.setId_gallery(galleryF.getValue().getId_gallery());
                return e;
            }
            return null;
        });

        return dialog;
    }

    private void warn(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }
}