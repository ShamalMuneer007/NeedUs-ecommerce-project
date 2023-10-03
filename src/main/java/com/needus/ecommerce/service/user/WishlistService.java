package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.user.Wishlist;

public interface WishlistService {

    Wishlist findWishlistById(Long wishlistId);

    void createWishlist(Wishlist wishlist);
}
