package com.project.artconnect.ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.project.artconnect.model.Member;
import com.project.artconnect.service.MemberService;
import com.project.artconnect.util.ConnectionManager;
import com.project.artconnect.util.ServiceProvider;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CommunityController {

    @FXML
    private TableView<Member> communityTable;

    @FXML
    private TableColumn<Member, Integer> memberIdColumn;

    @FXML
    private TableColumn<Member, Integer> userIdColumn;

    @FXML
    private TableColumn<Member, String> nameColumn;

    @FXML
    private TableColumn<Member, String> emailColumn;

    @FXML
    private TableColumn<Member, Integer> birthYearColumn;

    @FXML
    private TableColumn<Member, String> phoneColumn;

    @FXML
    private TableColumn<Member, String> cityColumn;

    @FXML
    private TableColumn<Member, String> membershipColumn;

    @FXML
    private TableColumn<Member, String> disciplinesColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> membershipFilter;

    private final MemberService memberService =
            ServiceProvider.getMemberService();

    private final Map<Integer, String> memberDisciplines = new HashMap<>();

    @FXML
    public void initialize() {

        memberIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("id_member"));

        userIdColumn.setCellValueFactory(
                new PropertyValueFactory<>("id_user"));

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name_user"));

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email"));

        birthYearColumn.setCellValueFactory(
                new PropertyValueFactory<>("birth_year"));

        phoneColumn.setCellValueFactory(
                new PropertyValueFactory<>("phone"));

        cityColumn.setCellValueFactory(
                new PropertyValueFactory<>("city"));

        membershipColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        String.valueOf(cellData.getValue().getMembership_type())));

        disciplinesColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        getMemberDisciplinesText(cellData.getValue().getId_member())));

        loadMembers();
        loadMembershipTypes();

        membershipFilter.setOnAction(event -> handleSearch());
    }

    private void loadMembers() {

        loadMemberDisciplines();

        ObservableList<Member> members =
                FXCollections.observableArrayList(
                        memberService.getAllMembers());

        communityTable.setItems(members);
    }

    private void loadMemberDisciplines() {

        memberDisciplines.clear();

        String sql =
                "SELECT md.id_member, d.name_discipline " +
                "FROM Member_Discipline md " +
                "JOIN Discipline d ON md.id_discipline = d.id_discipline " +
                "ORDER BY md.id_member, d.name_discipline";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {

            while (result.next()) {

                int idMember = result.getInt("id_member");
                String disciplineName = result.getString("name_discipline");

                if (memberDisciplines.containsKey(idMember)) {
                    memberDisciplines.put(
                            idMember,
                            memberDisciplines.get(idMember) + ", " + disciplineName);
                } else {
                    memberDisciplines.put(idMember, disciplineName);
                }
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private String getMemberDisciplinesText(int idMember) {

        String disciplines = memberDisciplines.get(idMember);

        if (disciplines == null) {
            return "";
        }

        return disciplines;
    }

    private void loadMembershipTypes() {

        ObservableList<String> types =
                FXCollections.observableArrayList();

        types.add("All membership types");

        for (Member member : memberService.getAllMembers()) {

            String membershipType =
                    String.valueOf(member.getMembership_type());

            if (membershipType != null
                    && !membershipType.equals("null")
                    && !membershipType.isEmpty()
                    && !types.contains(membershipType)) {

                types.add(membershipType);
            }
        }

        membershipFilter.setItems(types);
        membershipFilter.setValue("All membership types");
    }

    private String safeString(String value) {

        if (value == null) {
            return "";
        }

        return value;
    }

    @FXML
    private void handleSearch() {

        String searchText = searchField.getText();

        if (searchText == null) {
            searchText = "";
        }

        searchText = searchText.toLowerCase();

        String selectedMembership = membershipFilter.getValue();

        ObservableList<Member> filteredMembers =
                FXCollections.observableArrayList();

        for (Member member : memberService.getAllMembers()) {

            String membershipText =
                    String.valueOf(member.getMembership_type()).toLowerCase();

            String disciplinesText =
                    getMemberDisciplinesText(member.getId_member()).toLowerCase();

            boolean matchesSearch =
                    String.valueOf(member.getId_member()).contains(searchText)
                    || String.valueOf(member.getId_user()).contains(searchText)
                    || safeString(member.getName_user()).toLowerCase().contains(searchText)
                    || safeString(member.getEmail()).toLowerCase().contains(searchText)
                    || String.valueOf(member.getBirth_year()).contains(searchText)
                    || safeString(member.getPhone()).toLowerCase().contains(searchText)
                    || safeString(member.getCity()).toLowerCase().contains(searchText)
                    || membershipText.contains(searchText)
                    || disciplinesText.contains(searchText);

            boolean matchesMembership =
                    selectedMembership == null
                    || selectedMembership.equals("All membership types")
                    || String.valueOf(member.getMembership_type())
                            .equalsIgnoreCase(selectedMembership);

            if (matchesSearch && matchesMembership) {
                filteredMembers.add(member);
            }
        }

        communityTable.setItems(filteredMembers);
    }

    @FXML
    private void handleReset() {

        searchField.clear();
        membershipFilter.setValue("All membership types");
        loadMembers();
    }
}