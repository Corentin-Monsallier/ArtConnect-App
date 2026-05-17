package com.project.artconnect.ui;

import java.sql.*;

import com.project.artconnect.model.MembershipType;
import com.project.artconnect.model.UserRole;
import com.project.artconnect.util.ConnectionManager;
import com.project.artconnect.util.NavigationHelper;
import com.project.artconnect.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField birthYearField;
    @FXML private TextField phoneField;
    @FXML private TextField cityField;
    @FXML private ComboBox<MembershipType> membershipCombo;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        membershipCombo.setItems(FXCollections.observableArrayList(MembershipType.values()));
        membershipCombo.setValue(MembershipType.STANDARD);
        String pending = SessionManager.getInstance().getPendingEmail();
        if (!pending.isEmpty()) emailField.setText(pending);
    }

    @FXML
    private void handleRegister() {
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Name, email and password are required.");
            return;
        }

        // Check email not taken
        try (Connection conn = ConnectionManager.getConnection()) {
            PreparedStatement check = conn.prepareStatement("SELECT id_user FROM User_ WHERE email=?");
            check.setString(1, email);
            if (check.executeQuery().next()) {
                errorLabel.setText("Email already registered. Please login.");
                return;
            }

            // Parse birth year
            int birthYear = 0;
            try { birthYear = Integer.parseInt(birthYearField.getText().trim()); } catch (NumberFormatException ignored) {}

            conn.setAutoCommit(false);
            try {
                // Insert User_
                PreparedStatement us = conn.prepareStatement(
                        "INSERT INTO User_(name_user, email, birth_year, phone, city, password_user, role_user) VALUES (?, ?, ?, ?, ?, ?, 'MEMBER')",
                        Statement.RETURN_GENERATED_KEYS);
                us.setString(1, name);
                us.setString(2, email);
                us.setInt(3, birthYear);
                us.setString(4, phoneField.getText().trim());
                us.setString(5, cityField.getText().trim());
                us.setString(6, password);
                us.executeUpdate();
                ResultSet keys = us.getGeneratedKeys();
                int userId = keys.next() ? keys.getInt(1) : -1;
                if (userId == -1) { conn.rollback(); errorLabel.setText("Failed to create user."); return; }

                // Insert Member_
                String membershipVal = membershipCombo.getValue() == null
                        ? "standard" : membershipCombo.getValue().name().toLowerCase();
                PreparedStatement ms = conn.prepareStatement(
                        "INSERT INTO Member_(id_user, membership_type) VALUES (?, ?)");
                ms.setInt(1, userId);
                ms.setString(2, membershipVal);
                ms.executeUpdate();

                conn.commit();

                // Auto-login
                SessionManager.getInstance().login(userId, name, email, UserRole.MEMBER);
                SessionManager.getInstance().setPendingEmail("");
                NavigationHelper.goToRoleView(getStage());

            } catch (SQLException e) {
                conn.rollback();
                errorLabel.setText("Registration failed: " + e.getMessage());
            }

        } catch (SQLException e) {
            errorLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        NavigationHelper.goToLogin(getStage());
    }

    private Stage getStage() { return (Stage) nameField.getScene().getWindow(); }
}