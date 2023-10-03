package com.needus.ecommerce.repository.user;

import com.needus.ecommerce.entity.user.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
}
