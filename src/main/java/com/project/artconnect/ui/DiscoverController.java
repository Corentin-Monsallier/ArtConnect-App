package com.project.artconnect.ui;

import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.service.ExhibitionService;
import com.project.artconnect.service.WorkshopService;
import com.project.artconnect.util.ServiceProvider;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class DiscoverController {

    @FXML
    private FlowPane discoverPane;

    private final ExhibitionService exhibitionService = ServiceProvider.getExhibitionService();

    private final WorkshopService workshopService = ServiceProvider.getWorkshopService();

    @FXML
    public void initialize() {

        exhibitionService.getAllExhibitions()
                .stream()
                .limit(3)
                .forEach(this::addExhibitionCard);

        workshopService.getAllWorkshops()
                .stream()
                .limit(3)
                .forEach(this::addWorkshopCard);
    }

    private void addExhibitionCard(Exhibition exhibition) {

        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle(
                "-fx-background-color: #e3f2fd;" +
                "-fx-border-color: #2196f3;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;"
        );
        card.setPrefWidth(250);

        Label title = new Label(exhibition.getTitle_exhib());
        title.setStyle("-fx-font-weight: bold;");

        card.getChildren().addAll(
                new Label("FEATURED EXHIBITION"),
                title,
                new Label("Theme: " + exhibition.getTheme()),
                new Label("Gallery ID: " + exhibition.getId_gallery())
        );

        discoverPane.getChildren().add(card);
    }

    private void addWorkshopCard(Workshop workshop) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle(
                "-fx-background-color: #f1f8e9;" +
                "-fx-border-color: #4caf50;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;"
        );
        card.setPrefWidth(250);

        Label title = new Label(workshop.getTitle_workshop());
        title.setStyle(
                "-fx-font-weight: bold;"
        );

        card.getChildren().addAll(
                new Label("UPCOMING WORKSHOP"),
                title,
                new Label("Level: " + workshop.getLevel()),
                new Label("Price: $" + workshop.getPrice()),
                new Label("Artist ID: " + workshop.getId_artist())
        );

        discoverPane.getChildren().add(card);
    }
}