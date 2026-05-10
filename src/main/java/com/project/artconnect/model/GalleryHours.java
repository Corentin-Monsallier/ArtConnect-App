package com.project.artconnect.model;

import java.time.LocalTime;

public class GalleryHours {
    private int id_gallery;
    private String day_of_week;
    private LocalTime open_time;
    private LocalTime close_time;

    // constructors
    public GalleryHours() {}

    public GalleryHours(int id_gallery, String day_of_week, LocalTime open_time, LocalTime close_time) {
        this.id_gallery = id_gallery;
        this.day_of_week = day_of_week;
        this.open_time = open_time;
        this.close_time = close_time;
    }

    // getters and setters
    public int getId_gallery() { return id_gallery; }
    public void setId_gallery(int id_gallery) { this.id_gallery = id_gallery; }

    public String getDay_of_week() { return day_of_week; }
    public void setDay_of_week(String day_of_week) { this.day_of_week = day_of_week; }

    public LocalTime getOpen_time() { return open_time; }
    public void setOpen_time(LocalTime open_time) { this.open_time = open_time; }

    public LocalTime getClose_time() { return close_time; }
    public void setClose_time(LocalTime close_time) { this.close_time = close_time; }

    // toString method
    @Override
    public String toString() {
        return "GalleryHours{gallery=" + id_gallery + ", day=" + day_of_week + ", open=" + open_time + ", close=" + close_time + "}";
    }
}