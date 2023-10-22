package com.needus.ecommerce.service.user.Impl;

import com.needus.ecommerce.entity.product.Coupon;
import com.needus.ecommerce.entity.product.Products;
import com.needus.ecommerce.entity.user.Cart;
import com.needus.ecommerce.entity.user.CartItem;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.entity.user_order.OrderItem;
import com.needus.ecommerce.entity.user_order.enums.OrderStatus;
import com.needus.ecommerce.entity.user_order.enums.PaymentMethod;
import com.needus.ecommerce.entity.user_order.UserOrder;
import com.needus.ecommerce.exceptions.OrderTransactionException;
import com.needus.ecommerce.repository.user.UserOrderRepository;
import com.needus.ecommerce.service.product.CouponService;
import com.needus.ecommerce.service.product.ProductService;
import com.needus.ecommerce.service.user.*;
import com.needus.ecommerce.service.verification.EmailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class UserOrderServiceImpl implements UserOrderService {
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    UserOrderRepository orderRepository;
    @Autowired
    UserAddressService addressService;

    @Autowired
    ProductService productService;
    @Autowired
    WalletService walletService;
    @Autowired
    EmailService emailService;
    @Autowired
    CartService cartService;
    @Autowired
    CouponService couponService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Override
    public UserOrder placeOrder(Cart cart, UserInformation user, long addressId, String payment,Coupon coupon) throws MessagingException, OrderTransactionException {
        float discountedPrice = 0;
        if(Objects.nonNull(coupon)) {
            discountedPrice = coupon.getCouponDiscount();
        }
        List<CartItem> cartItems =cart.getCartItems();
        List<OrderItem> orderItems = new ArrayList<>();
            for(CartItem cartItem : cartItems){
                orderItems.add(orderItemService.save(cartItem));
            }
        float subTotalAmount = cartService.calculateTotalAmount(user);
        float totalAmount = subTotalAmount - subTotalAmount*discountedPrice;
        UserOrder order =  new UserOrder();
        order.setOrderItems(orderItems);
        if(payment.equalsIgnoreCase("cod")){
            order.setPaymentMethod(PaymentMethod.COD);
            order.setOrderStatus(OrderStatus.PROCESSING);
        }
        else if(payment.equalsIgnoreCase("online")){
            order.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
            order.setOrderStatus(OrderStatus.PENDING);
        }
        else if(payment.equalsIgnoreCase("wallet")){
            if(totalAmount>walletService.getWalletBalance(user)){
                log.error("User : "+user.getUserId()+"'s wallet has insufficient balance");
                throw new OrderTransactionException("wallet has insufficient balance");
            }
            walletService.walletDebit(user,totalAmount);
            order.setPaymentMethod(PaymentMethod.WALLET_PAYMENT);
            order.setOrderStatus(OrderStatus.PENDING);
        }
        order.setUserAddress(addressService.findAddressByAddressId(addressId));
        order.setUserInformation(user);
        if(Objects.nonNull(coupon)){
            order.setCoupon(coupon);
        }
        order.setTotalAmount(totalAmount);
        order.setOrderPlacedAt(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
        for(OrderItem item : orderItems){
            productService.reduceStock(item.getProduct().getProductId(),item.getQuantity());
        }
        cartService.removeAllCartItem(cart);
        emailService.sendInvoiceMail(order);
        return orderRepository.save(order);
    }

    @Override
    public Page<UserOrder> findUserOrderByUserId(UUID userId,int pageNo,int pageSize) {
        Sort sort = Sort.by(Sort.Order.desc("orderPlacedAt"));
        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize,sort);
        return orderRepository.findByUserInformation_UserId(userId,pageable);
    }

    @Override
    public List<OrderItem> findAllPendingOrders() {
        List<UserOrder> userOrders = orderRepository.findPendingOrder();
        List<OrderItem> orderItems = new ArrayList<>();
        for(UserOrder userOrder : userOrders){
            orderItems.addAll(userOrder.getOrderItems());
        }
        return orderItems;
    }

    @Override
    public List<UserOrder> findAllDeliveredOrders() {
        return orderRepository.findDeliveredOrder();
    }

    @Override
    public List<OrderItem> findAllRefundedOrders(List<UserOrder> userOrderInfo) {
        List<UserOrder> userOrders = orderRepository.findRefundedOrder();
        List<OrderItem> orderItems = new ArrayList<>();
        for(UserOrder userOrder : userOrders){
            orderItems.addAll(userOrder.getOrderItems());
        }
        return orderItems;
    }

    @Override
    public List<OrderItem> findAllCanceledOrders(List<UserOrder> userOrderInfo) {
        List<UserOrder> userOrders = orderRepository.findCanceledOrder();
        List<OrderItem> orderItems = new ArrayList<>();
        for(UserOrder userOrder : userOrders){
            orderItems.addAll(userOrder.getOrderItems());
        }
        return orderItems;
    }

    @Override
    public UserOrder findOrderDetailsById(Long orderId) {
        return orderRepository.findById(orderId).get();
    }

    @Override
    public void cancelOrder(Long orderId) {
        UserOrder order = orderRepository.findById(orderId).get();
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setOrderCancelledAt(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
        order.getOrderItems().forEach(orderItem ->{
            Products product = orderItem.getProduct();
            product.setStock(product.getStock()+orderItem.getQuantity());});
        if(
            order.getPaymentMethod().equals(PaymentMethod.ONLINE_PAYMENT)||
                order.getPaymentMethod().equals(PaymentMethod.WALLET_PAYMENT)){
                walletService.walletCredit(order.getUserInformation(),order.getTotalAmount());
        }
        orderRepository.save(order);
    }
    @Override
    public void returnOrder(Long orderId){
        UserOrder order = orderRepository.findById(orderId).get();
        order.setOrderStatus(OrderStatus.RETURNED);
        order.setOrderReturnedAt(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
        order.getOrderItems().forEach(orderItem ->{
            Products product = orderItem.getProduct();
            product.setStock(product.getStock()+orderItem.getQuantity());});
        walletService.walletCredit(order.getUserInformation(),order.getTotalAmount());
        orderRepository.save(order);
    }
    @Override
    public void requestReturnOrder(Long orderId){
        UserOrder order = orderRepository.findById(orderId).get();
        order.setOrderStatus(OrderStatus.RETURN_REQUESTED);
        order.setReturnRequestedAt(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
        orderRepository.save(order);
    }

    @Override
    public Page<UserOrder> findAllOrders(int pageNo , int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return orderRepository.findAll(pageable);
    }

    @Override
    public void changeOrderStatus(String value, Long orderId) {
        UserOrder order = orderRepository.findById(orderId).get();
        if(value.equalsIgnoreCase("1")){
            order.setOrderStatus(OrderStatus.PROCESSING);
        }
        else if(value.equalsIgnoreCase("2")){
            order.setOrderStatus(OrderStatus.SHIPPED);
            order.setOrderShippedAt(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
        }
        else if(value.equalsIgnoreCase("3")){
            order.setOrderStatus(OrderStatus.DELIVERED);
            order.setOrderDeliveredAt(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
        }
        else if(value.equalsIgnoreCase("4")){
            cancelOrder(orderId);
        }
        orderRepository.save(order);
    }

    @Override
    public boolean existByOrderId(Long orderId) {
        return orderRepository.existsById(orderId);
    }

    @Override
    public void cancelReturnRequest(Long orderId) {
        UserOrder order = orderRepository.findById(orderId).get();
        order.setOrderStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }

    @Override
    public List<UserOrder> findAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<UserOrder> findOrdersByDate(LocalDate currentDate) {
        return orderRepository.findByOrderPlacedAt(currentDate.atTime(12,0,0));
    }
}
