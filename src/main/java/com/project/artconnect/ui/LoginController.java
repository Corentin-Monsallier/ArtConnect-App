package com.project.artconnect.ui;

import java.sql.*;

import com.project.artconnect.model.UserRole;
import com.project.artconnect.util.ConnectionManager;
import com.project.artconnect.util.NavigationHelper;
import com.project.artconnect.util.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter email and password.");
            return;
        }

        String sql = "SELECT id_user, name_user, email, role_user FROM User_ WHERE email=? AND password_user=?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int      userId    = rs.getInt("id_user");
                String   name      = rs.getString("name_user");
                String   userEmail = rs.getString("email");
                String   roleStr   = rs.getString("role_user").toUpperCase().trim();
                UserRole role;
                try { role = UserRole.valueOf(roleStr); }
                catch (IllegalArgumentException e) { role = UserRole.MEMBER; }

                SessionManager.getInstance().login(userId, name, userEmail, role);
                NavigationHelper.goToRoleView(getStage());
            } else {
                PreparedStatement check = conn.prepareStatement("SELECT id_user FROM User_ WHERE email=?");
                check.setString(1, email);
                errorLabel.setText(check.executeQuery().next() ? "Wrong password." : "Email not found. Would you like to register?");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleGoToRegister() {
        String email = emailField.getText().trim();
        if (!email.isEmpty()) SessionManager.getInstance().setPendingEmail(email);
        NavigationHelper.goToRegister(getStage());
    }

    @FXML
    private void handleBrowseAsGuest() {
        NavigationHelper.goToViewer(getStage());
    }

    private Stage getStage() { return (Stage) emailField.getScene().getWindow(); }
}