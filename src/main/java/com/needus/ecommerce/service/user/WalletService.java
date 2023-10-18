package com.needus.ecommerce.service.user;

import com.needus.ecommerce.entity.user.UserInformation;
import com.needus.ecommerce.entity.user.Wallet;

public interface WalletService {
    void walletCredit(UserInformation user, Float totalAmount);

    Float getWalletBalance(UserInformation user);

    void walletDebit(UserInformation user, float totalAmount);

    Wallet createWallet(UserInformation user);
}
