package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user.Cart;
import com.needus.ecommerce.entity.user.UserInformation;

import java.util.List;

public interface CartService {
    Cart findUserCartById(Long cartId);

    void createCart(Cart cart);

    void addItemtoCart(UserInformation user, Products product);

    void removeCartItem(Long itemId);

    void addCartItem(Long itemId, int quantity);

    Float calculateTotalAmount(UserInformation user);

    void removeAllCartItem(Cart cart);
}
