package com.kakz.tradeIn.service;

import com.kakz.tradeIn.domain.WalletTransactionType;
import com.kakz.tradeIn.model.Wallet;
import com.kakz.tradeIn.model.WalletTransaction;
import com.kakz.tradeIn.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of the WalletTransactionService interface.
 * This service handles the creation and retrieval of wallet transactions.
 */
@Service
public class WalletTransactionServiceImpl implements WalletTransactionService{
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;
    /**
     * Creates a new transaction for a given wallet.
     *
     * @param wallet the wallet for which the transaction is to be created
     * @param type the type of transaction (e.g., WITHDRAWAL, WALLET_TRANSFER)
     * @param transferId a unique identifier for the transfer
     * @param purpose the purpose of the transaction
     * @param amount the amount involved in the transaction
     * @return the created wallet transaction
     */
    @Override
    public WalletTransaction createTransaction(Wallet wallet, WalletTransactionType type, String transferId, String purpose, Long amount) {
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);
        transaction.setDate(LocalDate.now());
        transaction.setWalletTransactionType(type);
        transaction.setTransferId(transferId);
        transaction.setPurpose(purpose);
        transaction.setAmount(amount);

        return walletTransactionRepository.save(transaction);
    }

    /**
     * Retrieves a list of wallet transactions based on the given wallet and transaction type.
     *
     * @param wallet the wallet for which transactions are to be retrieved
     * @param type the type of transactions to be retrieved (e.g., WITHDRAWAL, WALLET_TRANSFER)
     * @return a list of wallet transactions that match the given wallet and transaction type
     */
    @Override
    public List<WalletTransaction> getTransactions(Wallet wallet, WalletTransactionType type) {
        return walletTransactionRepository.findByWalletOrderByDateDesc(wallet);
    }
}
