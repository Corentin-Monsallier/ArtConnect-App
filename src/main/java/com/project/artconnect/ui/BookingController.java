package com.project.artconnect.ui;

import com.project.artconnect.model.Booking;
import com.project.artconnect.service.BookingService;
import com.project.artconnect.util.ServiceProvider;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class BookingController {

    @FXML
    private TableView<Booking> bookingTable;

    @FXML
    private TableColumn<Booking, Integer> memberColumn;

    @FXML
    private TableColumn<Booking, Integer> workshopColumn;

    @FXML
    private TableColumn<Booking, String> dateColumn;

    @FXML
    private TableColumn<Booking, String> statusColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusFilter;

    private final BookingService bookingService =
            ServiceProvider.getBookingService();

    @FXML
    public void initialize() {

        memberColumn.setCellValueFactory(
                new PropertyValueFactory<>("id_member"));

        workshopColumn.setCellValueFactory(
                new PropertyValueFactory<>("id_workshop"));

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("booking_date"));

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("payment_status"));

        loadBookings();
        loadStatuses();

        statusFilter.setOnAction(event -> handleSearch());
    }

    private void loadBookings() {

        bookingTable.setItems(
                FXCollections.observableArrayList(
                        bookingService.getAllBookings()));
    }

    private void loadStatuses() {

        statusFilter.setItems(
                FXCollections.observableArrayList(
                        "All payment status",
                        "pending",
                        "paid",
                        "cancelled"
                )
        );

        statusFilter.setValue("All payment status");
    }

    @FXML
    private void handleSearch() {

        String search =
                searchField.getText().toLowerCase();

        String selectedStatus =
                statusFilter.getValue();

        ObservableList<Booking> filtered =
                FXCollections.observableArrayList();

        for (Booking booking : bookingService.getAllBookings()) {

            boolean memberMatch =
                    String.valueOf(booking.getId_member())
                            .contains(search);

            boolean statusMatch =
                    selectedStatus.equals("All payment status")
                            || booking.getPayment_status()
                            .toString()
                            .equalsIgnoreCase(selectedStatus);

            if (memberMatch && statusMatch) {
                filtered.add(booking);
            }
        }

        bookingTable.setItems(filtered);
    }

    @FXML
    private void handleReset() {

        searchField.clear();
        statusFilter.setValue("All payment status");
        loadBookings();
    }
}