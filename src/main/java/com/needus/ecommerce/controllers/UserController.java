package com.needus.ecommerce.controllers;

import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.service.user.UserInformationService;
import com.needus.ecommerce.service.user.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserInformationService userService;
    @Autowired
    WishlistService wishlistService;
    @GetMapping("/wishlist-items/")
    public String checkWishlist(){
        UserInformation user = userService.getCurrentUser();
        Long wishlistId = user.getUserWishlist().getWishlistId();
        return "redirect:user/wishlist-items/{wishlistId}";
    }
    @GetMapping("/wishlist-items/{id}")
    public String wishlist(@PathVariable(name ="id") Long wishlistId, Model model){
        List<Products> products = wishlistService.findWishlistById(wishlistId).getProductsList();
        model.addAttribute("products",products);
        return "user/wishlist";
    }
}
