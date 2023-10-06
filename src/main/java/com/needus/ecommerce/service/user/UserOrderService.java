package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.user.Cart;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.entity.user.order.OrderItem;
import com.needus.ecommerce.entity.user.order.UserOrder;

import java.util.List;
import java.util.UUID;

public interface UserOrderService {
    void placeOrder(Cart cart, UserInformation user, long addressId, String payment);

    List<UserOrder> findUserOrderByUserId(UUID userId);

    List<OrderItem> findAllPendingOrders();

    List<OrderItem> findAllDeliveredOrders(List<UserOrder> userOrderInfo);

    List<OrderItem> findAllRefundedOrders(List<UserOrder> userOrderInfo);

    List<OrderItem> findAllCanceledOrders(List<UserOrder> userOrderInfo);

    UserOrder findOrderDetailsById(Long orderId);
}
