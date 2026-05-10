package com.project.artconnect.model;

public class City {
    private int id_city;
    private String city;
    private int code;
    private String country;

    // constructors
    public City() {}

    public City(int id_city, String city, int code, String country) {
        this.id_city = id_city;
        this.city = city;
        this.code = code;
        this.country = country;
    }

    // getters and setters
    public int getId_city() { return id_city; }
    public void setId_city(int id_city) { this.id_city = id_city; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    // toString method
    @Override
    public String toString() {
        return "City{id=" + id_city + ", city=" + city + ", country=" + country + "}";
    }
}