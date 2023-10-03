package com.needus.ecommerce.service.user.Impl;

import com.needus.ecommerce.entity.user.CartItem;
import com.needus.ecommerce.repository.user.CartItemRepository;
import com.needus.ecommerce.service.user.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartItemServiceImpl implements CartItemService {
    @Autowired
    CartItemRepository cartItemRepository;
    @Override
    public void saveItem(CartItem cartItem) {
        cartItemRepository.save(cartItem);
    }

    @Override
    public void deleteItemById(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }

    @Override
    public void addQuantity(Long itemId, int quantity) {
        CartItem item  = cartItemRepository.findById(itemId).get();
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }
}
