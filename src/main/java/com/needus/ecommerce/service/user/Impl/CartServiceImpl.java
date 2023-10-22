package com.needus.ecommerce.service.user.Impl;

import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user.Cart;
import com.needus.ecommerce.entity.user.CartItem;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.repository.user.CartRepository;
import com.needus.ecommerce.service.user.CartItemService;
import com.needus.ecommerce.service.user.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartItemService cartItemService;
    @Autowired
    CartRepository cartRepository;
    @Override
    public Cart findUserCartById(Long cartId) {
        return cartRepository.findById(cartId).get();
    }

    @Override
    public Cart createCart() {
        Cart cart = new Cart();
        return cartRepository.save(cart);
    }

    @Override
    public void addItemtoCart(UserInformation user, Products product) {
        Cart cart = user.getCart();
        if(cart.getCartItems()!=null) {
            for (CartItem cartItem : cart.getCartItems()) {
                if (cartItem.getProduct() != null && cartItem.getProduct().equals(product)) {
                    return;
                }
            }
        }
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        cartItemService.saveItem(cartItem);
        List<CartItem> items = cart.getCartItems();
        items.add(cartItem);
        cart.setCartItems(items);
        cartRepository.save(cart);
    }

    @Override
    public void removeCartItem(Long itemId) {
        cartItemService.deleteItemById(itemId);
    }

    @Override
    public void addCartItem(Long itemId, int quantity) {
        cartItemService.addQuantity(itemId,quantity);
    }

    @Override
    public Float calculateTotalAmount(UserInformation user) {

        Cart cart = user.getCart();
        float totalAmount = 0;
        for(CartItem item : user.getCart().getCartItems()){
            totalAmount = totalAmount+(item.getProduct().getProductPrice()*item.getQuantity());
        }
        return totalAmount;
    }

    @Override
    public void removeAllCartItem(Cart cart) {
        cart.getCartItems().removeAll(cart.getCartItems());
        cartRepository.save(cart);
    }

    @Override
    public void updateCart(Cart cart) {
        cartRepository.save(cart);
    }

    @Override
    public boolean productExists(UserInformation user, Products product) {
        Cart cart = user.getCart();
        return cart.getCartItems().stream().anyMatch(cartItem -> cartItem.getProduct().equals(product));
    }
}
