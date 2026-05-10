package com.project.artconnect.dao;

import java.util.List;

import com.project.artconnect.model.Address;

public interface AddressDao {
    List<Address> findAll();

    void save(Address address);

    void update(Address address);

    void delete(int id);
    
    List<Address> findByCityId(int id_city);
}
