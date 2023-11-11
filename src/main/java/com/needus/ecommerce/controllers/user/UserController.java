package com.needus.ecommerce.controllers.user;
import com.needus.ecommerce.entity.product.Coupon;
import com.needus.ecommerce.entity.product.ProductReview;
import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user.*;
import com.needus.ecommerce.entity.user_order.OrderItem;
import com.needus.ecommerce.entity.user_order.UserOrder;
import com.needus.ecommerce.exceptions.OrderTransactionException;
import com.needus.ecommerce.exceptions.ResourceNotFoundException;
import com.needus.ecommerce.exceptions.TechnicalIssueException;
import com.needus.ecommerce.exceptions.UnknownException;
import com.needus.ecommerce.model.user_order.UserOrderDto;
import com.needus.ecommerce.service.product.CategoryService;
import com.needus.ecommerce.service.product.CouponService;
import com.needus.ecommerce.service.product.ProductReviewService;
import com.needus.ecommerce.service.product.ProductService;
import com.needus.ecommerce.service.user.*;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    @Autowired
    CouponService couponService;
    @Autowired
    WalletHistoryService walletHistoryService;
    @Autowired
    ProductReviewService productReviewService;
    @Autowired
    private CategoryService categoryService;

    //wishlists
    @GetMapping("/wishlist-items")
    public String redirectWishlist(HttpSession session) {
        UserInformation user = userService.getCurrentUser();
        Long wishlistId = user.getUserWishlist().getWishlistId();
        return "redirect:/user/wishlist-items/" + wishlistId;
    }

    @GetMapping("/wishlist-items/{wishlistId}")
    public String wishlist(@PathVariable(name = "wishlistId") Long wishlistId, Model model) {
        int userCartSize = 0;
        List<Products> products;
        try {
            products = wishlistService.findWishlistById(wishlistId).getProductsList();
            if (userService.getCurrentUser() != null) {
                userCartSize = userService.getCurrentUser().getCart().getCartItems().size();
            }
        }
        catch (Exception e){
            log.error("Something went wrong while fetching products from wishlist");
            throw new TechnicalIssueException("Something went wrong while fetching products from wishlist");
        }
        model.addAttribute("cartSize",userCartSize);
        model.addAttribute("products", products);
        model.addAttribute("categories",categoryService.findAllNonDeletedCategories());
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        return "user/wishlist";
    }
    @PostMapping("/add-to-wishlist/{productId}")
    public String addItemToWishlist(HttpSession session,
                                    @PathVariable(name = "productId") Long productId,
                                    RedirectAttributes ra){
        try {
            session.removeAttribute("coupon");
            UserInformation user = userService.getCurrentUser();
            Products product = productService.findProductById(productId);
            if (wishlistService.productExists(user.getUserWishlist(), product)) {
                ra.addFlashAttribute("ErrorMsg", "Item is already present in the wishlist");
                return "redirect:/shop/home";
            }
            wishlistService.addProductToWishList(user, product);
        }
        catch (Exception e){
            log.error("Something went wrong while adding product to the wishlist");
        }
        ra.addFlashAttribute("message", "Item added to the wishlist");
        return "redirect:/shop/home";
    }
    @PostMapping("/remove-wishlist-item/{productId}")
    public String removeItemFromWishlist(@PathVariable(name = "productId") Long productId, RedirectAttributes ra) {
        UserInformation user = userService.getCurrentUser();
        Products product = productService.findProductById(productId);
        wishlistService.removeItem(user,product);
        ra.addFlashAttribute("message", "Item removed from the wishlist");
        return "redirect:/user/wishlist-items";
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
    public String cartItems(HttpSession session,
                            @PathVariable(name = "cartId") Long cartId, Model model) throws UnknownException {
        session.removeAttribute("coupon");
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

    @PostMapping("/add-to-cart")
    @ResponseBody
    public ResponseEntity<Map<String,Boolean>> addToCart(@RequestBody Map<String,Object> data,
                            RedirectAttributes ra){
        try {
            Long productId = Long.parseLong(data.get("productId").toString());
            log.info(" " +productId);
            UserInformation user = userService.getCurrentUser();
            Products product = productService.findProductById(productId);
            if (cartService.productExists(user, product)) {
//                ra.addFlashAttribute("message", "Item already present in the cart");
                return new ResponseEntity<>(Map.of("success",false),HttpStatus.OK);
            }

            cartService.addItemtoCart(user, product);
        }
        catch (Exception e){
            log.error("Something went wrong while adding the product to the cart");
            throw new TechnicalIssueException("Something went wrong while adding the product to the cart");
        }
//        ra.addFlashAttribute("message", "Item added to the cart");
        return new ResponseEntity<>(Map.of("success",true),HttpStatus.OK);
    }

    @PostMapping("/remove-cart-item/{itemId}")
    public String removeItemFromCart(@PathVariable(name = "itemId") Long itemId, RedirectAttributes ra) {
        try {
            cartService.removeCartItem(itemId);
        }
        catch (Exception e){
            log.error("Something went wrong while removing the item form the cart");
            throw new TechnicalIssueException("Something went wrong while removing the item form the cart");
        }
        return "redirect:/user/cart-items";
    }

    @PostMapping("/cart-items/add-quantity/{itemId}")
    public String addCartItem(
        @RequestParam(name = "quantity") String qty,
        @PathVariable(name = "itemId") Long itemId, RedirectAttributes ra) {
        log.info("adding item");
        int quantity = Integer.parseInt(qty);
        try {
            cartService.addCartItem(itemId, quantity);
        }
        catch (Exception e){
            log.error("Something went wrong while adding the quantity of the item in the cart");
            throw new TechnicalIssueException("Something went wrong while adding the quantity of the item in the cart");
        }
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
    public String orderCheckOut(HttpSession session,
                                RedirectAttributes ra,
                                Model model) {
        try {
            UserInformation user = userService.getCurrentUser();
            if (user.getUserAddresses().isEmpty() || user.getUserAddresses().stream().allMatch(UserAddress::isDeleted)) {
                return "redirect:/user/addAddress";
            }
            Cart cart = cartService.findUserCartById(user.getCart().getCart_id());
            if (cart.getCartItems().isEmpty()) {
                return "redirect:/shop/home";
            }
            List<CartItem> cartItems = cart.getCartItems();
            for (CartItem cartItem : cartItems) {
                if (cartItem.getQuantity() >= cartItem.getProduct().getStock()) {
                    log.error("cart item quantity exceeded stock : " + cartItem);
                    cartService.removeAllCartItem(cart);
                    throw new TechnicalIssueException("cart item quantity exceeded stock");
                }
            }
            float subTotalAmount = cartService.calculateTotalAmount(user);
            float discountPrice = 0F;
            int userCartSize = 0;
            Coupon coupon = (Coupon) session.getAttribute("coupon");
            if (Objects.nonNull(coupon)) {
                discountPrice = coupon.getCouponDiscount();
            }

            List<Coupon> coupons = couponService.findAllNonDeletedCoupons();
            List<Coupon> usedCoupons = couponService.findAllUserUsedCoupon(user);
            if (userService.getCurrentUser() != null) {
                userCartSize = userService.getCurrentUser().getCart().getCartItems().size();
            }
            coupons.removeAll(usedCoupons);
            model.addAttribute("cartSize", userCartSize);
            model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
            model.addAttribute("addresses", user.getUserAddresses().stream().filter(userAddress -> !userAddress.isDeleted()));
            model.addAttribute("subTotalAmount", subTotalAmount);
            model.addAttribute("items", cartItems);
            model.addAttribute("couponDiscount", discountPrice);
            model.addAttribute("coupons", coupons);
            model.addAttribute("walletAmount", user.getWallet().getBalanceAmount());
        }
        catch (Exception e){
            log.error("Something went wrong while checking out the cart");
            throw new TechnicalIssueException("Something went wrong while checking out the cart");
        }
        return "user/order/checkout";
    }

    @GetMapping("/addAddress")
    public String addUserAddress(Model model,@RequestParam(name="source") String source) {
        UserInformation user = userService.getCurrentUser();
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("source",source);
        return "user/addAddress";
    }

    @PostMapping("/addAddress/saveAddress/{userId}")
    public String saveUserAddress(@PathVariable(name = "userId") UUID userId,
                                  @ModelAttribute UserAddress userAddress,
                                  @RequestParam(name = "source") String source,
                                  RedirectAttributes ra) {
        UserInformation user = userService.findUserById(userId);
        userAddress.setUserInformation(user);
        userAddressService.saveAddress(userAddress);
        log.info("Source : "+source);
        if(Objects.equals(source, "userSettingsPage")){
            ra.addFlashAttribute("message", "Address added successfully");
            return "redirect:/user/profile-settings";
        }
        else {
            ra.addFlashAttribute("message", "Address added successfully");
            return "redirect:/user/order/checkout";
        }
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
        try {
            userAddressService.updateAddress(addressId, address);
        }
        catch (Exception e){
            log.error("Something went wrong while updating the address");
            throw new TechnicalIssueException("Something went wrong while updating the address");
        }
        ra.addFlashAttribute("message", "Address Successfully Updated");
        return "redirect:/user/order/checkout";
    }

    @PostMapping("/order/checkout/order-item")
    public String orderItem(@RequestParam(name = "address") long addressId,
                            @RequestParam(name = "payment") String payment,
                            HttpSession session) throws OrderTransactionException {
        Coupon coupon = (Coupon) session.getAttribute("coupon");
        log.info("Order placing starts......");
        UserInformation user = userService.getCurrentUser();
        Cart cart = cartService.findUserCartById(user.getCart().getCart_id());
        try {
            orderService.placeOrder(cart,user,addressId,payment,coupon);
        }
        catch (Exception e){
            log.error("Something went wrong while placing users order");
            throw new TechnicalIssueException("Something went wrong while placing users order");
        }
        session.removeAttribute("coupon");
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
    @PostMapping("/order/request-order-return/{orderId}")
    public String requestReturnOrder(@PathVariable(name = "orderId") Long orderId,
                              RedirectAttributes ra){
        orderService.requestReturnOrder(orderId);
        ra.addFlashAttribute("message","Return request for your order has been sent");
        return "redirect:/user/my-orders/order-details/"+orderId;
    }

    @GetMapping("/my-orders")
    public String showOrders( @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int pageSize,
                              Model model) {
        UserInformation userInformation = userService.getCurrentUser();
        Page<UserOrder> userOrderInfo = orderService.findUserOrderByUserId(userInformation.getUserId(),page,pageSize);
        Page<UserOrderDto> userOrders = userOrderInfo.map(UserOrderDto::new);
        model.addAttribute("categories",categoryService.findAllNonDeletedCategories());
        model.addAttribute("orders", userOrders);
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
        UserOrder orderDetails;
        UserInformation userInformation;
        try {
            userInformation = userService.getCurrentUser();
            orderDetails = orderService.findOrderDetailsById(orderId);
        }
        catch (Exception e){
            log.error("something went wrong while collecting order details of the user");
            throw new TechnicalIssueException("something went wrong while collecting order details of the user");
        }
        if(!orderDetails.getUserInformation().equals(userInformation)){
            log.error("User tried to access unauthorized resources");
            throw new AccessDeniedException("User tried to access unauthorized resources");
        }
        UserOrderDto userOrderDto = new UserOrderDto(orderDetails);
        List<OrderItem> orderItems = orderDetails.getOrderItems();
        boolean returnExpired = false;
        boolean returnEligible = false;
        if(Objects.nonNull(orderDetails.getOrderDeliveredAt())){
            returnExpired = LocalDateTime.now().isAfter(orderDetails.getOrderDeliveredAt().plusDays(7));
        }
        if(Objects.isNull(orderDetails.getReturnRequestedAt())){
            returnEligible = true;
        }
        model.addAttribute("returnEligible",returnEligible);
        model.addAttribute("returnExpired",returnExpired);
        model.addAttribute("orderDetails",userOrderDto);
        model.addAttribute("orderItems",orderItems);
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        return "user/order/orderDetails";
    }
    @GetMapping("/profile-settings")
    public String profileSettings(Model model){
        UserInformation userInformation;
        List<UserOrder> userOrdersWithCoupon;
        Page<UserOrder> userOrderInfo;
        Page<UserOrderDto> userOrders;
        List<WalletHistory> walletHistories;
        try {
            userInformation = userService.getCurrentUser();
            userOrdersWithCoupon = orderService.findAllOrdersWithCoupon(userInformation);
            userOrderInfo = orderService.findUserOrderByUserId(userInformation.getUserId(),1,10);
            userOrders = userOrderInfo.map(UserOrderDto::new);
            walletHistories = walletHistoryService.findAllUserWalletTransactions(userInformation.getWallet());
        }
        catch (Exception e){
            log.error("Something went wrong while collecting the user information");
            throw new TechnicalIssueException("Something went wrong while collecting user information");
        }
        model.addAttribute("couponOrders",userOrdersWithCoupon);
        model.addAttribute("orders", userOrders);
        model.addAttribute("user",userInformation);
        model.addAttribute("addresses",userInformation.getUserAddresses().stream().filter(userAddress -> !userAddress.isDeleted()).toList());
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("wallet",userInformation.getWallet());
        model.addAttribute("walletHistories",walletHistories);
        return "user/userSettings";
    }

    @PostMapping("/save-user-changes/{userId}")
    public String changeUserDetails(@RequestParam(name="username",required = false)String username,
                                    @RequestParam(name = "email",required = false)String email,
                                    @RequestParam(name = "phoneNumber")String phoneNumber,
                                    @PathVariable(name="userId") UUID userId,
                                    RedirectAttributes ra, Model model){
        userService.editUserDetails(userId,username,email,phoneNumber);
        ra.addFlashAttribute("message","User edited successfully");
        return "redirect:/user/profile-settings";
    }
    @PostMapping("/deleteAddress/{addressId}")
    public String deleteAddress(@PathVariable(name = "addressId") Long addressId,
                                RedirectAttributes ra){
        try {
            userAddressService.deleteAddress(addressId);
        }
        catch (Exception e){
            log.error("Something went wrong while deleting the user address");
            throw new TechnicalIssueException("Something went wrong while deleting the user address");
        }
        ra.addFlashAttribute("message","address deleted successfully");
        return "redirect:/user/profile-settings";
    }
    @PostMapping("/publish-product-review/{productId}")
    public String  publishProductReview(@ModelAttribute ProductReview productReview,
                                        @PathVariable(name = "productId") Long productId,
                                        RedirectAttributes ra){
        UserInformation user;
        Products product;
        try {
            user = userService.getCurrentUser();
            product = productService.findProductById(productId);
        }
        catch (Exception e){
            log.error("Something went wrong while publishing the review");
            throw new TechnicalIssueException("Something went wrong while publishing the review");
        }
        productReview.setProduct(product);
        productReview.setUserInformation(user);
        try {
            productReviewService.saveReview(productReview);
        }
        catch (Exception e){
            log.error("Something went wrong while saving the product review");
            throw new TechnicalIssueException("Something went wrong while saving the product review");
        }
        productService.setProductAverageRating(product,productReview.getRating());
        log.info("rating given : "+productReview.getRating());
        ra.addFlashAttribute("Product review added successfully");
        return "redirect:/shop/home/product-details/"+productId;
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
            log.error(e.getMessage());
            throw new TechnicalIssueException(e.getMessage());
        }
        assert order != null;
        return order.toString();
    }
    @PostMapping("/verify-payment")
    @ResponseBody
    public ResponseEntity<Map<String,Boolean>> verifyPayment(@RequestBody Map<String,Object> data){
//        ObjectMapper objectMapper = new ObjectMapper();
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
    @PostMapping("/checkout/apply-coupon")
    @ResponseBody
    public ResponseEntity<Map<String,Boolean>> applyCoupon(@RequestBody Map<String,Object> data, HttpSession httpSession){
        log.info("Applying coupon");
        UserInformation user;
        Coupon coupon;
        Long couponId = Long.parseLong(data.get("coupon").toString());
        log.info("coupon id : "+couponId);
        float subTotalAmount;
        try {
            coupon = couponService.findById(couponId);
            user = userService.getCurrentUser();
            subTotalAmount = cartService.calculateTotalAmount(user);
        }
        catch (Exception e){
            log.error("Something went wrong while applying coupon");
            throw new TechnicalIssueException("Something went wrong while applying coupon");
        }
        log.info(""+coupon.getMinPriceLimit());
        log.info(""+coupon.getMaxPriceLimit());
        if(coupon.getMaxPriceLimit()<subTotalAmount || coupon.getMinPriceLimit()>subTotalAmount){
            return new ResponseEntity<>(Map.of("success", false),HttpStatus.OK);
        }
        httpSession.setAttribute("coupon",coupon);
        return new ResponseEntity<>(Map.of("success", true),HttpStatus.OK);
    }
}
