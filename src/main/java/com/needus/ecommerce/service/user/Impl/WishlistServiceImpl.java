package com.needus.ecommerce.service.user.Impl;

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
    public Wishlist findWishlistById(Long wishlistId) {
        return wishlistRepository.findById(wishlistId).get();
    }

    @Override
    public void createWishlist(Wishlist wishlist) {
        wishlistRepository.save(wishlist);
    }
}
