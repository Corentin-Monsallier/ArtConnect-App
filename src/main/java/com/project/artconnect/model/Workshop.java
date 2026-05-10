package com.project.artconnect.model;

import java.time.LocalDateTime;

public class Workshop {
    private int id_workshop;
    private String title_workshop;
    private LocalDateTime date_workshop;
    private int duration_minutes;
    private int max_participants;
    private double price;
    private String level;
    private String location;
    private String description;
    private int id_artist;

    // constructors
    public Workshop() {}

    public Workshop(int id_workshop, String title_workshop, LocalDateTime date_workshop,
                    int duration_minutes, int max_participants, double price,
                    String level, String location, String description, int id_artist) {
        this.id_workshop = id_workshop;
        this.title_workshop = title_workshop;
        this.date_workshop = date_workshop;
        this.duration_minutes = duration_minutes;
        this.max_participants = max_participants;
        this.price = price;
        this.level = level;
        this.location = location;
        this.description = description;
        this.id_artist = id_artist;
    }

    // getters and setters
    public int getId_workshop() { return id_workshop; }
    public void setId_workshop(int id_workshop) { this.id_workshop = id_workshop; }

    public String getTitle_workshop() { return title_workshop; }
    public void setTitle_workshop(String title_workshop) { this.title_workshop = title_workshop; }

    public LocalDateTime getDate_workshop() { return date_workshop; }
    public void setDate_workshop(LocalDateTime date_workshop) { this.date_workshop = date_workshop; }

    public int getDuration_minutes() { return duration_minutes; }
    public void setDuration_minutes(int duration_minutes) { this.duration_minutes = duration_minutes; }

    public int getMax_participants() { return max_participants; }
    public void setMax_participants(int max_participants) { this.max_participants = max_participants; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getId_artist() { return id_artist; }
    public void setId_artist(int id_artist) { this.id_artist = id_artist; }

    // toString method
    @Override
    public String toString() {
        return "Workshop{id=" + id_workshop + ", title=" + title_workshop + ", location=" + location + "}";
    }
}
