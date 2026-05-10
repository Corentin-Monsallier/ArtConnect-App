package com.project.artconnect.model;

public class ExhibitionArtwork {
    private int id_artwork;
    private int id_exhibition;

    // constructors
    public ExhibitionArtwork() {}

    public ExhibitionArtwork(int id_artwork, int id_exhibition) {
        this.id_artwork = id_artwork;
        this.id_exhibition = id_exhibition;
    }

    // getters and setters
    public int getId_artwork() { return id_artwork; }
    public void setId_artwork(int id_artwork) { this.id_artwork = id_artwork; }

    public int getId_exhibition() { return id_exhibition; }
    public void setId_exhibition(int id_exhibition) { this.id_exhibition = id_exhibition; }

    // toString method
    @Override
    public String toString() {
        return "ExhibitionArtwork{artwork=" + id_artwork + ", exhibition=" + id_exhibition + "}";
    }
}