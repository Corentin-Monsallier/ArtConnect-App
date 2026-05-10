package com.project.artconnect.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.project.artconnect.dao.AddressDao;
import com.project.artconnect.model.Address;
import com.project.artconnect.util.ConnectionManager;

public class JdbcAddressDao implements AddressDao {

        @Override
        public List<Address> findAll() {

                List<Address> addresses = new ArrayList<>();

                String sql = "SELECT * FROM Address";

                try (
                                Connection connection = ConnectionManager.getConnection();
                                PreparedStatement statement = connection.prepareStatement(sql);
                                ResultSet result = statement.executeQuery()) {

                        while (result.next()) {

                                Address address = new Address();

                                address.setAddress_id(result.getInt("address_id"));
                                address.setNumber(result.getInt("number"));
                                address.setStreet(result.getString("street"));
                                address.setId_city(result.getInt("id_city"));

                                addresses.add(address);
                        }

                } catch (SQLException e) {
                        System.out.println(e);
                }

                return addresses;
        }

        @Override
        public void save(Address address) {

                String sql = "INSERT INTO Address(number, street, id_city) " + "VALUES (?, ?, ?)";

                try (
                                Connection connection = ConnectionManager.getConnection();
                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setInt(1, address.getNumber());
                        statement.setString(2, address.getStreet());
                        statement.setInt(3, address.getId_city());

                        statement.executeUpdate();

                } catch (SQLException e) {
                        System.out.println(e);
                }
        }

        @Override
        public void update(Address address) {

                String sql = "UPDATE Address " + "SET number=?, street=?, id_city=? " + "WHERE address_id=?";

                try (
                                Connection connection = ConnectionManager.getConnection();
                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setInt(1, address.getNumber());
                        statement.setString(2, address.getStreet());
                        statement.setInt(3, address.getId_city());
                        statement.setInt(4, address.getAddress_id());

                        statement.executeUpdate();

                } catch (SQLException e) {
                        System.out.println(e);
                }
        }

        @Override
        public void delete(int id) {

                String sql = "DELETE FROM Address WHERE address_id=?";

                try (
                                Connection connection = ConnectionManager.getConnection();
                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setInt(1, id);
                        statement.executeUpdate();

                } catch (SQLException e) {
                        System.out.println(e);
                }
        }

        @Override
        public List<Address> findByCityId(int id_city) {

                List<Address> addresses = new ArrayList<>();

                String sql = "SELECT * FROM Address WHERE id_city=?";

                try (
                                Connection connection = ConnectionManager.getConnection();
                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setInt(1, id_city);

                        ResultSet result = statement.executeQuery();

                        while (result.next()) {

                                Address address = new Address();

                                address.setAddress_id(result.getInt("address_id"));
                                address.setNumber(result.getInt("number"));
                                address.setStreet(result.getString("street"));
                                address.setId_city(result.getInt("id_city"));

                                addresses.add(address);
                        }

                } catch (SQLException e) {
                        System.out.println(e);
                }
                return addresses;
        }
}