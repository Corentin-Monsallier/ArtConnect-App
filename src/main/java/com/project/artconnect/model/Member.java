package com.project.artconnect.model;

public class Member extends User {
    private int id_member;
    private MembershipType membership_type;

    // constructors
    public Member() { super(); }

    public Member(int id_user, String name_user, String email, int birth_year,
                  String phone, String city,
                  int id_member, MembershipType membership_type) {
        super(id_user, name_user, email, birth_year, phone, city);
        this.id_member = id_member;
        this.membership_type = membership_type;
    }

    // getters and setters
    public int getId_member() { return id_member; }
    public void setId_member(int id_member) { this.id_member = id_member; }

    public MembershipType getMembership_type() { return membership_type; }
    public void setMembership_type(MembershipType membership_type) { this.membership_type = membership_type; }

    // toString method
    @Override
    public String toString() {
        return "Member{id=" + id_member + ", membership_type=" + membership_type + "}";
    }
}