package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.user.Wishlist;

public interface WishlistService {
    void createCart(Wishlist cart);

    Wishlist findWishlistById(Long wishlistId);
}
