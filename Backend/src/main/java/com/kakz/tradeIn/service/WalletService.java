package com.kakz.tradeIn.service;

import com.kakz.tradeIn.model.Order;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.model.Wallet;

public interface WalletService {
    Wallet getUserWallet(User user);

    Wallet addBalance(Wallet wallet,Long amount);
    Wallet findWalletById(Long id) throws Exception;
    Wallet walletToWalletTransfer(User sender,Wallet recieverWallet,Long amount) throws Exception;
    Wallet payOrderPayment(Order order, User user) throws Exception;
}
