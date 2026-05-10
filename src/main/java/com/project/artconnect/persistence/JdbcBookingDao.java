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

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Booking booking = new Booking();

                booking.setId_member(result.getInt("id_member"));
                booking.setId_workshop(result.getInt("id_workshop"));
                Timestamp timestamp = result.getTimestamp("booking_date");

                if (timestamp != null) {
                    booking.setBooking_date(timestamp.toLocalDateTime());
                }

                booking.setPayment_status(PaymentStatusType.valueOf(result.getString("payment_status").toUpperCase()));

                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return bookings;
    }

    @Override
    public void save(Booking booking) {

        String sql = "INSERT INTO Booking(id_member, id_workshop, booking_date, payment_status) "
                + "VALUES (?, ?, ?, ?)";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, booking.getId_member());
            statement.setInt(2, booking.getId_workshop());
            statement.setTimestamp(3, Timestamp.valueOf(booking.getBooking_date()));
            statement.setString(4, booking.getPayment_status().name().toLowerCase());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void update(Booking booking) {

        String sql = "UPDATE Booking " + "SET booking_date=?, payment_status=? "
                + "WHERE id_member=? AND id_workshop=?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setTimestamp(1, Timestamp.valueOf(booking.getBooking_date()));
            statement.setString(2, booking.getPayment_status().name().toLowerCase());
            statement.setInt(3, booking.getId_member());
            statement.setInt(4, booking.getId_workshop());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void delete(int id) {

        String sql = "DELETE FROM Booking WHERE id_member=?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public List<Booking> findByMemberId(int id_member) {

        List<Booking> bookings = new ArrayList<>();

        String sql = "SELECT * FROM Booking WHERE id_member=?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id_member);

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                Booking booking = new Booking();

                booking.setId_member(result.getInt("id_member"));
                booking.setId_workshop(result.getInt("id_workshop"));
                Timestamp timestamp = result.getTimestamp("booking_date");

                if (timestamp != null) {
                    booking.setBooking_date(timestamp.toLocalDateTime());
                }

                booking.setPayment_status(PaymentStatusType.valueOf(result.getString("payment_status").toUpperCase()));

                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return bookings;
    }

    @Override
    public List<Booking> findByWorkshopId(int id_workshop) {

        List<Booking> bookings = new ArrayList<>();

        String sql = "SELECT * FROM Booking WHERE id_workshop=?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id_workshop);

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                Booking booking = new Booking();

                booking.setId_member(result.getInt("id_member"));
                booking.setId_workshop(result.getInt("id_workshop"));
                Timestamp timestamp = result.getTimestamp("booking_date");

                if (timestamp != null) {
                    booking.setBooking_date(timestamp.toLocalDateTime());
                }

                booking.setPayment_status(PaymentStatusType.valueOf(result.getString("payment_status").toUpperCase()));
                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return bookings;
    }

    @Override
    public List<Booking> findByPaymentStatus(PaymentStatusType status) {

        List<Booking> bookings = new ArrayList<>();

        String sql = "SELECT * FROM Booking WHERE payment_status=?";

        try (
                Connection connection = ConnectionManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status.name().toLowerCase());
            ResultSet result = statement.executeQuery();

            while (result.next()) {

                Booking booking = new Booking();

                booking.setId_member(result.getInt("id_member"));
                booking.setId_workshop(result.getInt("id_workshop"));
                Timestamp timestamp = result.getTimestamp("booking_date");

                if (timestamp != null) {
                    booking.setBooking_date(timestamp.toLocalDateTime());
                }

                booking.setPayment_status(PaymentStatusType.valueOf(result.getString("payment_status").toUpperCase()));

                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return bookings;
    }
}