package com.kakz.tradeIn.service;

import com.kakz.tradeIn.domain.PaymentMethod;
import com.kakz.tradeIn.model.PaymentOrder;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.response.PaymentResponse;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;

public interface PaymentService  {
    PaymentOrder createPaymentOrder(User user, Long amount,
                                    PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrderById(Long paymentOrderId) throws Exception;

    boolean proceedPaymentOrder(PaymentOrder paymentOrder,String paymentId) throws RazorpayException;

    PaymentResponse createRazorpayPaymentLink(User user, Long amount, Long orderId) throws RazorpayException;

    PaymentResponse createStripPaymentLink(User user, Long amount,Long orderId) throws StripeException;
}
