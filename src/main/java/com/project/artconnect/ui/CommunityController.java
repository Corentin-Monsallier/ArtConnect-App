package com.project.artconnect.ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.project.artconnect.model.Member;
import com.project.artconnect.model.MembershipType;
import com.project.artconnect.service.MemberService;
import com.project.artconnect.util.ConnectionManager;
import com.project.artconnect.util.ServiceProvider;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class CommunityController {

    @FXML private TableView<Member> communityTable;
    @FXML private TableColumn<Member, Integer> memberIdColumn;
    @FXML private TableColumn<Member, Integer> userIdColumn;
    @FXML private TableColumn<Member, String> nameColumn;
    @FXML private TableColumn<Member, String> emailColumn;
    @FXML private TableColumn<Member, Integer> birthYearColumn;
    @FXML private TableColumn<Member, String> phoneColumn;
    @FXML private TableColumn<Member, String> cityColumn;
    @FXML private TableColumn<Member, String> membershipColumn;
    @FXML private TableColumn<Member, String> disciplinesColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> membershipFilter;

    private final MemberService memberService = ServiceProvider.getMemberService();
    private final Map<Integer, String> memberDisciplines = new HashMap<>();

    @FXML
    public void initialize() {
        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("id_member"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id_user"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name_user"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        birthYearColumn.setCellValueFactory(new PropertyValueFactory<>("birth_year"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        membershipColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getMembership_type())));
        disciplinesColumn.setCellValueFactory(c -> new SimpleStringProperty(memberDisciplines.getOrDefault(c.getValue().getId_member(), "")));
        loadMembers();
        loadMembershipTypes();
        membershipFilter.setOnAction(e -> handleSearch());
    }

    private void loadMembers() {
        loadMemberDisciplines();
        communityTable.setItems(FXCollections.observableArrayList(memberService.getAllMembers()));
    }

    private void loadMemberDisciplines() {
        memberDisciplines.clear();
        String sql = "SELECT md.id_member, d.name_discipline FROM Member_Discipline md JOIN Discipline d ON md.id_discipline = d.id_discipline ORDER BY md.id_member";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement s = c.prepareStatement(sql);
             ResultSet rs = s.executeQuery()) {
            while (rs.next())
                memberDisciplines.merge(rs.getInt("id_member"), rs.getString("name_discipline"), (a, b) -> a + ", " + b);
        } catch (SQLException e) { System.out.println(e); }
    }

    private void loadMembershipTypes() {
        ObservableList<String> types = FXCollections.observableArrayList("All membership types");
        for (Member m : memberService.getAllMembers()) {
            String t = String.valueOf(m.getMembership_type());
            if (!t.equals("null") && !types.contains(t)) types.add(t);
        }
        membershipFilter.setItems(types);
        membershipFilter.setValue("All membership types");
    }

    private String safe(String v) { return v == null ? "" : v; }

    @FXML private void handleSearch() {
        String txt = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String sel = membershipFilter.getValue();
        ObservableList<Member> filtered = FXCollections.observableArrayList();
        for (Member m : memberService.getAllMembers()) {
            boolean ms = safe(m.getName_user()).toLowerCase().contains(txt)
                    || safe(m.getEmail()).toLowerCase().contains(txt)
                    || safe(m.getCity()).toLowerCase().contains(txt)
                    || memberDisciplines.getOrDefault(m.getId_member(), "").toLowerCase().contains(txt);
            boolean mm = sel == null || sel.equals("All membership types")
                    || String.valueOf(m.getMembership_type()).equalsIgnoreCase(sel);
            if (ms && mm) filtered.add(m);
        }
        communityTable.setItems(filtered);
    }

    @FXML private void handleReset() { searchField.clear(); membershipFilter.setValue("All membership types"); loadMembers(); }

    @FXML private void handleAdd() {
        buildDialog(null).showAndWait().ifPresent(m -> { memberService.createMember(m); loadMembers(); loadMembershipTypes(); });
    }

    @FXML private void handleEdit() {
        Member sel = communityTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a member to edit."); return; }
        buildDialog(sel).showAndWait().ifPresent(m -> { memberService.updateMember(m); loadMembers(); });
    }

    @FXML private void handleDelete() {
        Member sel = communityTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a member to delete."); return; }
        new Alert(Alert.AlertType.CONFIRMATION,
                "Delete member \"" + sel.getName_user() + "\"?", ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try { memberService.deleteMember(sel.getId_member()); loadMembers(); }
                        catch (Exception e) { warn("Cannot delete: " + e.getMessage()); }
                    }
                });
    }

    private Dialog<Member> buildDialog(Member existing) {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Member" : "Edit Member");
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField nameF  = new TextField(existing != null ? safe(existing.getName_user()) : "");
        TextField emailF = new TextField(existing != null ? safe(existing.getEmail()) : "");
        TextField yearF  = new TextField(existing != null ? String.valueOf(existing.getBirth_year()) : "");
        TextField phoneF = new TextField(existing != null ? safe(existing.getPhone()) : "");
        TextField cityF  = new TextField(existing != null ? safe(existing.getCity()) : "");
        ComboBox<MembershipType> membershipF = new ComboBox<>(FXCollections.observableArrayList(MembershipType.values()));
        if (existing != null) membershipF.setValue(existing.getMembership_type());
        membershipF.setMaxWidth(Double.MAX_VALUE);

        VBox box = new VBox(8,
                new Label("Name:"), nameF,
                new Label("Email:"), emailF,
                new Label("Birth Year:"), yearF,
                new Label("Phone:"), phoneF,
                new Label("City:"), cityF,
                new Label("Membership:"), membershipF
        );
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setPrefWidth(400);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Member m = existing != null ? existing : new Member();
                m.setName_user(nameF.getText());
                m.setEmail(emailF.getText());
                try { m.setBirth_year(Integer.parseInt(yearF.getText())); } catch (NumberFormatException ignored) {}
                m.setPhone(phoneF.getText());
                m.setCity(cityF.getText());
                m.setMembership_type(membershipF.getValue());
                return m;
            }
            return null;
        });
        return dialog;
    }

    private void warn(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }
}