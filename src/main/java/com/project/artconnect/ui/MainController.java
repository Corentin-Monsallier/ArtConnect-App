package com.project.artconnect.ui;

import com.project.artconnect.util.NavigationHelper;
import com.project.artconnect.util.SessionManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class MainController {

    @FXML private TabPane mainTabPane;
    @FXML private Label adminLabel;

    @FXML
    public void initialize() {
        if (adminLabel != null)
            adminLabel.setText("Logged in as: " + SessionManager.getInstance().getUserName());
    }

    @FXML private void handleExit() { Platform.exit(); }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        NavigationHelper.goToViewer((Stage) mainTabPane.getScene().getWindow());
    }
}