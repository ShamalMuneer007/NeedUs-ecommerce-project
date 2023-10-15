package com.needus.ecommerce.service.user.Impl;

import com.needus.ecommerce.entity.user.UserAddress;
import com.needus.ecommerce.repository.user.UserAddressRepository;
import com.needus.ecommerce.service.user.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserAddressServiceImpl implements UserAddressService {
    @Autowired
    UserAddressRepository userAddressRepository;
    @Override
    public UserAddress saveAddress(UserAddress userAddress) {
        return userAddressRepository.save(userAddress);
    }

    @Override
    public UserAddress findAddressByAddressId(long addressId) {
        return userAddressRepository.findById(addressId).get();
    }

    @Override
    public void updateAddress(long addressId, UserAddress Address) {
        UserAddress newAddress = findAddressByAddressId(addressId);
        if(!newAddress.getFirstName().equals(Address.getFirstName())||Address.getFirstName().isEmpty()){
            newAddress.setFirstName(Address.getFirstName());
        }
        if(!newAddress.getLastName().equals(Address.getLastName())||Address.getLastName().isEmpty()){
            newAddress.setLastName(Address.getLastName());
        }
        if(!newAddress.getCompany().equals(Address.getCompany())||Address.getCompany().isEmpty()){
            newAddress.setCompany(Address.getCompany());
        }
        if(!newAddress.getStreetAddress().equals(Address.getStreetAddress())||Address.getStreetAddress().isEmpty()){
            newAddress.setStreetAddress(Address.getStreetAddress());
        }
        if(!newAddress.getCity().equals(Address.getCity())||Address.getCity().isEmpty()){
            newAddress.setCity(Address.getCity());
        }
        if(!newAddress.getState().equals(Address.getState())||Address.getState().isEmpty()){
            newAddress.setState(Address.getState());
        }
        if(!newAddress.getPostalCode().equals(Address.getPostalCode())||Address.getPostalCode().isEmpty()){
            newAddress.setPostalCode(Address.getPostalCode());
        }
        if(!newAddress.getContactNumber().equals(Address.getContactNumber())||Address.getContactNumber().isEmpty()){
            newAddress.setContactNumber(Address.getContactNumber());
        }
        userAddressRepository.save(newAddress);
    }

    @Override
    public boolean existsByAddressId(long addressId) {
        return userAddressRepository.existsById(addressId);
    }

    @Override
    public void deleteAddress(Long addressId) {
        UserAddress userAddress = userAddressRepository.findById(addressId).get();
        userAddress.setDeleted(true);
        userAddressRepository.save(userAddress);
    }
}
