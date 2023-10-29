package com.needus.ecommerce.repository.user;

import com.needus.ecommerce.entity.user.WalletHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletHistoryRepository extends JpaRepository<WalletHistory,Long> {
    List<WalletHistory> findByWallet_WalletId(UUID walletId, Sort sort);
}
