package com.project.artconnect.service.impl;

import java.util.List;
import java.util.Optional;

import com.project.artconnect.dao.MemberDao;
import com.project.artconnect.model.Member;
import com.project.artconnect.model.MembershipType;
import com.project.artconnect.service.MemberService;

public class JdbcMemberService implements MemberService {

    private final MemberDao memberDao;

    public JdbcMemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public List<Member> getAllMembers() {
        return memberDao.findAll();
    }

    @Override
    public Optional<Member> getMemberById(int id) {
        return memberDao.findAll()
                .stream()
                .filter(m -> m.getId_user() == id)
                .findFirst();
    }

    @Override
    public void createMember(Member member) {
        memberDao.save(member);
    }

    @Override
    public void updateMember(Member member) {
        memberDao.update(member);
    }

    @Override
    public void deleteMember(int id) {
        memberDao.delete(id);
    }

    @Override
    public List<Member> getMembersByType(MembershipType type) {
        return memberDao.findByMembershipType(type);
    }
}