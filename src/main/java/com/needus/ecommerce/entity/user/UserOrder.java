package com.needus.ecommerce.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    @ManyToOne
    @JoinColumn(name="products_list")
    private List<CartItem> cartItemList;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInformation userInformation;
    private String orderStatus;
    private Date orderPlacedAt;
    private Date orderDeliveredAt;
    private String paymentMethod;
}
