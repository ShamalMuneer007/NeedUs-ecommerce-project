package com.needus.ecommerce.repository.user;

import com.needus.ecommerce.entity.user.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress,Long> {
}
