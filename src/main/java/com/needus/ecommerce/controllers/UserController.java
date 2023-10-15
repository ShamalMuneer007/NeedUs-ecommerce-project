package com.needus.ecommerce.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.needus.ecommerce.entity.order.OrderTransactions;
import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user.Cart;
import com.needus.ecommerce.entity.user.CartItem;
import com.needus.ecommerce.entity.user.UserAddress;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.entity.order.OrderItem;
import com.needus.ecommerce.entity.order.UserOrder;
import com.needus.ecommerce.exceptions.OrderTransactionException;
import com.needus.ecommerce.exceptions.ResourceNotFoundException;
import com.needus.ecommerce.exceptions.TechnicalIssueException;
import com.needus.ecommerce.exceptions.UnknownException;
import com.needus.ecommerce.model.UserOrderDto;
import com.needus.ecommerce.service.product.ProductService;
import com.needus.ecommerce.service.user.*;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.StringReader;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        if (user.getUserAddresses().isEmpty()||user.getUserAddresses().stream().allMatch(UserAddress::isDeleted)) {
            return "redirect:/user/addAddress";
        }
        Cart cart = cartService.findUserCartById(user.getCart().getCart_id());
        if(cart.getCartItems().isEmpty()){
            return "redirect:/shop/home";
        }
        List<CartItem> cartItems = cart.getCartItems();
        for(CartItem cartItem : cartItems){
            if(cartItem.getQuantity()>=cartItem.getProduct().getStock()){
                log.error("cart item quantity exceeded stock : "+cartItem);
                cartService.removeAllCartItem(cart);
                throw new TechnicalIssueException("cart item quantity exceeded stock");
            }
        }
        float subTotalAmount = cartService.calculateTotalAmount(user);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("addresses", user.getUserAddresses().stream().filter(userAddress -> !userAddress.isDeleted()));
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
        if(!userAddressService.existsByAddressId(addressId)){
            throw new ResourceNotFoundException("address Id not found");
        }
        UserInformation user = userService.getCurrentUser();
        UserAddress userAddress = userAddressService.findAddressByAddressId(addressId);
        user.getUserAddresses().forEach(address->{if( !address.getUserInformation().equals(user)){
            try {
                throw new AccessDeniedException("User tried to access unauthorized resources");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }
        });
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
                            @RequestParam(name = "payment") String payment) throws OrderTransactionException {
        UserInformation user = userService.getCurrentUser();
        Cart cart = cartService.findUserCartById(user.getCart().getCart_id());
        try {
            orderService.placeOrder(cart, user, addressId, payment);
        }
        catch (Exception e){
            log.error("Something went wrong while placing users order");
            throw new TechnicalIssueException("Something went wrong while placing users order");
        }
        return "redirect:/user/order/order-successful";
    }
    @GetMapping("/order/order-successful")
    public String orderSuccess(){
        return "user/order/successful";
    }
    @PostMapping("/order/cancel-order/{orderId}")
    public String cancelOrder(@PathVariable(name = "orderId") Long orderId,
                              RedirectAttributes ra){
        orderService.cancelOrder(orderId);
        ra.addFlashAttribute("message","Your order has been cancelled");
        return "redirect:/user/my-orders/order-details/"+orderId;
    }

    @GetMapping("/my-orders")
    public String showOrders( @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int pageSize,
                              Model model) {
        UserInformation userInformation = userService.getCurrentUser();
        Page<UserOrder> userOrderInfo = orderService.findUserOrderByUserId(userInformation.getUserId(),page,pageSize);
        Page<UserOrderDto> userOrderDtos = userOrderInfo.map(UserOrderDto::new);
        model.addAttribute("orders", userOrderDtos);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        return "user/order/listOrders";
    }
    @GetMapping("/my-orders/order-details/{orderId}")
    public String orderDetails(@PathVariable(name = "orderId") Long orderId,
                               Model model) throws AccessDeniedException {
        if(!orderService.existByOrderId(orderId)){
            log.error("order Id does not exists");
            throw new ResourceNotFoundException("order Id does not exists");
        }
        UserInformation userInformation = userService.getCurrentUser();
        UserOrder orderDetails = orderService.findOrderDetailsById(orderId);
        if(!orderDetails.getUserInformation().equals(userInformation)){
            log.error("User tried to access unauthorized resources");
            throw new AccessDeniedException("User tried to access unauthorized resources");
        }
        UserOrderDto userOrderDto = new UserOrderDto(orderDetails);
        List<OrderItem> orderItems = orderDetails.getOrderItems();
        model.addAttribute("orderDetails",userOrderDto);
        model.addAttribute("orderItems",orderItems);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        return "user/order/orderDetails";
    }
    @GetMapping("/profile-settings")
    public String profileSettings(Model model){
        UserInformation userInformation = userService.getCurrentUser();
        Page<UserOrder> userOrderInfo = orderService.findUserOrderByUserId(userInformation.getUserId(),1,10);
        Page<UserOrderDto> userOrderDtos = userOrderInfo.map(UserOrderDto::new);
        model.addAttribute("orders", userOrderDtos);
        model.addAttribute("user",userInformation);
        model.addAttribute("addresses",userInformation.getUserAddresses().stream().filter(userAddress -> !userAddress.isDeleted()).toList());
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        return "user/userSettings";
    }
    @PostMapping("/deleteAddress/{addressId}")
    public String deleteAddress(@PathVariable(name = "addressId") Long addressId,
                                RedirectAttributes ra){
        userAddressService.deleteAddress(addressId);
        ra.addFlashAttribute("message","address deleted successfully");
        return "redirect:/user/profile-settings";
    }

    @PostMapping("/create-order")
    @ResponseBody
    public String createRazorPayOrder(@RequestBody Map<String,Object> data) throws RazorpayException {

        float amount = Float.parseFloat(data.get("amount").toString());
        log.info("payment-done : "+data+" amount : "+amount);
        RazorpayClient razorpayClient = new RazorpayClient("rzp_test_7sGRBU2Gye6g5s","lleVNC37FoIzvhHMdV9M3VN5");
        Order order = null;
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount*100); // amount in the smallest currency unit
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_324124");
            order = razorpayClient.Orders.create(orderRequest);
            log.info("Order  : "+order);
        } catch (RazorpayException e) {
            // Handle Exception
            System.out.println(e.getMessage());
        }
        return order.toString();
    }
    @PostMapping("/verify-payment")
    @ResponseBody
    public ResponseEntity<Map<String,Boolean>> verifyPayment(@RequestBody Map<String,Object> data) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        log.info(""+data.get("payment"));
        log.info(""+data.get("order"));

//        String orderJsonString = (String) data.get("order");
//        Map<String, Object> jsonMap = objectMapper.readValue(orderJsonString, new TypeReference<>() {
//        });
//        float amount = Float.parseFloat((String)jsonMap.get("amount"));
////        int age = (int) jsonMap.get("age");
////        String city = (String) jsonMap.get("city");
//        log.info("amount"+amount);

        return new ResponseEntity<>(Map.of("success", true),HttpStatus.OK);
    }

}
