package com.needus.ecommerce.entity.user.order;

import com.needus.ecommerce.entity.user.UserAddress;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.entity.user.enums.OrderStatus;
import com.needus.ecommerce.entity.user.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

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
    @OneToMany
    private List<OrderItem> orderItems;
    @ManyToOne
    @JoinColumn(name="user_id")
    private UserInformation userInformation;
    @ManyToOne
    private UserAddress userAddress;
    @CreatedDate
    private Date orderPlacedAt;
    private Date orderShippedAt;
    private Date orderDeliveredAt;
    private Date orderCancelledAt;
    private Date orderRefundedAt;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private float totalAmount;
}
