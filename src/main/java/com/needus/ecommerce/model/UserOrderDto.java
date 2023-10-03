package com.needus.ecommerce.model;

import com.needus.ecommerce.entity.user.CartItem;
import com.needus.ecommerce.entity.user.UserInformation;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Date;
import java.util.List;

public class UserOrderDto {
    private Long orderId;
    private List<CartItem> cartItemList;
    private UserInformation userInformation;
    private String orderStatus;
    private Date orderPlacedAt;
    private Date orderDeliveredAt;
    private String paymentMethod;
}
