package com.needus.ecommerce.service.user.Impl;

import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.entity.user.Wallet;
import com.needus.ecommerce.entity.user.WalletHistory;
import com.needus.ecommerce.repository.user.WalletRepository;
import com.needus.ecommerce.service.user.WalletHistoryService;
import com.needus.ecommerce.service.user.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WalletServiceImpl implements WalletService {
    @Autowired
    WalletHistoryService walletHistoryService;

    @Autowired
    WalletRepository walletRepository;
    @Override
    public void walletCredit(UserInformation user, Float totalAmount) {
        Wallet userWallet = user.getWallet();
        userWallet.setBalanceAmount(userWallet.getBalanceAmount()+totalAmount);
        WalletHistory walletHistory =
            walletHistoryService.commitWalletCreditTransaction(userWallet,totalAmount);
        userWallet.getWalletHistories().add(walletHistory);
        walletRepository.save(userWallet);
    }

    @Override
    public Float getWalletBalance(UserInformation user) {
        return walletRepository.findById(user.getWallet().getWalletId()).get().getBalanceAmount();
    }

    @Override
    public void walletDebit(UserInformation user, float totalAmount) {
            Wallet userWallet = user.getWallet();
            userWallet.setBalanceAmount(userWallet.getBalanceAmount()-totalAmount);
            WalletHistory walletHistory =
                walletHistoryService.commitWalletDebitTransaction(userWallet,totalAmount);
            userWallet.getWalletHistories().add(walletHistory);
            walletRepository.save(userWallet);
    }
}
