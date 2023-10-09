package com.needus.ecommerce.service.user.Impl;

import com.needus.ecommerce.entity.user.Cart;
import com.needus.ecommerce.entity.user.CartItem;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.entity.user.order.OrderItem;
import com.needus.ecommerce.entity.user.enums.OrderStatus;
import com.needus.ecommerce.entity.user.enums.PaymentMethod;
import com.needus.ecommerce.entity.user.order.UserOrder;
import com.needus.ecommerce.repository.user.UserOrderRepository;
import com.needus.ecommerce.service.product.ProductService;
import com.needus.ecommerce.service.user.CartService;
import com.needus.ecommerce.service.user.OrderItemService;
import com.needus.ecommerce.service.user.UserAddressService;
import com.needus.ecommerce.service.user.UserOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
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
    CartService cartService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @Override
    public void placeOrder(Cart cart, UserInformation user, long addressId, String payment) {
        float discounterPrice = 0;
        List<CartItem> cartItems =cart.getCartItems();
        List<OrderItem> orderItems = new ArrayList<>();
            for(CartItem cartItem : cartItems){
                orderItems.add(orderItemService.save(cartItem));
            }
        float subTotalAmount = cartService.calculateTotalAmount(user);
        float totalAmount = subTotalAmount + discounterPrice;
        UserOrder order =  new UserOrder();
        order.setOrderItems(orderItems);
        if(payment.equalsIgnoreCase("cod")){
            order.setPaymentMethod(PaymentMethod.COD);
            order.setOrderStatus(OrderStatus.PROCESSING);
        }
        else{
            order.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
            order.setOrderStatus(OrderStatus.PENDING);
        }
        order.setUserAddress(addressService.findAddressByAddressId(addressId));
        order.setUserInformation(user);
        order.setTotalAmount(totalAmount);
        order.setOrderPlacedAt(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
        for(OrderItem item : orderItems){
            productService.reduceStock(item.getProduct().getProductId(),item.getQuantity());
        }
        cartService.removeAllCartItem(cart);
        orderRepository.save(order);
    }

    @Override
    public Page<UserOrder> findUserOrderByUserId(UUID userId,int pageNo,int pageSize) {
        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize);
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
    public List<OrderItem> findAllDeliveredOrders(List<UserOrder> userOrderInfo) {
        List<UserOrder> userOrders = orderRepository.findDeliveredOrder();
        List<OrderItem> orderItems = new ArrayList<>();
        for(UserOrder userOrder : userOrders){
            orderItems.addAll(userOrder.getOrderItems());
        }
        return orderItems;
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
            order.setOrderStatus(OrderStatus.CANCELLED);
            order.setOrderCancelledAt(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
        }
        orderRepository.save(order);
    }
}
