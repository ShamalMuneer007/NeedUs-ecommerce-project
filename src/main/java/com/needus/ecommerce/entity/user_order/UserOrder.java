package com.needus.ecommerce.entity.user_order;

import com.needus.ecommerce.entity.product.Coupon;
import com.needus.ecommerce.entity.user.UserAddress;
import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.entity.user_order.enums.OrderStatus;
import com.needus.ecommerce.entity.user_order.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrder {
    @OneToOne
    @JoinColumn(name="transaction_id")
    OrderTransactions transaction;
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ORDER_SEQ")
    @SequenceGenerator(name="ORDER_SEQ", sequenceName="ORDER_SEQ", allocationSize=999)
    private Long orderId;
    @OneToMany
    private List<OrderItem> orderItems;
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name="user_id",updatable = false,referencedColumnName = "user_id")
    private UserInformation userInformation;
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "user_address",updatable = false,referencedColumnName = "id")
    private UserAddress userAddress;
    @CreatedDate
    private LocalDateTime orderPlacedAt;
    private LocalDateTime orderShippedAt;
    private LocalDateTime orderDeliveredAt;
    private LocalDateTime orderCancelledAt;
    private LocalDateTime orderReturnedAt;
    private LocalDateTime returnRequestedAt;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @ManyToOne
    private Coupon coupon;
    private float totalAmount;
}
