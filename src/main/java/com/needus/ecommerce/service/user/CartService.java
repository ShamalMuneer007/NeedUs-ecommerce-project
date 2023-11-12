package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user.Cart;
import com.needus.ecommerce.entity.user.UserInformation;

public interface CartService {
    Cart findUserCartById(Long cartId);

    Cart createCart();

    void addItemtoCart(UserInformation user, Products product);

    void removeCartItem(Long itemId);

    Float addCartItem(Long itemId, int quantity);

    Float calculateTotalAmount(UserInformation user);

    void removeAllCartItem(Cart cart);

    void updateCart(Cart cart);

    boolean productExists(UserInformation user, Products product);
}
