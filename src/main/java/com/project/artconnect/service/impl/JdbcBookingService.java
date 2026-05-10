package com.project.artconnect.service.impl;

import java.util.List;

import com.project.artconnect.dao.BookingDao;
import com.project.artconnect.model.Booking;
import com.project.artconnect.model.PaymentStatusType;
import com.project.artconnect.persistence.JdbcBookingDao;
import com.project.artconnect.service.BookingService;

public class JdbcBookingService
        implements BookingService {

    private final BookingDao bookingDao;

    public JdbcBookingService() {
        this.bookingDao = new JdbcBookingDao();
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingDao.findAll();
    }

    @Override
    public void createBooking(Booking booking) {
        bookingDao.save(booking);
    }

    @Override
    public void updateBooking(Booking booking) {
        bookingDao.update(booking);
    }

    @Override
    public void deleteBooking(int id) {
        bookingDao.delete(id);
    }

    @Override
    public List<Booking> getBookingsByMemberId(int id_member) {
        return bookingDao.findByMemberId(id_member);
    }

    @Override
    public List<Booking> getBookingsByWorkshopId(int id_workshop) {
        return bookingDao.findByWorkshopId(id_workshop);
    }

    @Override
    public List<Booking> getBookingsByPaymentStatus(PaymentStatusType status) {
        return bookingDao.findByPaymentStatus(status);
    }
}