package com.project.artconnect.model;

public class Artwork {
    private int id_artwork;
    private String title_art;
    private int creation_year;
    private String type;
    private String medium;
    private String dimensions;
    private String description;
    private Double price;
    private ArtworkStatus status;
    private int id_artist;

    // constructors
    public Artwork() {}

    public Artwork(int id_artwork, String title_art, int creation_year, String type,
                   String medium, String dimensions, String description,
                   Double price, ArtworkStatus status, int id_artist) {
        this.id_artwork = id_artwork;
        this.title_art = title_art;
        this.creation_year = creation_year;
        this.type = type;
        this.medium = medium;
        this.dimensions = dimensions;
        this.description = description;
        this.price = price;
        this.status = status;
        this.id_artist = id_artist;
    }

    // getters and setters
    public int getId_artwork() { return id_artwork; }
    public void setId_artwork(int id_artwork) { this.id_artwork = id_artwork; }

    public String getTitle_art() { return title_art; }
    public void setTitle_art(String title_art) { this.title_art = title_art; }

    public int getCreation_year() { return creation_year; }
    public void setCreation_year(int creation_year) { this.creation_year = creation_year; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMedium() { return medium; }
    public void setMedium(String medium) { this.medium = medium; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public ArtworkStatus getStatus() { return status; }
    public void setStatus(ArtworkStatus status) { this.status = status; }

    public int getId_artist() { return id_artist; }
    public void setId_artist(int id_artist) { this.id_artist = id_artist; }

    // toString method
    @Override
    public String toString() {
        return "Artwork{id=" + id_artwork + ", title=" + title_art + ", status=" + status + "}";
    }
}