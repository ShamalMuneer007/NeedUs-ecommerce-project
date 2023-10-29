package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.user.Wallet;
import com.needus.ecommerce.entity.user.WalletHistory;

import java.util.List;

public interface WalletHistoryService {
    WalletHistory commitWalletCreditTransaction(Wallet userWallet,Float totalAmount);

    WalletHistory commitWalletDebitTransaction(Wallet userWallet, float totalAmount);

    List<WalletHistory> findAllUserWalletTransactions(Wallet wallet);
}
