package com.needus.ecommerce.repository.user;

import com.needus.ecommerce.entity.user.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {

}
