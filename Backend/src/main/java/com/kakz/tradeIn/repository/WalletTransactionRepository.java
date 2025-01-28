package com.kakz.tradeIn.repository;

import com.kakz.tradeIn.model.Wallet;
import com.kakz.tradeIn.model.WalletTransaction;
import com.kakz.tradeIn.service.WalletTransactionService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction,Long> {

    List<WalletTransaction> findByWalletOrderByDateDesc(Wallet wallet);
}
