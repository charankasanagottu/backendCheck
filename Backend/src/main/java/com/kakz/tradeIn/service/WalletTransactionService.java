package com.kakz.tradeIn.service;

import com.kakz.tradeIn.domain.WalletTransactionType;
import com.kakz.tradeIn.model.Wallet;
import com.kakz.tradeIn.model.WalletTransaction;

import java.util.List;

public interface WalletTransactionService {
    WalletTransaction createTransaction(Wallet wallet,
                                        WalletTransactionType type,
                                        String transferId,
                                        String purpose,
                                        Long amount
    );

    List<WalletTransaction> getTransactions(Wallet wallet, WalletTransactionType type);
}
