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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrder {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ORDER_SEQ")
    @SequenceGenerator(name="ORDER_SEQ", sequenceName="ORDER_SEQ", allocationSize=999)
    private Long orderId;
    @OneToMany
    private List<OrderItem> orderItems;
    @ManyToOne
    @JoinColumn(name="user_id")
    private UserInformation userInformation;
    @ManyToOne
    private UserAddress userAddress;
    @CreatedDate
    private LocalDateTime orderPlacedAt;
    private LocalDateTime orderShippedAt;
    private LocalDateTime orderDeliveredAt;
    private LocalDateTime orderCancelledAt;
    private LocalDateTime orderRefundedAt;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private float totalAmount;
}
