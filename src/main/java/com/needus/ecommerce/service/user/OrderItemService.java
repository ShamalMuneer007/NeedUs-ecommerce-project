package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.user.CartItem;
import com.needus.ecommerce.entity.user_order.OrderItem;
import com.needus.ecommerce.model.user_order.OrderItemProjection;

import java.util.List;

public interface OrderItemService {
    public OrderItem save(CartItem cartItem);

    List<OrderItem> findAll();

    List<OrderItemProjection> findAllGroupByProduct();
}
