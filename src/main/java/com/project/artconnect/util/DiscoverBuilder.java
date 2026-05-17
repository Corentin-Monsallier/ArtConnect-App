package com.project.artconnect.util;

import java.util.List;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class DiscoverBuilder {

    public static void populate(FlowPane pane) {
        pane.getChildren().clear();

        // Featured exhibitions with gallery name
        List<Map<String, String>> exhibitions = ViewHelper.query(
                "SELECT e.title_exhib, e.theme, g.name_gallery " +
                        "FROM Exhibition e JOIN Gallery g ON e.id_gallery = g.id_gallery " +
                        "ORDER BY e.start_date DESC LIMIT 3");

        for (Map<String, String> row : exhibitions) {
            VBox card = makeCard("#e3f2fd", "#2196f3");
            card.getChildren().addAll(
                    bold("FEATURED EXHIBITION"),
                    bold(row.getOrDefault("title_exhib", "")),
                    new Label("Theme: " + row.getOrDefault("theme", "")),
                    new Label("Gallery: " + row.getOrDefault("name_gallery", ""))
            );
            pane.getChildren().add(card);
        }

        // Upcoming workshops with instructor name
        List<Map<String, String>> workshops = ViewHelper.query(
                "SELECT w.title_workshop, w.level, w.price, u.name_user AS instructor " +
                        "FROM Workshop w JOIN Artist a ON w.id_artist = a.id_artist " +
                        "JOIN User_ u ON a.id_user = u.id_user " +
                        "ORDER BY w.date_workshop ASC LIMIT 3");

        for (Map<String, String> row : workshops) {
            VBox card = makeCard("#f1f8e9", "#4caf50");
            card.getChildren().addAll(
                    bold("UPCOMING WORKSHOP"),
                    bold(row.getOrDefault("title_workshop", "")),
                    new Label("Instructor: " + row.getOrDefault("instructor", "")),
                    new Label("Level: " + row.getOrDefault("level", "")),
                    new Label("Price: $" + row.getOrDefault("price", ""))
            );
            pane.getChildren().add(card);
        }
    }

    private static VBox makeCard(String bg, String border) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color:" + bg + ";-fx-border-color:" + border +
                ";-fx-border-radius:5;-fx-background-radius:5;");
        card.setPrefWidth(250);
        return card;
    }

    private static Label bold(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold;");
        l.setWrapText(true);
        return l;
    }
}