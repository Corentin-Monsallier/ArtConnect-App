package com.project.artconnect.model;

public class Artist extends User {
    private int id_artist;
    private String bio;
    private String website_artist;
    private boolean is_active;

    // constructors
    public Artist() {
        super();
    }

    public Artist(int id_user, String name_user, String email, int birth_year,String phone, String city,
                  int id_artist, String bio, String website_artist, boolean is_active) {
        super(id_user, name_user, email, birth_year, phone, city);
        this.id_artist = id_artist;
        this.bio = bio;
        this.website_artist = website_artist;
        this.is_active = is_active;
    }

    // getters and setters
    public int getId_artist() { return id_artist; }
    public void setId_artist(int id_artist) { this.id_artist = id_artist; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getWebsite_artist() { return website_artist; }
    public void setWebsite_artist(String website_artist) { this.website_artist = website_artist; }

    public boolean isIs_active() { return is_active; }
    public void setIs_active(boolean is_active) { this.is_active = is_active; }
        
    // toString method
    @Override
    public String toString() {
        return "Artist{id=" + id_artist + ", bio=" + bio + ", active=" + is_active + "}";
    }
    
}