package com.kakz.tradeIn.controller;

import com.kakz.tradeIn.domain.WalletTransactionType;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.model.Wallet;
import com.kakz.tradeIn.model.WalletTransaction;
import com.kakz.tradeIn.model.Withdrawal;
import com.kakz.tradeIn.service.UserService;
import com.kakz.tradeIn.service.WalletService;
import com.kakz.tradeIn.service.WalletTransactionService;
import com.kakz.tradeIn.service.WithdrawalService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WithdrawalController {

    @Autowired
    private WithdrawalService withdrawalService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletTransactionService walletTransactionService;

    /**
     * Processes a withdrawal request by debiting the specified amount from the user's wallet.
     *
     * @param amount the amount of money to withdraw
     * @param jwt the JWT token used to authenticate the user
     * @return a ResponseEntity containing the details of the withdrawal and the HTTP status
     * @throws Exception if an error occurs during the withdrawal process
     */
    @PostMapping("/api/withdrawal/{amount}")
    @Operation(summary = "Processes a withdraw request by debiting the specified amount from the user's wallet'")
    public ResponseEntity<?> withdrawalRequest(
            @PathVariable Long amount,
            @RequestHeader("Authorization")String jwt) throws Exception {
        User user=userService.findUserProfileByJwt(jwt);
        Wallet userWallet=walletService.getUserWallet(user);

        Withdrawal withdrawal=withdrawalService.requestWithdrawal(amount,user);
        walletService.addBalance(userWallet, -withdrawal.getAmount());

        WalletTransaction walletTransaction = walletTransactionService.createTransaction(
                userWallet,
                WalletTransactionType.WITHDRAWAL,null,
                "bank account withdrawal",
                withdrawal.getAmount()
        );

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    /**
     * Proceed with the withdrawal request identified by the given ID and update its status based on the 'accept' flag.
     * If the withdrawal is declined, the amount is added back to the user's wallet balance.
     *
     * @param id the ID of the withdrawal request to be processed
     * @param accept a boolean indicating whether the request is accepted (true) or declined (false)
     * @param jwt the JSON Web Token for user authentication
     * @return a ResponseEntity containing the processed Withdrawal object and HTTP status code
     * @throws Exception if an error occurs during the withdrawal processing
     */
    @PatchMapping("/api/admin/withdrawal/{id}/proceed/{accept}")
    @Operation(summary = "Processes a withdraw request ")
    public ResponseEntity<?> proceedWithdrawal(
            @PathVariable Long id,
            @PathVariable boolean accept,
            @RequestHeader("Authorization")String jwt) throws Exception {
        User user=userService.findUserProfileByJwt(jwt);

        Withdrawal withdrawal=withdrawalService.procedWithdrawal(id,accept);

        Wallet userWallet=walletService.getUserWallet(user);
        if(!accept){
            walletService.addBalance(userWallet, withdrawal.getAmount());
        }

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    /**
     * Retrieves the withdrawal history for the authenticated user based on the provided JWT token.
     *
     * @param jwt the JSON Web Token used to authenticate the user
     * @return a ResponseEntity containing a list of Withdrawal objects and the HTTP status code
     * @throws Exception if an error occurs during the retrieval process
     */
    @GetMapping("/api/withdrawal")
    @Operation(summary = "Retrieves the withdraw history for the authenticated user")
    public ResponseEntity<List<Withdrawal>> getWithdrawalHistory(

            @RequestHeader("Authorization")String jwt) throws Exception {
        User user=userService.findUserProfileByJwt(jwt);

        List<Withdrawal> withdrawal=withdrawalService.getUsersWithdrawalHistory(user);

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    /**
     * Retrieves all withdrawal requests from the system.
     *
     * @param jwt the JSON Web Token used to authenticate the user making the request
     * @return a ResponseEntity containing a list of all withdrawal requests and the HTTP status
     * @throws Exception if an error occurs during the retrieval process
     */
    @GetMapping("/api/admin/withdrawal")
    @Operation(summary = "Retrieves all withdrawal requests from the system")
    public ResponseEntity<List<Withdrawal>> getAllWithdrawalRequest(

            @RequestHeader("Authorization")String jwt) throws Exception {
//        User user=userService.findUserProfileByJwt(jwt);

        List<Withdrawal> withdrawal=withdrawalService.getAllWithdrawalRequest();

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }
}
