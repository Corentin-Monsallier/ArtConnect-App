package com.project.artconnect.model;

public class User {
    private int id_user;
    private String name_user;
    private String email;
    private int birth_year;
    private String phone;
    private String city;
    private String password_user;
    private UserRole role_user;

    public User() {}

    // Used by Artist/Member subclasses
    public User(int id_user, String name_user, String email, int birth_year, String phone, String city) {
        this.id_user = id_user;
        this.name_user = name_user;
        this.email = email;
        this.birth_year = birth_year;
        this.phone = phone;
        this.city = city;
    }

    // Full constructor
    public User(int id_user, String name_user, String email, int birth_year,
                String phone, String city, String password_user, UserRole role_user) {
        this(id_user, name_user, email, birth_year, phone, city);
        this.password_user = password_user;
        this.role_user = role_user;
    }

    public int getId_user() { return id_user; }
    public void setId_user(int v) { id_user = v; }
    public String getName_user() { return name_user; }
    public void setName_user(String v) { name_user = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public int getBirth_year() { return birth_year; }
    public void setBirth_year(int v) { birth_year = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { phone = v; }
    public String getCity() { return city; }
    public void setCity(String v) { city = v; }
    public String getPassword_user() { return password_user; }
    public void setPassword_user(String v) { password_user = v; }
    public UserRole getRole_user() { return role_user; }
    public void setRole_user(UserRole v) { role_user = v; }

    @Override
    public String toString() {
        return "User{id=" + id_user + ", name=" + name_user + ", role=" + role_user + "}";
    }
}