package com.project.artconnect.model;

public class MemberDiscipline {
    private int id_member;
    private int id_discipline;

    // constructors
    public MemberDiscipline() {}

    public MemberDiscipline(int id_member, int id_discipline) {
        this.id_member = id_member;
        this.id_discipline = id_discipline;
    }

    // getters and setters
    public int getId_member() { return id_member; }
    public void setId_member(int id_member) { this.id_member = id_member; }

    public int getId_discipline() { return id_discipline; }
    public void setId_discipline(int id_discipline) { this.id_discipline = id_discipline; }

    // toString method
    @Override
    public String toString() {
        return "MemberDiscipline{member=" + id_member + ", discipline=" + id_discipline + "}";
    }
}