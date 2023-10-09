package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.user.Cart;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.entity.user.order.OrderItem;
import com.needus.ecommerce.entity.user.order.UserOrder;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface UserOrderService {
    void placeOrder(Cart cart, UserInformation user, long addressId, String payment);

    Page<UserOrder> findUserOrderByUserId(UUID userId,int pageNo,int pageSize);

    List<OrderItem> findAllPendingOrders();

    List<OrderItem> findAllDeliveredOrders(List<UserOrder> userOrderInfo);

    List<OrderItem> findAllRefundedOrders(List<UserOrder> userOrderInfo);

    List<OrderItem> findAllCanceledOrders(List<UserOrder> userOrderInfo);

    UserOrder findOrderDetailsById(Long orderId);

    void cancelOrder(Long orderId);

    Page<UserOrder> findAllOrders(int pageNo, int pageSize);

    void changeOrderStatus(String value, Long orderId);
}
