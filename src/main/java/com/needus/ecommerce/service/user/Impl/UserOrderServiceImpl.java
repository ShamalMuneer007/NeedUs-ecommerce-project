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
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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
    Calendar calendar =Calendar.getInstance();
    Date currentDate = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String formattedDate = dateFormat.format(currentDate);
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
        order.setOrderStatus(OrderStatus.PENDING);
        if(payment.equalsIgnoreCase("cod")){
            order.setPaymentMethod(PaymentMethod.COD);
        }
        else{
            order.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
        }
        order.setUserAddress(addressService.findAddressByAddressId(addressId));
        order.setUserInformation(user);
        order.setTotalAmount(totalAmount);
        order.setOrderPlacedAt(new Date());
        for(OrderItem item : orderItems){
            productService.reduceStock(item.getProduct().getProductId(),item.getQuantity());
        }
        cartService.removeAllCartItem(cart);
        orderRepository.save(order);
    }

    @Override
    public List<UserOrder> findUserOrderByUserId(UUID userId) {
        return orderRepository.findByUserInformation_UserId(userId);
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
        order.setOrderCancelledAt(new Date());
        orderRepository.save(order);
    }

    @Override
    public List<UserOrder> findAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public void changeOrderStatus(String value, Long orderId) {
        UserOrder order = orderRepository.findById(orderId).get();
        if(value.equalsIgnoreCase("1")){
            order.setOrderStatus(OrderStatus.PROCESSING);
        }
        else if(value.equalsIgnoreCase("2")){
            order.setOrderStatus(OrderStatus.SHIPPED);
        }
        else if(value.equalsIgnoreCase("3")){
            order.setOrderStatus(OrderStatus.DELIVERED);
        }
        else if(value.equalsIgnoreCase("4")){
            order.setOrderStatus(OrderStatus.CANCELLED);
        }
        orderRepository.save(order);
    }
}
