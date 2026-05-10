package com.project.artconnect.model;

import java.time.LocalDateTime;

public class Booking {
    private int id_member;
    private int id_workshop;
    private LocalDateTime booking_date;
    private PaymentStatusType payment_status; 

    // constructors
    public Booking() {}

    public Booking(int id_member, int id_workshop, LocalDateTime booking_date, PaymentStatusType payment_status) {
        this.id_member = id_member;
        this.id_workshop = id_workshop;
        this.booking_date = booking_date;
        this.payment_status = payment_status;
    }

    // getters and setters
    public int getId_member() { return id_member; }
    public void setId_member(int id_member) { this.id_member = id_member; }

    public int getId_workshop() { return id_workshop; }
    public void setId_workshop(int id_workshop) { this.id_workshop = id_workshop; }

    public LocalDateTime getBooking_date() { return booking_date; }
    public void setBooking_date(LocalDateTime booking_date) { this.booking_date = booking_date; }

    public PaymentStatusType getPayment_status() { return payment_status; }
    public void setPayment_status(PaymentStatusType payment_status) { this.payment_status = payment_status; }

    // toString method
    @Override
    public String toString() {
        return "Booking{member=" + id_member + ", workshop=" + id_workshop + ", status=" + payment_status + "}";
    }
}