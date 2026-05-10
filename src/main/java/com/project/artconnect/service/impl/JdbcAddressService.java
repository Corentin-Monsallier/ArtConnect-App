package com.project.artconnect.service.impl;

import java.util.List;

import com.project.artconnect.dao.AddressDao;
import com.project.artconnect.model.Address;
import com.project.artconnect.persistence.JdbcAddressDao;
import com.project.artconnect.service.AddressService;

public class JdbcAddressService implements AddressService {

    private final AddressDao addressDao;

    public JdbcAddressService() {
        this.addressDao = new JdbcAddressDao();
    }

    @Override
    public List<Address> getAllAddresses() {
        return addressDao.findAll();
    }

    @Override
    public void createAddress(Address address) {
        addressDao.save(address);
    }

    @Override
    public void updateAddress(Address address) {
        addressDao.update(address);
    }

    @Override
    public void deleteAddress(int id) {
        addressDao.delete(id);
    }

    @Override
    public List<Address> getAddressesByCityId(int id_city) {
        return addressDao.findByCityId(id_city);
    }
}