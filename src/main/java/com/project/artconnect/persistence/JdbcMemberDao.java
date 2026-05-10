package com.project.artconnect.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.project.artconnect.dao.MemberDao;
import com.project.artconnect.model.Member;
import com.project.artconnect.model.MembershipType;
import com.project.artconnect.util.ConnectionManager;

public class JdbcMemberDao implements MemberDao {

    @Override
    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();

        String sql = "SELECT * FROM Member_ m JOIN User_ u ON m.id_user = u.id_user";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                Member member = new Member();
                
                // User
                member.setId_user(result.getInt("id_user"));
                member.setName_user(result.getString("name_user"));
                member.setEmail(result.getString("email"));
                member.setBirth_year(result.getInt("birth_year"));
                member.setPhone(result.getString("phone"));
                member.setCity(result.getString("city"));
                // Member
                member.setId_member(result.getInt("id_member"));
                member.setMembership_type(MembershipType.valueOf(result.getString("membership_type").toUpperCase()));
                
                members.add(member);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return members;
    }

    @Override
    public void save(Member member) {
        String sqlUser = "INSERT INTO User_(name_user, email, birth_year, phone, city) VALUES (?, ?, ?, ?, ?)";
        String sqlMember = "INSERT INTO Member_(id_user, membership_type) VALUES (?, ?)";

        try (Connection connection = ConnectionManager.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement userStatement = connection.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            userStatement.setString(1, member.getName_user());
            userStatement.setString(2, member.getEmail());
            userStatement.setInt(3, member.getBirth_year());
            userStatement.setString(4, member.getPhone());
            userStatement.setString(5, member.getCity());

            userStatement.executeUpdate();

            ResultSet generatedKeys = userStatement.getGeneratedKeys();
            int generatedUserId = 0;
            if (generatedKeys.next()) {
                generatedUserId = generatedKeys.getInt(1);
            }

            PreparedStatement memberStatement = connection.prepareStatement(sqlMember);
            memberStatement.setInt(1, generatedUserId);
            memberStatement.setString(2, member.getMembership_type().name());
            memberStatement.executeUpdate();

            connection.commit();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void update(Member member) {
        String sqlUser = "UPDATE User_ SET name_user=?, email=?, birth_year=?, phone=?, city=? WHERE id_user=?";
        String sqlMember = "UPDATE Member_ SET membership_type=? WHERE id_member=?";

        try (Connection connection = ConnectionManager.getConnection()) {

            PreparedStatement userStatement = connection.prepareStatement(sqlUser);
            userStatement.setString(1, member.getName_user());
            userStatement.setString(2, member.getEmail());
            userStatement.setInt(3, member.getBirth_year());
            userStatement.setString(4, member.getPhone());
            userStatement.setString(5, member.getCity());
            userStatement.setInt(6, member.getId_user());
            userStatement.executeUpdate();

            PreparedStatement memberStatement = connection.prepareStatement(sqlMember);
            memberStatement.setString(1, member.getMembership_type().name());
            memberStatement.setInt(2, member.getId_member());
            memberStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE m, u FROM Member_ m JOIN User_ u ON m.id_user = u.id_user WHERE m.id_member = ?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public List<Member> findByMembershipType(MembershipType type) {
        List<Member> members = new ArrayList<>();

        String sql = "SELECT * FROM Member_ m JOIN User_ u ON m.id_user = u.id_user WHERE m.membership_type = ?";

        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, type.name());
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Member member = new Member();
                
                member.setId_user(result.getInt("id_user"));
                member.setName_user(result.getString("name_user"));
                member.setEmail(result.getString("email"));
                member.setBirth_year(result.getInt("birth_year"));
                member.setPhone(result.getString("phone"));
                member.setCity(result.getString("city"));
                member.setId_member(result.getInt("id_member"));
                member.setMembership_type(MembershipType.valueOf(result.getString("membership_type")));
                
                members.add(member);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return members;
    }
}