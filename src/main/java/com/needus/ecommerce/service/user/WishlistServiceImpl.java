package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.user.Wishlist;
import com.needus.ecommerce.repository.user.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WishlistServiceImpl implements WishlistService{

    @Autowired
    WishlistRepository wishlistRepository;
    @Override
    public void createCart(Wishlist cart) {

    }

    @Override
    public Wishlist findWishlistById(Long wishlistId) {
        return wishlistRepository.findById(wishlistId).get();
    }
}
