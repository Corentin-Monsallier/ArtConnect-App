package com.project.artconnect.ui;

import com.project.artconnect.model.Member;
import com.project.artconnect.service.MemberService;
import com.project.artconnect.util.ServiceProvider;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

    private final MemberService memberService = ServiceProvider.getMemberService();

    @FXML
    public void initialize() {

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name_user"));

        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        memberTable.setItems(FXCollections.observableArrayList(memberService.getAllMembers()));
    }
}