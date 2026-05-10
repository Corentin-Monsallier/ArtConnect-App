package com.project.artconnect.model;

import java.time.LocalDate;

public class Exhibition {
    private int id_exhibition;
    private String title_exhib;
    private String curator_name;
    private LocalDate start_date;
    private LocalDate end_date;
    private String theme;
    private String description;
    private int id_gallery;

    // constructors
    public Exhibition() {}

    public Exhibition(int id_exhibition, String title_exhib, String curator_name,
                      LocalDate start_date, LocalDate end_date,
                      String theme, String description, int id_gallery) {
        this.id_exhibition = id_exhibition;
        this.title_exhib = title_exhib;
        this.curator_name = curator_name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.theme = theme;
        this.description = description;
        this.id_gallery = id_gallery;
    }

    // getters and setters
    public int getId_exhibition() { return id_exhibition; }
    public void setId_exhibition(int id_exhibition) { this.id_exhibition = id_exhibition; }

    public String getTitle_exhib() { return title_exhib; }
    public void setTitle_exhib(String title_exhib) { this.title_exhib = title_exhib; }

    public String getCurator_name() { return curator_name; }
    public void setCurator_name(String curator_name) { this.curator_name = curator_name; }

    public LocalDate getStart_date() { return start_date; }
    public void setStart_date(LocalDate start_date) { this.start_date = start_date; }

    public LocalDate getEnd_date() { return end_date; }
    public void setEnd_date(LocalDate end_date) { this.end_date = end_date; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getId_gallery() { return id_gallery; }
    public void setId_gallery(int id_gallery) { this.id_gallery = id_gallery; }

    // toString method
    @Override
    public String toString() {
        return "Exhibition{id=" + id_exhibition + ", title=" + title_exhib + ", theme=" + theme + "}";
    }
}