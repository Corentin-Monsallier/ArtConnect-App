package com.project.artconnect.model;

class ArtworkTag {
    private int id_tag;
    private String name;
 
    // constructors
    public ArtworkTag() {}
 
    public ArtworkTag(int id_tag, String name) {
        this.id_tag = id_tag;
        this.name = name;
    }
 
    // getters and setters
    public int getId_tag() { return id_tag; }
    public void setId_tag(int id_tag) { this.id_tag = id_tag; }
 
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
 
    // toString method
    @Override
    public String toString() {
        return "Tag{id=" + id_tag + ", name=" + name + "}";
    }
}
