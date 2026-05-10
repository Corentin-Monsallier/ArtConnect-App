package com.project.artconnect.model;

public class ArtistDiscipline {
    private int id_artist;
    private int id_discipline;

    // constructors
    public ArtistDiscipline() {}

    public ArtistDiscipline(int id_artist, int id_discipline) {
        this.id_artist = id_artist;
        this.id_discipline = id_discipline;
    }

    // getters and setters
    public int getId_artist() { return id_artist; }
    public void setId_artist(int id_artist) { this.id_artist = id_artist; }

    public int getId_discipline() { return id_discipline; }
    public void setId_discipline(int id_discipline) { this.id_discipline = id_discipline; }

    // toString method
    @Override
    public String toString() {
        return "ArtistDiscipline{artist=" + id_artist + ", discipline=" + id_discipline + "}";
    }
}