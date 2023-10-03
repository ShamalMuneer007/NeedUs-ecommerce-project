package com.needus.ecommerce.controllers;

import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user.Cart;
import com.needus.ecommerce.entity.user.CartItem;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.exceptions.UnknownException;
import com.needus.ecommerce.service.product.ProductService;
import com.needus.ecommerce.service.user.CartService;
import com.needus.ecommerce.service.user.UserInformationService;
import com.needus.ecommerce.service.user.WishlistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserInformationService userService;

    @Autowired
    ProductService productService;
    @Autowired
    WishlistService wishlistService;

    @Autowired
    CartService cartService;
    //wishlists
    @GetMapping("/wishlist-items/")
    public String redirectWishlist(){
        UserInformation user = userService.getCurrentUser();
        Long wishlistId = user.getUserWishlist().getWishlistId();
        return "redirect:user/wishlist-items/"+wishlistId;
    }
    @GetMapping("/wishlist-items/{wishlistId}")
    public String wishlist(@PathVariable(name ="wishlistId") Long wishlistId, Model model){
        List<Products> products = wishlistService.findWishlistById(wishlistId).getProductsList();
        model.addAttribute("products",products);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        return "user/wishlist";
    }

    //methods for user cart
    @GetMapping("/cart-items")
    public String cartRedirect(){
        UserInformation user = userService.getCurrentUser();
        log.info("cart user id : "+user.getUserId());
        Long cartId = user.getCart().getCart_id();
        return "redirect:/user/cart-items/"+cartId;
    }
    @GetMapping("/cart-items/{cartId}")
    public String cartItems(@PathVariable(name="cartId") Long cartId,Model model) throws UnknownException {
        UserInformation user = userService.getCurrentUser();
        if(!cartId.equals(user.getCart().getCart_id())){
            throw new UnknownException();
        }
        List<CartItem> items = cartService.findUserCartById(cartId).getCartItems();
        float totalAmount = cartService.calculateTotalAmount(user);
        model.addAttribute("totalAmount",totalAmount);
        model.addAttribute("items",items);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        return "user/cart";
    }
    @PostMapping("/add-to-cart/{productId}")
    public String addToCart(@PathVariable(name="productId") Long productId,
                            RedirectAttributes ra, Model model){
        UserInformation user = userService.getCurrentUser();
        Products product = productService.findProductById(productId);
        cartService.addItemtoCart(user,product);
        ra.addFlashAttribute("message","Item added to the cart");
        return "redirect:/shop/home";
    }
    @PostMapping("/remove-cart-item/{itemId}")
    public String removeItemFromCart(@PathVariable(name="itemId") Long itemId,RedirectAttributes ra){
        cartService.removeCartItem(itemId);
        return "redirect:/user/cart-items";
    }
    @PostMapping("/cart-items/add-quantity/{itemId}")
    public String addCartItem(
        @RequestParam(name="quantity") String qty,
        @PathVariable(name="itemId") Long itemId,RedirectAttributes ra){
        log.info("adding item");
        int quantity = Integer.parseInt(qty);
        cartService.addCartItem(itemId,quantity);
        return "redirect:/user/cart-items";
    }
    @PostMapping("/cart-items/remove-all")
    public String removeAllCartItem(RedirectAttributes ra){
        UserInformation user = userService.getCurrentUser();
        Cart cart = cartService.findUserCartById(user.getCart().getCart_id());
        cartService.removeAllCartItem(cart);
        ra.addFlashAttribute("message","All items from the cart is removed");
        return "redirect:/user/cart-items";
    }
    @GetMapping("/order/checkout")
    public String orderCheckOut(RedirectAttributes ra , Model model){
        UserInformation user =userService.getCurrentUser();
        Cart cart = cartService.findUserCartById(user.getCart().getCart_id());
        List<CartItem> cartItems = cart.getCartItems();
        model.addAttribute("items",cartItems);
        return "user/order/checkout";
    }
}
