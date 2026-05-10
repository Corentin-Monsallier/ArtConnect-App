package com.project.artconnect.model;

public class Gallery {
    private int id_gallery;
    private String name_gallery;
    private int rating;
    private String website_gallery;
    private int address_id;

    // constructors
    public Gallery() {}

    public Gallery(int id_gallery, String name_gallery, int rating, String website_gallery, int address_id) {
        this.id_gallery = id_gallery;
        this.name_gallery = name_gallery;
        this.rating = rating;
        this.website_gallery = website_gallery;
        this.address_id = address_id;
    }

    // getters and setters
    public int getId_gallery() { return id_gallery; }
    public void setId_gallery(int id_gallery) { this.id_gallery = id_gallery; }

    public String getName_gallery() { return name_gallery; }
    public void setName_gallery(String name_gallery) { this.name_gallery = name_gallery; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getWebsite_gallery() { return website_gallery; }
    public void setWebsite_gallery(String website_gallery) { this.website_gallery = website_gallery; }

    public int getAddress_id() { return address_id; }
    public void setAddress_id(int address_id) { this.address_id = address_id; }

    // toString method
    @Override
    public String toString() {
        return "Gallery{id=" + id_gallery + ", name=" + name_gallery + ", rating=" + rating + "}";
    }
}