package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.user.CartItem;

public interface CartItemService {
    void saveItem(CartItem cartItem);

    void deleteItemById(Long itemId);

    void addQuantity(Long itemId, int quantity);
}
