package com.kakz.tradeIn.model;

import com.kakz.tradeIn.domain.WalletTransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * Represents a transaction within a wallet.
 *
 * This entity tracks the details of a transaction, including the type of transaction,
 * the associated wallet, the date of the transaction, and metadata such as the purpose
 * and transfer identifier.
 */
@Entity
@Data
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Wallet wallet;

    private WalletTransactionType walletTransactionType;

    /**
     * The date of the transaction.
     *
     * This field represents the specific date on which the transaction occurred within the wallet.
     * It is used for tracking the chronological order of transactions.
     */
    private LocalDate date;

    private String transferId;
    private String purpose;
    private Long amount;
}
