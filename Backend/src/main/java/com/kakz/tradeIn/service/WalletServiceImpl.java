package com.kakz.tradeIn.service;

import com.kakz.tradeIn.domain.OrderType;
import com.kakz.tradeIn.model.Order;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.model.Wallet;
import com.kakz.tradeIn.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * WalletServiceImpl handles the operations related to user's wallets such as fetching wallet details,
 * adding balance to the wallet, transferring funds between wallets and processing order payments.
 */
@Service
public class WalletServiceImpl implements WalletService{
    /**
     * Repository interface for Wallet entities.
     *
     * This repository is responsible for handling CRUD operations and custom queries
     * related to Wallet entities in the database.
     *
     * It is automatically wired into the containing class WalletServiceImpl
     * for performing operations on Wallet objects.
     */
    @Autowired
    private WalletRepository walletRepository;

    /**
     * Retrieves the wallet associated with the given user. If no wallet is found, a new wallet is created,
     * associated with the user, and saved to the repository.
     *
     * @param user the user whose wallet is to be retrieved.
     * @return the wallet associated with the user, or a newly created wallet if none exists.
     */
    @Override
    public Wallet getUserWallet(User user) {
        Wallet wallet = walletRepository.findByUserId(user.getId());
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
            walletRepository.save(wallet);
        }
        return wallet;
    }

    /**
     * Adds a specified amount to the balance of the given wallet.
     *
     * @param wallet the wallet to which the balance will be added
     * @param amount the amount to add to the wallet's balance
     * @return the updated Wallet object after the new balance has been saved
     */
    @Override
    public Wallet addBalance(Wallet wallet, Long amount) {
        BigDecimal balance = wallet.getBalance();
        BigDecimal newBalance =balance.add(BigDecimal.valueOf(amount));
        wallet.setBalance(newBalance);

        return walletRepository.save(wallet);
    }

    /**
     * Finds a Wallet by its unique identifier.
     *
     * @param id the unique identifier of the wallet to find.
     * @return the Wallet associated with the given id.
     * @throws Exception if no wallet is found with the given id.
     */
    @Override
    public Wallet findWalletById(Long id) throws Exception {
        Optional<Wallet> wallet = walletRepository.findById(id);
        if(wallet.isPresent()){
            return wallet.get();
        }
        throw new Exception("Wallet not found");
    }

    /**
     * Transfers a specified amount from the sender's wallet to the receiver's wallet.
     *
     * @param sender the user initiating the transfer
     * @param recieverWallet the wallet that will receive the funds
     * @param amount the amount of money to transfer
     * @return the sender's wallet after the transfer
     * @throws Exception if the sender has insufficient funds
     */
    @Override
    public Wallet walletToWalletTransfer(User sender, Wallet recieverWallet, Long amount) throws Exception {
        Wallet senderWallet = getUserWallet(sender);
        if(senderWallet.getBalance().compareTo(BigDecimal.valueOf(amount))<0){
            throw new Exception("InSufficient funds to transfer");
        }
        BigDecimal senderBalance = senderWallet.
                getBalance()
                .subtract(BigDecimal.valueOf(amount));
        senderWallet.setBalance(senderBalance);
        walletRepository.save(senderWallet);
        BigDecimal receiverBalanceUpdated = recieverWallet.getBalance().add(BigDecimal.valueOf(amount));

        recieverWallet.setBalance(receiverBalanceUpdated);
        walletRepository.save(recieverWallet);
        return senderWallet;
    }

    /**
     * Processes the payment for a given order by updating the user's wallet balance.
     * If the order is of type BUY, the order price is subtracted from the wallet balance.
     * If the order is of type SELL, the order price is added to the wallet balance.
     *
     * @param order the order for which payment is to be processed.
     * @param user the user whose wallet is to be updated.
     * @return the updated Wallet object after processing the order payment.
     * @throws Exception if the wallet does not contain sufficient funds for a BUY order.
     */
    @Override
    public Wallet payOrderPayment(Order order, User user) throws Exception {
        Wallet wallet = getUserWallet(user);

        if(order.getOrderType().equals(OrderType.BUY)){
            BigDecimal newBalance = wallet.getBalance().subtract(order.getPrice());
            if(newBalance.compareTo(order.getPrice())<0){
                throw new Exception("Insufficient funds for the Transaction");
            }
            wallet.setBalance(newBalance);
        }
        else if(order.getOrderType().equals(OrderType.SELL)){
            BigDecimal newBalance = wallet.getBalance().add(order.getPrice());
            wallet.setBalance(newBalance);

        }
        walletRepository.save(wallet);
        return wallet;
    }

}
