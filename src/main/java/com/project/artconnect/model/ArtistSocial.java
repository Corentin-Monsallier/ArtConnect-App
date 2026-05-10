package com.project.artconnect.model;

public class ArtistSocial {
    private int id_social;
    private String platform;
    private String link;
    private int id_artist;

    // constructors
    public ArtistSocial() {}

    public ArtistSocial(int id_social, String platform, String link, int id_artist) {
        this.id_social = id_social;
        this.platform = platform;
        this.link = link;
        this.id_artist = id_artist;
    }

    // getters and setters
    public int getId_social() { return id_social; }
    public void setId_social(int id_social) { this.id_social = id_social; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public int getId_artist() { return id_artist; }
    public void setId_artist(int id_artist) { this.id_artist = id_artist; }

    // toString method
    @Override
    public String toString() {
        return "ArtistSocial{id=" + id_social + ", platform=" + platform + ", link=" + link + "}";
    }
}