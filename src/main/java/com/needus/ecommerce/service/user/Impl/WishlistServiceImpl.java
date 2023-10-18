package com.needus.ecommerce.service.user.Impl;

import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.entity.user.Wishlist;
import com.needus.ecommerce.repository.user.WishlistRepository;
import com.needus.ecommerce.service.user.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    WishlistRepository wishlistRepository;

    @Override
    public void removeItem(UserInformation user, Products product) {
        Wishlist wishlist = user.getUserWishlist();
        wishlist.getProductsList().remove(product);
        wishlistRepository.save(wishlist);
    }

    @Override
    public Wishlist findWishlistById(Long wishlistId) {
        return wishlistRepository.findById(wishlistId).get();
    }

    @Override
    public void createWishlist(Wishlist wishlist) {
        wishlistRepository.save(wishlist);
    }

    @Override
    public void addProductToWishList(UserInformation user, Products product) {
        Wishlist wishlist = user.getUserWishlist();
        wishlist.getProductsList().add(product);
        wishlistRepository.save(wishlist);
    }
}
