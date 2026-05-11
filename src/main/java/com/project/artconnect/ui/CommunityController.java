package com.project.artconnect.ui;

import com.project.artconnect.model.Member;
import com.project.artconnect.service.MemberService;
import com.project.artconnect.util.ServiceProvider;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CommunityController {

    @FXML
    private TableView<Member> memberTable;

    @FXML
    private TableColumn<Member, String> nameColumn;

    @FXML
    private TableColumn<Member, String> emailColumn;

    @FXML
    private TableColumn<Member, String> cityColumn;

    @FXML
    private TextField searchField;

    private final MemberService memberService =
            ServiceProvider.getMemberService();

    @FXML
    public void initialize() {

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name_user"));

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email"));

        cityColumn.setCellValueFactory(
                new PropertyValueFactory<>("city"));

        loadMembers();
    }

    private void loadMembers() {

        memberTable.setItems(
                FXCollections.observableArrayList(
                        memberService.getAllMembers()));
    }

    @FXML
    private void handleSearch() {

        String searchText = searchField.getText();

        if (searchText == null || searchText.isEmpty()) {
            loadMembers();
            return;
        }

        var filtered = memberService.getAllMembers()
                .stream()
                .filter(m -> m.getName_user()
                        .toLowerCase()
                        .contains(searchText.toLowerCase()))
                .toList();

        memberTable.setItems(
                FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleReset() {

        searchField.clear();

        loadMembers();
    }
}