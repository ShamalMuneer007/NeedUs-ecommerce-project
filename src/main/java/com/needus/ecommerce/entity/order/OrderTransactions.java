package com.needus.ecommerce.entity.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderTransactions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID transactionId;
    @Column(name = "razorPay_order_id")
    private String razorPayOrderId;
    private Float amount;
    private String Notes;
    private LocalDateTime transactionAt;
    private String receipt;
}
