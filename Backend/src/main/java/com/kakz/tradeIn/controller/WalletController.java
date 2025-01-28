package com.kakz.tradeIn.controller;

import com.kakz.tradeIn.domain.WalletTransactionType;
import com.kakz.tradeIn.model.*;
import com.kakz.tradeIn.response.PaymentResponse;
import com.kakz.tradeIn.service.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * WalletController is responsible for handling wallet-related operations such as retrieving wallet information,
 * transferring funds between wallets, paying for orders, and depositing money into wallets.
 */
@RestController
//@RequestMapping("")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletTransactionService walletTransactionService;

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderService orderService;

    /**
     * Retrieves the user's wallet information based on the provided JWT authorization token.
     *
     * @param authorization the JWT authorization token used to authenticate and identify the user.
     * @return a ResponseEntity containing the user's Wallet object with a status of HttpStatus.ACCEPTED.
     * @throws Exception if there is an issue with finding the user profile by JWT or fetching the wallet.
     */
    @GetMapping("/api/wallet")
    @Operation(summary = "Retrieves the user's wallet information based on the provided JWT authorization'")
    public ResponseEntity<Wallet> getuserWallet(
            @RequestHeader("Authorization") String authorization) throws Exception {
        User user  = userService.findUserProfileByJwt(authorization);
        Wallet wallet = walletService.getUserWallet(user);
        return new ResponseEntity<>(wallet,HttpStatus.ACCEPTED);
    }

    /**
     * Transfers a specified amount from the sender's wallet to the receiver's wallet.
     *
     * @param authorization the JWT authorization token of the sender used to authenticate and identify the user.
     * @param receiverWalletId the ID of the receiver's wallet.
     * @param walletTransaction the WalletTransaction object containing details of the transaction including the amount.
     * @return a ResponseEntity containing the updated Wallet object of the sender with a status of HttpStatus.ACCEPTED.
     * @throws Exception if any error occurs during the transaction process, such as user authentication failure,
     *                   wallet not found, insufficient balance, or any issues in the transfer process.
     */
    @PutMapping("api/wallet/{receiverWalletId}/transfer")
    @Operation(summary = "Transfers a specified amount from the sender's wallet to the receiver's wallet.")
    public ResponseEntity<Wallet> walletToWalletTransfer(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long receiverWalletId,
            @RequestBody WalletTransaction walletTransaction
            ) throws Exception {
        User senderUser = userService.findUserProfileByJwt(authorization);
        Wallet receiverWallet = walletService.findWalletById(receiverWalletId);
        Wallet wallet = walletService.walletToWalletTransfer(
                senderUser,
                receiverWallet,
                walletTransaction.getAmount());

        WalletTransaction walletTransac =walletTransactionService.createTransaction(
                wallet,
                WalletTransactionType.WALLET_TRANSFER,receiverWallet.getId().toString(),
                walletTransaction.getPurpose(),
                -walletTransaction.getAmount()
        );
        return new ResponseEntity<>(wallet,HttpStatus.ACCEPTED);
    }

    /**
     * Processes the payment for an order using the sender's wallet.
     *
     * @param authorization the JWT authorization token used to authenticate and identify the user.
     * @param orderId the ID of the order to be paid.
     * @return a ResponseEntity containing the updated Wallet object with a status of HttpStatus.ACCEPTED.
     * @throws Exception if there is an issue with finding the user profile by JWT, fetching the order,
     *                   or processing the payment.
     */
    @PutMapping("api/wallet/order/{orderId}/pay")
    @Operation(summary = "Processes the payment for an order using the sender's wallet'")
    public ResponseEntity<Wallet> payOrderPayment(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long orderId
    ) throws Exception {

        User senderUser = userService.findUserProfileByJwt(authorization);
        Order order = orderService.getOrderById(orderId);
        Wallet wallet = walletService.payOrderPayment(order,senderUser);

        return new ResponseEntity<>(wallet,HttpStatus.ACCEPTED);
    }

    /**
     * Adds money to the user's wallet based on the provided payment details.
     *
     * @param jwt the JWT authorization token used to authenticate and identify the user.
     * @param orderId the ID of the payment order.
     * @param paymentId the ID of the payment transaction.
     * @return ResponseEntity containing the updated Wallet object with a status of HttpStatus.OK.
     * @throws Exception if there is an issue with user authentication, fetching the payment order, or processing the payment.
     */
    @PutMapping("/api/wallet/deposit")
   @Operation(summary = "Adds money to the user's wallet based on the provided payment details'")
    public ResponseEntity<Wallet> addMoneyToWallet(
            @RequestHeader("Authorization")String jwt,
            @RequestParam(name="order_id") Long orderId,
            @RequestParam(name="payment_id")String paymentId
    ) throws Exception {
        User user =userService.findUserProfileByJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);

        if(wallet.getBalance()==null) {
            wallet.setBalance(BigDecimal.ZERO);
        }
        PaymentOrder order = paymentService.getPaymentOrderById(orderId);
        Boolean status=paymentService.proceedPaymentOrder(order,paymentId);
        PaymentResponse res = new PaymentResponse();
        res.setPayment_url("deposit success");

        if(status){
            wallet=walletService.addBalance(wallet, order.getAmount());
        }

        return new ResponseEntity<>(wallet,HttpStatus.OK);

    }


    @GetMapping("/api/wallet/transactions")
    public ResponseEntity<List<WalletTransaction>> getWalletTransaction(
            @RequestHeader("Authorization")String jwt) throws Exception {
        User user=userService.findUserProfileByJwt(jwt);

        Wallet wallet = walletService.getUserWallet(user);

        List<WalletTransaction> transactions=walletTransactionService.getTransactions(wallet,null);

        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

}
