package com.project.artconnect.util;

import com.project.artconnect.model.UserRole;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationHelper {

    public static void navigateTo(Stage stage, String fxmlPath, String title, double w, double h) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    NavigationHelper.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), w, h);
            stage.setTitle(title);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void goToViewer(Stage stage) {
        navigateTo(stage, "/com/project/artconnect/ui/ViewerView.fxml", "ArtConnect – Browse", 1200, 800);
    }

    public static void goToLogin(Stage stage) {
        navigateTo(stage, "/com/project/artconnect/ui/LoginView.fxml", "ArtConnect – Login", 600, 500);
    }

    public static void goToRegister(Stage stage) {
        navigateTo(stage, "/com/project/artconnect/ui/RegisterView.fxml", "ArtConnect – Register", 600, 600);
    }

    public static void goToRoleView(Stage stage) {
        SessionManager session = SessionManager.getInstance();
        UserRole role = session.getRole();
        switch (role) {
            case ADMIN  -> navigateTo(stage, "/com/project/artconnect/ui/MainView.fxml",
                    "ArtConnect Pro – Admin", 1200, 800);
            case MEMBER -> navigateTo(stage, "/com/project/artconnect/ui/MemberView.fxml",
                    "ArtConnect – " + session.getUserName(), 1200, 800);
            case ARTIST -> navigateTo(stage, "/com/project/artconnect/ui/ArtistView.fxml",
                    "ArtConnect – " + session.getUserName(), 1200, 800);
            default     -> goToViewer(stage);
        }
    }
}