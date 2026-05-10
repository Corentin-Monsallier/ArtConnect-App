package com.project.artconnect.model;

public class Address {
    private int address_id;
    private int number;
    private String street;
    private int id_city;

    // constructors
    public Address() {}

    public Address(int address_id, int number, String street, int id_city) {
        this.address_id = address_id;
        this.number = number;
        this.street = street;
        this.id_city = id_city;
    }

    // getters and setters
    public int getAddress_id() { return address_id; }
    public void setAddress_id(int address_id) { this.address_id = address_id; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public int getId_city() { return id_city; }
    public void setId_city(int id_city) { this.id_city = id_city; }

    // toString method
    @Override
    public String toString() {
        return "Address{id=" + address_id + ", number=" + number + ", street=" + street + "}";
    }
}