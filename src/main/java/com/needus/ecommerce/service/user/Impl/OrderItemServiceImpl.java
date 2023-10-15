package com.needus.ecommerce.service.user.Impl;

import com.needus.ecommerce.entity.user.CartItem;
import com.needus.ecommerce.entity.order.OrderItem;
import com.needus.ecommerce.repository.user.OrderItemRepository;
import com.needus.ecommerce.service.user.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    @Autowired
    OrderItemRepository repository;
    @Override
    public OrderItem save(CartItem cartItem) {
        return repository.save(new OrderItem(cartItem));
    }
}
