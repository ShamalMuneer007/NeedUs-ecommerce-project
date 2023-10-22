package com.needus.ecommerce.service.user.Impl;

import com.needus.ecommerce.entity.user.CartItem;
import com.needus.ecommerce.entity.user_order.OrderItem;
import com.needus.ecommerce.model.user_order.OrderItemProjection;
import com.needus.ecommerce.repository.user.OrderItemRepository;
import com.needus.ecommerce.service.user.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    @Autowired
    OrderItemRepository repository;
    @Override
    public OrderItem save(CartItem cartItem) {
        return repository.save(new OrderItem(cartItem));
    }

    @Override
    public List<OrderItem> findAll() {
        return repository.findAll();
    }

    @Override
    public List<OrderItemProjection> findAllGroupByProduct() {
        return repository.findAllGroupByProducts();
    }
}
