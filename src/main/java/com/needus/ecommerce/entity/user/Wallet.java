package com.needus.ecommerce.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    @OneToMany(mappedBy = "wallet",cascade = CascadeType.ALL)
    List<WalletHistory> walletHistories = new LinkedList<>();
    @OneToOne
        @JoinColumn(name = "user_id")
    UserInformation userInformation;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID walletId;
    private Float balanceAmount = 0F;
}
