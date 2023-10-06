package com.needus.ecommerce.controllers;

import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user.Cart;
import com.needus.ecommerce.entity.user.CartItem;
import com.needus.ecommerce.entity.user.UserAddress;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.entity.user.order.OrderItem;
import com.needus.ecommerce.entity.user.order.PaymentMethod;
import com.needus.ecommerce.entity.user.order.UserOrder;
import com.needus.ecommerce.exceptions.UnknownException;
import com.needus.ecommerce.model.UserAddressDto;
import com.needus.ecommerce.service.product.ProductService;
import com.needus.ecommerce.service.user.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserInformationService userService;
    @Autowired
    UserAddressService userAddressService;

    @Autowired
    ProductService productService;
    @Autowired
    WishlistService wishlistService;

    @Autowired
    UserOrderService orderService;

    @Autowired
    CartService cartService;

    //wishlists
    @GetMapping("/wishlist-items/")
    public String redirectWishlist() {
        UserInformation user = userService.getCurrentUser();
        Long wishlistId = user.getUserWishlist().getWishlistId();
        return "redirect:user/wishlist-items/" + wishlistId;
    }

    @GetMapping("/wishlist-items/{wishlistId}")
    public String wishlist(@PathVariable(name = "wishlistId") Long wishlistId, Model model) {
        List<Products> products = wishlistService.findWishlistById(wishlistId).getProductsList();
        model.addAttribute("products", products);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        return "user/wishlist";
    }

    //methods for user cart
    @GetMapping("/cart-items")
    public String cartRedirect() {
        UserInformation user = userService.getCurrentUser();
        log.info("cart user id : " + user.getUserId());
        Long cartId = user.getCart().getCart_id();
        return "redirect:/user/cart-items/" + cartId;
    }

    @GetMapping("/cart-items/{cartId}")
    public String cartItems(@PathVariable(name = "cartId") Long cartId, Model model) throws UnknownException {
        UserInformation user = userService.getCurrentUser();
        if (!cartId.equals(user.getCart().getCart_id())) {
            throw new UnknownException();
        }
        List<CartItem> items = cartService.findUserCartById(cartId).getCartItems();
        float totalAmount = cartService.calculateTotalAmount(user);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("items", items);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        return "user/cart";
    }

    @PostMapping("/add-to-cart/{productId}")
    public String addToCart(@PathVariable(name = "productId") Long productId,
                            RedirectAttributes ra, Model model) {
        UserInformation user = userService.getCurrentUser();
        Products product = productService.findProductById(productId);
        cartService.addItemtoCart(user, product);
        ra.addFlashAttribute("message", "Item added to the cart");
        return "redirect:/shop/home";
    }

    @PostMapping("/remove-cart-item/{itemId}")
    public String removeItemFromCart(@PathVariable(name = "itemId") Long itemId, RedirectAttributes ra) {
        cartService.removeCartItem(itemId);
        return "redirect:/user/cart-items";
    }

    @PostMapping("/cart-items/add-quantity/{itemId}")
    public String addCartItem(
        @RequestParam(name = "quantity") String qty,
        @PathVariable(name = "itemId") Long itemId, RedirectAttributes ra) {
        log.info("adding item");
        int quantity = Integer.parseInt(qty);
        cartService.addCartItem(itemId, quantity);
        return "redirect:/user/cart-items";
    }

    @PostMapping("/cart-items/remove-all")
    public String removeAllCartItem(RedirectAttributes ra) {
        UserInformation user = userService.getCurrentUser();
        Cart cart = cartService.findUserCartById(user.getCart().getCart_id());
        cartService.removeAllCartItem(cart);
        ra.addFlashAttribute("message", "All items from the cart is removed");
        return "redirect:/user/cart-items";
    }

    @GetMapping("/order/checkout")
    public String orderCheckOut(RedirectAttributes ra, Model model) {
        UserInformation user = userService.getCurrentUser();
        List<UserAddress> userAddresses = new ArrayList<>();
        List<UserAddressDto> userAddressDtoList = new ArrayList<>();
        if (user.getUserAddresses().isEmpty()) {
            return "redirect:/user/addAddress";
        }
        userAddresses = user.getUserAddresses();
        for (UserAddress userAddress : userAddresses) {
            userAddressDtoList.add(new UserAddressDto(userAddress));
        }
        Cart cart = cartService.findUserCartById(user.getCart().getCart_id());
        List<CartItem> cartItems = cart.getCartItems();
        float subTotalAmount = cartService.calculateTotalAmount(user);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("addresses", userAddressDtoList);
        model.addAttribute("subTotalAmount", subTotalAmount);
        model.addAttribute("items", cartItems);
        return "user/order/checkout";
    }

    @GetMapping("/addAddress")
    public String addUserAddress(Model model) {
        UserInformation user = userService.getCurrentUser();
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("userId", user.getUserId());
        return "user/addAddress";
    }

    @PostMapping("/addAddress/saveAddress/{userId}")
    public String saveUserAddress(@PathVariable(name = "userId") UUID userId,
                                  @ModelAttribute UserAddress userAddress,
                                  RedirectAttributes ra) {
        UserInformation user = userService.findUserById(userId);
        userAddress.setUserInformation(user);
        userAddress = userAddressService.saveAddress(userAddress);
        List<UserAddress> userAddresses = new ArrayList<>();
        if (user.getUserAddresses() != null) {
            userAddresses = user.getUserAddresses();
        }
        userAddresses.add(userAddress);
        userService.updateUser(userId, user);
        ra.addFlashAttribute("message", "Address added successfully");
        return "redirect:/user/order/checkout";
    }

    @GetMapping("/changeAddress/{addressId}")
    public String updateAddress(@PathVariable(name = "addressId") long addressId,
                                Model model) {
        UserInformation user = userService.getCurrentUser();
        UserAddress userAddress = userAddressService.findAddressByAddressId(addressId);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("addressId", addressId);
        model.addAttribute("oldAddress", userAddress);
        return "user/updateAddress";
    }

    @PostMapping("/updateAddress/saveAddress/{addressId}")
    public String saveAddressChanges(@PathVariable(name = "addressId") long addressId,
                                     UserAddress address,
                                     RedirectAttributes ra) {
        userAddressService.updateAddress(addressId, address);
        ra.addFlashAttribute("message", "Address Successfully Updated");
        return "redirect:/user/order/checkout";
    }

    @PostMapping("/order/checkout/order-item")
    public String orderItem(@RequestParam(name = "address") long addressId,
                            @RequestParam(name = "payment") String payment) {
        UserInformation user = userService.getCurrentUser();
        Cart cart = cartService.findUserCartById(user.getCart().getCart_id());
        orderService.placeOrder(cart, user, addressId, payment);
        return "redirect:/shop/home";
    }

    @GetMapping("/my-orders")
    public String showOrders(Model model) {
        UserInformation userInformation = userService.getCurrentUser();
        List<UserOrder> userOrderInfo = orderService.findUserOrderByUserId(userInformation.getUserId());
        model.addAttribute("orders", userOrderInfo);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        return "user/order/listOrders";
    }
    @GetMapping("/my-orders/order-details/{orderId}")
    public String orderDetails(@PathVariable(name = "orderId") Long orderId,
                               Model model){
        UserOrder orderDetails = orderService.findOrderDetailsById(orderId);
        List<OrderItem> orderItems = orderDetails.getOrderItems();
        model.addAttribute("orderDetails",orderDetails);
        model.addAttribute("orderItems",orderItems);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        return "user/order/orderDetails";
    }

}
