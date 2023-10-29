package com.needus.ecommerce.service.user.Impl;

import com.needus.ecommerce.entity.user.Wallet;
import com.needus.ecommerce.entity.user.WalletHistory;
import com.needus.ecommerce.entity.user.enums.WalletTransactionType;
import com.needus.ecommerce.repository.user.WalletHistoryRepository;
import com.needus.ecommerce.service.user.WalletHistoryService;
import com.needus.ecommerce.service.user.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class WalletHistoryServiceImpl implements WalletHistoryService {

    @Autowired
    WalletHistoryRepository walletHistoryRepository;
    @Override
    public WalletHistory commitWalletCreditTransaction(Wallet userWallet,Float totalAmount) {
        WalletHistory walletHistory = new WalletHistory();
        walletHistory.setWalletTransactionType(WalletTransactionType.CREDITED);
        walletHistory.setAmount(totalAmount);
        walletHistory.setWalletTransactionTime(LocalDateTime.now());
        walletHistory.setWallet(userWallet);
        return walletHistoryRepository.save(walletHistory);
    }

    @Override
    public WalletHistory commitWalletDebitTransaction(Wallet userWallet, float totalAmount) {
        WalletHistory walletHistory = new WalletHistory();
        walletHistory.setWalletTransactionType(WalletTransactionType.DEBITED);
        walletHistory.setAmount(totalAmount);
        walletHistory.setWalletTransactionTime(LocalDateTime.now());
        walletHistory.setWallet(userWallet);
        return walletHistoryRepository.save(walletHistory);
    }

    @Override
    public List<WalletHistory> findAllUserWalletTransactions(Wallet wallet) {
        Sort sort = Sort.by(Sort.Order.desc("walletTransactionTime"));
        return walletHistoryRepository.findByWallet_WalletId(wallet.getWalletId(),sort);
    }
}
