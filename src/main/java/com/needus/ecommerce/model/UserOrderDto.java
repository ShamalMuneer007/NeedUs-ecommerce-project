package com.needus.ecommerce.model;

import com.needus.ecommerce.entity.user.CartItem;
import com.needus.ecommerce.entity.user.UserAddress;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.entity.user.enums.OrderStatus;
import com.needus.ecommerce.entity.user.enums.PaymentMethod;
import com.needus.ecommerce.entity.user.order.OrderItem;
import com.needus.ecommerce.entity.user.order.UserOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderDto {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private Long orderId;
    private List<OrderItem> orderItems;
    private UserInformation userInformation;
    private UserAddress userAddress;
    private LocalDateTime orderPlacedAt;
    private LocalDateTime orderShippedAt;
    private LocalDateTime orderDeliveredAt;
    private LocalDateTime orderCancelledAt;
    private LocalDateTime orderRefundedAt;
    private String expectedDelivery;
    private String orderedDate;
    private String cancelledDate;
    private String deliveredDate;
    private PaymentMethod paymentMethod;
    private OrderStatus orderStatus;
    private float totalAmount;

    public UserOrderDto(UserOrder userOrder) {
        this.orderItems = userOrder.getOrderItems();
        this.orderId = userOrder.getOrderId();
        this.userInformation = userOrder.getUserInformation();
        this.userAddress = userOrder.getUserAddress();
        this.orderStatus = userOrder.getOrderStatus();
        this.totalAmount =userOrder.getTotalAmount();
        this.orderPlacedAt = userOrder.getOrderPlacedAt();
        this.orderShippedAt = userOrder.getOrderShippedAt();
        this.orderDeliveredAt = userOrder.getOrderDeliveredAt();
        this.orderRefundedAt = userOrder.getOrderRefundedAt();
        this.orderCancelledAt = userOrder.getOrderCancelledAt();
        this.paymentMethod =userOrder.getPaymentMethod();
        this.expectedDelivery = this.orderPlacedAt.plusDays(7).format(formatter);
        this.orderedDate = this.orderPlacedAt.format(formatter);
        if(this.orderDeliveredAt!=null)
            this.deliveredDate = this.orderDeliveredAt.format(formatter);
        if(this.orderCancelledAt!=null)
            this.cancelledDate = this.orderCancelledAt.format(formatter);
    }
}
