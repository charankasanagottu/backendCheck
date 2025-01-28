package com.kakz.tradeIn.controller;

import com.kakz.tradeIn.domain.PaymentMethod;
import com.kakz.tradeIn.model.PaymentOrder;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.response.PaymentResponse;
import com.kakz.tradeIn.service.PaymentService;
import com.kakz.tradeIn.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The PaymentController class is responsible for handling payment-related operations and endpoints.
 * It processes payment requests using different payment methods, such as RAZORPAY and STRIPE.
 */
@RestController
public class PaymentController {

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    /**
     * Handles payment requests by creating a payment order and generating a payment link for the specified payment method.
     *
     * @param paymentMethod the payment method to be used (e.g., RAZORPAY or STRIPE)
     * @param amount the amount to be paid
     * @param jwt the authorization token extracted from the request header
     * @return a ResponseEntity containing the PaymentResponse and HTTP status code
     * @throws Exception if an error occurs while processing the payment or retrieving user information
     */
    @PostMapping("/api/payment/{paymentMethod}/amount/{amount}")
    @Operation(summary = "Create a payment order and generate a payment link for the specified payment method")
    public ResponseEntity<PaymentResponse> paymentHandler(
            @PathVariable PaymentMethod paymentMethod,
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);

        PaymentResponse paymentResponse;

        PaymentOrder order= paymentService.createPaymentOrder(user, amount,paymentMethod);

        if(paymentMethod.equals(PaymentMethod.RAZORPAY)){
            paymentResponse=paymentService.createRazorpayPaymentLink(user,amount, order.getId());
        }
        else{
            paymentResponse=paymentService.createStripPaymentLink(user,amount, order.getId());
        }

        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }


}
