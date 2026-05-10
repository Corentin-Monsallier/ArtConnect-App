package com.project.artconnect.model;

public class Discipline {
    private int id_discipline;
    private String name_discipline;

    // constructors
    public Discipline() {}

    public Discipline(int id_discipline, String name_discipline) {
        this.id_discipline = id_discipline;
        this.name_discipline = name_discipline;
    }

    // getters and setters
    public int getId_discipline() { return id_discipline; }
    public void setId_discipline(int id_discipline) { this.id_discipline = id_discipline; }

    public String getName_discipline() { return name_discipline; }
    public void setName_discipline(String name_discipline) { this.name_discipline = name_discipline; }

    // toString method
    @Override
    public String toString() {
        return "Discipline{id=" + id_discipline + ", name=" + name_discipline + "}";
    }
}