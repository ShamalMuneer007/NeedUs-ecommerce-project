package com.needus.ecommerce.entity.user;

import com.needus.ecommerce.entity.user.enums.WalletTransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WalletHistory {
    @ManyToOne
        @JoinColumn(name = "wallet_id")
    Wallet wallet;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime walletTransactionTime;
    private Float amount;
    @Enumerated(EnumType.STRING)
    private WalletTransactionType walletTransactionType;

}
