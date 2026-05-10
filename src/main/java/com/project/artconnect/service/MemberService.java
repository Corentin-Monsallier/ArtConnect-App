package com.project.artconnect.service;

import java.util.List;
import java.util.Optional;

import com.project.artconnect.model.Member;
import com.project.artconnect.model.MembershipType;

public interface MemberService {

    List<Member> getAllMembers();

    Optional<Member> getMemberById(int id);

    void createMember(Member member);

    void updateMember(Member member);

    void deleteMember(int id);

    List<Member> getMembersByType(MembershipType type);
}