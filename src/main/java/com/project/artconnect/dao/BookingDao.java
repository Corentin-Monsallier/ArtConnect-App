package com.project.artconnect.dao;

import java.util.List;

import com.project.artconnect.model.Booking;
import com.project.artconnect.model.PaymentStatusType;

public interface BookingDao {
    List<Booking> findAll();

    void save(Booking booking);

    void update(Booking booking);

    void delete(int id);

    List<Booking> findByMemberId(int id_member);

    List<Booking> findByWorkshopId(int id_workshop);

    List<Booking> findByPaymentStatus(PaymentStatusType status);
}
