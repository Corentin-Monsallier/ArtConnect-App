package com.project.artconnect.service;

import java.util.List;

import com.project.artconnect.model.Booking;
import com.project.artconnect.model.PaymentStatusType;

public interface BookingService {

    List<Booking> getAllBookings();

    void createBooking(Booking booking);

    void updateBooking(Booking booking);

    void deleteBooking(int id);

    List<Booking> getBookingsByMemberId(int id_member);

    List<Booking> getBookingsByWorkshopId(int id_workshop);

    List<Booking> getBookingsByPaymentStatus(PaymentStatusType status);
}