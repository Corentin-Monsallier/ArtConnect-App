package com.project.artconnect.dao;

import java.util.List;

import com.project.artconnect.model.Member;
import com.project.artconnect.model.MembershipType;

public interface MemberDao {
    List<Member> findAll();

    void save(Member member);

    void update(Member member);

    void delete(int id);
    
    List<Member> findByMembershipType(MembershipType type);
}