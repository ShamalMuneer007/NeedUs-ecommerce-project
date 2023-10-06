package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.user.CartItem;
import com.needus.ecommerce.entity.user.order.OrderItem;

public interface OrderItemService {
    public OrderItem save(CartItem cartItem);
}
