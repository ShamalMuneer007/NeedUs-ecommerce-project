package com.needus.ecommerce.repository.user;

import com.needus.ecommerce.entity.user.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOrderRepository extends JpaRepository<UserOrder,Long> {
}
