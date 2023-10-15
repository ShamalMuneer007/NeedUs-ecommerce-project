package com.needus.ecommerce.repository.user;

import com.needus.ecommerce.entity.user.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletHistoryRepository extends JpaRepository<WalletHistory,Long> {
}
