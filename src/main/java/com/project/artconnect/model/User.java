package com.project.artconnect.model;

public class User {
    private int id_user;
    private String name_user;
    private String email;
    private int birth_year;
    private String phone;
    private String city;

    // constructors
    public User() {}

    public User(int id_user, String name_user, String email, int birth_year, String phone, String city) {
        this.id_user = id_user;
        this.name_user = name_user;
        this.email = email;
        this.birth_year = birth_year;
        this.phone = phone;
        this.city = city;
    }

    // getters and setters
    public int getId_user() { return id_user; }
    public void setId_user(int id_user) { this.id_user = id_user; }

    public String getName_user() { return name_user; }
    public void setName_user(String name_user) { this.name_user = name_user; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getBirth_year() { return birth_year; }
    public void setBirth_year(int birth_year) { this.birth_year = birth_year; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    // toString method
    @Override
    public String toString() {
        return "User{id=" + id_user + ", name=" + name_user + ", email=" + email + "}";
    }
}