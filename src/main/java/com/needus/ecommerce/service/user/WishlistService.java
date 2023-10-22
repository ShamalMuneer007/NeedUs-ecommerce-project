package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.entity.user.Wishlist;

public interface WishlistService {

     void removeItem(UserInformation user, Products product);

    Wishlist findWishlistById(Long wishlistId);

    void createWishlist(Wishlist wishlist);

    void addProductToWishList(UserInformation user, Products product);

    boolean productExists(Wishlist userWishlist, Products product);
}
