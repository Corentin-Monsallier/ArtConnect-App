package com.project.artconnect.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.project.artconnect.dao.BookingDao;
import com.project.artconnect.model.Booking;
import com.project.artconnect.model.PaymentStatusType;
import com.project.artconnect.util.ConnectionManager;

public class JdbcBookingDao implements BookingDao {

    @Override
    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement s = c.prepareStatement(sql);
             ResultSet rs = s.executeQuery()) {
            while (rs.next()) {
                bookings.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return bookings;
    }

    @Override
    public void save(Booking booking) {
        String sql = "INSERT INTO Booking(id_member, id_workshop, booking_date, payment_status) VALUES (?, ?, ?, ?)";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, booking.getId_member());
            s.setInt(2, booking.getId_workshop());
            s.setTimestamp(3, Timestamp.valueOf(booking.getBooking_date()));
            s.setString(4, booking.getPayment_status().name().toLowerCase());
            s.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void update(Booking booking) {
        String sql = "UPDATE Booking SET booking_date=?, payment_status=? WHERE id_member=? AND id_workshop=?";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setTimestamp(1, Timestamp.valueOf(booking.getBooking_date()));
            s.setString(2, booking.getPayment_status().name().toLowerCase());
            s.setInt(3, booking.getId_member());
            s.setInt(4, booking.getId_workshop());
            s.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void delete(int id_member) {
        deleteByPK(id_member, -1);
    }

    public void deleteByPK(int id_member, int id_workshop) {
        String sql = id_workshop >= 0
                ? "DELETE FROM Booking WHERE id_member=? AND id_workshop=?"
                : "DELETE FROM Booking WHERE id_member=?";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, id_member);
            if (id_workshop >= 0) s.setInt(2, id_workshop);
            s.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<Booking> findByMemberId(int id_member) {
        return findBy("id_member", id_member);
    }

    @Override
    public List<Booking> findByWorkshopId(int id_workshop) {
        return findBy("id_workshop", id_workshop);
    }

    private List<Booking> findBy(String col, int val) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking WHERE " + col + "=?";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, val);
            ResultSet rs = s.executeQuery();
            while (rs.next()) bookings.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return bookings;
    }

    @Override
    public List<Booking> findByPaymentStatus(PaymentStatusType status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking WHERE payment_status=?";
        try (Connection c = ConnectionManager.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, status.name().toLowerCase());
            ResultSet rs = s.executeQuery();
            while (rs.next()) bookings.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return bookings;
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId_member(rs.getInt("id_member"));
        b.setId_workshop(rs.getInt("id_workshop"));
        Timestamp ts = rs.getTimestamp("booking_date");
        if (ts != null) b.setBooking_date(ts.toLocalDateTime());
        b.setPayment_status(PaymentStatusType.valueOf(rs.getString("payment_status").toUpperCase()));
        return b;
    }
}