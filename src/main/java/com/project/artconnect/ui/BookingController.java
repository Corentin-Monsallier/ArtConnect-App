package com.project.artconnect.ui;

import java.time.LocalDateTime;
import java.util.List;

import com.project.artconnect.model.Booking;
import com.project.artconnect.model.Member;
import com.project.artconnect.model.PaymentStatusType;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.persistence.JdbcBookingDao;
import com.project.artconnect.service.BookingService;
import com.project.artconnect.service.MemberService;
import com.project.artconnect.service.WorkshopService;
import com.project.artconnect.util.ServiceProvider;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class BookingController {

    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, Integer> memberColumn;
    @FXML private TableColumn<Booking, Integer> workshopColumn;
    @FXML private TableColumn<Booking, String> dateColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    private final BookingService bookingService = ServiceProvider.getBookingService();
    private final MemberService memberService = ServiceProvider.getMemberService();
    private final WorkshopService workshopService = ServiceProvider.getWorkshopService();
    private final JdbcBookingDao bookingDao = new JdbcBookingDao();

    @FXML
    public void initialize() {
        memberColumn.setCellValueFactory(new PropertyValueFactory<>("id_member"));
        workshopColumn.setCellValueFactory(new PropertyValueFactory<>("id_workshop"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("booking_date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("payment_status"));
        loadBookings();
        statusFilter.setItems(FXCollections.observableArrayList("All payment status", "PENDING", "PAID", "CANCELLED"));
        statusFilter.setValue("All payment status");
        statusFilter.setOnAction(e -> handleSearch());
    }

    private void loadBookings() {
        bookingTable.setItems(FXCollections.observableArrayList(bookingService.getAllBookings()));
    }

    @FXML private void handleSearch() {
        String search = searchField.getText().toLowerCase();
        String sel = statusFilter.getValue();
        ObservableList<Booking> filtered = FXCollections.observableArrayList();
        for (Booking b : bookingService.getAllBookings()) {
            boolean m = String.valueOf(b.getId_member()).contains(search);
            boolean s = sel.equals("All payment status") || b.getPayment_status().name().equalsIgnoreCase(sel);
            if (m && s) filtered.add(b);
        }
        bookingTable.setItems(filtered);
    }

    @FXML private void handleReset() { searchField.clear(); statusFilter.setValue("All payment status"); loadBookings(); }

    @FXML private void handleAdd() {
        buildDialog(null).showAndWait().ifPresent(b -> { bookingService.createBooking(b); loadBookings(); });
    }

    @FXML private void handleEdit() {
        Booking sel = bookingTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a booking to edit."); return; }
        buildDialog(sel).showAndWait().ifPresent(b -> { bookingService.updateBooking(b); loadBookings(); });
    }

    @FXML private void handleDelete() {
        Booking sel = bookingTable.getSelectionModel().getSelectedItem();
        if (sel == null) { warn("Please select a booking to delete."); return; }
        new Alert(Alert.AlertType.CONFIRMATION,
                "Delete booking for member #" + sel.getId_member() + " / workshop #" + sel.getId_workshop() + "?",
                ButtonType.YES, ButtonType.NO)
                .showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES) {
                        try {
                            bookingDao.deleteByPK(sel.getId_member(), sel.getId_workshop());
                            loadBookings();
                        } catch (Exception e) { warn("Cannot delete: " + e.getMessage()); }
                    }
                });
    }

    private Dialog<Booking> buildDialog(Booking existing) {
        Dialog<Booking> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Booking" : "Edit Booking");
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        List<Member> members = memberService.getAllMembers();
        List<Workshop> workshops = workshopService.getAllWorkshops();

        ComboBox<Member> memberF = new ComboBox<>(FXCollections.observableArrayList(members));
        memberF.setConverter(new StringConverter<>() {
            public String toString(Member m) { return m == null ? "" : "#" + m.getId_member() + " – " + m.getName_user(); }
            public Member fromString(String s) { return null; }
        });
        memberF.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Workshop> workshopF = new ComboBox<>(FXCollections.observableArrayList(workshops));
        workshopF.setConverter(new StringConverter<>() {
            public String toString(Workshop w) { return w == null ? "" : "#" + w.getId_workshop() + " – " + w.getTitle_workshop(); }
            public Workshop fromString(String s) { return null; }
        });
        workshopF.setMaxWidth(Double.MAX_VALUE);

        ComboBox<PaymentStatusType> statusF = new ComboBox<>(FXCollections.observableArrayList(PaymentStatusType.values()));
        statusF.setMaxWidth(Double.MAX_VALUE);

        if (existing != null) {
            members.stream().filter(m -> m.getId_member() == existing.getId_member()).findFirst().ifPresent(memberF::setValue);
            workshops.stream().filter(w -> w.getId_workshop() == existing.getId_workshop()).findFirst().ifPresent(workshopF::setValue);
            statusF.setValue(existing.getPayment_status());
            memberF.setDisable(true);
            workshopF.setDisable(true);
        }

        VBox box = new VBox(10,
                new Label("Member:"), memberF,
                new Label("Workshop:"), workshopF,
                new Label("Payment Status:"), statusF
        );
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setPrefWidth(460);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                if (existing == null && (memberF.getValue() == null || workshopF.getValue() == null)) {
                    warn("Please select a member and a workshop."); return null;
                }
                if (statusF.getValue() == null) { warn("Please select a payment status."); return null; }
                Booking b = existing != null ? existing : new Booking();
                if (existing == null) {
                    b.setId_member(memberF.getValue().getId_member());
                    b.setId_workshop(workshopF.getValue().getId_workshop());
                    b.setBooking_date(LocalDateTime.now());
                }
                b.setPayment_status(statusF.getValue());
                return b;
            }
            return null;
        });
        return dialog;
    }

    private void warn(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }
}