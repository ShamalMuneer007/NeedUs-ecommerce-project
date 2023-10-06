package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.user.UserAddress;

public interface UserAddressService {
    public UserAddress saveAddress(UserAddress userAddress);

    public UserAddress findAddressByAddressId(long addressId);

    void updateAddress(long addressId, UserAddress address);
}
