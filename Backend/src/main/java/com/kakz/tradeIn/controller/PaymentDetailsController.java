package com.kakz.tradeIn.controller;

import com.kakz.tradeIn.model.PaymentDetails;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.service.PaymentDetailsService;
import com.kakz.tradeIn.service.PaymentDetailsServiceImpl;
import com.kakz.tradeIn.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The PaymentDetailsController handles requests related to payment details
 * for authenticated users. It provides endpoints to add payment details and
 * retrieve payment details of the authenticated user.
 */
@RestController
@RequestMapping("/api")
public class PaymentDetailsController {
    @Autowired
    private PaymentDetailsService paymentDetailsService;

    @Autowired
    private UserService userService;

    /**
     * Adds payment details for the authenticated user.
     *
     * @param jwt a JSON Web Token for authenticating the user
     * @param paymentDetailsReq an object containing the payment details to be added
     * @return a ResponseEntity containing the added PaymentDetails object and an HTTP status of CREATED
     * @throws Exception if there is an error during the process
     */
    @PostMapping("/payment-details")
    @Operation(summary = "Adds payment details for the authenticated user")
    public ResponseEntity<PaymentDetails> addPaymentDetails(
            @RequestHeader("Authorization") String jwt,
            @RequestBody PaymentDetails paymentDetailsReq
    ) throws Exception {
        User user  = userService.findUserProfileByJwt(jwt);
        PaymentDetails paymentDetails = paymentDetailsService.addPaymentDetails(
                paymentDetailsReq.getAccountNumber(),
                paymentDetailsReq.getAccountHolderName(),
                paymentDetailsReq.getIfsc(),
                paymentDetailsReq.getBank(),
                user
        );
        return new ResponseEntity<>(paymentDetails, HttpStatus.CREATED);
    }

    /**
     * Retrieves the payment details of the authenticated user.
     *
     * @param jwt a JSON Web Token for authenticating the user
     * @return a ResponseEntity containing the user's PaymentDetails and an HTTP status of OK
     * @throws Exception if there is an error during the process
     */
    @GetMapping("/payment-details")
    @Operation(summary = "Retrieves the payment details of the authenticated user")
    public ResponseEntity<PaymentDetails> getUsersPaymentDetails(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        PaymentDetails paymentDetails = paymentDetailsService
                .getUsersPaymentDetails(user);
        return new ResponseEntity<>(paymentDetails, HttpStatus.OK);
    }
}
