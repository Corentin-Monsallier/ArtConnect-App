package com.project.artconnect.service;

import java.util.List;

import com.project.artconnect.model.Address;

public interface AddressService {

    List<Address> getAllAddresses();

    void createAddress(Address address);

    void updateAddress(Address address);

    void deleteAddress(int id);

    List<Address> getAddressesByCityId(int id_city);
}